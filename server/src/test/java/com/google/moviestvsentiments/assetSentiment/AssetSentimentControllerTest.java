package com.google.moviestvsentiments.assetSentiment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.moviestvsentiments.AssetUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class AssetSentimentControllerTest {

    private static final String ACCOUNT_NAME = "testAccount";
    private static final Asset ASSET = AssetUtil.createAsset("assetId", AssetType.MOVIE, "assetTitle");
    private static final UserSentiment SENTIMENT_1 = UserSentiment.create(ACCOUNT_NAME, "assetId", AssetType.MOVIE,
            SentimentType.THUMBS_UP, Instant.MAX);
    private static final UserSentiment SENTIMENT_2 = UserSentiment.create(ACCOUNT_NAME,"assetId2", AssetType.SHOW,
            SentimentType.THUMBS_DOWN, Instant.EPOCH);
    private static final String SENTIMENT_LIST_JSON = "[ { \"assetId\": \"assetId\", \"assetType\": \"MOVIE\", " +
            "\"accountName\": \"testAccount\", \"sentimentType\": \"THUMBS_UP\", \"timestamp\": \""
            + SENTIMENT_1.getTimestamp() + "\"}, {\"assetId\": \"assetId2\", \"assetType\": \"SHOW\", \"accountName\": " +
            "\"testAccount\", \"sentimentType\": \"THUMBS_DOWN\", \"timestamp\": \"" + SENTIMENT_2.getTimestamp() + "\"}]";
    private static final String UPDATE_SENTIMENT_URL = "/sentiment?accountName=" + ACCOUNT_NAME + "&assetId=assetId&" +
            "assetType=MOVIE&sentimentType=THUMBS_UP&timestamp=" + Instant.MAX;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetSentimentRepository assetSentimentRepository;

    @MockBean
    private UserSentimentRepository userSentimentRepository;

    @Test
    public void getAssets_withReaction_invokesRepositoryOnce() throws Exception {
        mockMvc.perform(get("/assets?assetType=MOVIE&sentimentType=THUMBS_UP&accountName=" + ACCOUNT_NAME));

        verify(assetSentimentRepository, times(1)).getAssetsWithSentiment(AssetType.MOVIE,
                ACCOUNT_NAME, SentimentType.THUMBS_UP);
        verifyNoMoreInteractions(assetSentimentRepository);
    }

    @Test
    public void getAssets_withReaction_returnsAssets() throws Exception {
        UserSentiment sentiment = UserSentiment.create(ASSET.getAssetId(), ACCOUNT_NAME, ASSET.getAssetType(),
                SentimentType.THUMBS_UP, Instant.EPOCH);
        when(assetSentimentRepository.getAssetsWithSentiment(AssetType.MOVIE, ACCOUNT_NAME, SentimentType.THUMBS_UP))
                .thenReturn(Arrays.asList(new AssetSentiment(ASSET, sentiment)));

        mockMvc.perform(get("/assets?assetType=MOVIE&sentimentType=THUMBS_UP&accountName=" + ACCOUNT_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].asset.assetId", equalTo(ASSET.getAssetId())))
                .andExpect(jsonPath("$[0].asset.assetType", equalTo("MOVIE")))
                .andExpect(jsonPath("$[0].asset.title", equalTo(ASSET.getTitle())));
    }

    @Test
    public void getAssets_withUnspecified_invokesRepositoryTwice() throws Exception {
        mockMvc.perform(get("/assets?assetType=MOVIE&sentimentType=UNSPECIFIED&accountName=" + ACCOUNT_NAME));

        verify(assetSentimentRepository, times(1)).getAssetsWithSentiment(AssetType.MOVIE,
                ACCOUNT_NAME, SentimentType.UNSPECIFIED);
        verify(assetSentimentRepository, times(1)).getAssetsWithoutSentiment(AssetType.MOVIE,
                ACCOUNT_NAME);
        verifyNoMoreInteractions(assetSentimentRepository);
    }

    @Test
    public void getAssets_withUnspecified_returnsAssets() throws Exception {
        UserSentiment sentiment = UserSentiment.create(ASSET.getAssetId(), ACCOUNT_NAME, ASSET.getAssetType(),
                SentimentType.UNSPECIFIED, Instant.EPOCH);
        Asset asset2 = AssetUtil.createAsset("assetId2", AssetType.MOVIE, "assetTitle2");
        when(assetSentimentRepository.getAssetsWithSentiment(AssetType.MOVIE, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(new ArrayList<>(Arrays.asList(new AssetSentiment(ASSET, sentiment))));
        when(assetSentimentRepository.getAssetsWithoutSentiment(AssetType.MOVIE, ACCOUNT_NAME))
                .thenReturn(Arrays.asList(new AssetSentiment(asset2, null)));

        mockMvc.perform(get("/assets?assetType=MOVIE&sentimentType=UNSPECIFIED&accountName=" + ACCOUNT_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].asset.assetId", equalTo(ASSET.getAssetId())))
                .andExpect(jsonPath("$[0].asset.assetType", equalTo("MOVIE")))
                .andExpect(jsonPath("$[0].asset.title", equalTo(ASSET.getTitle())))
                .andExpect(jsonPath("$[1].asset.assetId", equalTo(asset2.getAssetId())))
                .andExpect(jsonPath("$[1].asset.assetType", equalTo("MOVIE")))
                .andExpect(jsonPath("$[1].asset.title", equalTo(asset2.getTitle())));
    }

    @Test
    public void updateSentiment_invokesRepository() throws Exception {
        mockMvc.perform(put(UPDATE_SENTIMENT_URL));

        verify(userSentimentRepository).save(SENTIMENT_1);
    }

    @Test
    public void updateSentiment_successful_returnsOk() throws Exception {
        mockMvc.perform(put(UPDATE_SENTIMENT_URL))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSentiment_successful_returnsSentiment() throws Exception {
        when(userSentimentRepository.save(SENTIMENT_1)).thenReturn(SENTIMENT_1);

        mockMvc.perform(put(UPDATE_SENTIMENT_URL))
                .andExpect(jsonPath("$.accountName", equalTo(ACCOUNT_NAME)))
                .andExpect(jsonPath("$.assetId", equalTo(SENTIMENT_1.getAssetId())))
                .andExpect(jsonPath("$.assetType", equalTo(SENTIMENT_1.getAssetType().toString())))
                .andExpect(jsonPath("$.sentimentType", equalTo(SENTIMENT_1.getSentimentType().toString())))
                .andExpect(jsonPath("$.timestamp", equalTo(SENTIMENT_1.getTimestamp().toString())));
    }

    @Test
    public void updateSentiment_jpaException_returnsBadRequest() throws Exception {
        when(userSentimentRepository.save(SENTIMENT_1)).thenThrow(JpaSystemException.class);

        mockMvc.perform(put(UPDATE_SENTIMENT_URL))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSentiment_otherException_returnsServerError() throws Exception {
        when(userSentimentRepository.save(SENTIMENT_1)).thenThrow(RuntimeException.class);

        mockMvc.perform(put(UPDATE_SENTIMENT_URL))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void updateSentiment_failure_returnsError() throws Exception {
        final String errorMessage = "Invalid account name";
        when(userSentimentRepository.save(SENTIMENT_1)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(put(UPDATE_SENTIMENT_URL))
                .andExpect(jsonPath("$", equalTo(errorMessage)));
    }

    @Test
    public void updateSentiments_invokesRepository() throws Exception {
        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON));

        verify(userSentimentRepository).saveAll(Arrays.asList(SENTIMENT_1, SENTIMENT_2));
    }

    @Test
    public void updateSentiments_successful_returnsOk() throws Exception {
        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateSentiments_successful_returnsSentiments() throws Exception {
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenReturn(Arrays.asList(SENTIMENT_1, SENTIMENT_2));

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(jsonPath("$.sentiments[0].sentimentType", equalTo("THUMBS_UP")))
                .andExpect(jsonPath("$.sentiments[1].sentimentType", equalTo("THUMBS_DOWN")));
    }

    @Test
    public void updateSentiments_successful_returnsNullError() throws Exception {
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenReturn(Arrays.asList(SENTIMENT_1, SENTIMENT_2));

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(jsonPath("$.error", equalTo(null)));
    }

    @Test
    public void updateSentiments_jpaException_returnsBadRequest() throws Exception {
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenThrow(JpaSystemException.class);

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSentiments_otherException_returnsServerError() throws Exception {
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void updateSentiments_failure_returnsNullList() throws Exception {
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenThrow(JpaSystemException.class);

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(jsonPath("$.sentiments", equalTo(null)));
    }

    @Test
    public void updateSentiments_failure_returnsError() throws Exception {
        final String errorMessage = "Error message";
        when(userSentimentRepository.saveAll(any(Iterable.class))).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(put("/sentiments").contentType(MediaType.APPLICATION_JSON).content(SENTIMENT_LIST_JSON))
                .andExpect(jsonPath("$.error", equalTo(errorMessage)));
    }
}
