package com.google.moviestvsentiments.service.account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.google.moviestvsentiments.model.Account;
import java.util.List;
import javax.inject.Inject;

/**
 * A ViewModel that handles fetching and updating accounts.
 */
public class AccountViewModel extends ViewModel {

    private AccountRepository repository;

    @Inject
    AccountViewModel(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a new AccountViewModel object.
     * @param repository The repository to use when accessing accounts.
     * @return A new AccountViewModel object.
     */
    public static AccountViewModel create(AccountRepository repository) {
        return new AccountViewModel(repository);
    }

    /**
     * Adds the given account name into the accounts table. If the name already exists,
     * it is ignored. The account record is created with is_current set to false.
     * @param name The name of the account to add.
     */
    public void addAccount(String name) {
        repository.addAccount(name);
    }

    /**
     * Returns a LiveData list of all accounts sorted by name in alphabetical order.
     */
    public LiveData<List<Account>> getAlphabetizedAccounts() {
        return repository.getAlphabetizedAccounts();
    }

    /**
     * Returns a LiveData object containing the currently signed-in account or null if all accounts
     * are signed out.
     */
    public LiveData<Account> getCurrentAccount() {
        return repository.getCurrentAccount();
    }

    /**
     * Updates the given account record's is_current value.
     * @param name The name of the account to update.
     * @param isCurrent The value to set is_current to.
     * @return True if the record exists and was updated successfully.
     */
    public boolean setIsCurrent(String name, boolean isCurrent) {
        return repository.setIsCurrent(name, isCurrent);
    }
}
