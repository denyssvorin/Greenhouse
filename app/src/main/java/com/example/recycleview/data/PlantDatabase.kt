package com.example.recycleview.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Plant::class], version = 1)
abstract class PlantDatabase: RoomDatabase() {
    abstract fun plantDao(): PlantDao
}