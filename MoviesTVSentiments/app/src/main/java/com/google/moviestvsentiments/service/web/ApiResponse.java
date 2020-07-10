package com.google.moviestvsentiments.service.web;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A container that stores Response objects returned by Retrofit.
 * @param <T> The type of the Response body.
 */
public class ApiResponse<T> {

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to make network request.";

    private final Response<T> response;

    /**
     * Constructs an ApiResponse with a 500 status, a null body and the provided error message.
     * This constructor should be used only when Retrofit fails to make a network request.
     * @param error The error to take the error message from.
     */
    public ApiResponse(Throwable error) {
        response = Response.error(500, ResponseBody.create(null, error.getMessage()));
    }

    /**
     * Constructs an ApiResponse with the given response.
     * @param response The response from the server.
     */
    public ApiResponse(Response<T> response) {
        this.response = response;
    }

    /**
     * Returns true if the ApiResponse has a 2xx status code.
     */
    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    /**
     * Returns the body of the ApiResponse, or null if the response was unsuccessful.
     */
    public T getBody() {
        return response.body();
    }

    /**
     * Returns the error message from an unsuccessful response, or a default error if the error
     * message cannot be extracted from the response body.
     */
    public String getError() {
        try {
            return response.errorBody().string();
        } catch (IOException e) {
            return DEFAULT_ERROR_MESSAGE;
        }
    }
}
