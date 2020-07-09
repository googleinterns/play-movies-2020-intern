package com.google.moviestvsentiments.webscrape;

import com.google.moviestvsentiments.assetSentiment.Asset;
import com.google.moviestvsentiments.assetSentiment.AssetSentimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * A controller that handles requests related to scraping Assets from OMDB.
 */
@RestController
public class AssetScrapeController {

    @Autowired
    private AssetSentimentRepository assetSentimentRepository;

    @Autowired
    private AssetScraper assetScraper;

    /**
     * Scrapes Assets listed at the given url and saves them in the Assets database table. The url should point to an
     * IMDB page listing Assets. If an error occurs, the error message is returned.
     * @param url The IMDB page listing the Assets and their ids.
     * @param apiKey The OMDB api key to use when fetching Asset details.
     * @return A ResponseEntity containing the error message, if an error occurs.
     */
    @GetMapping("/scrape")
    public ResponseEntity<String> scrapeAssets(@RequestParam("url") String url,
                                       @RequestParam("apiKey") String apiKey) {
        try {
            List<String> assetIds = assetScraper.scrapeIds(url);
            List<Asset> assets = assetScraper.scrapeAssets(assetIds, apiKey);
            assetSentimentRepository.saveAll(assets);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
