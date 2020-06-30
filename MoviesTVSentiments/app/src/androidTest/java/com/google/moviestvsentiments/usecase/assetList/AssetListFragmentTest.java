package com.google.moviestvsentiments.usecase.assetList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.os.Bundle;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import com.google.moviestvsentiments.HiltTestActivity;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
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

@UninstallModules(DatabaseModule.class)
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
                assetType, sentimentType);
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
                assetType, otherSentiment);
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
        Asset asset = AssetUtil.createAsset("assetId1", assetType);
        database.assetSentimentDao().addAsset(asset);
        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(SentimentType.UNSPECIFIED));
        Intents.init();

        onView(withId(R.id.asset_card)).perform(click());

        intended(allOf(
            hasComponent(DetailsActivity.class.getName()),
            hasExtra(AssetListFragment.EXTRA_ASSET_SENTIMENT, AssetSentiment.create(asset,
                    SentimentType.UNSPECIFIED))
        ));
        Intents.release();
    }
}
