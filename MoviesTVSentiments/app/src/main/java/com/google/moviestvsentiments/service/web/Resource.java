package com.google.moviestvsentiments.service.web;

import com.google.auto.value.AutoValue;
import javax.annotation.Nullable;

/**
 * A container for a value, its loading status and an error message.
 * @param <T> The type of value stored.
 */
@AutoValue
public abstract class Resource<T> {

    /**
     * Represents the loading status of a Resource.
     */
    public enum Status {
        LOADING,
        SUCCESS,
        ERROR
    }

    /**
     * Creates a new Resource with the provided value and a status of LOADING.
     * @param value The value to store in the Resource.
     * @param <T> The type of the value.
     * @return A new Resource with the provided value and a status of LOADING.
     */
    public static <T> Resource<T> loading(T value) {
        return new AutoValue_Resource<>(Status.LOADING, value, null);
    }

    /**
     * Creates a new Resource with the provided value and a status of SUCCESS.
     * @param value The value to store in the Resource.
     * @param <T> The type of the value.
     * @return A new Resource with the provided value and a status of SUCCESS.
     */
    public static <T> Resource<T> success(T value) {
        return new AutoValue_Resource<>(Status.SUCCESS, value, null);
    }

    /**
     * Creates a new Resource with the provided value, error message and a status of ERROR.
     * @param value The value to store in the Resource.
     * @param error The error message to store in the Resource.
     * @param <T> The type of the value.
     * @return A new Resource with the provided value, error message and a status of ERROR.
     */
    public static <T> Resource<T> error(T value, String error) {
        return new AutoValue_Resource<>(Status.ERROR, value, error);
    }

    /**
     * Returns the loading status of the Resource.
     */
    public abstract Status getStatus();

    /**
     * Returns the value of the Resource.
     */
    @Nullable
    public abstract T getValue();

    /**
     * Returns the error message of the Resource.
     */
    @Nullable
    public abstract String getError();
}
