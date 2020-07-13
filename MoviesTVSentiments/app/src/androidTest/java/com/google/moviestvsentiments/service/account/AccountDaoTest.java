package com.google.moviestvsentiments.service.account;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

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
        accountDao.addAccount("John Doe", false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).name()).isEqualTo("John Doe");
    }

    @Test
    public void addAndGetAccount_returnsIsCurrent() {
        accountDao.addAccount("John Doe", false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).isCurrent()).isFalse();
    }

    @Test
    public void addAndGetAccount_returnsTimestamp() {
        accountDao.addAccount("John Doe", false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).timestamp().getEpochSecond()).isGreaterThan(0);
    }

    @Test
    public void addAndGetAccount_returnsIsPending() {
        accountDao.addAccount("John Doe", true);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).isPending()).isTrue();
    }

    @Test
    public void addAndGetAccount_multipleAccounts_returnsAllAccounts() {
        accountDao.addAccount("John Doe", false);
        accountDao.addAccount("Jane Doe", true);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).name()).isEqualTo("Jane Doe");
        assertThat(accounts.get(1).name()).isEqualTo("John Doe");
    }

    @Test
    public void addAndGetAccount_duplicateAccount_returnsOneAccount() {
        accountDao.addAccount("John Doe", false);
        accountDao.addAccount("John Doe", false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts).hasSize(1);
    }

    @Test
    public void setIsCurrent_nonexistentAccount_returnsFalse() {
        boolean updated = accountDao.setIsCurrent("Nonexistent Account", false);

        assertThat(updated).isFalse();
    }

    @Test
    public void setIsCurrent_existingAccount_returnsTrue() {
        accountDao.addAccount("Existing Account", false);

        boolean updated = accountDao.setIsCurrent("Existing Account", false);

        assertThat(updated).isTrue();
    }

    @Test
    public void getCurrentAccount_withNoCurrentSet_returnsNull() {
        Account account = LiveDataTestUtil.getValue(accountDao.getCurrentAccount());

        assertThat(account).isNull();
    }

    @Test
    public void getCurrentAccount_withCurrentSet_returnsAccount() {
        accountDao.addAccount("John Doe", false);
        accountDao.setIsCurrent("John Doe", true);

        Account account = LiveDataTestUtil.getValue(accountDao.getCurrentAccount());

        assertThat(account.name()).isEqualTo("John Doe");
        assertThat(account.isCurrent()).isTrue();
    }
}
