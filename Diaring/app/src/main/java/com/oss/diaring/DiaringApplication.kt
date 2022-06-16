package com.oss.diaring

import android.app.Application
import com.oss.diaring.data.sharedpreference.SharedPrefManagerImpl
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DiaringApplication : Application() {
    lateinit var sharedPreferences: SharedPrefManagerImpl

    override fun onCreate() {
        super.onCreate()

        sharedPreferences = SharedPrefManagerImpl(applicationContext)

        setUpTimber()
    }

    private fun setUpTimber() {
        Timber.plant(Timber.DebugTree())
    }
}