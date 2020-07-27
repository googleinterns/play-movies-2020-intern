package com.google.moviestvsentiments.usecase.navigation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.di.WebModule;
import com.google.moviestvsentiments.model.Account;
import com.google.moviestvsentiments.service.account.AccountDao;
import com.google.moviestvsentiments.usecase.signin.SigninActivity;
import com.google.moviestvsentiments.util.LiveDataTestUtil;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import java.time.Instant;
import javax.inject.Inject;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@UninstallModules({DatabaseModule.class, WebModule.class})
@HiltAndroidTest
public class SentimentsNavigationActivityTest {

    private static final String ACCOUNT_NAME = "Account Name";

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(hiltRule).around(new InstantTaskExecutorRule());

    @Inject
    AccountDao accountDao;

    @Before
    public void setUp() {
        hiltRule.inject();
        accountDao.addAccount(ACCOUNT_NAME, Instant.EPOCH, false);
        accountDao.setIsCurrent(ACCOUNT_NAME, true);

        Intents.init();

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                SentimentsNavigationActivity.class);
        intent.putExtra(SigninActivity.EXTRA_ACCOUNT_NAME, ACCOUNT_NAME);
        ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void hamburgerMenu_bindsCorrectly() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());

        onView(withId(R.id.hamburgerAccountName)).check(matches(withText(ACCOUNT_NAME)));
        onView(withId(R.id.hamburgerAccountIconText)).check(matches(
                withText(ACCOUNT_NAME.substring(0, 1))));
    }

    @Test
    public void hamburgerMenu_selectSignOut_clearsCurrentAccount() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.hamburger_nav),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        Account account = LiveDataTestUtil.getValue(accountDao.getCurrentAccount());
        assertThat(account).isNull();
    }

    @Test
    public void hamburgerMenu_selectSignOut_sendsIntentToSigninActivity() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.hamburger_nav),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        intended(hasComponent(SigninActivity.class.getName()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
