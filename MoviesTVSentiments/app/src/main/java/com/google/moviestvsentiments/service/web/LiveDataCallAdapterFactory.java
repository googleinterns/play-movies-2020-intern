package com.google.moviestvsentiments.service.web;

import androidx.lifecycle.LiveData;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * A Retrofit CallAdapter factory that creates LiveDataCallAdapters.
 */
public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }

        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType != ApiResponse.class) {
            throw new IllegalArgumentException("Type must be an ApiResponse");
        }

        if (! (observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Resource must be parameterized");
        }

        Type bodyType = getParameterUpperBound(0, (ParameterizedType) observableType);
        return LiveDataCallAdapter.create(bodyType);
    }
}
