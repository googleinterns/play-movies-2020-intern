package com.google.moviestvsentiments.assetSentiment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc
public class AssetSentimentControllerTest {

    private static final String ACCOUNT_NAME = "testAccount";
    private static final Asset ASSET = Asset.create("assetId", AssetType.MOVIE, "assetTitle");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetSentimentRepository assetSentimentRepository;

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
                SentimentType.THUMBS_UP);
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
                SentimentType.UNSPECIFIED);
        Asset asset2 = Asset.create("assetId2", AssetType.MOVIE, "assetTitle2");
        when(assetSentimentRepository.getAssetsWithSentiment(AssetType.MOVIE, ACCOUNT_NAME, SentimentType.UNSPECIFIED))
                .thenReturn(Arrays.asList(new AssetSentiment(ASSET, sentiment)));
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
}
