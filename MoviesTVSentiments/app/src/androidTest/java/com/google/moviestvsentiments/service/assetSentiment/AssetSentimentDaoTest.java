package com.google.moviestvsentiments.service.assetSentiment;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import com.google.moviestvsentiments.service.liveData.LiveDataTestUtil;

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
        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "incorrectId", AssetType.MOVIE));

        assertThat(result).isNull();
    }

    @Test
    public void addAndGetAsset_withIncorrectType_returnsNull() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "assetId", AssetType.SHOW));

        assertThat(result).isNull();
    }

    @Test
    public void addAndGetAsset_returnsAsset() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "assetId", AssetType.MOVIE));

        assertThat(result.asset()).isEqualTo(MOVIE_ASSET);
    }

    @Test
    public void addAndGetAsset_withNoSentiment_returnsUnspecified() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "assetId", AssetType.MOVIE));

        assertThat(result.sentimentType()).isEqualTo(SentimentType.UNSPECIFIED);
    }

    @Test
    public void addAndGetAsset_withSentiment_returnsAssetSentiment() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);

        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "assetId", AssetType.MOVIE));

        assertThat(result).isEqualTo(AssetSentiment.create(MOVIE_ASSET, SentimentType.THUMBS_UP));
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectType_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.SHOW, "accountName", SentimentType.UNSPECIFIED));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectName_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "incorrectName", SentimentType.THUMBS_UP));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectSentiment_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_DOWN));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_multipleAssets_returnsAllAssetSentiments() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.updateSentiment("accountName", "assetId2",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_UP));

        assertThat(results).containsExactly(
                AssetSentiment.create(MOVIE_ASSET, SentimentType.THUMBS_UP),
                AssetSentiment.create(MOVIE_ASSET_2, SentimentType.THUMBS_UP));
    }

    @Test
    public void updateSentimentAndGetAssets_unspecifiedSentiment_returnsAllAssetSentiments() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.UNSPECIFIED));

        assertThat(results).containsExactly(
                AssetSentiment.create(MOVIE_ASSET, SentimentType.UNSPECIFIED),
                AssetSentiment.create(MOVIE_ASSET_2, SentimentType.UNSPECIFIED));
    }

    @Test
    public void deleteAllSentimentsAndGetAssets_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.deleteAllSentiments("accountName");
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_UP));

        assertThat(results).isEmpty();
    }

    @Test
    public void deleteAllSentiments_shouldNotAffectOtherAccounts() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        assetSentimentDao.deleteAllSentiments("otherName");
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_UP));

        assertThat(results).containsExactly(AssetSentiment.create(MOVIE_ASSET, SentimentType.THUMBS_UP));
    }
}
