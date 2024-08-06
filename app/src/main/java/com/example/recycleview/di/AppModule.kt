package com.example.recycleview.di

import android.app.Application
import android.content.Context
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.alarm.AlarmSchedulerImpl
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.domain.imageconverter.PlantImageConverter
import com.example.recycleview.domain.imageconverter.PlantImageConverterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRealm(@ApplicationContext context: Context): Realm {
        Realm.init(context)

        val realmConfig = RealmConfiguration.Builder()
            .name("plant.realm")
            .schemaVersion(1)
            .build()

        Realm.setDefaultConfiguration(realmConfig)
        return Realm.getDefaultInstance()
    }

    @Provides
    fun provideImageConverter(context: Application): PlantImageConverter {
        return PlantImageConverterImpl(context)
    }

    @Provides
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManagerImpl(context)

    @Provides
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmSchedulerImpl(context)
}