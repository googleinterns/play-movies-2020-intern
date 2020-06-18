package com.google.moviestvsentiments;

import android.content.Context;
import androidx.room.Room;
import com.google.moviestvsentiments.service.account.AccountDao;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentDao;
import com.google.moviestvsentiments.service.database.AsyncDatabaseExecutor;
import com.google.moviestvsentiments.service.database.DatabaseExecutor;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * A Hilt module that provides in-memory database and dao objects.
 */
@Module
@InstallIn(ApplicationComponent.class)
public class InMemoryDatabaseModule {

    private static final int NUMBER_OF_WRITE_THREADS = 4;

    /**
     * Returns the singleton SentimentsDatabase.
     * @param context The application context in which the database should be built.
     * @return The SentimentsDatabase object for the application.
     */
    @Provides
    @Singleton
    public SentimentsDatabase provideSentimentsDatabase(@ApplicationContext Context context) {
        return Room.inMemoryDatabaseBuilder(context, SentimentsDatabase.class).build();
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
     * Returns the singleton DatabaseExecutor.
     */
    @Provides
    @Singleton
    public DatabaseExecutor provideDatabaseExecutor() {
        return AsyncDatabaseExecutor.create(NUMBER_OF_WRITE_THREADS);
    }
}