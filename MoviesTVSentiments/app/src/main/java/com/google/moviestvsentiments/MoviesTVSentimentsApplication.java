package com.google.moviestvsentiments;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import com.google.moviestvsentiments.service.account.AccountSyncWorker;
import com.google.moviestvsentiments.service.assetSentiment.UserSentimentSyncWorker;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import dagger.hilt.android.HiltAndroidApp;

/**
 * An application that uses Hilt for dependency injection.
 */
@HiltAndroidApp
public class MoviesTVSentimentsApplication extends ToastApplication implements Configuration.Provider {

    @Inject
    HiltWorkerFactory workerFactory;

    private boolean toastDisplayed;

    @Override
    public void onCreate() {
        super.onCreate();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        WorkRequest syncAccountsWorkRequest =
                new PeriodicWorkRequest.Builder(AccountSyncWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();
        WorkRequest syncSentimentsWorkRequest =
                new PeriodicWorkRequest.Builder(UserSentimentSyncWorker.class, 1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(syncAccountsWorkRequest);
        WorkManager.getInstance(getApplicationContext()).enqueue(syncSentimentsWorkRequest);
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setWorkerFactory(workerFactory).build();
    }

    /**
     * Displays a toast notifying the user that the app is in offline mode only if the toast
     * has not already been displayed. This method should only be called on the main thread.
     */
    @Override
    public void displayOfflineToast() {
        if (!toastDisplayed) {
            toastDisplayed = true;
            super.displayOfflineToast();
        }
    }
}
