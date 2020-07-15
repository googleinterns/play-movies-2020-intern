package com.google.moviestvsentiments;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import com.google.moviestvsentiments.service.account.AccountRepository;
import javax.inject.Inject;
import dagger.hilt.android.HiltAndroidApp;

/**
 * An application that uses Hilt for dependency injection.
 */
@HiltAndroidApp
public class MoviesTVSentimentsApplication extends Application {

    @Inject
    AccountRepository accountRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                accountRepository.syncPendingAccounts();
            }
        });
    }
}
