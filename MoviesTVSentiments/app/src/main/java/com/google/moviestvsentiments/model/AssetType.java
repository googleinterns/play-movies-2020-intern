package com.google.moviestvsentiments.model;

import androidx.room.TypeConverter;

/**
 * A type of an asset object.
 */
public enum AssetType {
    MOVIE(0),
    SHOW(1);

    private final int type;

    private AssetType(int type) {
        this.type = type;
    }

    /**
     * Converts an AssetType into an int.
     * @param assetType The AssetType to convert.
     * @return The int value associated with the AssetType.
     */
    @TypeConverter
    public static int assetTypeToInt(AssetType assetType) {
        return assetType.type;
    }

    /**
     * Converts an int into an AssetType.
     * @param type The int to convert.
     * @return The AssetType associated with the int value.
     */
    @TypeConverter
    public static AssetType intToAssetType(int type) {
        return AssetType.values()[type];
    }
}
