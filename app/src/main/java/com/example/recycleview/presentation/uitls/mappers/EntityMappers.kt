package com.example.recycleview.presentation.uitls.mappers

import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.domain.models.PlantScheduleData

fun PlantScheduleData.toPlantScheduleEntity(
    scheduleId: String,
    plantId: String
): PlantScheduleEntity {
    return PlantScheduleEntity(
        scheduleId = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = localTimeToString(time),
        daysInterval = daysInterval,
        firstTriggerDate = localDateToString(firstTriggerDate)
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

fun PlantSchedule.toAlarmPlantDeletion(): AlarmPlant {
    return AlarmPlant(
        scheduleId = id,
        plantId = plantId,
        plantName = "",
        plantImagePath = "",
        message = "",
        firstTriggerTimeAndDateInMillis = 0L,
        repeatIntervalDaysInMillis = 0L
    )
}

fun PlantEntity.toPlant(): Plant {
    return Plant(
        id = plantId,
        imagePath = plantImagePath,
        name = plantName,
        description = plantDescription
    )
}

fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        plantId = id,
        plantImagePath = imagePath,
        plantName = name,
        plantDescription = description
    )
}

fun PlantScheduleData.toAlarmPlant(
    scheduleId: String,
    plantId: String,
    plantName: String,
    plantImagePath: String?,
): AlarmPlant {

    val firstTriggerMillis = calculateNextNotificationDateLong(
        startDate = firstTriggerDate,
        notificationTime = time,
        interval = daysInterval.toLong()
    )

    val daysRepeatIntervalInMillis = daysToMillis(daysInterval)

    return AlarmPlant(
        scheduleId = scheduleId,
        plantId = plantId,
        plantName = plantName,
        message = notificationMessage,
        firstTriggerTimeAndDateInMillis = firstTriggerMillis,
        repeatIntervalDaysInMillis = daysRepeatIntervalInMillis,
        plantImagePath = plantImagePath
    )
}
