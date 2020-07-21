package com.google.moviestvsentiments.assetSentiment;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * A Repository that provides functions for accessing AssetSentiment objects.
 */
public interface AssetSentimentRepository extends PagingAndSortingRepository<Asset, Asset.AssetCompositeKey> {

    /**
     * Returns a list of Assets that have a null banner image URL.
     */
    @Query("SELECT asset FROM Asset asset WHERE asset.banner IS NULL")
    List<Asset> getAssetsWithoutBanner();

    /**
     * Returns a list of AssetSentiments with reactions that match the given account name and sentiment type.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking assets for sentiments.
     * @param sentimentType The sentiment type to check for.
     * @return A list of AssetSentiments with reactions matching the given asset type, account name and sentiment type.
     */
    @Query("SELECT new com.google.moviestvsentiments.assetSentiment.AssetSentiment(asset, sentiment) " +
            "FROM Asset asset " +
            "INNER JOIN UserSentiment sentiment " +
            "ON asset.assetId = sentiment.assetId AND asset.assetType = sentiment.assetType " +
            "WHERE asset.assetType = :assetType AND sentiment.accountName = :accountName " +
            "AND sentiment.sentimentType = :sentimentType")
    List<AssetSentiment> getAssetsWithSentiment(@Param("assetType") AssetType assetType,
                                                @Param("accountName") String accountName,
                                                @Param("sentimentType") SentimentType sentimentType);

    /**
     * Returns a list of assets that have not been reacted to by the given account name and therefore have no matching
     * user sentiment record. The returned AssetSentiment objects have a null UserSentiment field.
     * @param assetType The type of asset to include in the results.
     * @param accountName The account name to use when checking for sentiments.
     * @return A list of AssetSentiments that have not been reacted to by the given account.
     */
    @Query("SELECT new com.google.moviestvsentiments.assetSentiment.AssetSentiment(asset) " +
            "FROM Asset asset " +
            "WHERE asset.assetType = :assetType AND NOT EXISTS " +
            "(SELECT sentiment FROM UserSentiment sentiment " +
                "WHERE asset.assetId = sentiment.assetId AND sentiment.accountName = :accountName " +
                "AND asset.assetType = sentiment.assetType)")
    List<AssetSentiment> getAssetsWithoutSentiment(@Param("assetType") AssetType assetType,
                                                   @Param("accountName") String accountName);
}
