package com.google.moviestvsentiments.usecase.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.account.AccountViewModel;
import com.google.moviestvsentiments.usecase.addAccount.AddAccountActivity;

import dagger.hilt.android.AndroidEntryPoint;
import java.util.List;
import javax.inject.Inject;

/**
 * An Activity that displays a list of account names and allows for the selection of an existing
 * account or the creation of a new account.
 */
@AndroidEntryPoint
public class SigninActivity extends AppCompatActivity implements AccountListAdapter.AccountClickListener {

    private static final int ADD_ACCOUNT_REQUEST_CODE = 1;

    @Inject
    AccountViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        RecyclerView accountList = findViewById(R.id.accountList);
        accountList.setHasFixedSize(true);
        final AccountListAdapter adapter = AccountListAdapter.create(this);
        accountList.setAdapter(adapter);
        accountList.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getAlphabetizedAccounts().observe(this, new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                adapter.setAccounts(accounts);
            }
        });
    }

    /**
     * Initiates the add account process if addAccount is true or sets the selected account to be
     * the current account if addAccount is false.
     * @param addAccount True if the user has requested to add a new account. False if the user
     *                   has selected an existing account.
     * @param accountName The name of the selected account or "Add Account" if addAccount is true.
     */
    @Override
    public void onAccountClick(boolean addAccount, String accountName) {
        if (!addAccount) {
            viewModel.setIsCurrent(accountName, true);
        } else {
            Intent intent = new Intent(this, AddAccountActivity.class);
            startActivityForResult(intent, ADD_ACCOUNT_REQUEST_CODE);
        }
    }
}