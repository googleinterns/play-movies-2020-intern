package com.google.moviestvsentiments.service.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * A Retrofit CallAdapter factory that creates SynchronousCallAdapters.
 */
public class SynchronousCallAdapterFactory extends CallAdapter.Factory {

    @Nullable
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != ApiResponse.class) {
            return null;
        }

        if (! (returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Resource must be parameterized");
        }

        Type bodyType = getParameterUpperBound(0, (ParameterizedType) returnType);
        return SynchronousCallAdapter.create(bodyType);
    }
}
