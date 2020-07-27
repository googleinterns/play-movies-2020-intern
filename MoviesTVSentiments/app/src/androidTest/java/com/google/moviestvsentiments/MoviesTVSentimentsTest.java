package com.google.moviestvsentiments;

import dagger.hilt.android.testing.CustomTestApplication;

/**
 * A custom test application that allows toasts to be displayed in Hilt Espresso tests.
 */
@CustomTestApplication(ToastApplication.class)
public interface MoviesTVSentimentsTest { }
