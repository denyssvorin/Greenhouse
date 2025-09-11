package com.example.recycleview

import android.app.Application
import androidx.work.Configuration
import com.example.recycleview.di.AppComponent
import com.example.recycleview.di.DaggerAppComponent

class PlantApplication : Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(appComponent.workerFactory())
            .build()

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }
}