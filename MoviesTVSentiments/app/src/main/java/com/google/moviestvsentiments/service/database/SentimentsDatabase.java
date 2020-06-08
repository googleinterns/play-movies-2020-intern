package com.google.moviestvsentiments.service.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.account.AccountDao;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SentimentsDatabase provides a high-level interface for performing database queries.
 */
@Database(entities = {Account.class}, version = 1, exportSchema = false)
public abstract class SentimentsDatabase extends RoomDatabase {

    /**
     * accountDao returns an object that allows queries on the accounts table.
     */
    public abstract AccountDao accountDao();

    private static volatile SentimentsDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * getDatabase returns the database instance. If the instance does not already exist, it is
     * built.
     * @param context The application context to use when building the database.
     */
    public static SentimentsDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (SentimentsDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            SentimentsDatabase.class, "sentiments_database").build();
                }
            }
        }
        return instance;
    }
}
