package com.google.moviestvsentiments.usecase.signin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import androidx.test.rule.ActivityTestRule;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class SigninActivityTest {

    @Rule
    public RuleChain rule = RuleChain.outerRule(new HiltAndroidRule(this))
            .around(new ActivityTestRule<>(SigninActivity.class));

    @Test
    public void signinActivity_displaysOnlyAddAccount() {
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void signinActivity_clickAddAccount_addsAccount() {
        onView(withId(R.id.accountTextView)).perform(click());

        onView(allOf(withId(R.id.accountTextView), not(withText("Add Account"))))
                .check(matches(withText("Test Account")));
    }
}
