package com.example.recycleview

import android.app.Application
import com.example.recycleview.di.alarmSchedulerModule
import com.example.recycleview.di.imageConverterModule
import com.example.recycleview.di.notificationWorkerModule
import com.example.recycleview.di.preferencesManagerModule
import com.example.recycleview.di.realmModule
import com.example.recycleview.di.restartAlarmsWorkerModule
import com.example.recycleview.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class PlantApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        println("Start koin at thread name ${Thread.currentThread().name}")
        println("Start koin at thread id ${Thread.currentThread().id}")

        startKoin {
            androidContext(this@PlantApplication)
            workManagerFactory()
            modules(
                realmModule,
                imageConverterModule,
                preferencesManagerModule,
                alarmSchedulerModule,
                viewModelsModule,
                restartAlarmsWorkerModule,
                notificationWorkerModule
            )
        }
    }
}