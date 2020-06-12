package com.google.moviestvsentiments.service.account;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.google.moviestvsentiments.model.Account;
import java.util.List;

/**
 * A high-level interface for querying the accounts database table.
 */
@Dao
public abstract class AccountDao {

    /**
     * Adds the given account name into the accounts table. If the name already exists,
     * it is ignored. The account record is created with is_current set to false.
     * @param name The name of the account to add.
     */
    @Query("INSERT OR IGNORE INTO accounts_table (account_name) VALUES (:name)")
    public abstract void addAccount(String name);

    /**
     * Returns a LiveData list of all accounts in alphabetical order.
     */
    @Query("SELECT * FROM accounts_table ORDER BY account_name ASC")
    public abstract LiveData<List<Account>> getAlphabetizedAccounts();

    /**
     * Returns a LiveData object containing the currently signed-in account or null if all accounts
     * are signed-out.
     */
    @Query("SELECT * FROM accounts_table WHERE is_current = 1")
    public abstract LiveData<Account> getCurrentAccount();

    /**
     * Updates the given account record's is_current value.
     * @param name The name of the account to update.
     * @param isCurrent The value to set is_current to.
     * @return True if the record exists and was updated successfully.
     */
    public boolean setIsCurrent(String name, boolean isCurrent) {
        int updated = setIsCurrentQuery(name, isCurrent);
        return updated > 0;
    }

    // Room update query methods can only have void or int return types, so this method exists
    // to allow the public API to return a boolean.
    @Query("UPDATE accounts_table SET is_current = :isCurrent WHERE account_name = :name")
    protected abstract int setIsCurrentQuery(String name, boolean isCurrent);
}
