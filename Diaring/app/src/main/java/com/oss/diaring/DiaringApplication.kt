package com.oss.diaring

import android.app.Application
import android.content.Context
import com.oss.diaring.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class DiaringApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this

        setUpKoinInjection()
        setUpTimber()
    }

    override fun onTerminate() {
        super.onTerminate()
        appContext = null
    }
    private fun setUpTimber() {
        Timber.plant(Timber.DebugTree())
    }

    private fun setUpKoinInjection() {
        startKoin {
            androidContext(this@DiaringApplication)
            modules(appModule)
        }
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}