package com.google.moviestvsentiments.account;

import java.util.List;

/**
 * Represents the response type for the add account endpoint.
 */
public class AddAccountResponse {

    private List<Account> accounts;
    private String error;

    private AddAccountResponse(List<Account> accounts, String error) {
        this.accounts = accounts;
        this.error = error;
    }

    /**
     * Creates a new AddAccountResponse with the given account list and error.
     * @param accounts The list of accounts that were successfully created.
     * @param error The error message that occurred when creating the accounts.
     * @return A new AddAccountResponse with the given account list and error.
     */
    public static AddAccountResponse create(List<Account> accounts, String error) {
        return new AddAccountResponse(accounts, error);
    }

    /**
     * Returns the list of accounts that were created successfully.
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Returns the message of the error that occurred when creating the accounts.
     */
    public String getError() {
        return error;
    }
}
