package com.google.moviestvsentiments.usecase.details;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.Matchers.equalTo;

import android.content.Intent;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.rule.ActivityTestRule;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.di.WebModule;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.assetSentiment.AssetSentimentDao;
import com.google.moviestvsentiments.usecase.assetList.AssetListFragment;
import com.google.moviestvsentiments.util.AssetUtil;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@HiltAndroidTest
@UninstallModules({DatabaseModule.class, WebModule.class})
@RunWith(JUnitParamsRunner.class)
public class DetailsActivityTest {

    private ActivityTestRule<DetailsActivity> activityTestRule =
            new ActivityTestRule<>(DetailsActivity.class, false, false);

    private HiltAndroidRule hiltTestRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(hiltTestRule).around(activityTestRule)
            .around(new InstantTaskExecutorRule());

    @Inject
    AssetSentimentDao assetSentimentDao;

    private Object[] displaysAssetInfoValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED},
            new Object[] {SentimentType.THUMBS_UP},
            new Object[] {SentimentType.THUMBS_DOWN}
        };
    }

    @Test
    @Parameters(method = "displaysAssetInfoValues")
    public void detailsActivity_displaysAssetInfo(SentimentType sentimentType) {
        final String title = "Asset Title";
        final String plot = "Description of the plot";
        final String imdbRating = "4.7";
        Asset asset = AssetUtil.defaultMovieBuilder("assetId").setTitle(title).setPlot(plot)
                .setImdbRating(imdbRating).build();
        AssetSentiment assetSentiment = AssetSentiment.create(asset, sentimentType);
        Intent intent = new Intent();
        intent.putExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, assetSentiment);

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.asset_title)).check(matches(withText(title)));
        onView(withId(R.id.description)).check(matches(withText(plot)));
        onView(withId(R.id.rating)).check(matches(withText(imdbRating)));
    }

    private Object[] displaysSentimentValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED, R.drawable.ic_outline_thumb_up_24,
                    R.drawable.ic_outline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_UP, R.drawable.ic_baseline_thumb_up_24,
                    R.drawable.ic_outline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_DOWN, R.drawable.ic_outline_thumb_up_24,
                    R.drawable.ic_baseline_thumb_down_24}
        };
    }

    @Test
    @Parameters(method = "displaysSentimentValues")
    public void detailsActivity_displaysSentiment(SentimentType sentimentType, int thumbsUpIcon,
                                                  int thumbsDownIcon) {
        AssetSentiment assetSentiment = AssetSentiment.create(
                AssetUtil.createMovieAsset("assetId"), sentimentType);
        Intent intent = new Intent();
        intent.putExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, assetSentiment);

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.thumbs_up)).check(matches(withTagValue(equalTo(thumbsUpIcon))));
        onView(withId(R.id.thumbs_down)).check(matches(withTagValue(equalTo(thumbsDownIcon))));
    }

    private Object[] changeSentiment_updatesDisplayValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED, R.id.thumbs_up,
                    R.drawable.ic_baseline_thumb_up_24, R.drawable.ic_outline_thumb_down_24},
            new Object[] {SentimentType.UNSPECIFIED, R.id.thumbs_down,
                    R.drawable.ic_outline_thumb_up_24, R.drawable.ic_baseline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_UP, R.id.thumbs_up,
                    R.drawable.ic_outline_thumb_up_24, R.drawable.ic_outline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_UP, R.id.thumbs_down,
                    R.drawable.ic_outline_thumb_up_24, R.drawable.ic_baseline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_DOWN, R.id.thumbs_up,
                    R.drawable.ic_baseline_thumb_up_24, R.drawable.ic_outline_thumb_down_24},
            new Object[] {SentimentType.THUMBS_DOWN, R.id.thumbs_down,
                    R.drawable.ic_outline_thumb_up_24, R.drawable.ic_outline_thumb_down_24},
        };
    }

    @Test
    @Parameters(method = "changeSentiment_updatesDisplayValues")
    public void detailsActivity_changeSentiment_updatesDisplay(SentimentType originalSentiment,
                                   int iconToClick, int thumpUpDrawable, int thumbDownDrawable) {
        AssetSentiment assetSentiment = AssetSentiment.create(
                AssetUtil.createMovieAsset("assetId"), originalSentiment);
        Intent intent = new Intent();
        intent.putExtra(AssetListFragment.EXTRA_ACCOUNT_NAME, "accountName");
        intent.putExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, assetSentiment);
        activityTestRule.launchActivity(intent);

        onView(withId(iconToClick)).perform(click());

        onView(withId(R.id.thumbs_up)).check(matches(withTagValue(equalTo(thumpUpDrawable))));
        onView(withId(R.id.thumbs_down)).check(matches(withTagValue(equalTo(thumbDownDrawable))));
    }

    private Object[] changeSentiment_updatesDatabaseValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED, R.id.thumbs_up, SentimentType.THUMBS_UP},
            new Object[] {SentimentType.UNSPECIFIED, R.id.thumbs_down, SentimentType.THUMBS_DOWN},
            new Object[] {SentimentType.THUMBS_UP, R.id.thumbs_up, SentimentType.UNSPECIFIED},
            new Object[] {SentimentType.THUMBS_UP, R.id.thumbs_down, SentimentType.THUMBS_DOWN},
            new Object[] {SentimentType.THUMBS_DOWN, R.id.thumbs_up, SentimentType.THUMBS_UP},
            new Object[] {SentimentType.THUMBS_DOWN, R.id.thumbs_down, SentimentType.UNSPECIFIED}
        };
    }

    @Test
    @Parameters(method = "changeSentiment_updatesDatabaseValues")
    public void detailsActivity_changeSentiment_updatesDatabase(SentimentType originalSentiment,
                                                    int iconToClick, SentimentType finalSentiment) {
        final String accountName = "Account Name";
        final String assetId = "assetId";
        hiltTestRule.inject();
        Asset asset = AssetUtil.createMovieAsset(assetId);
        assetSentimentDao.addAsset(asset);
        AssetSentiment assetSentiment = AssetSentiment.create(asset, originalSentiment);
        Intent intent = new Intent();
        intent.putExtra(AssetListFragment.EXTRA_ACCOUNT_NAME, accountName);
        intent.putExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, assetSentiment);
        activityTestRule.launchActivity(intent);

        onView(withId(iconToClick)).perform(click());

        assetSentiment = LiveDataTestUtil.getValue(assetSentimentDao.getAsset(accountName, assetId,
                AssetType.MOVIE));
        assertThat(assetSentiment).isEqualTo(AssetSentiment.create(asset, finalSentiment));
    }
}
