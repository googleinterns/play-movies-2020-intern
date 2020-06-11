package com.google.moviestvsentiments.model;

import androidx.room.Embedded;

import com.google.auto.value.AutoValue;

/**
 * A container that combines an asset with a sentiment type.
 */
@AutoValue
public abstract class AssetSentiment {
    @AutoValue.CopyAnnotations
    @Embedded
    public abstract Asset asset();

    public abstract SentimentType sentimentType();

    /**
     * Creates a new AssetSentiment object with the given fields. Room needs this factory method to
     * create AssetSentiment objects.
     * @return A new AssetSentiment object with the specified fields.
     */
    public static AssetSentiment create(Asset asset, SentimentType sentimentType) {
        return new AutoValue_AssetSentiment(asset, sentimentType);
    }
}
