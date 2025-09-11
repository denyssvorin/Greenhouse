package com.example.recycleview.di

import android.content.Context
import com.example.recycleview.data.imagemanager.PlantImageManagerImpl
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.repository.PlantRepositoryImpl
import com.example.recycleview.data.repository.PlantScheduleRepositoryImpl
import com.example.recycleview.data.scheduler.alarm.AlarmSchedulerImpl
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.domain.repository.PlantRepository
import com.example.recycleview.domain.repository.PlantScheduleRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob())

    @Provides
    fun providePlantRepository(plantDao: PlantDao): PlantRepository =
        PlantRepositoryImpl(plantDao)

    @Provides
    fun provideImageConverter(context: Context): PlantImageManager =
        PlantImageManagerImpl(context.applicationContext)

    @Provides
    fun provideAlarmScheduler(context: Context): AlarmScheduler =
        AlarmSchedulerImpl(context.applicationContext)

    @Provides
    fun providePlantScheduleRepository(plantScheduleDao: PlantScheduleDao): PlantScheduleRepository =
        PlantScheduleRepositoryImpl(plantScheduleDao)
}
