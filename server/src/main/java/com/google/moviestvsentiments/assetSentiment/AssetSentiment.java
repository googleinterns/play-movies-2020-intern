package com.google.moviestvsentiments.assetSentiment;

import javax.persistence.Embedded;

/**
 * A container that combines an Asset with a UserSentiment.
 */
public class AssetSentiment {

    @Embedded
    private Asset asset;

    @Embedded
    private UserSentiment userSentiment;

    // This constructor is required to support the Spring JPA queries in the AssetSentimentRepository.
    public AssetSentiment(Asset asset) {
        this(asset, null);
    }

    // This constructor is required to support the Spring JPA queries in the AssetSentimentRepository.
    public AssetSentiment(Asset asset, UserSentiment userSentiment) {
        this.asset = asset;
        this.userSentiment = userSentiment;
    }

    /**
     * Returns the Asset associated with this AssetSentiment.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Returns the UserSentiment associated with this AssetSentiment.
     */
    public UserSentiment getUserSentiment() {
        return userSentiment;
    }
}
