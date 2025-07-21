package com.example.recycleview.data.plant

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_table")
data class PlantEntity(
    @PrimaryKey(autoGenerate = false) val plantId: String = "",
    val plantImagePath: String?,
    val plantName: String = "plant_name",
    val plantDescription: String = "plant_description"
)