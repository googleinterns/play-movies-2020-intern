package com.google.moviestvsentiments.assertions;

import static com.google.common.truth.Truth.assertThat;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

/**
 * A ViewAssertion that checks the number of items in a RecyclerView.
 */
public class RecyclerViewItemCountAssertion implements ViewAssertion {

    private final int expectedCount;
    private int timeout;

    private RecyclerViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
        timeout = 0;
    }

    /**
     * Returns a new RecyclerViewItemCountAssertion that checks for the given number of items.
     * @param expectedCount The expected number of items.
     * @return A new RecyclerViewItemCountAssertion that checks for the given number of items.
     */
    public static RecyclerViewItemCountAssertion withItemCount(int expectedCount) {
        return new RecyclerViewItemCountAssertion(expectedCount);
    }

    /**
     * Sets the timeout of this RecyclerViewItemCountAssertion to the provided value and returns
     * the RecyclerViewItemCountAssertion for further method chaining.
     * @param millis The timeout in milliseconds.
     * @return The invoked RecyclerViewItemCountAssertion
     */
    public RecyclerViewItemCountAssertion withTimeout(int millis) {
        timeout = millis;
        return this;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        int timePolled = 0;
        while (timePolled < timeout && adapter.getItemCount() != expectedCount) {
            try {
                Thread.sleep(10);
                timePolled += 10;
            } catch (InterruptedException e) {
                break;
            }
        }
        assertThat(adapter.getItemCount()).isEqualTo(expectedCount);
    }
}
