package com.google.moviestvsentiments.webscrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.moviestvsentiments.assetSentiment.Asset;
import com.google.moviestvsentiments.assetSentiment.AssetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functions to support scraping Assets from OMDB.
 */
@Configuration
public class AssetScraper {

    /**
     * Represents a response from the OMDB API.
     */
    public static class OmdbResponse {

        public static class Rating {
            @Key("Source") public String source;
            @Key("Value") public String value;
        }

        @Key("imdbID") public String assetId;
        @Key("Type") public String assetType;
        @Key("Title") public String title;
        @Key("Poster") public String poster;
        @Key public String imdbRating;
        @Key("Ratings") List<Rating> ratings;
        @Key("Plot") public String plot;
        @Key("Runtime") public String runtime;
        @Key("Year") public String year;
    }

    private static final String IMDB_ASSET_XPATH = "//td[@class='titleColumn']/a";
    private static final String IMDB_ASSET_URL_PREFIX = "/title/";
    private static final String OMDB_MOVIE_TYPE = "movie";
    private static final String OMDB_SHOW_TYPE = "series";
    private static final String OMDB_ROTTEN_TOMATOES_SOURCE = "Rotten Tomatoes";

    private final Clock clock;
    private final HttpRequestFactory requestFactory;
    private final Logger logger;

    // Public default constructor is required for Spring to autowire AssetScraper in AssetScrapeController.
    public AssetScraper() {
        clock = Clock.systemUTC();
        requestFactory = defaultRequestFactory();
        logger = LoggerFactory.getLogger(AssetScraper.class);
    }

    /**
     * Returns the default HttpRequestFactory that uses NetHttpTransport to send HTTP requests and Jackson to parse
     * JSON responses.
     */
    private static HttpRequestFactory defaultRequestFactory() {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        return httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) {
                request.setParser(new JsonObjectParser(jsonFactory));
            }
        });
    }

    /**
     * Returns a list of IMDB asset ids scraped from the provided url.
     * @param url The url of the IMDB page to scrape asset ids from.
     * @return A list of IDMB asset ids.
     * @throws IOException If fetching the page at the given url fails.
     */
    public List<String> scrapeIds(String url) throws IOException {
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = client.getPage(url);
        List<HtmlAnchor> assetLinks = page.getByXPath(IMDB_ASSET_XPATH);

        List<String> assetIds = new ArrayList<>();
        for (HtmlAnchor assetLink : assetLinks) {
            String assetUrl = assetLink.getHrefAttribute();
            int startPosition = IMDB_ASSET_URL_PREFIX.length();
            int endPosition = assetUrl.lastIndexOf("/");
            assetIds.add(assetUrl.substring(startPosition, endPosition));
        }
        return assetIds;
    }

    /**
     * Returns a list of Assets scraped from OMDB. The given OMDB api key and the list of Asset ids is used to query
     * OMDB for the Assets.
     * @param assetIds The list of Asset ids to query OMDB for.
     * @param omdbApiKey The api key to use when querying OMDB.
     * @return A list of Assets scraped from OMDB.
     * @throws IOException If the OMDB query fails.
     */
    public List<Asset> scrapeAssets(List<String> assetIds, String omdbApiKey) throws IOException {
        List<Asset> assets = new ArrayList<>();
        for (String assetId : assetIds) {
            String url = "http://www.omdbapi.com/?plot=full&apikey=" + omdbApiKey + "&i=" + assetId;
            HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
            OmdbResponse response = request.execute().parseAs(OmdbResponse.class);
            Asset asset = createAsset(response);
            if (asset != null) {
                assets.add(asset);
            }
        }
        return assets;
    }

    /**
     * Creates a new Asset from the given OmdbResponse. The Asset's timestamp is set to the current time.
     * @param omdbResponse The OmdbResponse to use when creating the Asset.
     * @return A new Asset created using the OmdbResponse.
     */
    private Asset createAsset(OmdbResponse omdbResponse) {
        Asset asset = new Asset();
        asset.setAssetId(omdbResponse.assetId);
        asset.setTitle(omdbResponse.title);
        asset.setPoster(omdbResponse.poster);
        asset.setImdbRating(omdbResponse.imdbRating);
        asset.setPlot(omdbResponse.plot);
        asset.setRuntime(omdbResponse.runtime);
        asset.setYear(omdbResponse.year);
        asset.setTimestamp(Instant.now(clock));

        if (OMDB_MOVIE_TYPE.equals(omdbResponse.assetType)) {
            asset.setAssetType(AssetType.MOVIE);
        } else if (OMDB_SHOW_TYPE.equals(omdbResponse.assetType)) {
            asset.setAssetType(AssetType.SHOW);
        } else {
            logger.warn("Unknown OMDB asset type: " + omdbResponse.assetType);
            return null;
        }

        omdbResponse.ratings.stream().filter(rating -> OMDB_ROTTEN_TOMATOES_SOURCE.equals(rating))
                .findAny().ifPresent(rating -> asset.setRottenTomatoesRating(rating.value));

        return asset;
    }
}
