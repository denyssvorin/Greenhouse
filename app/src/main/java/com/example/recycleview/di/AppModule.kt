package com.example.recycleview.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.data.imagemanager.PlantImageManagerImpl
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plant.PlantDatabase
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.repository.PlantRepositoryImpl
import com.example.recycleview.data.repository.PlantScheduleRepositoryImpl
import com.example.recycleview.data.scheduler.alarm.AlarmSchedulerImpl
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.datastore.PreferencesManager
import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.domain.repository.PlantRepository
import com.example.recycleview.domain.repository.PlantScheduleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application) =
        Room.databaseBuilder(app, PlantDatabase::class.java, "plant_database")
            .build()

    @Provides
    fun providePlantDao(db: PlantDatabase) = db.plantDao()

    @Provides
    fun providePlantScheduleDao(db: PlantDatabase) = db.plantScheduleDao()

    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManagerImpl(context)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    fun providePlantRepository(plantDao: PlantDao): PlantRepository =
        PlantRepositoryImpl(plantDao)

    @Provides
    fun provideImageConverter(context: Application): PlantImageManager {
        return PlantImageManagerImpl(context)
    }

    @Provides
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmSchedulerImpl(context)

    @Provides
    fun providePlantScheduleRepository(plantScheduleDao: PlantScheduleDao): PlantScheduleRepository =
        PlantScheduleRepositoryImpl(plantScheduleDao)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope