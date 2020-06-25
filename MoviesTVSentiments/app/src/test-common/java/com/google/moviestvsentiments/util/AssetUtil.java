package com.google.moviestvsentiments.util;

import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;

/**
 * AssetUtil provides helper functions for working with Asset objects.
 */
public class AssetUtil {

    /**
     * Creates an asset of type MOVIE with the given id.
     * @param assetId The id to create the asset with.
     * @return A movie asset with the given id.
     */
    public static Asset createMovieAsset(String assetId) {
        return Asset.builder().setId(assetId).setType(AssetType.MOVIE).setTitle("assetTitle")
                .setPoster("posterURL").setBanner("bannerURL").setPlot("plotDescription")
                .setRuntime("runtime").setYear("year").setTimestamp(1).build();
    }

    /**
     * Creates an asset of type SHOW with the given id.
     * @param assetId The id to create the asset with.
     * @return A show asset with the given id.
     */
    public static Asset createShowAsset(String assetId) {
        return Asset.builder().setId(assetId).setType(AssetType.SHOW).setTitle("assetTitle")
                .setPoster("posterURL").setBanner("bannerURL").setYear("year")
                .setPlot("plotDescription").setRuntime("runtime").setTimestamp(1).build();
    }
}
