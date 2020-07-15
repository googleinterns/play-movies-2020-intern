package com.google.moviestvsentiments.service.assetSentiment;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.work.WorkerInject;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * A Worker that syncs pending UserSentiments with the server.
 */
public class UserSentimentSyncWorker extends Worker {

    private final AssetSentimentRepository repository;

    @WorkerInject
    public UserSentimentSyncWorker(@Assisted @NonNull Context context,
                                   @Assisted @NonNull WorkerParameters parameters,
                                   AssetSentimentRepository repository) {
        super(context, parameters);
        this.repository = repository;
    }

    @Override
    public Result doWork() {
        if (repository.syncPendingSentiments()) {
            return Result.success();
        }
        return Result.retry();
    }
}
