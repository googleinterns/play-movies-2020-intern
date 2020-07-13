package com.google.moviestvsentiments.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.time.Instant;

/**
 * A record in the accounts database table.
 */
@Entity(tableName = "accounts_table")
public class Account {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "account_name")
    public String name;

    @NonNull
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    public Instant timestamp;

    @NonNull
    @ColumnInfo(name = "is_current", defaultValue = "0")
    public boolean isCurrent;
}
