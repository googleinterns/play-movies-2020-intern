package com.google.moviestvsentiments.model;

import android.os.Parcelable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * A container that combines an asset with a sentiment type.
 */
@AutoValue
public abstract class AssetSentiment implements Parcelable {
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

    /**
     * Creates a new AssetSentiment object using the Asset and UserSentiment provided by the server.
     * Jackson needs this factory method to create AssetSentiment objects. Room should not use this
     * method, which is why it has the Ignore annotation. Note that the UserSentiment object sent
     * by the server can be null, in the case when fetching UNSPECIFIED sentiments and the account
     * has no user sentiment record yet.
     * @param asset The Asset that is being reacted to.
     * @param userSentiment The UserSentiment object associated with the Asset.
     * @return A new AssetSentiment object matching the Asset and UserSentiment.
     */
    @Ignore
    @JsonCreator
    public static AssetSentiment createFromServer(@JsonProperty("asset") Asset asset,
                                      @JsonProperty("userSentiment") UserSentiment userSentiment) {
        if (userSentiment == null) {
            return new AutoValue_AssetSentiment(asset, SentimentType.UNSPECIFIED);
        }
        return new AutoValue_AssetSentiment(asset, userSentiment.sentimentType);
    }
}
