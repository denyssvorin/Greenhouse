package com.example.recycleview.data.datastore.di

import com.example.recycleview.data.datastore.PreferencesManagerImpl
import com.example.recycleview.domain.datastore.PreferencesManager
import dagger.Binds
import dagger.Module

@Module
abstract class PreferencesModule {

    @Binds
    abstract fun providePreferences(impl: PreferencesManagerImpl): PreferencesManager
}