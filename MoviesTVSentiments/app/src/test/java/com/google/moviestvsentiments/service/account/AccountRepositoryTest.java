package com.google.moviestvsentiments.service.account;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import com.google.moviestvsentiments.util.MainThreadDatabaseExecutor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class AccountRepositoryTest {

    private static final Account ACCOUNT = createAccount("Account Name", 1337, true);

    private static Account createAccount(String accountName, long timestamp, boolean isCurrent) {
        Account account = new Account();
        account.name = accountName;
        account.timestamp = timestamp;
        account.isCurrent = isCurrent;
        return account;
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AccountDao dao;
    private AccountRepository repository;

    @Before
    public void setUp() {
        dao = mock(AccountDao.class);
        repository = AccountRepository.create(dao, new MainThreadDatabaseExecutor());
    }

    @Test
    public void addAccount_invokesDao() {
        repository.addAccount("Account Name");

        verify(dao).addAccount("Account Name");
    }

    @Test
    public void getAlphabetizedAccounts_returnsAccounts() {
        MutableLiveData<List<Account>> accountsData = new MutableLiveData<>();
        accountsData.setValue(Arrays.asList(ACCOUNT));
        when(dao.getAlphabetizedAccounts()).thenReturn(accountsData);

        List<Account> results = LiveDataTestUtil.getValue(repository.getAlphabetizedAccounts());

        assertThat(results).containsExactly(ACCOUNT);
    }

    @Test
    public void getCurrentAccount_returnsAccount() {
        MutableLiveData<Account> accountData = new MutableLiveData<>();
        accountData.setValue(ACCOUNT);
        when(dao.getCurrentAccount()).thenReturn(accountData);

        Account result = LiveDataTestUtil.getValue(repository.getCurrentAccount());

        assertThat(result).isEqualTo(ACCOUNT);
    }

    @Test
    public void setIsCurrent_invokesDao() {
        repository.setIsCurrent("Account Name", false);

        verify(dao).setIsCurrent("Account Name", false);
    }
}
