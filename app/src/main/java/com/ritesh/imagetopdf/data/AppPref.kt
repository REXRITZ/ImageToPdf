package com.ritesh.imagetopdf.data

import android.content.SharedPreferences
import android.view.View
import javax.inject.Inject

class AppPref @Inject constructor(
    private val preferences: SharedPreferences
) {

    fun setAppTheme(isDarkMode: Boolean) {
        preferences.edit().apply {
            putBoolean("dark_mode", isDarkMode)
            apply()
        }
    }

    val isDarkMode: Boolean
        get() = preferences.getBoolean("dark_mode", false)

    fun setRateDialogVisibility() {
        preferences.edit().apply {
            putInt("rate_visibility", View.GONE)
            apply()
        }
    }

    val rateDialogVisibility: Int
        get() = preferences.getInt("rate_visibility", View.VISIBLE)

    fun setFirstTime(firstTime: Boolean) {
        preferences.edit().apply {
            putBoolean("first_time", firstTime)
            apply()
        }
    }

    val isFirstTime: Boolean
        get() = preferences.getBoolean("first_time", true)

}