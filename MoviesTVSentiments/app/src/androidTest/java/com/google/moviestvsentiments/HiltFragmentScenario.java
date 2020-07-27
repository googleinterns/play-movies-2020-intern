package com.google.moviestvsentiments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

/**
 * HiltFragmentScenario provides an API to launch fragments that use Hilt for dependency injection.
 */
public class HiltFragmentScenario {

    /**
     * Launches a fragment hosted by an empty HiltTestActivity.
     * @param fragmentClass The class of the fragment to instantiate.
     * @param args The arguments to pass to the fragment.
     * @param <F> The type of the fragment.
     * @return The ActivityScenario used to launch the HiltTestActivity.
     */
    public static <F extends Fragment> ActivityScenario launchHiltFragment(Class<F> fragmentClass,
                                                                           Bundle args) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                HiltTestActivity.class);
        return launchHiltFragmentWithIntent(fragmentClass, intent, args);
    }

    /**
     * Launches a fragment using the provided intent. This method allows for launching the fragment
     * using activities other than HiltTestActivity or with intent extras.
     * @param fragmentClass The class of the fragment to instantiate.
     * @param intent The intent to use when launching the hosting activity.
     * @param args The arguments to pass to the fragment.
     * @param <F> The type of the fragment.
     * @return The ActivityScenario used to launch the provided intent.
     */
    public static <F extends Fragment> ActivityScenario launchHiltFragmentWithIntent(
                Class<F> fragmentClass, Intent intent, Bundle args) {
        ActivityScenario<HiltTestActivity> activityScenario = ActivityScenario.launch(intent);
        activityScenario.onActivity(activity -> {
            Fragment fragment = activity.getSupportFragmentManager().getFragmentFactory()
                    .instantiate(fragmentClass.getClassLoader(), fragmentClass.getName());
            fragment.setArguments(args);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, null)
                    .commitNow();
        });
        return activityScenario;
    }
}
