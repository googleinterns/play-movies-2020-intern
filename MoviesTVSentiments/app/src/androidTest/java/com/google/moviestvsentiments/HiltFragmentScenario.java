package com.google.moviestvsentiments;

import android.content.Intent;
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
     * @param <F> The type of the fragment.
     */
    public static <F extends Fragment> void launchHiltFragment(Class<F> fragmentClass) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                HiltTestActivity.class);
        launchHiltFragmentWithIntent(fragmentClass, intent);
    }

    /**
     * Launches a fragment using the provided intent. This method allows for launching the fragment
     * using activities other than HiltTestActivity or with intent extras.
     * @param fragmentClass The class of the fragment to instantiate.
     * @param intent The intent to use when launching the hosting activity.
     * @param <F> The type of the fragment.
     */
    public static <F extends Fragment> void launchHiltFragmentWithIntent(Class<F> fragmentClass,
                                                                         Intent intent) {
        ActivityScenario<HiltTestActivity> activityScenario = ActivityScenario.launch(intent);
        activityScenario.onActivity(activity -> {
            Fragment fragment = activity.getSupportFragmentManager().getFragmentFactory()
                    .instantiate(fragmentClass.getClassLoader(), fragmentClass.getName());
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, null)
                    .commitNow();
        });
    }
}
