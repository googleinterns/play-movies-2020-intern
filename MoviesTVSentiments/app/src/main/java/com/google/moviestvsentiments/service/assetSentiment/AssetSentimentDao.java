package com.google.moviestvsentiments.service.assetSentiment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import java.util.List;

@Dao
public abstract class AssetSentimentDao {

    /**
     * Adds the given asset into the assets table. If the asset already exists, it is ignored.
     * @param asset The asset to insert.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void addAsset(Asset asset);

    /**
     * Returns the asset with the given id and type or null if it does not exist.
     * @param assetId The id of the asset.
     * @param assetType The type of the asset.
     * @return The asset matching the id and type.
     */
    @Query("SELECT * FROM assets_table WHERE asset_id = :assetId AND asset_type = :assetType")
    public abstract Asset getAsset(String assetId, AssetType assetType);
}
