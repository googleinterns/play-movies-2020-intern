package com.google.moviestvsentiments.service.account;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Account;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/**
 * A repository that handles fetching and updating accounts.
 */
public class AccountRepository {

    private final AccountDao accountDao;
    private final Executor executor;

    @Inject
    AccountRepository(AccountDao accountDao, Executor executor) {
        this.accountDao = accountDao;
        this.executor = executor;
    }

    /**
     * Returns a new AccountRepository object.
     * @param accountDao The Dao object to use when accessing the local database.
     * @param executor The Executor to use when writing to the database.
     * @return A new AccountRepository object.
     */
    public static AccountRepository create(AccountDao accountDao, Executor executor) {
        return new AccountRepository(accountDao, executor);
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
     * Returns a LiveData list of all accounts sorted by name in alphabetical order.
     */
    LiveData<List<Account>> getAlphabetizedAccounts() {
        return accountDao.getAlphabetizedAccounts();
    }

    /**
     * Returns a LiveData object containing the currently signed-in account or null if all accounts
     * are signed out.
     */
    LiveData<Account> getCurrentAccount() {
        return accountDao.getCurrentAccount();
    }

    /**
     * Updates the given account record's is_current value.
     * @param name The name of the account to update.
     * @param isCurrent The value to set is_current to.
     */
    void setIsCurrent(String name, boolean isCurrent) {
        executor.execute(() -> {
            accountDao.setIsCurrent(name, isCurrent);
        });
    }
}
