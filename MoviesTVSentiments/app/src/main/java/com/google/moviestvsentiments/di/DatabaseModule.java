package com.google.moviestvsentiments.di;

import android.content.Context;
import androidx.room.Room;
import com.google.moviestvsentiments.service.account.AccountDao;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentDao;
import com.google.moviestvsentiments.service.database.AsyncDatabaseExecutor;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import java.time.Clock;
import java.util.concurrent.Executor;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * A Hilt module that provides database dependencies.
 */
@InstallIn(ApplicationComponent.class)
@Module
public class DatabaseModule {

    /**
     * Returns the singleton SentimentsDatabase.
     * @param appContext The application context in which the database should be built.
     * @return The SentimentsDatabase object for the application.
     */
    @Provides
    @Singleton
    public SentimentsDatabase provideSentimentsDatabase(@ApplicationContext Context appContext) {
        return Room.databaseBuilder(appContext, SentimentsDatabase.class,
                "sentiments_database").build();
    }

    /**
     * Returns the AccountDao object for the given database.
     * @param database The database to get the AccountDao from.
     * @return The AccountDao object.
     */
    @Provides
    public AccountDao provideAccountDao(SentimentsDatabase database) {
        return database.accountDao();
    }

    /**
     * Returns the AssetSentimentDao object for the given database.
     * @param database The database to get the AssetSentimentDao from.
     * @return The AssetSentimentDao object.
     */
    @Provides
    public AssetSentimentDao provideAssetSentimentDao(SentimentsDatabase database) {
        return database.assetSentimentDao();
    }

    /**
     * Returns the singleton database Executor.
     */
    @Provides
    @Singleton
    public Executor provideDatabaseExecutor() {
        return AsyncDatabaseExecutor.create();
    }

    /**
     * Returns the Clock object for use in setting timestamps.
     */
    @Provides
    @Singleton
    public Clock provideClock() {
        return Clock.systemUTC();
    }
}
