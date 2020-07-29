package com.google.moviestvsentiments.usecase.uiautomator;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;
import com.google.moviestvsentiments.di.DatabaseModule;
import com.google.moviestvsentiments.di.WebModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

@HiltAndroidTest
@UninstallModules({DatabaseModule.class, WebModule.class})
@SdkSuppress(minSdkVersion = 18)
public class UIAutomatorTest {

    private static final String PACKAGE_NAME = "com.google.moviestvsentiments";
    private static final int LAUNCH_TIMEOUT = 5000;

    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);

    private UiDevice device;

    @Before
    public void startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.pressHome();

        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage).isNotNull();
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void chooseCurrentAccount_closeAndReopenApp_goesToHomeScreen()
                throws UiObjectNotFoundException, RemoteException {
        device.findObject(new UiSelector().text("Add Account")).click();
        device.findObject(new UiSelector().text("Account Name")).setText("Local Account");
        device.findObject(new UiSelector().text("ADD ACCOUNT")).click();
        device.findObject(new UiSelector().text("Local Account")).click();

        device.pressRecentApps();
        device.findObject(new UiSelector().className("android.widget.ListView").childSelector(
                new UiSelector().className("android.widget.FrameLayout"))).swipeUp(100);
        Context context = ApplicationProvider.getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);

        assertThat(device.findObject(new UiSelector().text("Home")).exists()).isTrue();
    }
}
