package com.google.moviestvsentiments.service.assetSentiment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.MainThreadDatabaseExecutor;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class AssetSentimentRepositoryTest {

    private static final Asset ASSET = Asset.builder().setId("assetId").setType(AssetType.SHOW)
        .setTitle("assetTitle").setPoster("posterURL").setBanner("bannerURL").setYear("year")
        .setPlot("plotDescription").setRuntime("runtime").setTimestamp(1).build();

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AssetSentimentDao dao;
    private AssetSentimentRepository repository;

    @Before
    public void setUp() {
        dao = mock(AssetSentimentDao.class);
        repository = AssetSentimentRepository.create(dao, new MainThreadDatabaseExecutor());
    }

    @Test
    public void addAsset_invokesDao() {
        repository.addAsset(ASSET);

        verify(dao).addAsset(ASSET);
    }

    @Test
    public void getAsset_returnsAsset() {
        MutableLiveData<AssetSentiment> assetData = new MutableLiveData<>();
        assetData.setValue(AssetSentiment.create(ASSET, SentimentType.THUMBS_DOWN));
        when(dao.getAsset("Account Name", "assetId", AssetType.SHOW))
                .thenReturn(assetData);

        AssetSentiment result = LiveDataTestUtil.getValue(repository.getAsset("Account Name",
                "assetId", AssetType.SHOW));

        assertThat(result).isEqualTo(AssetSentiment.create(ASSET, SentimentType.THUMBS_DOWN));
    }

    @Test
    public void getAssets_returnsAssets() {
        MutableLiveData<List<AssetSentiment>> assetData = new MutableLiveData<>();
        assetData.setValue(Arrays.asList(AssetSentiment.create(ASSET, SentimentType.UNSPECIFIED)));
        when(dao.getAssets(AssetType.SHOW, "Account Name", SentimentType.UNSPECIFIED))
                .thenReturn(assetData);

        List<AssetSentiment> result = LiveDataTestUtil.getValue(repository.getAssets(AssetType.SHOW,
                "Account Name", SentimentType.UNSPECIFIED));

        assertThat(result).containsExactly(AssetSentiment.create(ASSET, SentimentType.UNSPECIFIED));
    }

    @Test
    public void updateSentiment_invokesDao() {
        repository.updateSentiment("Account Name", "assetId", AssetType.SHOW,
                SentimentType.THUMBS_UP);

        verify(dao).updateSentiment("Account Name", "assetId", AssetType.SHOW,
                SentimentType.THUMBS_UP);
    }

    @Test
    public void deleteAllSentiments_invokesDao() {
        repository.deleteAllSentiments("Account Name");

        verify(dao).deleteAllSentiments("Account Name");
    }
}
