package com.google.moviestvsentiments.model;

import androidx.room.Embedded;

/**
 * A container that combines an asset with a sentiment type.
 */
public class AssetSentiment {
    @Embedded public Asset asset;
    public SentimentType sentimentType;
}
