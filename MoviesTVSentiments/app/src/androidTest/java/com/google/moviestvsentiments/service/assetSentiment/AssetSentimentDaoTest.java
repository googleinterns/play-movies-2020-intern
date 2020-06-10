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
    private Asset asset = Asset.builder().setId("assetId")
            .setType(AssetType.MOVIE).setTitle("assetTitle").setPoster("posterURL")
            .setBanner("bannerURL").setPlot("plotDescription").setRuntime("runtime").setYear("year")
            .setTimestamp(1).build();

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
        assetSentimentDao.addAsset(asset);
        Asset result = assetSentimentDao.getAsset("incorrectId", AssetType.MOVIE);

        assertNull(result);
    }

    @Test
    public void addAndGetAsset_withIncorrectType_returnsNull() {
        assetSentimentDao.addAsset(asset);
        Asset result = assetSentimentDao.getAsset("assetId", AssetType.SHOW);

        assertNull(result);
    }

    @Test
    public void addAndGetAsset_returnsAsset() {
        assetSentimentDao.addAsset(asset);
        Asset result = assetSentimentDao.getAsset("assetId", AssetType.MOVIE);

        assertEquals("assetTitle", result.title());
    }
}
