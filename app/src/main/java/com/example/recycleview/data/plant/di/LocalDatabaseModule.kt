package com.example.recycleview.data.plant.di

import android.content.Context
import androidx.room.Room
import com.example.recycleview.data.plant.PlantDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context.applicationContext, PlantDatabase::class.java, "plant_database")
            .build()

    @Provides
    fun providePlantDao(db: PlantDatabase) = db.plantDao()

    @Provides
    fun providePlantScheduleDao(db: PlantDatabase) = db.plantScheduleDao()
}