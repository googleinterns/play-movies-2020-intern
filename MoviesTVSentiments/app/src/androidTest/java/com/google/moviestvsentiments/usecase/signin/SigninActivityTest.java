package com.google.moviestvsentiments.usecase.signin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.usecase.addAccount.AddAccountActivity;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class SigninActivityTest {

    @Rule
    public RuleChain rule = RuleChain.outerRule(new HiltAndroidRule(this))
            .around(new IntentsTestRule<>(SigninActivity.class));

    @Test
    public void signinActivity_displaysOnlyAddAccount() {
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void signinActivity_clickAddAccount_sendsIntentToAddAccountActivity() {
        onView(withId(R.id.accountTextView)).perform(click());

        intended(hasComponent(AddAccountActivity.class.getName()));
    }
}
