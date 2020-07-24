package com.google.moviestvsentiments.service.assetSentiment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.model.UserSentiment;

import java.time.Instant;
import java.util.ArrayList;
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
     * Returns a LiveData view of the asset with the given id and type or null if the asset does not
     * exist. The asset is combined with the matching user sentiment and returned as an
     * AssetSentiment object.
     * @param accountName The name of the account to match with the sentiment.
     * @param assetId The id of the asset.
     * @param assetType The type of the asset.
     * @return A LiveData object containing the AssetSentiment matching the account name, asset id
     * and asset type.
     */
    @Query("SELECT L.*, sentiment_type AS sentimentType " +
            "FROM assets_table AS L " +
            "LEFT JOIN user_sentiments_table AS R " +
            "ON L.asset_id = R.asset_id AND account_name = :accountName AND L.asset_type = R.asset_type " +
            "WHERE L.asset_id = :assetId AND L.asset_type = :assetType")
    public abstract LiveData<AssetSentiment> getAsset(String accountName, String assetId,
                                                      AssetType assetType);

    /**
     * Returns a LiveData list of AssetSentiments matching the given type that have been reacted to
     * with the given sentiment by the given account. If the sentiment type is UNSPECIFIED, assets
     * with no corresponding user sentiment record will also be returned.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A LiveData list of AssetSentiments with reactions matching the given account
     * name and sentiment type.
     */
    public LiveData<List<AssetSentiment>> getAssets(AssetType assetType, String accountName,
                                                    SentimentType sentimentType) {
        LiveData<List<AssetSentiment>> reactionResults = getAssetsWithSentiment(assetType,
                accountName, sentimentType);
        if (sentimentType != SentimentType.UNSPECIFIED) {
            return reactionResults;
        }

        LiveData<List<AssetSentiment>> noReactionResults = getAssetsWithoutSentiment(assetType,
                accountName);
        MediatorLiveData<List<AssetSentiment>> allResults = new MediatorLiveData<>();
        allResults.addSource(reactionResults, values -> combineLiveDataLists(allResults, values,
                noReactionResults));
        allResults.addSource(noReactionResults, values -> combineLiveDataLists(allResults, values,
                reactionResults));
        return allResults;
    }

    /**
     * Returns a LiveData list of AssetSentiments with reactions that match the given account name
     * and sentiment type.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A LiveData list of AssetSentiments with reactions matching the given asset type,
     * account name and sentiment type.
     */
    @Query("SELECT *, :sentimentType AS sentimentType " +
            "FROM assets_table as L " +
            "WHERE EXISTS " +
            "(SELECT * FROM user_sentiments_table AS R " +
                "WHERE L.asset_id = R.asset_id AND account_name = :accountName " +
                "AND asset_type = :assetType AND sentiment_type = :sentimentType)")
    protected abstract LiveData<List<AssetSentiment>> getAssetsWithSentiment(AssetType assetType,
                                                 String accountName, SentimentType sentimentType);

    /**
     * Returns a LiveData list of assets that have not been reacted to by the given account name and
     * therefore have no matching user sentiment record.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking for sentiments.
     * @return A LiveData list of AssetSentiments that have not been reacted to by the given account.
     */
    @Query("SELECT *, NULL AS sentimentType " +
            "FROM assets_table AS L " +
            "WHERE L.asset_type = :assetType AND NOT EXISTS " +
            "(SELECT * FROM user_sentiments_table AS R " +
                "WHERE L.asset_id = R.asset_id AND account_name = :accountName " +
                "AND L.asset_type = R.asset_type)")
    protected abstract LiveData<List<AssetSentiment>> getAssetsWithoutSentiment(AssetType assetType,
                                                                                String accountName);

    /**
     * Combines a list of AssetSentiments with a LiveData list of AssetSentiments. The result is
     * stored in the given MediatorLiveData object. This function is used as the onChanged callback
     * for the mediator, in order to combine two LiveData lists.
     * @param mediator The MediatorLiveData where the merged list will be stored.
     * @param values A list of AssetSentiments.
     * @param other A LiveData object containing a list of AssetSentiments.
     */
    private static void combineLiveDataLists(MediatorLiveData<List<AssetSentiment>> mediator,
                            List<AssetSentiment> values, LiveData<List<AssetSentiment>> other) {
        ArrayList<AssetSentiment> result = new ArrayList<>(values);
        if (other.getValue() != null) {
            result.addAll(other.getValue());
        }
        mediator.postValue(result);
    }

    /**
     * Inserts or replaces the user sentiment specified by the asset and account with the given
     * sentiment type.
     * @param accountName The account that the user sentiment is associated with.
     * @param assetId The id of the asset that the user sentiment is associated with.
     * @param assetType The type of the asset that the user sentiment is associated with.
     * @param sentimentType The type of the new user sentiment.
     * @param isPending Whether the user sentiment needs to be synced with the server or not.
     * @param timestamp The timestamp of the user sentiment.
     */
    @Query("INSERT OR REPLACE INTO user_sentiments_table " +
            "(asset_id, account_name, asset_type, sentiment_type, is_pending, timestamp) " +
            "VALUES (:assetId, :accountName, :assetType, :sentimentType, :isPending, :timestamp)")
    public abstract void updateSentiment(String accountName, String assetId, AssetType assetType,
                                 SentimentType sentimentType, boolean isPending, Instant timestamp);

    /**
     * Inserts or replaces the given list of UserSentiments.
     * @param sentiments The UserSentiments to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateSentiments(List<UserSentiment> sentiments);

    /**
     * Deletes all user sentiments corresponding to the given account name.
     * @param accountName The account to delete all sentiments for.
     */
    @Query("DELETE FROM user_sentiments_table WHERE account_name = :accountName")
    public abstract void deleteAllSentiments(String accountName);

    /**
     * Returns a list of all UserSentiments that have isPending set to true.
     */
    @Query("SELECT * FROM user_sentiments_table WHERE is_pending = 1")
    public abstract List<UserSentiment> getPendingSentiments();
}
