package com.google.moviestvsentiments.service.database;

import android.os.Process;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * An Executor that executes all queries on background threads.
 */
public class AsyncDatabaseExecutor implements Executor {

    private final ExecutorService executor;

    /**
     * Creates an AsyncDatabaseExecutor object.
     */
    private AsyncDatabaseExecutor() {
        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
                        r.run();
                    }
                });
                return thread;
            }
        });
    }

    /**
     * Returns a new AsyncDatabaseExecutor.
     */
    public static AsyncDatabaseExecutor create() {
        return new AsyncDatabaseExecutor();
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
