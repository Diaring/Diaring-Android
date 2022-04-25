package com.oss.diaring

import android.app.Application
import android.content.Context
import timber.log.Timber

class DiaringApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        setUpTimber()
    }

    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }
    private fun setUpTimber() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}