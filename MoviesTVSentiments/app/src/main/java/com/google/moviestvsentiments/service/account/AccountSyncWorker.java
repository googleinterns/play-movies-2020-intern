package com.google.moviestvsentiments.service.account;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.work.WorkerInject;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker that syncs pending Accounts with the server.
 */
public class AccountSyncWorker extends Worker {

    private static final int MAX_RUN_ATTEMPTS = 3;

    private final AccountRepository repository;

    @WorkerInject
    public AccountSyncWorker(@Assisted @NonNull Context context,
                             @Assisted @NonNull WorkerParameters parameters,
                             AccountRepository repository) {
        super(context, parameters);
        this.repository = repository;
    }

    @Override
    public Result doWork() {
        if (getRunAttemptCount() > MAX_RUN_ATTEMPTS) {
            return Result.failure();
        }
        if (repository.syncPendingAccounts()) {
            return Result.success();
        }
        return Result.retry();
    }
}
