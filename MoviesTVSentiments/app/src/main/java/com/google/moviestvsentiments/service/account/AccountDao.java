package com.google.moviestvsentiments.service.account;

import androidx.room.Dao;
import androidx.room.Query;
import com.google.moviestvsentiments.model.Account;
import java.util.List;

/**
 * AccountDao provides a high-level interface for querying the accounts database table.
 */
@Dao
public interface AccountDao {

    /**
     * addAccount adds the given account name into the accounts table. If the name already exists,
     * it is ignored. The account record is created with is_current set to false.
     * @param name The name of the account to add.
     */
    @Query("INSERT OR IGNORE INTO accounts_table (account_name) VALUES (:name)")
    public void addAccount(String name);

    /**
     * getAlphabetizedAccounts returns a list of all accounts in alphabetical order.
     */
    @Query("SELECT * FROM accounts_table ORDER BY account_name ASC")
    public List<Account> getAlphabetizedAccounts();

    /**
     * @return The currently in-use account.
     */
    @Query("SELECT * FROM accounts_table WHERE is_current = 1")
    public Account getCurrentAccount();

    /**
     * Updates the given account record's is_current value.
     * @param name The name of the account to update.
     * @param isCurrent The value to set is_current to.
     * @return The number of updated records. This will be 1 if the account exists and 0 otherwise.
     */
    @Query("UPDATE accounts_table SET is_current = :isCurrent WHERE account_name = :name")
    public int setIsCurrent(String name, boolean isCurrent);
}
