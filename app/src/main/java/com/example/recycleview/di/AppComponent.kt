package com.example.recycleview.di

import android.content.Context
import com.example.recycleview.data.datastore.di.PreferencesModule
import com.example.recycleview.data.plant.di.LocalDatabaseModule
import com.example.recycleview.di.worker.DaggerWorkerFactory
import com.example.recycleview.di.worker.WorkerModule
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.datastore.PreferencesManager
import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.domain.repository.PlantRepository
import com.example.recycleview.domain.repository.PlantScheduleRepository
import com.example.recycleview.presentation.activity.MainActivity
import com.example.recycleview.presentation.details.di.DetailsModule
import com.example.recycleview.presentation.edit.di.EditModule
import com.example.recycleview.presentation.home.di.HomeModule
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        WorkerModule::class,
        ViewModelBuilderModule::class,
        LocalDatabaseModule::class,
        PreferencesModule::class,
        AppModule::class,
        DetailsModule::class,
        HomeModule::class,
        EditModule::class
    ]
)
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(activity: MainActivity)
    fun workerFactory(): DaggerWorkerFactory

    fun preferencesManager(): PreferencesManager
    fun alarmScheduler(): AlarmScheduler
    fun plantRepository(): PlantRepository
    fun plantScheduleRepository(): PlantScheduleRepository
    fun imageConverter(): PlantImageManager
    fun applicationScope(): CoroutineScope
}