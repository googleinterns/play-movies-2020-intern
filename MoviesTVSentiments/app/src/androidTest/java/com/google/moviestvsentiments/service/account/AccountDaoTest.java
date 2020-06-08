package com.google.moviestvsentiments.service.account;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AccountDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AccountDao accountDao;
    private SentimentsDatabase database;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, SentimentsDatabase.class)
                .allowMainThreadQueries().build();
        accountDao = database.accountDao();
    }

    @After
    public void closeDatabase() {
        database.close();
    }

    @Test
    public void addAndGetAccount_returnsName() {
        accountDao.addAccount("John Doe");
        List<Account> accounts = accountDao.getAlphabetizedAccounts();
        assertEquals("John Doe", accounts.get(0).name);
        assertTrue(accounts.get(0).timestamp > 0);
    }

    @Test
    public void addAndGetAccount_returnsIsCurrent() {
        accountDao.addAccount("John Doe");
        List<Account> accounts = accountDao.getAlphabetizedAccounts();
        assertFalse(accounts.get(0).isCurrent);
    }

    @Test
    public void addAndGetAccount_returnsTimestamp() {
        accountDao.addAccount("John Doe");
        List<Account> accounts = accountDao.getAlphabetizedAccounts();
        assertTrue(accounts.get(0).timestamp > 0);
    }

    @Test
    public void addAndGetAccount_multipleAccounts_returnsAllAccounts() {
        accountDao.addAccount("John Doe");
        accountDao.addAccount("Jane Doe");
        List<Account> accounts = accountDao.getAlphabetizedAccounts();
        assertEquals(2, accounts.size());
        assertEquals("Jane Doe", accounts.get(0).name);
        assertEquals("John Doe", accounts.get(1).name);
    }

    @Test
    public void addAndGetAccount_duplicateAccount_returnsOneAccount() {
        accountDao.addAccount("John Doe");
        accountDao.addAccount("John Doe");
        List<Account> accounts = accountDao.getAlphabetizedAccounts();
        assertEquals(1, accounts.size());
    }

    @Test
    public void setIsCurrent_nonexistentAccount_returns0() {
        int updated = accountDao.setIsCurrent("Nonexistent Account", false);
        assertEquals(0, updated);
    }

    @Test
    public void setIsCurrent_existingAccount_returns1() {
        accountDao.addAccount("Existing Account");
        int updated = accountDao.setIsCurrent("Existing Account", false);
        assertEquals(1, updated);
    }

    @Test
    public void getCurrentAccount_withNoCurrentSet_returnsNull() {
        Account account = accountDao.getCurrentAccount();
        assertNull(account);
    }

    @Test
    public void getCurrentAccount_withCurrentSet_returnsAccount() {
        accountDao.addAccount("John Doe");
        accountDao.setIsCurrent("John Doe", true);
        Account account = accountDao.getCurrentAccount();
        assertEquals("John Doe", account.name);
        assertTrue(account.isCurrent);
    }
}
