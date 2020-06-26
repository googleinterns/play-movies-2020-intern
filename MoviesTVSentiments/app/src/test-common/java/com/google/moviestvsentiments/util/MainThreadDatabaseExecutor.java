package com.google.moviestvsentiments.util;

import java.util.concurrent.Executor;

/**
 * An Executor that executes all queries on the main thread for use in unit tests.
 */
public class MainThreadDatabaseExecutor implements Executor {

    /**
     * Runs the given Room database queries on the main thread.
     * @param command A runnable that performs the Room database queries.
     */
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
