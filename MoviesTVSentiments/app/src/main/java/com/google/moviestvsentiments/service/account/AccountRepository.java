package com.google.moviestvsentiments.service.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.NetworkBoundResource;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.service.web.WebService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * A repository that handles fetching and updating accounts.
 */
public class AccountRepository {

    private final AccountDao accountDao;
    private final Executor executor;
    private final WebService webService;
    private final Clock clock;

    @Inject
    AccountRepository(AccountDao accountDao, Executor executor, WebService webService, Clock clock) {
        this.accountDao = accountDao;
        this.executor = executor;
        this.webService = webService;
        this.clock = clock;
    }

    /**
     * Returns a new AccountRepository object.
     * @param accountDao The Dao object to use when accessing the local database.
     * @param executor The Executor to use when writing to the database.
     * @param webService The WebService to use when fetching from the server.
     * @return A new AccountRepository object.
     */
    public static AccountRepository create(AccountDao accountDao, Executor executor,
                                           WebService webService, Clock clock) {
        return new AccountRepository(accountDao, executor, webService, clock);
    }

    /**
     * Adds the given account name into the accounts table. If the name already exists, the account
     * will be updated. The account is first sent to the server. If the server saves the account
     * successfully, then the account will be saved locally with isPending set to false. Otherwise,
     * it will be saved locally with isPending set to true.
     * @param name The name of the account to add.
     */
    void addAccount(String name) {
        Instant timestamp = Instant.now(clock);
        LiveData<ApiResponse<Account>> apiCall = webService.addAccount(name, timestamp);
        apiCall.observeForever(new Observer<ApiResponse<Account>>() {
            @Override
            public void onChanged(ApiResponse<Account> response) {
                apiCall.removeObserver(this);
                boolean isPending = !response.isSuccessful();
                executor.execute(() -> {
                    accountDao.addAccount(name, timestamp, isPending);
                });
            }
        });
    }

    /**
     * Returns a LiveData list of all accounts sorted by name in alphabetical order. The account
     * list is pulled from the server and cached locally in the Room database.
     */
    LiveData<Resource<List<Account>>> getAlphabetizedAccounts() {
        return new NetworkBoundResource<List<Account>, List<Account>>() {
            @Override
            protected void saveCallResult(List<Account> accounts) {
                executor.execute(() -> {
                    accountDao.addAccounts(accounts);
                });
            }

            @Override
            protected boolean shouldFetch(List<Account> data) {
                return true;
            }

            @Override
            protected LiveData<List<Account>> loadFromRoom() {
                return accountDao.getAlphabetizedAccounts();
            }

            @Override
            protected LiveData<ApiResponse<List<Account>>> performNetworkCall() {
                return webService.getAlphabetizedAccounts();
            }
        }.getResult();
    }

    /**
     * Returns a LiveData object containing the currently signed-in account or null if all accounts
     * are signed out. This method does not make a request to the server, as the server does not
     * have the notion of current accounts.
     */
    LiveData<Account> getCurrentAccount() {
        return accountDao.getCurrentAccount();
    }

    /**
     * Updates the given account record's is_current value. This method does not make a request to
     * the server, as the server does not have the notion of current accounts.
     * @param name The name of the account to update.
     * @param isCurrent The value to set is_current to.
     */
    void setIsCurrent(String name, boolean isCurrent) {
        executor.execute(() -> {
            accountDao.setIsCurrent(name, isCurrent);
        });
    }

    /**
     * Sends the list of pending Accounts to the server. If the server successfully adds them to its
     * database, then the Accounts are updated locally to no longer be pending. This method should
     * not be called from the main thread.
     * @return True if the Accounts are synced successfully.
     */
    public boolean syncPendingAccounts() {
        List<Account> pendingAccounts = accountDao.getPendingAccounts();
        if (pendingAccounts.isEmpty()) {
            return true;
        }

        ApiResponse<List<Account>> response = webService.syncPendingAccounts(pendingAccounts);
        if (response.isSuccessful()) {
            List<String> accountNames = response.getBody().stream().map(account -> account.name())
                    .collect(Collectors.toList());
            accountDao.clearIsPending(accountNames);
        }
        return response.isSuccessful();
    }
}
