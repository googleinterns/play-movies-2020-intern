package com.google.moviestvsentiments;

import android.app.Application;
import android.widget.Toast;
import androidx.annotation.VisibleForTesting;

/**
 * An Application that provides methods for displaying toasts.
 */
public class ToastApplication extends Application {

    private boolean toastDisplayed;

    /**
     * Displays a toast notifying the user that the app is in offline mode.
     */
    public void displayOfflineToast() {
        if (!toastDisplayed) {
            toastDisplayed = true;
            Toast.makeText(getApplicationContext(), R.string.offlineToast, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Resets the toastDisplayed flag to false so that tests of the toast can make sure it is
     * displayed.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void resetToastDisplayed() {
        toastDisplayed = false;
    }
}
