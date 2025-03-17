package com.example.recycleview.di

import android.util.Log
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.data.imagemanager.PlantImageManagerImpl
import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plant.RealmPlantDaoImpl
import com.example.recycleview.data.realm.plantschedule.PlantScheduleDao
import com.example.recycleview.data.realm.plantschedule.RealmPlantScheduleDaoImpl
import com.example.recycleview.data.scheduler.alarm.AlarmSchedulerImpl
import com.example.recycleview.data.scheduler.alarm.restart.RestartAlarmsWorker
import com.example.recycleview.data.scheduler.notification.NotificationWorker
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.presentation.details.DetailsViewModel
import com.example.recycleview.presentation.edit.EditPlantViewModel
import com.example.recycleview.presentation.home.HomeViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val realmModule = module(createdAtStart = true) {
    single<Realm> {
        Realm.init(androidContext())

        Log.i("realmModule", "realm create at thread name ${Thread.currentThread().name}")
        Log.i("realmModule", "realm create at thread id ${Thread.currentThread().id}")

        val realmConfig = RealmConfiguration.Builder()
            .name("plant.realm")
            .schemaVersion(1)
            .build()

        Realm.setDefaultConfiguration(realmConfig)
        Realm.getDefaultInstance()
    }

    single<PlantDao> { RealmPlantDaoImpl(get()) }
    single<PlantScheduleDao> { RealmPlantScheduleDaoImpl(get()) }
}

val imageConverterModule = module {
    factory<PlantImageManager> { PlantImageManagerImpl(get()) }
}

val preferencesManagerModule = module {
    singleOf(::PreferencesManagerImpl) bind PreferencesManager::class
}

val alarmSchedulerModule = module {
    single<AlarmScheduler> { AlarmSchedulerImpl(get()) }
}

val viewModelsModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::EditPlantViewModel)
    viewModelOf(::DetailsViewModel)
}

val restartAlarmsWorkerModule = module {
    workerOf(::RestartAlarmsWorker)
}

val notificationWorkerModule = module {
    workerOf(::NotificationWorker)
}