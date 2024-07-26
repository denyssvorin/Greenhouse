package com.example.recycleview.di

import com.example.recycleview.data.alarm.AlarmScheduler
import com.example.recycleview.data.alarm.AlarmSchedulerImpl
import com.example.recycleview.data.alarm.restartalarm.RestartAlarmsWorker
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.data.imageconverter.PlantImageConverter
import com.example.recycleview.data.imageconverter.PlantImageConverterImpl
import com.example.recycleview.data.notification.NotificationWorker
import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plant.RealmPlantDaoImpl
import com.example.recycleview.data.realm.plantschedule.PlantScheduleDao
import com.example.recycleview.data.realm.plantschedule.RealmPlantScheduleDaoImpl
import com.example.recycleview.ui.details.DetailsViewModel
import com.example.recycleview.ui.edit.EditPlantViewModel
import com.example.recycleview.ui.home.HomeViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideRealm(@ApplicationContext context: Context): Realm {
//        Realm.init(context)
//
//        val realmConfig = RealmConfiguration.Builder()
//            .name("plant.realm")
//            .schemaVersion(1)
//            .build()
//
//        Realm.setDefaultConfiguration(realmConfig)
//        return Realm.getDefaultInstance()
//    }
//
//    @Provides
//    fun provideImageConverter(context: Application): PlantImageConverter {
//        return PlantImageConverterImpl(context)
//    }
//
//    @Provides
//    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
//        PreferencesManagerImpl(context)
//
//    @Provides
//    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
//        AlarmSchedulerImpl(context)
//}

val realmModule = module(createdAtStart = true) {
    single<Realm> {
//        runBlocking(Dispatchers.Main) {
            Realm.init(androidContext())

            println("realm create at thread name ${Thread.currentThread().name}")
            println("realm create at thread id ${Thread.currentThread().id}")

            val realmConfig = RealmConfiguration.Builder()
                .name("plant.realm")
                .schemaVersion(1)
                .build()

            Realm.setDefaultConfiguration(realmConfig)
            Realm.getDefaultInstance()
//        }
    }

    single<PlantDao> { RealmPlantDaoImpl(get()) }
    single<PlantScheduleDao> { RealmPlantScheduleDaoImpl(get()) }
}

val imageConverterModule = module {
    factory<PlantImageConverter> { PlantImageConverterImpl(get()) }
}

val preferencesManagerModule = module {
//            single<PreferencesManager> { PreferencesManagerImpl(get()) }
    singleOf(::PreferencesManagerImpl) bind PreferencesManager::class
//            singleOf(::PreferencesManagerImpl) { bind<PreferencesManager>() }
}

val alarmSchedulerModule = module {
    single<AlarmScheduler> { AlarmSchedulerImpl(get()) }
}

val viewModelsModule = module {
//    viewModel { HomeViewModel(get(), get()) }
//    viewModel { EditPlantViewModel(get(), get()) }
//    viewModel { DetailsViewModel(get(), get(), get()) }
    viewModelOf(::HomeViewModel)
    viewModelOf(::EditPlantViewModel)
    viewModelOf(::DetailsViewModel)
}

val restartAlarmsWorkerModule = module {
//    worker { (context: Context, parameters: WorkerParameters) ->
//        RestartAlarmsWorker(
//            get(),
//            get(),
//            get(),
//            context,
//            parameters
//        )
//    }
    workerOf(::RestartAlarmsWorker)
}

val notificationWorkerModule = module {
//    worker { (context: Context, parameters: WorkerParameters) ->
//        NotificationWorker(
//            get(),
//            context,
//            parameters
//        )
//    }
    workerOf(::NotificationWorker)
}