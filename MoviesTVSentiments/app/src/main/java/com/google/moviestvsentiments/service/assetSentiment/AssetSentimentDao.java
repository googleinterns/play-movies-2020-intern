package com.google.moviestvsentiments.service.assetSentiment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import java.util.List;

@Dao
public abstract class AssetSentimentDao {

    /**
     * Adds the given asset into the assets table. If the asset already exists, it is ignored.
     * @param asset The asset to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addAsset(Asset asset);

    /**
     * Returns the asset with the given id and type or null if it does not exist.
     * @param assetId The id of the asset.
     * @param assetType The type of the asset.
     * @return The asset matching the id and type.
     */
    @Query("SELECT * FROM assets_table WHERE asset_id = :assetId AND asset_type = :assetType")
    public abstract Asset getAsset(String assetId, AssetType assetType);

    /**
     * Returns a list of assets matching the given type that have been reacted to with the given
     * sentiment by the given account. If the sentiment type is UNSPECIFIED, assets with no
     * corresponding user sentiment record will also be returned.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A list of assets with reactions matching the given account name and sentiment type.
     */
    public List<Asset> getAssets(AssetType assetType, String accountName,
                                 SentimentType sentimentType) {
        List<Asset> results = getAssetsWithSentiment(assetType, accountName, sentimentType);
        if (sentimentType == SentimentType.UNSPECIFIED) {
            results.addAll(getAssetsWithoutSentiment(assetType, accountName));
        }
        return results;
    }

    /**
     * Returns a list of assets with reactions that match the given account name and sentiment type.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A list of assets with reactions matching the given account name and sentiment type.
     */
    @Query("SELECT * FROM assets_table as L " +
            "WHERE EXISTS " +
            "(SELECT * FROM user_sentiments_table AS R " +
                "WHERE L.asset_id = R.asset_id AND account_name = :accountName " +
                "AND asset_type = :assetType AND sentiment_type = :sentimentType)")
    protected abstract List<Asset> getAssetsWithSentiment(AssetType assetType, String accountName,
                                                          SentimentType sentimentType);

    /**
     * Returns a list of assets that have not been reacted to by the given account name and
     * therefore have no matching user sentiment record.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking for sentiments.
     * @return A list of assets that have not been reacted to by the given account.
     */
    @Query("SELECT * FROM assets_table AS L " +
            "WHERE L.asset_type = :assetType AND NOT EXISTS " +
            "(SELECT * FROM user_sentiments_table AS R " +
                "WHERE L.asset_id = R.asset_id AND account_name = :accountName " +
                "AND L.asset_type = R.asset_type)")
    protected abstract List<Asset> getAssetsWithoutSentiment(AssetType assetType, String accountName);

    /**
     * Inserts or replaces the user sentiment specified by the asset and account with the given
     * sentiment type.
     * @param accountName The account that the user sentiment is associated with.
     * @param assetId The id of the asset that the user sentiment is associated with.
     * @param assetType The type of the asset that the user sentiment is associated with.
     * @param sentimentType The type of the new user sentiment.
     */
    @Query("INSERT OR REPLACE INTO user_sentiments_table " +
            "(asset_id, account_name, asset_type, sentiment_type) " +
            "VALUES (:assetId, :accountName, :assetType, :sentimentType)")
    public abstract void updateSentiment(String accountName, String assetId, AssetType assetType,
                                         SentimentType sentimentType);

    /**
     * Deletes all user sentiments corresponding to the given account name.
     * @param accountName The account to delete all sentiments for.
     */
    @Query("DELETE FROM user_sentiments_table WHERE account_name = :accountName")
    public abstract void deleteAllSentiments(String accountName);
}
