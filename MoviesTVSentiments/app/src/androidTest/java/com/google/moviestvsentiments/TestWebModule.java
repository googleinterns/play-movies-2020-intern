package com.google.moviestvsentiments;

import com.google.moviestvsentiments.service.web.WebService;
import com.google.moviestvsentiments.webTestUtil.TestWebService;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

/**
 * A Hilt module that provides a WebService for use in tests.
 */
@Module
@InstallIn(ApplicationComponent.class)
public class TestWebModule {

    /**
     * Returns the singleton WebService object.
     */
    @Provides
    @Singleton
    public WebService provideWebService() {
        return new TestWebService();
    }
}
