package com.example.myapplication

import android.app.Application
import com.example.myapplication.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(searchModule)
        }
    }
}
