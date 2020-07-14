package com.google.moviestvsentiments.service.assetSentiment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.web.Resource;
import java.util.List;
import javax.inject.Inject;

/**
 * A ViewModel that handles fetching and updating assets and user sentiments.
 */
public class AssetSentimentViewModel extends ViewModel {

    private final AssetSentimentRepository repository;

    @Inject
    AssetSentimentViewModel(AssetSentimentRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a new AssetSentimentViewModel.
     * @param repository The repository object to use when accessing assets and user sentiments.
     * @return A new AssetSentimentViewModel object.
     */
    public static AssetSentimentViewModel create(AssetSentimentRepository repository) {
        return new AssetSentimentViewModel(repository);
    }

    /**
     * Adds the given asset into the assets table. If the asset already exists, it is ignored.
     * @param asset The asset to insert.
     */
    public void addAsset(Asset asset) {
        repository.addAsset(asset);
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
    public LiveData<AssetSentiment> getAsset(String accountName, String assetId,
                                             AssetType assetType) {
        return repository.getAsset(accountName, assetId, assetType);
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
    public LiveData<Resource<List<AssetSentiment>>> getAssets(AssetType assetType,
                                                  String accountName, SentimentType sentimentType) {
        return repository.getAssets(assetType, accountName, sentimentType);
    }

    /**
     * Inserts or replaces the user sentiment specified by the asset and account with the given
     * sentiment type.
     * @param accountName The account that the user sentiment is associated with.
     * @param assetId The id of the asset that the user sentiment is associated with.
     * @param assetType The type of the asset that the user sentiment is associated with.
     * @param sentimentType The type of the new user sentiment.
     */
    public void updateSentiment(String accountName, String assetId, AssetType assetType,
                                SentimentType sentimentType) {
        repository.updateSentiment(accountName, assetId, assetType, sentimentType);
    }

    /**
     * Deletes all user sentiments corresponding to the given account name.
     * @param accountName The account to delete all sentiments for.
     */
    public void deleteAllSentiments(String accountName) {
        repository.deleteAllSentiments(accountName);
    }
}
