package com.google.moviestvsentiments.usecase.liked;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;

import android.content.Intent;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import com.google.moviestvsentiments.HiltFragmentScenario;
import com.google.moviestvsentiments.HiltTestActivity;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.model.AssetType;
import com.google.moviestvsentiments.model.SentimentType;
import com.google.moviestvsentiments.service.database.SentimentsDatabase;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import com.google.moviestvsentiments.util.AssetUtil;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import javax.inject.Inject;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class LikedFragmentTest {

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

    @Test
    public void likedFragment_startsWithEmptyMovieList() {
        HiltFragmentScenario.launchHiltFragment(LikedFragment.class);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
    }

    @Test
    public void likedFragment_startsWithEmptyShowList() {
        HiltFragmentScenario.launchHiltFragment(LikedFragment.class);

        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void likedFragment_displaysMoviesWithThumbsUp() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(LikedFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(1));
    }

    @Test
    public void likedFragment_displaysShowsWithThumbsUp() {
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.SHOW, SentimentType.THUMBS_UP);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(LikedFragment.class, intent);

        onView(withId(R.id.tvshows_list)).check(withItemCount(1));
    }

    @Test
    public void likedFragment_ignoresAssetsWithNoReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(LikedFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void likedFragment_ignoresAssetsWithUnspecifiedReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.UNSPECIFIED);
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.UNSPECIFIED);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(LikedFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void likedFragment_ignoresAssetsWithThumbsDown() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.THUMBS_DOWN);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(LikedFragment.class, intent);

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }
}
