package com.google.moviestvsentiments.service.web;

/**
 * A container for a value, its loading status and an error message.
 * @param <T> The type of value stored.
 */
public class Resource<T> {

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
        return new Resource<>(Status.LOADING, value, null);
    }

    /**
     * Creates a new Resource with the provided value and a status of SUCCESS.
     * @param value The value to store in the Resource.
     * @param <T> The type of the value.
     * @return A new Resource with the provided value and a status of SUCCESS.
     */
    public static <T> Resource<T> success(T value) {
        return new Resource<>(Status.SUCCESS, value, null);
    }

    /**
     * Creates a new Resource with the provided value, error message and a status of ERROR.
     * @param value The value to store in the Resource.
     * @param error The error message to store in the Resource.
     * @param <T> The type of the value.
     * @return A new Resource with the provided value, error message and a status of ERROR.
     */
    public static <T> Resource<T> error(T value, String error) {
        return new Resource<>(Status.ERROR, value, error);
    }

    private final Status status;
    private final T value;
    private final String error;

    private Resource(Status status, T value, String error) {
        this.status = status;
        this.value = value;
        this.error = error;
    }

    /**
     * Returns the loading status of the Resource.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the value of the Resource.
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the error message of the Resource.
     */
    public String getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource<?> resource = (Resource<?>) o;

        if (status != resource.status) return false;
        if (value != null ? !value.equals(resource.value) : resource.value != null) return false;
        return error != null ? error.equals(resource.error) : resource.error == null;
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }
}
