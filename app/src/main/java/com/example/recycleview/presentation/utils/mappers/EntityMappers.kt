package com.example.recycleview.presentation.utils.mappers

import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.presentation.details.models.PlantScheduleData


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

fun PlantScheduleData.toPlantSchedule(
    scheduleId: String,
    plantId: String
): PlantSchedule {
    return PlantSchedule(
        id = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = localTimeToString(time),
        daysInterval = daysInterval,
        firstTriggerDate = localDateToString(firstTriggerDate)
    )
}