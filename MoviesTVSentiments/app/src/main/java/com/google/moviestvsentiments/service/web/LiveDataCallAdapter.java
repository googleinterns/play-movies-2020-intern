package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A Retrofit adapter that converts a Call object into an ApiResponse wrapped by a LiveData object.
 * @param <T> The type of the Call and ApiResponse objects.
 */
public class LiveDataCallAdapter<T> implements CallAdapter<T, LiveData<ApiResponse<T>>> {

    /**
     * A LiveData class that posts ApiResponse values when a Retrofit call completes.
     * @param <R> The type of the Call and ApiResponse objects.
     */
    private static class LiveDataApiResponse<R> extends LiveData<ApiResponse<R>> {

        private AtomicBoolean started = new AtomicBoolean(false);
        private Call<R> call;

        private LiveDataApiResponse(Call<R> call) {
            this.call = call;
        }

        @Override
        protected void onActive() {
            super.onActive();
            if (started.compareAndSet(false, true)) {
                call.enqueue(new Callback<R>() {
                    @Override
                    public void onResponse(Call<R> call, Response<R> response) {
                        postValue(new ApiResponse<>(response));
                    }

                    @Override
                    public void onFailure(Call<R> call, Throwable throwable) {
                        postValue(new ApiResponse<R>(throwable));
                    }
                });
            }
        }
    }

    private final Type responseType;

    private LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    /**
     * Creates a new LiveDataCallAdapater with the given response type.
     * @param responseType The value type that the adapter uses when converting the HTTP response
     *                     body to a JSON object.
     * @param <T> The type of the Call and ApiResponse objects that will be used with the adapter.
     * @return A new LiveDataCallAdapter with the given response type.
     */
    public static <T> LiveDataCallAdapter<T> create(Type responseType) {
        return new LiveDataCallAdapter<>(responseType);
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<T>> adapt(Call<T> call) {
        return new LiveDataApiResponse<>(call);
    }
}
