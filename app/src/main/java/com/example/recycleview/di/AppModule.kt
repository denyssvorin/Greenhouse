package com.example.recycleview.di

import android.app.Application
import androidx.room.Room
import com.example.recycleview.data.PlantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePlantDao(db: PlantDatabase) = db.plantDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


}
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope