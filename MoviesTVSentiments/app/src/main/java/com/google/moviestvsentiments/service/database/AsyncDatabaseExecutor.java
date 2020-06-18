package com.google.moviestvsentiments.service.database;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A DatabaseExecutor that executes all queries on background threads.
 */
public class AsyncDatabaseExecutor implements DatabaseExecutor {

    private final ExecutorService executor;

    /**
     * Creates an AsyncDatabaseExecutor object.
     * @param numberOfThreads The number of threads to use when performing writes to the database.
     */
    private AsyncDatabaseExecutor(int numberOfThreads) {
        executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Returns a new AsyncDatabaseExecutor.
     * @param numberOfThreads The number of threads to use when performing writes to the database.
     * @return A new AsyncDatabaseExecutor.
     */
    public static AsyncDatabaseExecutor create(int numberOfThreads) {
        if (numberOfThreads < 1) {
            throw new IllegalArgumentException("numberOfThreads must be greater than 0: " +
                    numberOfThreads);
        }
        return new AsyncDatabaseExecutor(numberOfThreads);
    }

    /**
     * Runs the given Room database queries on a background thread.
     * @param command A runnable that performs the Room database queries.
     */
    @Override
    public void execute(Runnable command) {
        executor.execute(command);
    }
}
