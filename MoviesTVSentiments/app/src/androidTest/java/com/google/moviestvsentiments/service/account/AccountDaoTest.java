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
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AccountDaoTest {

    private static final String ACCOUNT_NAME_1 = "John Doe";
    private static final String ACCOUNT_NAME_2 = "Jane Doe";

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
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).name()).isEqualTo(ACCOUNT_NAME_1);
    }

    @Test
    public void addAndGetAccount_returnsIsCurrent() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).isCurrent()).isFalse();
    }

    @Test
    public void addAndGetAccount_returnsTimestamp() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).timestamp().getEpochSecond())
                .isEqualTo(Instant.MAX.getEpochSecond());
    }

    @Test
    public void addAndGetAccount_returnsIsPending() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, true);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts.get(0).isPending()).isTrue();
    }

    @Test
    public void addAndGetAccount_multipleAccounts_returnsAllAccounts() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        accountDao.addAccount(ACCOUNT_NAME_2, Instant.MAX, true);
        List<Account> accounts = LiveDataTestUtil.getValue(accountDao.getAlphabetizedAccounts());

        assertThat(accounts).hasSize(2);
        assertThat(accounts.get(0).name()).isEqualTo(ACCOUNT_NAME_2);
        assertThat(accounts.get(1).name()).isEqualTo(ACCOUNT_NAME_1);
    }

    @Test
    public void addAndGetAccount_duplicateAccount_returnsOneAccount() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
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
        accountDao.addAccount("Existing Account", Instant.MAX, false);

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
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.MAX, false);
        accountDao.setIsCurrent(ACCOUNT_NAME_1, true);

        Account account = LiveDataTestUtil.getValue(accountDao.getCurrentAccount());

        assertThat(account.name()).isEqualTo(ACCOUNT_NAME_1);
        assertThat(account.isCurrent()).isTrue();
    }

    @Test
    public void getPendingAccounts_withNonePending_returnsEmptyList() {
        List<Account> pendingAccounts = accountDao.getPendingAccounts();

        assertThat(pendingAccounts).isEmpty();
    }

    @Test
    public void getPendingAccounts_withSomePending_returnsAccounts() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.EPOCH, true);
        accountDao.addAccount(ACCOUNT_NAME_2, Instant.EPOCH, true);

        List<Account> pendingAccounts = accountDao.getPendingAccounts();

        assertThat(pendingAccounts).containsExactly(
                Account.create(ACCOUNT_NAME_1, Instant.EPOCH, false, true),
                Account.create(ACCOUNT_NAME_2, Instant.EPOCH, false, true));
    }

    @Test
    public void clearIsPending_andGetPending_returnsEmptyList() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.EPOCH, true);

        accountDao.clearIsPending(Arrays.asList(ACCOUNT_NAME_1));
        List<Account> pendingAccounts = accountDao.getPendingAccounts();

        assertThat(pendingAccounts).isEmpty();
    }

    @Test
    public void clearIsPending_doesNotClearIsCurrent() {
        accountDao.addAccount(ACCOUNT_NAME_1, Instant.EPOCH, true);
        accountDao.setIsCurrent(ACCOUNT_NAME_1, true);

        accountDao.clearIsPending(Arrays.asList(ACCOUNT_NAME_1));
        Account account = LiveDataTestUtil.getValue(accountDao.getCurrentAccount());

        assertThat(account).isEqualTo(Account.create(ACCOUNT_NAME_1, Instant.EPOCH, true,
                false));
    }
}
