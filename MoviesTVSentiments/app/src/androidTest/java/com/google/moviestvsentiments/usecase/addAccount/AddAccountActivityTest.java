package com.google.moviestvsentiments.usecase.addAccount;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static androidx.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import com.google.moviestvsentiments.R;
import org.junit.Rule;
import org.junit.Test;

public class AddAccountActivityTest {

    @Rule
    public IntentsTestRule<AddAccountActivity> rule = new IntentsTestRule<>(AddAccountActivity.class);

    @Test
    public void addAccountActivity_editTextDisplaysPlaceholderText() {
        onView(withId(R.id.editAccountName)).check(matches(withHint("Account Name")));
    }

    @Test
    public void addAccountActivity_addButtonDisplaysText() {
        onView(withId(R.id.addAccountButton)).check(matches(withText("Add Account")));
    }

    @Test
    public void addAccountActivity_emptyText_sendsResultCanceled() {
        onView(withId(R.id.addAccountButton)).perform(click());

        assertThat(rule.getActivityResult(), hasResultCode(Activity.RESULT_CANCELED));
    }

    @Test
    public void addAccountActivity_withText_sendsText() {
        onView(withId(R.id.editAccountName)).perform(typeText("Test Account"),
                closeSoftKeyboard());

        onView(withId(R.id.addAccountButton)).perform(click());

        assertThat(rule.getActivityResult(), hasResultCode(Activity.RESULT_OK));
        assertThat(rule.getActivityResult(), hasResultData(IntentMatchers.hasExtra(
                AddAccountActivity.EXTRA_ACCOUNT_NAME, "Test Account")));
    }
}
