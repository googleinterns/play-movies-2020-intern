package com.google.moviestvsentiments.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import java.time.Instant;

/**
 * A record in the user sentiments database table
 */
@Entity(tableName = "user_sentiments_table",
        primaryKeys = {"asset_id", "account_name", "asset_type"})
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
    public Instant timestamp;

    @NonNull
    @ColumnInfo(name = "is_pending", defaultValue = "0")
    public boolean isPending;

    /**
     * Creates a new UserSentiment object with the provided fields.
     * @param assetId The id of the asset associated with the UserSentiment.
     * @param accountName The name of the account associated with the UserSentiment.
     * @param assetType The type of the asset associated with the UserSentiment.
     * @param sentimentType The type of the UserSentiment.
     * @param timestamp The timestamp of the UserSentiment.
     * @param isPending Whether the UserSentiment needs to be synced with the server or not.
     * @return A new UserSentiment object with the provided fields.
     */
    public static UserSentiment create(String assetId, String accountName, AssetType assetType,
                           SentimentType sentimentType, Instant timestamp, boolean isPending) {
        UserSentiment sentiment = new UserSentiment();
        sentiment.assetId = assetId;
        sentiment.accountName = accountName;
        sentiment.assetType = assetType;
        sentiment.sentimentType = sentimentType;
        sentiment.timestamp = timestamp;
        sentiment.isPending = isPending;
        return sentiment;
    }
}
