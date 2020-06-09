package com.google.moviestvsentiments.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * A record in the assets database table.
 */
@Entity(tableName = "assets_table", primaryKeys = {"asset_id", "asset_type"})
public class Asset {

    @NonNull
    @ColumnInfo(name = "asset_id")
    public String id;

    @NonNull
    @ColumnInfo(name = "asset_type")
    public AssetType type;

    @NonNull
    @ColumnInfo(name = "title")
    public String title;

    @NonNull
    @ColumnInfo(name = "poster")
    public String poster;

    @NonNull
    @ColumnInfo(name = "banner")
    public String banner;

    @NonNull
    @ColumnInfo(name = "plot")
    public String plot;

    @NonNull
    @ColumnInfo(name = "runtime")
    public String runtime;

    @NonNull
    @ColumnInfo(name = "year")
    public String year;

    @NonNull
    @ColumnInfo(name = "timestamp", defaultValue = "CURRENT_TIMESTAMP")
    public long timestamp;

    @ColumnInfo(name = "imdb_rating")
    public String imdbRating;

    @ColumnInfo(name = "rt_rating")
    public String rottenTomatoesRating;
    
    public Asset(String id, AssetType type, String title, String poster, String banner, String plot,
                 String runtime, String year, long timestamp, String imdbRating,
                 String rottenTomatoesRating) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.poster = poster;
        this.banner = banner;
        this.plot = plot;
        this.runtime = runtime;
        this.year = year;
        this.timestamp = timestamp;
        this.imdbRating = imdbRating;
        this.rottenTomatoesRating = rottenTomatoesRating;
    }
}
