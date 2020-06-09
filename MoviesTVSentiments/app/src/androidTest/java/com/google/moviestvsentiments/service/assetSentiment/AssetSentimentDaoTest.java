package com.google.moviestvsentiments.service.assetSentiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AssetSentimentDaoTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AssetSentimentDao assetSentimentDao;
    private SentimentsDatabase database;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, SentimentsDatabase.class)
                .allowMainThreadQueries().build();
        assetSentimentDao = database.assetSentimentDao();
    }

    @After
    public void closeDatabase() {
        database.close();
    }

    @Test
    public void addAndGetAsset_withIncorrectId_returnsNull() {
        Asset asset = new Asset("assetId", AssetType.MOVIE, "assetTitle", "posterURL",
                "bannerURL", "plotDescription", "runtime", "year",
                1, "imdbRating", "rtRating");
        assetSentimentDao.addAsset(asset);
        asset = assetSentimentDao.getAsset("incorrectId", AssetType.MOVIE);
        assertNull(asset);
    }

    @Test
    public void addAndGetAsset_withIncorrectType_returnsNull() {
        Asset asset = new Asset("assetId", AssetType.MOVIE, "assetTitle", "posterURL",
                "bannerURL", "plotDescription", "runtime", "year",
                1,"imdbRating", "rtRating");
        assetSentimentDao.addAsset(asset);
        asset = assetSentimentDao.getAsset("assetId", AssetType.SHOW);
        assertNull(asset);
    }

    @Test
    public void addAndGetAsset_returnsAsset() {
        Asset asset = new Asset("assetId", AssetType.MOVIE, "assetTitle", "posterURL",
                "bannerURL", "plotDescription", "runtime", "year",
                1,"imdbRating", "rtRating");
        assetSentimentDao.addAsset(asset);
        Asset result = assetSentimentDao.getAsset("assetId", AssetType.MOVIE);
        assertEquals("assetTitle", result.title);
    }
}
