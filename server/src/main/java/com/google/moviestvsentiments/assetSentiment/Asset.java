package com.google.moviestvsentiments.assetSentiment;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A record in the assets database table.
 */
@Entity
@IdClass(Asset.AssetCompositeKey.class)
public class Asset {

    // This class is necessary for Spring JPA to support a composite primary key on the Asset table.
    static class AssetCompositeKey implements Serializable {

        private String assetId;
        private AssetType assetType;

        public AssetCompositeKey() {}

        public AssetCompositeKey(String assetId, AssetType assetType) {
            this.assetId = assetId;
            this.assetType = assetType;
        }

        /**
         * Returns the asset id.
         */
        public String getAssetId() {
            return assetId;
        }

        /**
         * Returns the type of the asset.
         */
        public AssetType getAssetType() {
            return assetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssetCompositeKey that = (AssetCompositeKey) o;
            return assetId.equals(that.assetId) &&
                    assetType == that.assetType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(assetId, assetType);
        }
    }

    @Id
    private String assetId;

    @Id
    private AssetType assetType;

    private String title;
    private String poster;
    private String banner;
    private String imdbRating;
    private String rottenTomatoesRating;
    @Lob private String plot;
    private String runtime;
    private String year;
    private Instant timestamp;

    /**
     * Returns the asset id.
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
     * Returns the asset's type.
     */
    public AssetType getAssetType() {
        return assetType;
    }

    /**
     * Sets the asset's type.
     * @param assetType The new type of the asset.
     */
    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    /**
     * Returns the asset's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the asset's title.
     * @param title The new title of the asset.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the asset's poster image URL.
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Sets the asset's poster image URL.
     * @param poster The URL of the new poster image.
     */
    public void setPoster(String poster) {
        this.poster = poster;
    }

    /**
     * Returns the asset's banner image URL.
     */
    public String getBanner() {
        return banner;
    }

    /**
     * Sets the asset's banner image URL.
     * @param banner The URL of the new banner image.
     */
    public void setBanner(String banner) {
        this.banner = banner;
    }

    /**
     * Returns the asset's rating on imdb.
     */
    public String getImdbRating() {
        return imdbRating;
    }

    /**
     * Sets the asset's imdb rating.
     * @param imdbRating The new imdb rating of the asset.
     */
    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    /**
     * Returns the asset's rating on rotten tomatoes.
     */
    public String getRottenTomatoesRating() {
        return rottenTomatoesRating;
    }

    /**
     * Sets the asset's rotten tomatoes rating.
     * @param rottenTomatoesRating The new rotten tomatoes rating of the asset.
     */
    public void setRottenTomatoesRating(String rottenTomatoesRating) {
        this.rottenTomatoesRating = rottenTomatoesRating;
    }

    /**
     * Returns the description of the plot of the asset.
     */
    public String getPlot() {
        return plot;
    }

    /**
     * Sets the description of the plot of the asset.
     * @param plot The new plot description for the asset.
     */
    public void setPlot(String plot) {
        this.plot = plot;
    }

    /**
     * Returns the runtime of the asset.
     */
    public String getRuntime() {
        return runtime;
    }

    /**
     * Sets the runtime of the asset.
     * @param runtime The new runtime of the asset.
     */
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    /**
     * Returns the year that the asset was released.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the year that the asset was released.
     * @param year The new release year for the asset.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Returns the timestamp of when the asset was last updated.
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of when the asset was last updated.
     * @param timestamp The new timestamp of the asset.
     */
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
