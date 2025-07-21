package com.example.recycleview.data.scheduler.utils

import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.data.scheduler.notification.NotificationItem
import com.example.recycleview.data.scheduler.notification.NotificationWorkerItem
import com.example.recycleview.domain.alarm.AlarmPlant

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

fun PlantEntity.toNotificationItem(scheduleId: String, notificationMessage: String): NotificationItem {
    return NotificationItem(
        scheduleId = scheduleId,
        plantImagePath = plantImagePath,
        plantName = plantName,
        notificationMessage = notificationMessage
    )
}