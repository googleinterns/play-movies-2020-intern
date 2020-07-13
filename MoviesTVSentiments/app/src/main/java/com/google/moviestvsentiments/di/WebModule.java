package com.google.moviestvsentiments.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.moviestvsentiments.service.web.LiveDataCallAdapterFactory;
import com.google.moviestvsentiments.service.web.WebService;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * A Hilt module that provides web-related dependencies.
 */
@InstallIn(ApplicationComponent.class)
@Module
public class WebModule {

    private static final String API_BASE_URL = "http://10.0.2.2:8080";

    /**
     * Returns the singleton WebService object.
     */
    @Provides
    @Singleton
    public WebService provideWebService() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build();
        return retrofit.create(WebService.class);
    }
}
