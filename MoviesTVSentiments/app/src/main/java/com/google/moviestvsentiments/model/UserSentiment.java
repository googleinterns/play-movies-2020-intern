package com.google.moviestvsentiments.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.TypeConverters;

/**
 * A record in the user sentiments database table
 */
@Entity(tableName = "user_sentiments_table", primaryKeys = {"asset_id", "account_name",
    "asset_type"}, foreignKeys = @ForeignKey(entity = Asset.class, parentColumns = "asset_id",
    childColumns = "asset_id", onDelete = ForeignKey.CASCADE))
@TypeConverters({AssetType.class, SentimentType.class})
public class UserSentiment {

    @NonNull
    @ColumnInfo(name = "asset_id")
    public String assetId;

    @NonNull
    @ColumnInfo(name = "account_name")
    public String accountName;

    @NonNull
    @ColumnInfo(name = "asset_type")
    public AssetType assetType;

    @NonNull
    @ColumnInfo(name = "sentiment_type", defaultValue = "0")
    public SentimentType sentimentType;

    @NonNull
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    public long timestamp;
}
