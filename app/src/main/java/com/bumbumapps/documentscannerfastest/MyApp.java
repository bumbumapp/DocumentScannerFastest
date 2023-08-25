package com.bumbumapps.documentscannerfastest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

//import com.onesignal.OneSignal;
import com.bumbumapps.documentscannerfastest.main_utils.Constant;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import me.pqpo.smartcropperlib.SmartCropper;

public class MyApp extends Application {
    private static MyApp instance;
    private boolean showAds = true;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        SmartCropper.buildImageDetector(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        initTheme();
    }

    private void initTheme() {
        int savedTheme = getSavedTheme();
        if (savedTheme == Constant.THEME_LIGHT) {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO,Constant.THEME_LIGHT);
        } else if (savedTheme == Constant.THEME_DARK) {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES,Constant.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constant.KEY_THEME, Constant.THEME_UNDEFINED);
    }


    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constant.KEY_THEME, theme).apply();
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }
    public static MyApp getInstance() {
        return instance;
    }

    public boolean isShowAds() {
        return showAds;
    }

    public void setShowAds(boolean showAds) {
        this.showAds = showAds;
    }
}
