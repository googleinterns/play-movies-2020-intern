package com.google.moviestvsentiments.usecase.home;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import com.google.moviestvsentiments.HiltTestActivity;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import javax.inject.Inject;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import com.google.moviestvsentiments.HiltFragmentScenario;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import com.google.moviestvsentiments.util.AssetUtil;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class HomeFragmentTest {

    private static final int RECYCLER_VIEW_TIMEOUT = 1500;

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    @Inject
    SentimentsDatabase database;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
    }

    @Test
    public void homeFragment_startsWithEmptyMovieList() {
        HiltFragmentScenario.launchHiltFragment(HomeFragment.class);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
    }

    @Test
    public void homeFragment_startsWithEmptyShowList() {
        HiltFragmentScenario.launchHiltFragment(HomeFragment.class);

        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void homeFragment_displaysAssetsWithNoReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));

        HiltFragmentScenario.launchHiltFragment(HomeFragment.class);

        onView(withId(R.id.movies_list)).check(withItemCount(1).withTimeout(RECYCLER_VIEW_TIMEOUT));
    }

    @Test
    public void homeFragment_displaysAssetsWithUnspecifiedReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(HomeFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(1).withTimeout(RECYCLER_VIEW_TIMEOUT));
    }

    @Test
    public void homeFragment_ignoresAssetsWithReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(HomeFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
    }
}
