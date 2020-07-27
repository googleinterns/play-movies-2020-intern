package com.google.moviestvsentiments;

import android.app.Application;
import android.widget.Toast;

/**
 * An Application that provides methods for displaying toasts.
 */
public class ToastApplication extends Application {

    /**
     * Displays a toast notifying the user that the app is in offline mode.
     */
    public void displayOfflineToast() {
        Toast.makeText(getApplicationContext(), R.string.offlineToast, Toast.LENGTH_LONG).show();
    }
}
