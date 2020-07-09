package com.google.moviestvsentiments;

import com.google.moviestvsentiments.assetSentiment.Asset;
import com.google.moviestvsentiments.assetSentiment.AssetType;

/**
 * Provides helper functions for working with Assets in tests.
 */
public class AssetUtil {

    /**
     * Creates a new Asset with the provided id, type and title.
     * @param assetId The id of the new Asset.
     * @param assetType The type of the new Asset.
     * @param title The title of the new Asset.
     * @return A new Asset with the provided id, type and title.
     */
    public static Asset createAsset(String assetId, AssetType assetType, String title) {
        Asset asset = new Asset();
        asset.setAssetId(assetId);
        asset.setAssetType(assetType);
        asset.setTitle(title);
        return asset;
    }
}
