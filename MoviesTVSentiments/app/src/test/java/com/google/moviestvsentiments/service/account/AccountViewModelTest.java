package com.google.moviestvsentiments.service.account;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class AccountViewModelTest {

    private static final Account ACCOUNT = createAccount("Account Name", Instant.ofEpochSecond(13), true);

    private static Account createAccount(String accountName, Instant timestamp, boolean isCurrent) {
        Account account = new Account();
        account.name = accountName;
        account.timestamp = timestamp;
        account.isCurrent = isCurrent;
        return account;
    }

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AccountRepository repository;
    private AccountViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(AccountRepository.class);
        viewModel = AccountViewModel.create(repository);
    }

    @Test
    public void addAccount_invokesRepository() {
        viewModel.addAccount("Account Name");

        verify(repository).addAccount("Account Name");
    }

    @Test
    public void getAlphabetizedAccounts_returnsAccounts() {
        MutableLiveData<Resource<List<Account>>> accountsData = new MutableLiveData<>();
        accountsData.setValue(Resource.success(Arrays.asList(ACCOUNT)));
        when(repository.getAlphabetizedAccounts()).thenReturn(accountsData);

        Resource<List<Account>> resource = LiveDataTestUtil.getValue(viewModel.getAlphabetizedAccounts());

        assertThat(resource.getValue()).containsExactly(ACCOUNT);
    }

    @Test
    public void getCurrentAccount_returnsAccount() {
        MutableLiveData<Account> accountData = new MutableLiveData<>();
        accountData.setValue(ACCOUNT);
        when(repository.getCurrentAccount()).thenReturn(accountData);

        Account result = LiveDataTestUtil.getValue(viewModel.getCurrentAccount());

        assertThat(result).isEqualTo(ACCOUNT);
    }

    @Test
    public void setIsCurrent_invokesRepository() {
        viewModel.setIsCurrent("Account Name", true);

        verify(repository).setIsCurrent("Account Name", true);
    }
}
