package com.example.recycleview.di

import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plant.RealmPlantDaoImpl
import com.example.recycleview.data.realm.plantschedule.PlantScheduleDao
import com.example.recycleview.data.realm.plantschedule.RealmPlantScheduleDaoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class RealmDaoModule {
    @Binds
    abstract fun bindRealmPlantDao(impl: RealmPlantDaoImpl): PlantDao

    @Binds
    abstract fun bindRealmPlantScheduleDao(impl: RealmPlantScheduleDaoImpl): PlantScheduleDao
}

