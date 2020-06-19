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

    private RecyclerViewItemCountAssertion(int expectedCount) {
        this.expectedCount = expectedCount;
    }

    /**
     * Returns a new RecyclerViewItemCountAssertion that checks for the given number of items.
     * @param expectedCount The expected number of items.
     * @return A new RecyclerViewItemCountAssertion that checks for the given number of items.
     */
    public static RecyclerViewItemCountAssertion withItemCount(int expectedCount) {
        return new RecyclerViewItemCountAssertion(expectedCount);
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        if (noViewFoundException != null) {
            throw noViewFoundException;
        }
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        assertThat(adapter.getItemCount()).isEqualTo(expectedCount);
    }
}
