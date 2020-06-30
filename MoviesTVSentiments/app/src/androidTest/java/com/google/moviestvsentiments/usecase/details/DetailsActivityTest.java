package com.google.moviestvsentiments.usecase.details;

import android.content.Intent;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.rule.ActivityTestRule;

import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.usecase.assetList.AssetListFragment;
import com.google.moviestvsentiments.util.AssetUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.moviestvsentiments.assertions.ImageViewDrawableMatcher.withDrawable;

@RunWith(JUnitParamsRunner.class)
public class DetailsActivityTest {

    @Rule
    public ActivityTestRule<DetailsActivity> activityTestRule =
            new ActivityTestRule<>(DetailsActivity.class, false, false);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

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
        Asset asset = AssetUtil.defaultMovieBuilder("assetId").setTitle("Asset Title")
                .setPlot("Description of the plot").setImdbRating("4.7").build();
        AssetSentiment assetSentiment = AssetSentiment.create(asset, sentimentType);
        Intent intent = new Intent();
        intent.putExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, assetSentiment);

        activityTestRule.launchActivity(intent);

        onView(withId(R.id.asset_title)).check(matches(withText("Asset Title")));
        onView(withId(R.id.rating)).check(matches(withText("4.7")));
        onView(withId(R.id.description)).check(matches(withText("Description of the plot")));
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

        onView(withId(R.id.thumbs_up)).check(matches(withDrawable(thumbsUpIcon)));
        onView(withId(R.id.thumbs_down)).check(matches(withDrawable(thumbsDownIcon)));
    }
}
