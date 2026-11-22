package com.nutriguard.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * Application class for NutriGuard.
 * Enforces crisp, bright, high-contrast light theme for optimal readability of nutrition data.
 */
public class NutriGuardApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enforce bright, crisp theme (avoids dark gray washed-out screens on devices with system dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
