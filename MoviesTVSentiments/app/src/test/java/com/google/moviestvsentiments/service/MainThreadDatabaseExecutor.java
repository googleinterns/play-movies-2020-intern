package com.google.moviestvsentiments.service;

import com.google.moviestvsentiments.service.database.DatabaseExecutor;

/**
 * A DatabaseExecutor that executes all queries on the main thread for use in unit tests.
 */
public class MainThreadDatabaseExecutor implements DatabaseExecutor {

    /**
     * Runs the given Room database queries on the main thread.
     * @param command A runnable that performs the Room database queries.
     */
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
