package com.example.recycleview.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.data.plant.PlantDatabase
import com.example.recycleview.data.repo.PlantRepository
import com.example.recycleview.data.repo.PlantRepositoryImpl
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.alarm.AlarmSchedulerImpl
import com.example.recycleview.domain.imageconverter.PlantImageConverter
import com.example.recycleview.domain.imageconverter.PlantImageConverterImpl
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
    fun provideRepo(db: PlantDatabase): PlantRepository {
        return PlantRepositoryImpl(db)
    }

    @Provides
    fun provideImageConverter(context: Application): PlantImageConverter {
        return PlantImageConverterImpl(context)
    }

    @Provides
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmSchedulerImpl(context)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope