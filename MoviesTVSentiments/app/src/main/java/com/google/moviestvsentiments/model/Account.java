package com.google.moviestvsentiments.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.time.Instant;

/**
 * A record in the accounts database table.
 */
@AutoValue
@Entity(tableName = "accounts_table")
public abstract class Account {

    @AutoValue.CopyAnnotations
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "account_name")
    @JsonProperty
    public abstract String name();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    @JsonProperty
    public abstract Instant timestamp();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "is_current", defaultValue = "0")
    @JsonIgnore
    public abstract boolean isCurrent();

    @AutoValue.CopyAnnotations
    @NonNull
    @ColumnInfo(name = "is_pending", defaultValue = "0")
    @JsonIgnore
    public abstract boolean isPending();

    /**
     * Returns an Account builder with its fields set to the fields of this Account.
     */
    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setName(String name);
        public abstract Builder setTimestamp(Instant timestamp);
        public abstract Builder setIsCurrent(boolean value);
        public abstract Builder setIsPending(boolean isPending);
        public abstract Account build();
    }

    /**
     * Creates a new Account object with the given fields. Room needs this factory method to create
     * Account objects.
     * @param name The name of the Account.
     * @param timestamp The timestamp of the Account.
     * @param isCurrent Whether the Account is currently logged in or not.
     * @param isPending Whether the Account needs to be synced with the server or not.
     * @return A new Account object with the given fields.
     */
    public static Account create(String name, Instant timestamp, boolean isCurrent, boolean isPending) {
        return builder().setName(name).setTimestamp(timestamp).setIsCurrent(isCurrent)
                .setIsPending(isPending).build();
    }

    /**
     * Creates a new Account object using the provided server Account fields. Jackson needs this
     * factory method to create Account objects. Room should not use this method, which is why it
     * has the Ignore annotation.
     * @param name The name of the Account.
     * @param timestamp The timestamp of the Account.
     * @return A new Account object with the given name and timestamp.
     */
    @Ignore
    @JsonCreator
    public static Account createFromServer(@JsonProperty("name") String name,
                                           @JsonProperty("timestamp") Instant timestamp) {
        return builder().setName(name).setTimestamp(timestamp).build();
    }

    /**
     * Returns an Account builder.
     */
    public static Builder builder() {
        return new AutoValue_Account.Builder().setIsCurrent(false).setIsPending(false);
    }
}
