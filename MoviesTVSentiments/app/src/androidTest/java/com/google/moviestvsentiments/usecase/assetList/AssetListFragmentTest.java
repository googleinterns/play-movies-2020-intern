package com.google.moviestvsentiments.usecase.assetList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;

import android.content.Intent;
import android.os.Bundle;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
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

    @Test
    public void assetListFragment_startsWithEmptyMovieList() {
        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(SentimentType.THUMBS_UP));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_startsWithEmptyShowList() {
        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(SentimentType.UNSPECIFIED));

        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_withUnspecified_displaysAssetsWithNoReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));

        HiltFragmentScenario.launchHiltFragment(AssetListFragment.class,
                createFragmentArgs(SentimentType.UNSPECIFIED));

        onView(withId(R.id.movies_list)).check(withItemCount(1));
    }

    @Test
    public void assetListFragment_withUnspecified_displaysAssetsWithUnspecifiedReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId1"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                AssetType.SHOW, SentimentType.UNSPECIFIED);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.UNSPECIFIED));

        onView(withId(R.id.tvshows_list)).check(withItemCount(1));
    }

    @Test
    public void assetListFragment_withUnspecified_ignoresAssetsWithReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId1"));
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId1",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.UNSPECIFIED));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_withThumbsUp_displaysAssetsWithThumbsUp() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.THUMBS_UP);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_UP));

        onView(withId(R.id.movies_list)).check(withItemCount(1));
        onView(withId(R.id.tvshows_list)).check(withItemCount(1));
    }

    @Test
    public void assetListFragment_withThumbsUp_ignoresAssetsWithUnspecifiedReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.UNSPECIFIED);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_UP));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_withThumbsUp_ignoresAssetsWithThumbsDown() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.THUMBS_DOWN);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_UP));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_withThumbsDown_displaysAssetsWithThumbsDown() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_DOWN);
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.THUMBS_DOWN);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_DOWN));

        onView(withId(R.id.movies_list)).check(withItemCount(1));
        onView(withId(R.id.tvshows_list)).check(withItemCount(1));
    }

    @Test
    public void assetListFragment_withThumbsDown_ignoresAssetsWithUnspecifiedReaction() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.UNSPECIFIED);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_DOWN));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }

    @Test
    public void assetListFragment_withThumbsDown_ignoresAssetsWithThumbsUp() {
        database.assetSentimentDao().addAsset(AssetUtil.createMovieAsset("assetId"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId",
                AssetType.MOVIE, SentimentType.THUMBS_UP);
        database.assetSentimentDao().addAsset(AssetUtil.createShowAsset("assetId2"));
        database.assetSentimentDao().updateSentiment("Test Account", "assetId2",
                AssetType.SHOW, SentimentType.THUMBS_UP);
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HiltTestActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Test Account");

        HiltFragmentScenario.launchHiltFragmentWithIntent(AssetListFragment.class, intent,
                createFragmentArgs(SentimentType.THUMBS_DOWN));

        onView(withId(R.id.movies_list)).check(withItemCount(0));
        onView(withId(R.id.tvshows_list)).check(withItemCount(0));
    }
}
