package com.google.moviestvsentiments.usecase.signin;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.moviestvsentiments.assertions.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.allOf;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
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
import com.google.moviestvsentiments.usecase.navigation.SentimentsNavigationActivity;

@UninstallModules(DatabaseModule.class)
@HiltAndroidTest
public class SigninActivityTest {

    @Rule
    public RuleChain rule = RuleChain.outerRule(new HiltAndroidRule(this))
            .around(new IntentsTestRule<>(SigninActivity.class));

    @Test
    public void signinActivity_displaysOnlyAddAccount() {
        onView(withId(R.id.accountList)).check(withItemCount(1));
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void signinActivity_clickAddAccount_sendsIntentToAddAccountActivity() {
        onView(withId(R.id.accountTextView)).perform(click());

        intended(hasComponent(AddAccountActivity.class.getName()));
    }

    @Test
    public void signinActivity_addAccountResultCanceled_displaysOnlyAddAccount() {
        ActivityResult result = new ActivityResult(Activity.RESULT_CANCELED, new Intent());
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);

        onView(withId(R.id.accountTextView)).perform(click());

        onView(withId(R.id.accountList)).check(withItemCount(1));
        onView(withId(R.id.accountTextView)).check(matches(withText("Add Account")));
    }

    @Test
    public void signinActivity_addAccountResultOk_displaysNewAccount() {
        Intent intent = new Intent();
        intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_NAME, "Account Name");
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, intent);
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);

        onView(withId(R.id.accountTextView)).perform(click());

        onView(withId(R.id.accountList)).check(withItemCount(2));
    }

    @Test
    public void signinActivity_clickAccountName_sendsIntentToSentimentsNavigationActivity() {
        Intent intent = new Intent();
        intent.putExtra(AddAccountActivity.EXTRA_ACCOUNT_NAME, "Account Name");
        ActivityResult result = new ActivityResult(Activity.RESULT_OK, intent);
        intending(hasComponent(AddAccountActivity.class.getName())).respondWith(result);
        onView(withId(R.id.accountTextView)).perform(click());

        onView(allOf(withId(R.id.accountTextView), withText("Account Name"))).perform(click());

        intended(allOf(hasComponent(SentimentsNavigationActivity.class.getName()),
                hasExtra(SigninActivity.EXTRA_ACCOUNT_NAME, "Account Name")));
    }
}
