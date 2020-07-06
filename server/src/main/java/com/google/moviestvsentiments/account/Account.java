package com.google.moviestvsentiments.account;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

/**
 * A record in the accounts database table.
 */
@Entity
public class Account {
    
    @Id
    private String accountName;
    private long timestamp;

    // The default constructor is required by the Spring JPA.
    protected Account() {}

    private Account(String accountName, long timestamp) {
        this.accountName = accountName;
        this.timestamp = timestamp;
    }

    /**
     * Creates a new Account with the given name and timestamp.
     * @param accountName The name for the new account.
     * @param timestamp The timestamp for the new account.
     * @return A new Account with the given name and timestamp.
     */
    public static Account create(String accountName, long timestamp) {
        return new Account(accountName, timestamp);
    }

    /**
     * Returns the account name.
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the account name.
     * @param accountName The new name for the account.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Returns the account's timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the account's timestamp.
     * @param timestamp The new timestamp for the account.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return timestamp == account.timestamp &&
                accountName.equals(account.accountName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountName, timestamp);
    }
}