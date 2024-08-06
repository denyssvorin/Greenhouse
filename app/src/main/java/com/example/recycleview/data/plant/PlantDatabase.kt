package com.example.recycleview.data.plant

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.data.plantschedule.PlantScheduleDao

@Database(entities = [PlantEntity::class, PlantScheduleEntity::class], version = 1)
abstract class PlantDatabase: RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun plantScheduleDao(): PlantScheduleDao
}