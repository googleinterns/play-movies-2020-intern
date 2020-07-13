package com.google.moviestvsentiments.service.account;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.NetworkBoundResource;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.service.web.WebService;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/**
 * A repository that handles fetching and updating accounts.
 */
public class AccountRepository {

    private final AccountDao accountDao;
    private final Executor executor;
    private final WebService webService;

    @Inject
    AccountRepository(AccountDao accountDao, Executor executor, WebService webService) {
        this.accountDao = accountDao;
        this.executor = executor;
        this.webService = webService;
    }

    /**
     * Returns a new AccountRepository object.
     * @param accountDao The Dao object to use when accessing the local database.
     * @param executor The Executor to use when writing to the database.
     * @param webService The WebService to use when fetching from the server.
     * @return A new AccountRepository object.
     */
    public static AccountRepository create(AccountDao accountDao, Executor executor,
                                           WebService webService) {
        return new AccountRepository(accountDao, executor, webService);
    }

    /**
     * Adds the given account name into the accounts table. If the name already exists,
     * it is ignored. The account record is created with is_current set to false.
     * @param name The name of the account to add.
     */
    void addAccount(String name) {
        executor.execute(() -> {
            accountDao.addAccount(name);
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
}
