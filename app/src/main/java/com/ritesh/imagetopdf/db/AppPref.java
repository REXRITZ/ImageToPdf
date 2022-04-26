package com.ritesh.imagetopdf.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.Locale;

public class AppPref {

    private static volatile AppPref instance;


    private final SharedPreferences sharedPreferences;
    private AppPref(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static AppPref getInstance(Context context) {
        if (instance == null) {
            synchronized (AppPref.class) {
                if (instance == null) {
                    instance = new AppPref(context);
                }
            }
        }
        return instance;
    }

    public void setAppTheme(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode",isDarkMode);
        editor.apply();
    }

    public boolean isDarkMode() {
        return sharedPreferences.getBoolean("dark_mode", false);
    }

    public void setRateDialogVisibility() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rate_visibility", View.GONE);
        editor.apply();
    }

    public int getRateDialogVisibility() {
        return sharedPreferences.getInt("rate_visibility", View.VISIBLE);
    }

}
