package com.google.moviestvsentiments.webscrape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.moviestvsentiments.AssetUtil;
import com.google.moviestvsentiments.assetSentiment.Asset;
import com.google.moviestvsentiments.assetSentiment.AssetSentimentRepository;
import com.google.moviestvsentiments.assetSentiment.AssetType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class AssetScrapeControllerTest {

    private static final String OMDB_URL = "testurl";
    private static final String API_KEY = "testApiKey";
    private static final String SCRAPE_ASSETS_TEST_URL = "/scrape?url=" + OMDB_URL + "&apiKey=" + API_KEY;
    private static final String SCRAPE_BANNERS_TEST_URL = "/banners?apiKey=" + API_KEY;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssetSentimentRepository repository;

    @MockBean
    private AssetScraper scraper;

    @Test
    public void scrapeAssets_invokesDependenciesCorrectly() throws Exception {
        List<String> assetIds = Arrays.asList("assetId1", "assetId2");
        List<Asset> assets = Arrays.asList(AssetUtil.createAsset("assetId1", AssetType.MOVIE, "title1"),
                AssetUtil.createAsset("assetId2", AssetType.SHOW, "title2"));
        when(scraper.scrapeIds(OMDB_URL)).thenReturn(assetIds);
        when(scraper.scrapeAssets(assetIds, API_KEY)).thenReturn(assets);

        mockMvc.perform(get(SCRAPE_ASSETS_TEST_URL));

        verify(scraper).scrapeIds(OMDB_URL);
        verify(scraper).scrapeAssets(assetIds, API_KEY);
        verify(repository).saveAll(assets);
        verifyNoMoreInteractions(scraper, repository);
    }

    @Test
    public void scrapeAssets_returnsOk() throws Exception {
        List<String> assetIds = Arrays.asList("assetId1", "assetId2");
        List<Asset> assets = Arrays.asList(AssetUtil.createAsset("assetId1", AssetType.MOVIE, "title1"),
                AssetUtil.createAsset("assetId2", AssetType.SHOW, "title2"));
        when(scraper.scrapeIds(OMDB_URL)).thenReturn(assetIds);
        when(scraper.scrapeAssets(assetIds, API_KEY)).thenReturn(assets);

        mockMvc.perform(get(SCRAPE_ASSETS_TEST_URL)).andExpect(status().isOk());
    }

    @Test
    public void scrapeAssets_failure_returnsServerError() throws Exception {
        final String errorMessage = "Failed to scrape Asset ids";
        when(scraper.scrapeIds(OMDB_URL)).thenThrow(new IOException(errorMessage));

        mockMvc.perform(get(SCRAPE_ASSETS_TEST_URL))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$", equalTo(errorMessage)));
    }

    @Test
    public void scrapeBanners_successful_savesAsset() throws Exception {
        final String bannerUrl = "testBannerUrl";
        Asset asset = AssetUtil.createAsset("assetId1", AssetType.MOVIE, "title1");
        when(repository.getAssetsWithoutBanner()).thenReturn(Arrays.asList(asset));
        when(scraper.scrapeBannerUrl(API_KEY, asset)).thenReturn(bannerUrl);

        mockMvc.perform(put(SCRAPE_BANNERS_TEST_URL));

        assertThat(asset.getBanner()).isEqualTo(bannerUrl);
        verify(repository).save(asset);
    }

    @Test
    public void scrapeBanners_failure_returnsAssetId() throws Exception {
        Asset asset = AssetUtil.createAsset("assetId1", AssetType.MOVIE, "title1");
        when(repository.getAssetsWithoutBanner()).thenReturn(Arrays.asList(asset));
        when(scraper.scrapeBannerUrl(API_KEY, asset)).thenThrow(new RuntimeException("Scrape failure"));

        mockMvc.perform(put(SCRAPE_BANNERS_TEST_URL))
                .andExpect(jsonPath("$[0]", equalTo("assetId1")));
    }
}
