package com.google.moviestvsentiments.account;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * A record in the accounts database table.
 */
@Entity
public class Account {
    
    @Id
    private String name;
    private Instant timestamp;

    // The default constructor is required by the Spring JPA.
    protected Account() {}

    private Account(String name, Instant timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    /**
     * Creates a new Account with the given name and timestamp.
     * @param name The name for the new account.
     * @param timestamp The timestamp for the new account.
     * @return A new Account with the given name and timestamp.
     */
    public static Account create(String name, Instant timestamp) {
        return new Account(name, timestamp);
    }

    /**
     * Returns the account name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the account name.
     * @param name The new name for the account.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the account's timestamp.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the account's timestamp.
     * @param timestamp The new timestamp for the account.
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return timestamp.equals(account.timestamp) &&
                name.equals(account.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timestamp);
    }
}