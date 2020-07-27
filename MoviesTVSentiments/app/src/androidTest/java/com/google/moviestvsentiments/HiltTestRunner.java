package com.google.moviestvsentiments;

import android.app.Application;
import android.content.Context;
import androidx.test.runner.AndroidJUnitRunner;

/**
 * A test runner that uses Hilt for dependency injection.
 */
public class HiltTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, MoviesTVSentimentsTest_Application.class.getName(), context);
    }
}
