package com.example.recycleview.data.plantschedule

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.recycleview.data.plant.PlantEntity

@Entity(
    tableName = "plant_schedule_table",
    foreignKeys = [
        ForeignKey(
            entity = PlantEntity::class,
            parentColumns = ["plantId"],
            childColumns = ["plantId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PlantWateringScheduleEntity(
    @PrimaryKey(autoGenerate = false) val scheduleId: String,
    val plantId: Int = 0,
    val notificationMessage: String = "",
    val time: String = "0",
    val daysInterval: Int = 0,
    val firstTriggerDate: String = "0"
)

