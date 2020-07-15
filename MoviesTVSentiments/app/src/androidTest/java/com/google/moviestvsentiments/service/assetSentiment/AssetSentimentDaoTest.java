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
import com.google.moviestvsentiments.model.UserSentiment;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import com.google.moviestvsentiments.util.AssetUtil;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class AssetSentimentDaoTest {

    private static final Asset MOVIE_ASSET = AssetUtil.createMovieAsset("assetId");
    private static final Asset MOVIE_ASSET_2 = AssetUtil.createMovieAsset("assetId2");

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
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);

        AssetSentiment result = LiveDataTestUtil.getValue(assetSentimentDao.getAsset("accountName",
                "assetId", AssetType.MOVIE));

        assertThat(result).isEqualTo(AssetSentiment.create(MOVIE_ASSET, SentimentType.THUMBS_UP));
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectType_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED, false, Instant.EPOCH);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.SHOW, "accountName", SentimentType.UNSPECIFIED));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectName_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "incorrectName", SentimentType.THUMBS_UP));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_withIncorrectSentiment_returnsEmptyList() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_DOWN));

        assertThat(results).isEmpty();
    }

    @Test
    public void updateSentimentAndGetAssets_multipleAssets_returnsAllAssetSentiments() {
        assetSentimentDao.addAsset(MOVIE_ASSET);
        assetSentimentDao.addAsset(MOVIE_ASSET_2);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
        assetSentimentDao.updateSentiment("accountName", "assetId2",
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
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
                AssetType.MOVIE, SentimentType.UNSPECIFIED, false, Instant.EPOCH);
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
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
        assetSentimentDao.deleteAllSentiments("accountName");
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_UP));

        assertThat(results).isEmpty();
    }

    @Test
    public void deleteAllSentiments_shouldNotAffectOtherAccounts() {
        assetSentimentDao.addAsset(MOVIE_ASSET);

        assetSentimentDao.updateSentiment("accountName", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP, false, Instant.EPOCH);
        assetSentimentDao.deleteAllSentiments("otherName");
        List<AssetSentiment> results = LiveDataTestUtil.getValue(assetSentimentDao.getAssets(
                AssetType.MOVIE, "accountName", SentimentType.THUMBS_UP));

        assertThat(results).containsExactly(AssetSentiment.create(MOVIE_ASSET, SentimentType.THUMBS_UP));
    }

    @Test
    public void getPendingSentiments_withNonePending_returnsEmptyList() {
        List<UserSentiment> sentiments = assetSentimentDao.getPendingSentiments();

        assertThat(sentiments).isEmpty();
    }

    @Test
    public void getPendingSentiments_withSomePending_returnsSentiments() {
        assetSentimentDao.updateSentiment("accountName", "assetId", AssetType.MOVIE,
                SentimentType.THUMBS_DOWN, true, Instant.EPOCH);
        assetSentimentDao.updateSentiment("accountName2", "assetId2", AssetType.SHOW,
                SentimentType.THUMBS_UP, true, Instant.EPOCH);

        List<UserSentiment> sentiments = assetSentimentDao.getPendingSentiments();

        assertThat(sentiments).hasSize(2);
    }

    @Test
    public void updateSentiments_andGetPending_returnsEmptyList() {
        assetSentimentDao.updateSentiment("accountName", "assetId", AssetType.MOVIE,
                SentimentType.THUMBS_DOWN, true, Instant.EPOCH);
        assetSentimentDao.updateSentiment("accountName2", "assetId2", AssetType.SHOW,
                SentimentType.THUMBS_UP, true, Instant.EPOCH);

        assetSentimentDao.updateSentiments(Arrays.asList(
                UserSentiment.create("assetId", "accountName", AssetType.MOVIE,
                        SentimentType.THUMBS_DOWN, Instant.EPOCH, false),
                UserSentiment.create("assetId2", "accountName2", AssetType.SHOW,
                        SentimentType.THUMBS_UP, Instant.EPOCH, false)
        ));
        List<UserSentiment> sentiments = assetSentimentDao.getPendingSentiments();

        assertThat(sentiments).isEmpty();
    }
}
