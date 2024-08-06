package com.example.recycleview.presentation.utils.mappers

import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.models.PlantScheduleData

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

fun PlantScheduleData.toPlantScheduleEntity(scheduleId: String): PlantScheduleEntity {
    return PlantScheduleEntity().apply {
        _id = scheduleId
        notificationMessage = this@toPlantScheduleEntity.notificationMessage
        time = localTimeToString(this@toPlantScheduleEntity.time)
        daysInterval = this@toPlantScheduleEntity.daysInterval
        firstTriggerDate = localDateToString(this@toPlantScheduleEntity.firstTriggerDate)
    }
}