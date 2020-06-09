package com.google.moviestvsentiments.service.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.model.UserSentiment;
import com.google.moviestvsentiments.service.account.AccountDao;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentDao;

/**
 * A high-level interface for performing database queries.
 */
@Database(entities = {Account.class, Asset.class, UserSentiment.class}, version = 1,
        exportSchema = false)
@TypeConverters({AssetType.class, SentimentType.class})
public abstract class SentimentsDatabase extends RoomDatabase {

    /**
     * Returns an object that performs queries on the accounts table.
     */
    public abstract AccountDao accountDao();

    /**
     * Returns an object that performs queries on the assets and user sentiments tables.
     */
    public abstract AssetSentimentDao assetSentimentDao();
}
