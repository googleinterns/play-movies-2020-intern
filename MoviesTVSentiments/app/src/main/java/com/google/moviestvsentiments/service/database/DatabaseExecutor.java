package com.google.moviestvsentiments.service.database;

/**
 * An interface that wraps execution of long-running database queries, such as writes.
 */
public interface DatabaseExecutor {

    /**
     * Runs the given Room database queries.
     * @param command A runnable that performs the Room database queries.
     */
    void execute(Runnable command);
}
