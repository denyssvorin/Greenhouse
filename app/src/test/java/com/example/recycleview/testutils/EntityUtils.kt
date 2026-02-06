package com.example.recycleview.testutils

import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.models.PlantSchedule

fun createPlantSchedule(
    id: String = "1",
    plantId: String = "1",
    notificationMessage: String = "message",
    time: String = "time",
    daysInterval: Int = 1,
    firstTriggerDate: String = "firstTriggerDate",
) = PlantSchedule(
    id = id,
    plantId = plantId,
    notificationMessage = notificationMessage,
    time = time,
    daysInterval = daysInterval,
    firstTriggerDate = firstTriggerDate
)

fun createPlantScheduleEntity(
    id: String = "1",
    plantId: String = "1",
    notificationMessage: String = "message",
    time: String = "time",
    daysInterval: Int = 1,
    firstTriggerDate: String = "firstTriggerDate",
) = PlantScheduleEntity(
    scheduleId = id,
    plantId = plantId,
    notificationMessage = notificationMessage,
    time = time,
    daysInterval = daysInterval,
    firstTriggerDate = firstTriggerDate
)

fun createPlantEntity(
    plantId: String = "1",
    plantImagePath: String = "plantImagePathTest",
    plantName: String = "plantNameTest",
    plantDescription: String = "plantDescriptionTest",
) = PlantEntity(
    plantId = plantId,
    plantImagePath = plantImagePath,
    plantName = plantName,
    plantDescription = plantDescription
)

fun createPlant(
    plantId: String = "1",
    plantImagePath: String = "imagePathTest",
    plantName: String = "nameTest",
    plantDescription: String = "descriptionTest",
) = Plant(
    id = plantId,
    imagePath = plantImagePath,
    name = plantName,
    description = plantDescription
)

fun createAlarmPlant(
    scheduleId: String = "1",
    plantId: String = "1",
    plantName: String = "plantNameTest",
    plantImagePath: String? = "plantImagePath",
    message: String = "message",
    firstTriggerTimeAndDateInMillis: Long = 1L,
    repeatIntervalDaysInMillis: Long = 1L,
) = AlarmPlant(
    scheduleId = scheduleId,
    plantId = plantId,
    plantName = plantName,
    plantImagePath = plantImagePath,
    message = message,
    firstTriggerTimeAndDateInMillis = firstTriggerTimeAndDateInMillis,
    repeatIntervalDaysInMillis = repeatIntervalDaysInMillis
)