package com.example.recycleview.domain.utils

import com.example.recycleview.data.realm.plant.PlantEntity
import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmPlant
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
        scheduleId = _id,
        plantId = plant?._id.toString(),
        plantName = plantName,
        plantImagePath = plantImagePath,
        message = notificationMessage,

        firstTriggerTimeAndDateInMillis = calculateNextNotificationDateLong(
            startDate = firstTriggerDate,
            notificationTime = time,
            interval = daysInterval?.toLong()
                ?: (1 * 60 * 1000L), // default interval 24h
        ),
        repeatIntervalDaysInMillis = daysToMillis(
            daysInterval ?: 1
        )
    )
}

fun PlantEntity.toNotificationItem(scheduleId: String, notificationMessage: String): NotificationItem {
    return NotificationItem(
        scheduleId = scheduleId,
        plantImagePath = plantImagePath,
        plantName = plantName,
        notificationMessage = notificationMessage
    )
}