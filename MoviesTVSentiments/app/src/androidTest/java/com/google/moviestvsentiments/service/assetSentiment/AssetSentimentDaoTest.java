package com.google.moviestvsentiments.service.assetSentiment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.provider.Telephony;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AssetSentimentDaoTest {

    private static final Asset MOVIE_ASSET = createMovieAsset("assetId");
    private static final Asset MOVIE_ASSET_2 = createMovieAsset("assetId2");

    private static Asset createMovieAsset(String assetId) {
        return Asset.builder().setId(assetId).setType(AssetType.MOVIE).setTitle("assetTitle")
                .setPoster("posterURL").setBanner("bannerURL").setPlot("plotDescription")
                .setRuntime("runtime").setYear("year").setTimestamp(1).build();
    }

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
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = assetSentimentDao.getAsset("accountName", "incorrectId", AssetType.MOVIE);

        assertNull(result);
    }

    @Test
    public void addAndGetAsset_withIncorrectType_returnsNull() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = assetSentimentDao.getAsset("accountName", "assetId", AssetType.SHOW);

        assertNull(result);
    }

    @Test
    public void addAndGetAsset_returnsAsset() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = assetSentimentDao.getAsset("accountName", "assetId", AssetType.MOVIE);

        assertEquals("assetTitle", result.asset.title());
    }

    @Test
    public void addAndGetAsset_withNoSentiment_returnsUnspecified() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = assetSentimentDao.getAsset("accountName", "assetId", AssetType.MOVIE);

        assertEquals(SentimentType.UNSPECIFIED, result.sentimentType);
    }

    @Test
    public void addAndGetAsset_withSentiment_returnsSentiment() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);

        AssetSentiment result = assetSentimentDao.getAsset("accountName", "assetId", AssetType.MOVIE);

        assertEquals(SentimentType.THUMBS_UP, result.sentimentType);
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectType_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.SHOW, "accountName",
                SentimentType.UNSPECIFIED);

        assertEquals(0, results.size());
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectName_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "incorrectName",
                SentimentType.THUMBS_UP);

        assertEquals(0, results.size());
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectSentiment_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.THUMBS_DOWN);

        assertEquals(0, results.size());
    }

    @Test
    public void updateSentimentAndGetAssets_multipleAssets_returnsAllAssets() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.updateSentiment("accountName", "assetId2",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.THUMBS_UP);

        assertEquals(2, results.size());
        assertEquals("assetId", results.get(0).asset.id());
        assertEquals("assetId2", results.get(1).asset.id());
    }

    @Test
    public void updateSentimentAndGetAssets_returnsSentimentValue() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.THUMBS_DOWN);

        assertEquals(1, results.size());
        assertEquals(SentimentType.THUMBS_DOWN, results.get(0).sentimentType);
    }

    @Test
    public void updateSentimentAndGetAssets_unspecifiedSentiment_returnsAllAssets() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.UNSPECIFIED);

        assertEquals(2, results.size());
        assertEquals("assetId", results.get(0).asset.id());
        assertEquals("assetId2", results.get(1).asset.id());
    }

    @Test
    public void updateSentimentAndGetAssets_unspecifiedSentiment_returnsSentimentValue() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.UNSPECIFIED);

        assertEquals(2, results.size());
        assertEquals(SentimentType.UNSPECIFIED, results.get(0).sentimentType);
        assertEquals(SentimentType.UNSPECIFIED, results.get(1).sentimentType);
    }

    @Test
    public void deleteAllSentimentsAndGetAssets_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.deleteAllSentiments("accountName");
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.THUMBS_UP);

        assertEquals(0, results.size());
    }

    @Test
    public void deleteAllSentiments_shouldNotAffectOtherAccounts() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.deleteAllSentiments("otherName");
        List<AssetSentiment> results = assetSentimentDao.getAssets(AssetType.MOVIE, "accountName",
                SentimentType.THUMBS_UP);

        assertEquals(1, results.size());
        assertEquals("assetTitle", results.get(0).asset.title());
    }
}
