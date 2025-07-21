package com.example.recycleview.data.repository.mappers

import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.models.PlantSchedule

fun PlantSchedule.toPlantScheduleEntity(
    scheduleId: String,
    plantId: String
): PlantScheduleEntity {
    return PlantScheduleEntity(
        scheduleId = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = time,
        daysInterval = daysInterval,
        firstTriggerDate = firstTriggerDate
    )
}

fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        plantId = this.id,
        plantImagePath = this.imagePath,
        plantName = this.name,
        plantDescription = this.description
    )
}

fun PlantEntity.toPlant(): Plant {
    return Plant(
        id = this.plantId,
        imagePath = this.plantImagePath,
        name = this.plantName,
        description = this.plantDescription
    )
}


fun PlantScheduleEntity.toPlantSchedule(): PlantSchedule {
    return PlantSchedule(
        id = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = time,
        daysInterval = daysInterval,
        firstTriggerDate = firstTriggerDate
    )
}

fun PlantSchedule.toPlantScheduleEntity(): PlantScheduleEntity {
    return PlantScheduleEntity(
        scheduleId = id,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = time,
        daysInterval = daysInterval,
        firstTriggerDate = firstTriggerDate
    )
}
