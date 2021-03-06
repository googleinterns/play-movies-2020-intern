package com.google.moviestvsentiments.util;

import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;
import java.time.Instant;

/**
 * AssetUtil provides helper functions for working with Asset objects.
 */
public class AssetUtil {

    /**
     * Creates an asset with the given type and id.
     * @param assetId The id to create the asset with.
     * @param assetType The type of the asset to create.
     * @return An asset with the given type and id.
     */
    public static Asset createAsset(String assetId, AssetType assetType) {
        if (assetType == AssetType.MOVIE) {
            return createMovieAsset(assetId);
        }
        return createShowAsset(assetId);
    }

    /**
     * Creates an asset of type MOVIE with the given id.
     * @param assetId The id to create the asset with.
     * @return A movie asset with the given id.
     */
    public static Asset createMovieAsset(String assetId) {
        return defaultMovieBuilder(assetId).build();
    }

    /**
     * Creates an asset of type SHOW with the given id.
     * @param assetId The id to create the asset with.
     * @return A show asset with the given id.
     */
    public static Asset createShowAsset(String assetId) {
        return Asset.builder().setId(assetId).setType(AssetType.SHOW).setTitle("assetTitle")
                .setPoster("posterURL").setBanner("bannerURL").setYear("year")
                .setPlot("plotDescription").setRuntime("runtime").setTimestamp(Instant.EPOCH)
                .build();
    }

    /**
     * Returns the default Asset Builder object for a movie asset with the given asset id.
     * @param assetId The id of the asset to create.
     * @return The default Asset Builder object for a movie asset with the given asset id.
     */
    public static Asset.Builder defaultMovieBuilder(String assetId) {
        return Asset.builder().setId(assetId).setType(AssetType.MOVIE).setTitle("assetTitle")
                .setPoster("posterURL").setBanner("bannerURL").setPlot("plotDescription")
                .setRuntime("runtime").setYear("year").setTimestamp(Instant.EPOCH);
    }
}
