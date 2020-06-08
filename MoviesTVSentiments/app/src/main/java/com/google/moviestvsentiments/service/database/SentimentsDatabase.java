package com.google.moviestvsentiments.service.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.account.AccountDao;

/**
 * A high-level interface for performing database queries.
 */
@Database(entities = {Account.class}, version = 1, exportSchema = false)
public abstract class SentimentsDatabase extends RoomDatabase {

    /**
     * Returns an object that performs queries on the accounts table.
     */
    public abstract AccountDao accountDao();
}
