package com.google.moviestvsentiments.service.liveData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test utility functions related to LiveData.
 */
public class LiveDataTestUtil {

    /**
     * Gets the value from a LiveData object. If the LiveData value is not set within 1 second, a
     * LiveDataTestException is thrown.
     * @param liveData The LiveData object to get the value from.
     * @param <T> The type of value stored in the LiveData object.
     * @return The value contained within a LiveData object.
     */
    public static <T> T getValue(LiveData<T> liveData) {
        final Object[] value = {null};
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                value[0] = t;
                latch.countDown();
            }
        };
        liveData.observeForever(observer);

        try {
            if (!latch.await(1, TimeUnit.SECONDS)) {
                throw new LiveDataTestException("Value was not set within 1 second");
            }
            return (T)value[0];
        } catch (InterruptedException e) {
            throw new LiveDataTestException("Failed to wait for LiveData value to be set", e);
        }
    }

    /**
     * An error that occurs when using LiveData in test methods.
     */
    private static class LiveDataTestException extends RuntimeException {
        private LiveDataTestException(String message) {
            this(message, null);
        }

        private LiveDataTestException(String message, Throwable t) {
            super(message, t);
        }
    }
}
