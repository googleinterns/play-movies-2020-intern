package com.google.moviestvsentiments;

import android.content.Context;
import androidx.room.Room;
import com.google.moviestvsentiments.service.account.AccountDao;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentDao;
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

    @Provides
    @Singleton
    public SentimentsDatabase provideSentimentsDatabase(@ApplicationContext Context context) {
        return Room.inMemoryDatabaseBuilder(context, SentimentsDatabase.class)
                .allowMainThreadQueries().build();
    }

    @Provides
    public AccountDao provideAccountDao(SentimentsDatabase database) {
        return database.accountDao();
    }

    @Provides
    public AssetSentimentDao provideAssetSentimentDao(SentimentsDatabase database) {
        return database.assetSentimentDao();
    }
}