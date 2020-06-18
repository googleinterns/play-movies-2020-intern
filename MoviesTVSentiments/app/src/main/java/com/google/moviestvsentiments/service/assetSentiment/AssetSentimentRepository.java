package com.google.moviestvsentiments.service.assetSentiment;

import androidx.lifecycle.LiveData;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.DatabaseExecutor;
import java.util.List;
import javax.inject.Inject;

/**
 * A repository that handles fetching and updating assets and their user sentiments.
 */
public class AssetSentimentRepository {

    private final AssetSentimentDao assetSentimentDao;
    private final DatabaseExecutor executor;

    @Inject
    AssetSentimentRepository(AssetSentimentDao assetSentimentDao, DatabaseExecutor executor) {
        this.assetSentimentDao = assetSentimentDao;
        this.executor = executor;
    }

    /**
     * Returns a new AssetSentimentRepository.
     * @param assetSentimentDao The Dao object to use when accessing the local database.
     * @param executor The DatabaseExecutor to use when writing to the database.
     * @return A new AssetSentimentRepository object.
     */
    public static AssetSentimentRepository create(AssetSentimentDao assetSentimentDao,
                                                  DatabaseExecutor executor) {
        return new AssetSentimentRepository(assetSentimentDao, executor);
    }

    /**
     * Adds the given asset into the assets table. If the asset already exists, it is ignored.
     * @param asset The asset to insert.
     */
    void addAsset(Asset asset) {
        executor.execute(() -> {
            assetSentimentDao.addAsset(asset);
        });
    }

    /**
     * Returns a LiveData object containing the asset with the given id and type or null if the
     * asset does not exist. The asset is combined with the matching user sentiment and returned as
     * an AssetSentiment object.
     * @param accountName The name of the account to match with the sentiment.
     * @param assetId The id of the asset.
     * @param assetType The type of the asset.
     * @return A LiveData object containing the AssetSentiment matching the account name, asset id
     * and asset type.
     */
    LiveData<AssetSentiment> getAsset(String accountName, String assetId, AssetType assetType) {
        return assetSentimentDao.getAsset(accountName, assetId, assetType);
    }

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
    LiveData<List<AssetSentiment>> getAssets(AssetType assetType, String accountName,
                                                    SentimentType sentimentType) {
        return assetSentimentDao.getAssets(assetType, accountName, sentimentType);
    }

    /**
     * Inserts or replaces the user sentiment specified by the asset and account with the given
     * sentiment type.
     * @param accountName The account that the user sentiment is associated with.
     * @param assetId The id of the asset that the user sentiment is associated with.
     * @param assetType The type of the asset that the user sentiment is associated with.
     * @param sentimentType The type of the new user sentiment.
     */
    void updateSentiment(String accountName, String assetId, AssetType assetType,
                         SentimentType sentimentType) {
        executor.execute(() -> {
            assetSentimentDao.updateSentiment(accountName, assetId, assetType, sentimentType);
        });
    }

    /**
     * Deletes all user sentiments corresponding to the given account name.
     * @param accountName The account to delete all sentiments for.
     */
    void deleteAllSentiments(String accountName) {
        executor.execute(() -> {
            assetSentimentDao.deleteAllSentiments(accountName);
        });
    }
}
