package com.google.moviestvsentiments.service.assetSentiment;

import static com.google.common.truth.Truth.assertThat;
import static com.google.moviestvsentiments.util.UserSentimentListMatcher.containsUserSentiments;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.model.UserSentiment;
import com.google.moviestvsentiments.service.web.ApiResponse;
import com.google.moviestvsentiments.service.web.Resource;
import com.google.moviestvsentiments.service.web.WebService;
import com.google.moviestvsentiments.util.AssetUtil;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import com.google.moviestvsentiments.util.MainThreadDatabaseExecutor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class AssetSentimentRepositoryTest {

    private static final String ACCOUNT_NAME = "Account Name";
    private static final String ASSET_ID = "assetId";
    private static final Asset ASSET = AssetUtil.createShowAsset(ASSET_ID);

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private AssetSentimentDao dao;
    private AssetSentimentRepository repository;
    private WebService webService;

    @Before
    public void setUp() {
        dao = mock(AssetSentimentDao.class);
        webService = mock(WebService.class);
        Clock clock = Clock.fixed(Instant.EPOCH, ZoneId.systemDefault());
        repository = AssetSentimentRepository.create(dao, new MainThreadDatabaseExecutor(),
                webService, clock);
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
        when(dao.getAsset(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW)).thenReturn(assetData);

        AssetSentiment result = LiveDataTestUtil.getValue(repository.getAsset(ACCOUNT_NAME,
                ASSET_ID, AssetType.SHOW));

        assertThat(result).isEqualTo(AssetSentiment.create(ASSET, SentimentType.THUMBS_DOWN));
    }

    @Test
    public void getAssets_webServiceFailure_returnsLocalAssets() {
        MutableLiveData<List<AssetSentiment>> localData = new MutableLiveData<>(
                Arrays.asList(AssetSentiment.create(ASSET, SentimentType.UNSPECIFIED)));
        when(dao.getAssets(AssetType.SHOW, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(localData);
        MutableLiveData<ApiResponse<List<AssetSentiment>>> remoteData = new MutableLiveData<>(
                new ApiResponse(new RuntimeException("Network error")));
        when(webService.getAssets(AssetType.SHOW, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(remoteData);

        Resource<List<AssetSentiment>> result = LiveDataTestUtil.getValue(
                repository.getAssets(AssetType.SHOW, ACCOUNT_NAME, SentimentType.UNSPECIFIED));

        assertThat(result.getStatus()).isEqualTo(Resource.Status.ERROR);
        assertThat(result.getValue()).containsExactly(AssetSentiment.create(ASSET,
                SentimentType.UNSPECIFIED));
    }

    @Test
    public void getAssets_webServiceSuccess_addsRemoteAccountsToDatabase() {
        MutableLiveData<List<AssetSentiment>> localData = new MutableLiveData<>(
                Arrays.asList(AssetSentiment.create(ASSET, SentimentType.UNSPECIFIED)));
        when(dao.getAssets(AssetType.SHOW, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(localData);
        AssetSentiment remoteSentiment = AssetSentiment.create(AssetUtil.createShowAsset(
                "assetId2"), SentimentType.UNSPECIFIED);
        MutableLiveData<ApiResponse<List<AssetSentiment>>> remoteData = new MutableLiveData<>(
                new ApiResponse(Response.success(Arrays.asList(remoteSentiment))));
        when(webService.getAssets(AssetType.SHOW, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(remoteData);

        LiveDataTestUtil.getValue(repository.getAssets(AssetType.SHOW, ACCOUNT_NAME,
                SentimentType.UNSPECIFIED));

        verify(dao).addAssets(Arrays.asList(remoteSentiment.asset()));
        verify(dao).updateSentiments(argThat(containsUserSentiments(UserSentiment.create(
                remoteSentiment.asset().id(), ACCOUNT_NAME, AssetType.SHOW, SentimentType.UNSPECIFIED,
                Instant.EPOCH, false))));
    }

    @Test
    public void updateSentiment_webServiceFailure_setsIsPendingTrue() {
        MutableLiveData<ApiResponse<UserSentiment>> apiResponse = new MutableLiveData<>(
                new ApiResponse(new RuntimeException("Network error")));
        when(webService.updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW,
                SentimentType.THUMBS_UP, Instant.EPOCH)).thenReturn(apiResponse);

        repository.updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW, SentimentType.THUMBS_UP);

        verify(dao).updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW, SentimentType.THUMBS_UP,
                true, Instant.EPOCH);
    }

    @Test
    public void updateSentiment_webServiceSuccess_setsIsPendingFalse() {
        MutableLiveData<ApiResponse<UserSentiment>> apiResponse = new MutableLiveData<>(
                new ApiResponse(Response.success(new UserSentiment())));
        when(webService.updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW,
                SentimentType.THUMBS_UP, Instant.EPOCH)).thenReturn(apiResponse);

        repository.updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW, SentimentType.THUMBS_UP);

        verify(dao).updateSentiment(ACCOUNT_NAME, ASSET_ID, AssetType.SHOW, SentimentType.THUMBS_UP,
                false, Instant.EPOCH);
    }

    @Test
    public void deleteAllSentiments_invokesDao() {
        repository.deleteAllSentiments(ACCOUNT_NAME);

        verify(dao).deleteAllSentiments(ACCOUNT_NAME);
    }

    @Test
    public void syncPendingSentiments_nonePending_doesNotInvokeWebService() {
        when(dao.getPendingSentiments()).thenReturn(Arrays.asList());

        repository.syncPendingSentiments();

        verifyZeroInteractions(webService);
    }

    @Test
    public void syncPendingSentiments_failure_doesNotUpdateSentiments() {
        List<UserSentiment> sentimentList = Arrays.asList(UserSentiment.create(ASSET_ID,
                ACCOUNT_NAME, AssetType.SHOW, SentimentType.THUMBS_UP, Instant.MAX, true));
        when(dao.getPendingSentiments()).thenReturn(sentimentList);
        when(webService.syncPendingSentiments(sentimentList)).thenReturn(
                new ApiResponse(new RuntimeException("Network failure")));

        repository.syncPendingSentiments();

        verify(dao, times(0)).updateSentiments(anyList());
    }

    @Test
    public void syncPendingSentiments_success_updatesSentiments() {
        List<UserSentiment> sentimentList = Arrays.asList(UserSentiment.create(ASSET_ID,
                ACCOUNT_NAME, AssetType.SHOW, SentimentType.THUMBS_UP, Instant.MAX, true));
        when(dao.getPendingSentiments()).thenReturn(sentimentList);
        when(webService.syncPendingSentiments(sentimentList)).thenReturn(
                new ApiResponse(Response.success(sentimentList)));

        repository.syncPendingSentiments();

        verify(dao).updateSentiments(sentimentList);
    }
}
