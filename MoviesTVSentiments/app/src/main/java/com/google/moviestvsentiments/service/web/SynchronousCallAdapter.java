package com.google.moviestvsentiments.service.web;

import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;

/**
 * A Retrofit adapter that synchronously executes a Call object and converts it into an ApiResponse.
 * @param <T> The type of the Call and ApiResponse objects.
 */
public class SynchronousCallAdapter<T> implements CallAdapter<T, ApiResponse<T>> {

    private final Type responseType;

    private SynchronousCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    /**
     * Creates a new SynchronousCallAdapter with the given response type.
     * @param responseType The value type that the adapter uses when converting the HTTP response
     *                     body to a Java object.
     * @param <T> The type of the Call and ApiResponse objects that will be used with the adapter.
     * @return A new SynchronousCallAdapter with the given response type.
     */
    public static <T> SynchronousCallAdapter<T> create(Type responseType) {
        return new SynchronousCallAdapter<>(responseType);
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public ApiResponse<T> adapt(Call<T> call) {
        try {
            Response<T> response = call.execute();
            return new ApiResponse<>(response);
        } catch (Exception e) {
            return new ApiResponse<>(e);
        }
    }
}
