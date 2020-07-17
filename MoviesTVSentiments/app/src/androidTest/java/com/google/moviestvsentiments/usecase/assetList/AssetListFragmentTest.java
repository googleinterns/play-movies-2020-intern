package com.google.moviestvsentiments.usecase.assetList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import android.content.Intent;
import android.os.Bundle;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import com.google.moviestvsentiments.HiltTestActivity;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.di.WebModule;
import com.google.moviestvsentiments.model.Asset;
import com.google.moviestvsentiments.model.AssetSentiment;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.inject.Inject;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import com.google.moviestvsentiments.HiltFragmentScenario;
import com.google.moviestvsentiments.usecase.details.DetailsActivity;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import com.google.moviestvsentiments.util.AssetUtil;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import java.time.Instant;

@UninstallModules({DatabaseModule.class, WebModule.class})
@HiltAndroidTest
@RunWith(JUnitParamsRunner.class)
public class AssetListFragmentTest {

    private static Bundle createFragmentArgs(SentimentType sentimentType) {
        Bundle arguments = new Bundle();
        arguments.putSerializable("sentimentType", sentimentType);
        return arguments;
    }

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Inject
    SentimentsDatabase database;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
    }

    private Object[] startsWithEmptyListValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED},
            new Object[] {SentimentType.THUMBS_UP},
            new Object[] {SentimentType.THUMBS_DOWN}
        };
    }

    @Test
    @Parameters(method = "startsWithEmptyListValues")
    public void assetListFragment_startsWithEmptyList(SentimentType sentimentType) {
        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(sentimentType));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    private Object[] displaysAssetsWithSentimentValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED, AssetType.MOVIE, R.id.movies_list},
            new Object[] {SentimentType.UNSPECIFIED, AssetType.SHOW, R.id.tvshows_list},
            new Object[] {SentimentType.THUMBS_UP, AssetType.MOVIE, R.id.movies_list},
            new Object[] {SentimentType.THUMBS_UP, AssetType.SHOW, R.id.tvshows_list},
            new Object[] {SentimentType.THUMBS_DOWN, AssetType.MOVIE, R.id.movies_list},
            new Object[] {SentimentType.THUMBS_DOWN, AssetType.SHOW, R.id.tvshows_list},
        };
    }

    @Test
    @Parameters(method = "displaysAssetsWithSentimentValues")
    public void assetListFragment_withSentiment_displaysAssetsWithSentiment(
            SentimentType sentimentType, AssetType assetType, int listId) {
        database.assetSentimentDao().addAsset(AssetUtil.createAsset("assetId1", assetType));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                assetType, sentimentType, false, Instant.EPOCH);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(sentimentType));

        onView(withId(listId)).check(withItemCount(1));
    }

    @Test
    public void assetListFragment_withUnspecified_displaysAssetsWithNoReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));

        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(SentimentType.UNSPECIFIED));

        onView(withId(R.id.movies_list)).check(withItemCount(1));
    }

    private Object[] ignoresAssetsWithOtherSentimentValues() {
        return new Object[] {
            new Object[] {SentimentType.UNSPECIFIED, SentimentType.THUMBS_UP, AssetType.MOVIE},
            new Object[] {SentimentType.UNSPECIFIED, SentimentType.THUMBS_DOWN, AssetType.MOVIE},
            new Object[] {SentimentType.UNSPECIFIED, SentimentType.THUMBS_UP, AssetType.SHOW},
            new Object[] {SentimentType.UNSPECIFIED, SentimentType.THUMBS_DOWN, AssetType.SHOW},
            new Object[] {SentimentType.THUMBS_UP, SentimentType.UNSPECIFIED, AssetType.MOVIE},
            new Object[] {SentimentType.THUMBS_UP, SentimentType.THUMBS_DOWN, AssetType.MOVIE},
            new Object[] {SentimentType.THUMBS_UP, SentimentType.UNSPECIFIED, AssetType.SHOW},
            new Object[] {SentimentType.THUMBS_UP, SentimentType.THUMBS_DOWN, AssetType.SHOW},
            new Object[] {SentimentType.THUMBS_DOWN, SentimentType.THUMBS_UP, AssetType.MOVIE},
            new Object[] {SentimentType.THUMBS_DOWN, SentimentType.UNSPECIFIED, AssetType.MOVIE},
            new Object[] {SentimentType.THUMBS_DOWN, SentimentType.THUMBS_UP, AssetType.SHOW},
            new Object[] {SentimentType.THUMBS_DOWN, SentimentType.UNSPECIFIED, AssetType.SHOW},
        };
    }

    @Test
    @Parameters(method = "ignoresAssetsWithOtherSentimentValues")
    public void assetListFragment_withSentiment_ignoresAssetsWithOtherSentiment(
            SentimentType sentimentType, SentimentType otherSentiment, AssetType assetType) {
        database.assetSentimentDao().addAsset(AssetUtil.createAsset("assetId", assetType));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                assetType, otherSentiment, false, Instant.EPOCH);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(sentimentType));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    private Object[] sendsIntentToDetailsActivityValues() {
        return new Object[] {
            new Object[] {AssetType.MOVIE},
            new Object[] {AssetType.SHOW}
        };
    }

    @Test
    @Parameters(method = "sendsIntentToDetailsActivityValues")
    public void assetListFragment_clickAsset_sendsIntentToDetailsActivity(AssetType assetType) {
        final String accountName = "Test Account";
        Asset asset = AssetUtil.createAsset("assetId1", assetType);
        database.assetSentimentDao().addAsset(asset);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, accountName);
        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.UNSPECIFIED));
        Intents.init();

        onView(withId(R.id.asset_card)).perform(click());

        intended(allOf(
            hasComponent(DetailsActivity.class.getName()),
            hasExtra(AssetListFragment.EXTRA_ACCOUNT_NAME, accountName),
            hasExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, AssetSentiment.create(asset,
                    SentimentType.UNSPECIFIED))
        ));
        Intents.release();
    }

    private Object[] displaysAssetReactSheetValues() {
        return new Object[] {
                new Object[] {AssetType.MOVIE, SentimentType.UNSPECIFIED, R.drawable.ic_outline_thumb_up_24,
                    R.drawable.ic_outline_thumb_down_24},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_UP, R.drawable.ic_baseline_thumb_up_24,
                        R.drawable.ic_outline_thumb_down_24},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_DOWN, R.drawable.ic_outline_thumb_up_24,
                        R.drawable.ic_baseline_thumb_down_24},
                new Object[] {AssetType.SHOW, SentimentType.UNSPECIFIED, R.drawable.ic_outline_thumb_up_24,
                        R.drawable.ic_outline_thumb_down_24},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_UP, R.drawable.ic_baseline_thumb_up_24,
                        R.drawable.ic_outline_thumb_down_24},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_DOWN, R.drawable.ic_outline_thumb_up_24,
                        R.drawable.ic_baseline_thumb_down_24},
        };
    }

    @Test
    @Parameters(method = "displaysAssetReactSheetValues")
    public void longClickAsset_displaysAssetReactSheet(AssetType assetType, SentimentType sentimentType,
                                                       int thumbUpDrawable, int thumbDownDrawable) {
        database.assetSentimentDao().addAsset(AssetUtil.createAsset("assetId1", assetType));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                assetType, sentimentType, false, Instant.EPOCH);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");
        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(sentimentType));

        onView(withId(R.id.asset_card)).perform(longClick());

        onView(withId(R.id.react_sheet_title)).check(matches(withText("assetTitle")));
        onView(withId(R.id.react_sheet_details)).check(matches(allOf(
                withText(containsString("year")),
                withText(containsString("runtime")))));
        onView(withId(R.id.react_sheet_thumbs_up)).check(matches(withTagValue(equalTo(thumbUpDrawable))));
        onView(withId(R.id.react_sheet_thumbs_down)).check(matches(withTagValue(equalTo(thumbDownDrawable))));
    }

    private Object[] clickReaction_updatesSentimentValues() {
        return new Object[] {
                new Object[] {AssetType.MOVIE, SentimentType.UNSPECIFIED, R.id.react_sheet_like,
                        SentimentType.THUMBS_UP},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_UP, R.id.react_sheet_like,
                        SentimentType.UNSPECIFIED},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_DOWN, R.id.react_sheet_like,
                        SentimentType.THUMBS_UP},
                new Object[] {AssetType.MOVIE, SentimentType.UNSPECIFIED, R.id.react_sheet_dislike,
                        SentimentType.THUMBS_DOWN},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_UP, R.id.react_sheet_dislike,
                        SentimentType.THUMBS_DOWN},
                new Object[] {AssetType.MOVIE, SentimentType.THUMBS_DOWN, R.id.react_sheet_dislike,
                        SentimentType.UNSPECIFIED},
                new Object[] {AssetType.SHOW, SentimentType.UNSPECIFIED, R.id.react_sheet_like,
                        SentimentType.THUMBS_UP},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_UP, R.id.react_sheet_like,
                        SentimentType.UNSPECIFIED},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_DOWN, R.id.react_sheet_like,
                        SentimentType.THUMBS_UP},
                new Object[] {AssetType.SHOW, SentimentType.UNSPECIFIED, R.id.react_sheet_dislike,
                        SentimentType.THUMBS_DOWN},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_UP, R.id.react_sheet_dislike,
                        SentimentType.THUMBS_DOWN},
                new Object[] {AssetType.SHOW, SentimentType.THUMBS_DOWN, R.id.react_sheet_dislike,
                        SentimentType.UNSPECIFIED},
        };
    }

    @Test
    @Parameters(method = "clickReaction_updatesSentimentValues")
    public void longClickAsset_clickReaction_updatesSentiment(AssetType assetType,
              SentimentType originalSentiment, int reactionButtonId, SentimentType finalSentiment) {
        database.assetSentimentDao().addAsset(AssetUtil.createAsset("assetId1", assetType));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                assetType, originalSentiment, false, Instant.EPOCH);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");
        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(originalSentiment));

        onView(withId(R.id.asset_card)).perform(longClick());
        onView(withId(reactionButtonId)).perform(click());

        AssetSentiment assetSentiment = LiveDataTestUtil.getValue(database.assetSentimentDao()
                .getAsset("Test Account", "assetId1", assetType));
        assertThat(assetSentiment.sentimentType()).isEqualTo(finalSentiment);
    }
}
