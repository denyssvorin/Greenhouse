package com.example.recycleview.data.plant

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recycleview.data.plantschedule.PlantWateringScheduleEntity
import com.example.recycleview.data.plantschedule.PlantScheduleDao

@Database(entities = [PlantEntity::class, PlantWateringScheduleEntity::class], version = 1)
abstract class PlantDatabase: RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun plantScheduleDao(): PlantScheduleDao
}