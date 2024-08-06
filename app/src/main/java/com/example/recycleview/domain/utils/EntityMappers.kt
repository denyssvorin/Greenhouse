package com.example.recycleview.domain.utils

import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.notification.NotificationItem
import com.example.recycleview.domain.notification.NotificationWorkerItem

fun AlarmPlant.toNotificationServiceItem(): NotificationWorkerItem {
    return NotificationWorkerItem(
        scheduleId = scheduleId,
        plantId = plantId,
        message = message
    )
}

fun PlantScheduleEntity.toAlarmPlant(
    plantName: String,
    plantImagePath: String?,
): AlarmPlant {
    return AlarmPlant(
        scheduleId = scheduleId,
        plantId = plantId,
        plantName = plantName,
        plantImagePath = plantImagePath,
        message = notificationMessage,

        firstTriggerTimeAndDateInMillis = calculateNextNotificationDateLong(
            startDate = firstTriggerDate,
            notificationTime = time,
            interval = daysInterval.toLong(),
        ),
        repeatIntervalDaysInMillis = daysToMillis(
            daysInterval
        )
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

fun Plant.toNotificationItem(scheduleId: String, notificationMessage: String): NotificationItem {
    return NotificationItem(
        scheduleId = scheduleId,
        plantImagePath = imagePath,
        plantName = name,
        notificationMessage = notificationMessage
    )
}