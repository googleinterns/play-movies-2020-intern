package com.google.moviestvsentiments.assetSentiment;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A record in the user sentiments database table.
 */
@Entity
@IdClass(UserSentiment.UserSentimentCompositeKey.class)
public class UserSentiment {

    // This class is necessary for Spring JPA to support a composite primary key on the UserSentiment table.
    static class UserSentimentCompositeKey implements Serializable {
        private String assetId;
        private String accountName;
        private AssetType assetType;

        public UserSentimentCompositeKey() {}

        public UserSentimentCompositeKey(String assetId, String accountName, AssetType assetType) {
            this.assetId = assetId;
            this.accountName = accountName;
            this.assetType = assetType;
        }

        /**
         * Returns the id of the asset associated with this sentiment.
         */
        public String getAssetId() {
            return assetId;
        }

        /**
         * Returns the name of the account associated with this sentiment.
         */
        public String getAccountName() {
            return accountName;
        }

        /**
         * Returns the type of the asset associated with this sentiment.
         */
        public AssetType getAssetType() {
            return assetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserSentimentCompositeKey that = (UserSentimentCompositeKey) o;
            return assetId.equals(that.assetId) &&
                    accountName.equals(that.accountName) &&
                    assetType == that.assetType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(assetId, accountName, assetType);
        }
    }

    @Id private String assetId;
    @Id private String accountName;
    @Id private AssetType assetType;
    private SentimentType sentimentType;
    private Instant timestamp;

    /**
     * Creates a new UserSentiment with the provided fields.
     * @param accountName The name of the account associated with the UserSentiment.
     * @param assetId The id of the asset associated with the UserSentiment.
     * @param assetType The type of the asset associated with the UserSentiment.
     * @param sentimentType The type of the UserSentiment.
     * @param timestamp The timestamp of the UserSentiment.
     * @return A new UserSentiment with the provided fields.
     */
    public static UserSentiment create(String accountName, String assetId, AssetType assetType,
                                       SentimentType sentimentType, Instant timestamp) {
        UserSentiment userSentiment = new UserSentiment();
        userSentiment.accountName = accountName;
        userSentiment.assetId = assetId;
        userSentiment.assetType = assetType;
        userSentiment.sentimentType = sentimentType;
        userSentiment.timestamp = timestamp;
        return userSentiment;
    }

    /**
     * Returns the id of the asset associated with this sentiment.
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * Sets the asset id.
     * @param assetId The new asset id.
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    /**
     * Returns the account name associated with this sentiment.
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the account name associated with this sentiment.
     * @param accountName The new account name.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Returns the type of the asset associated with this sentiment.
     */
    public AssetType getAssetType() {
        return assetType;
    }

    /**
     * Sets the type of the asset associated with this sentiment.
     * @param assetType The new type of the asset.
     */
    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    /**
     * Returns the type of the sentiment.
     */
    public SentimentType getSentimentType() {
        return sentimentType;
    }

    /**
     * Sets the type of the sentiment.
     * @param sentimentType The new type of the sentiment.
     */
    public void setSentimentType(SentimentType sentimentType) {
        this.sentimentType = sentimentType;
    }

    /**
     * Returns the timestamp of when the sentiment was last updated.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of when the sentiment was last updated.
     * @param timestamp The new timestamp of the sentiment.
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSentiment sentiment = (UserSentiment) o;
        return assetId.equals(sentiment.assetId) &&
                accountName.equals(sentiment.accountName) &&
                assetType == sentiment.assetType &&
                sentimentType == sentiment.sentimentType &&
                Objects.equals(timestamp, sentiment.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetId, accountName, assetType, sentimentType, timestamp);
    }
}
