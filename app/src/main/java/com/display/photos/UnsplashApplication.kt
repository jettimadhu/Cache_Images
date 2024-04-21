package com.display.photos

import android.app.Application
import com.display.photos.di.allModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class UnsplashApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    /**
     * To initialize the Koin
     */
    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@UnsplashApplication)
            modules(allModules)
        }
    }
}