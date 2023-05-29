package com.ritesh.imagetopdf

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.ritesh.imagetopdf.data.AppPref
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(){

    @Inject
    lateinit var appPref: AppPref

    override fun onCreate() {
        super.onCreate()

        if (appPref.isDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}