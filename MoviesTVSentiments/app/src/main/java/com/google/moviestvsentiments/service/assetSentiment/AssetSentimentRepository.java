package com.google.moviestvsentiments.service.assetSentiment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.model.UserSentiment;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.NetworkBoundResource;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.service.web.WebService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/**
 * A repository that handles fetching and updating assets and their user sentiments.
 */
public class AssetSentimentRepository {

    private final AssetSentimentDao assetSentimentDao;
    private final Executor executor;
    private final WebService webService;
    private final Clock clock;

    @Inject
    AssetSentimentRepository(AssetSentimentDao assetSentimentDao, Executor executor,
                             WebService webService, Clock clock) {
        this.assetSentimentDao = assetSentimentDao;
        this.executor = executor;
        this.webService = webService;
        this.clock = clock;
    }

    /**
     * Returns a new AssetSentimentRepository.
     * @param assetSentimentDao The Dao object to use when accessing the local database.
     * @param executor The Executor to use when writing to the database.
     * @param webService The WebService to use when fetching from the server.
     * @param clock The clock to use when generating UserSentiment timestamps.
     * @return A new AssetSentimentRepository object.
     */
    public static AssetSentimentRepository create(AssetSentimentDao assetSentimentDao,
                                          Executor executor, WebService webService, Clock clock) {
        return new AssetSentimentRepository(assetSentimentDao, executor, webService, clock);
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
     * with no corresponding user sentiment record will also be returned. The AssetSentiment list
     * is pulled from the server and cached locally in the Room database.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A LiveData list of AssetSentiments with reactions matching the given account
     * name and sentiment type.
     */
    LiveData<Resource<List<AssetSentiment>>> getAssets(AssetType assetType, String accountName,
                                                       SentimentType sentimentType) {
        return new NetworkBoundResource<List<AssetSentiment>, List<AssetSentiment>>() {
            @Override
            protected void saveCallResult(List<AssetSentiment> assetSentiments) {
                executor.execute(() -> {
                    for (AssetSentiment assetSentiment : assetSentiments) {
                        Asset asset = assetSentiment.asset();
                        assetSentimentDao.addAsset(asset);
                        assetSentimentDao.updateSentiment(accountName, asset.id(), assetType,
                                assetSentiment.sentimentType(), false, Instant.now(clock));
                    }
                });
            }

            @Override
            protected boolean shouldFetch(List<AssetSentiment> data) {
                return true;
            }

            @Override
            protected LiveData<List<AssetSentiment>> loadFromRoom() {
                return assetSentimentDao.getAssets(assetType, accountName, sentimentType);
            }

            @Override
            protected LiveData<ApiResponse<List<AssetSentiment>>> performNetworkCall() {
                return webService.getAssets(assetType, accountName, sentimentType);
            }
        }.getResult();
    }

    /**
     * Inserts or replaces the user sentiment specified by the asset and account with the given
     * sentiment type. The user sentiment is first sent to the server. If the server saves the
     * sentiment successfully, then the sentiment will be saved locally with isPending set to false.
     * Otherwise, the sentiment will be saved locally with isPending set to true.
     * @param accountName The account that the user sentiment is associated with.
     * @param assetId The id of the asset that the user sentiment is associated with.
     * @param assetType The type of the asset that the user sentiment is associated with.
     * @param sentimentType The type of the new user sentiment.
     */
    void updateSentiment(String accountName, String assetId, AssetType assetType,
                         SentimentType sentimentType) {
        Instant timestamp = Instant.now(clock);
        LiveData<ApiResponse<UserSentiment>> apiCall = webService.updateSentiment(accountName,
                assetId, assetType, sentimentType, timestamp);
        apiCall.observeForever(new Observer<ApiResponse<UserSentiment>>() {
            @Override
            public void onChanged(ApiResponse<UserSentiment> response) {
                apiCall.removeObserver(this);
                boolean isPending = !response.isSuccessful();
                executor.execute(() -> {
                    assetSentimentDao.updateSentiment(accountName, assetId, assetType,
                            sentimentType, isPending, timestamp);
                });
            }
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

    /**
     * Sends the list of pending UserSentiments to the server. If the server successfully saves them
     * in its database, then the UserSentiments are updated locally to no longer be pending.
     */
    public void syncPendingSentiments() {
        executor.execute(() -> {
            List<UserSentiment> pendingSentiments = assetSentimentDao.getPendingSentiments();
            if (pendingSentiments.isEmpty()) {
                return;
            }

            ApiResponse<List<UserSentiment>> response = webService.syncPendingSentiments(pendingSentiments);
            if (response.isSuccessful()) {
                assetSentimentDao.updateSentiments(response.getBody());
            }
        });
    }
}
