package com.example.recycleview.data.mappers

import com.example.recycleview.data.alarm.AlarmPlant
import com.example.recycleview.data.notification.NotificationItem
import com.example.recycleview.data.notification.NotificationWorkerItem
import com.example.recycleview.data.realm.plant.PlantEntity
import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.PlantScheduleData
import com.example.recycleview.utils.calculateNextNotificationDateLong
import com.example.recycleview.utils.daysToMillis
import com.example.recycleview.utils.localDateToString
import com.example.recycleview.utils.localTimeToString

fun PlantScheduleData.toPlantScheduleEntity(scheduleId: String): PlantScheduleEntity {
    return PlantScheduleEntity().apply {
        _id = scheduleId
        notificationMessage = this@toPlantScheduleEntity.notificationMessage
        time = localTimeToString(this@toPlantScheduleEntity.time)
        daysInterval = this@toPlantScheduleEntity.daysInterval
        firstTriggerDate = localDateToString(this@toPlantScheduleEntity.firstTriggerDate)
    }
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

fun AlarmPlant.toNotificationServiceItem(): NotificationWorkerItem {
    return NotificationWorkerItem(
        scheduleId = scheduleId,
        plantId = plantId,
        message = message
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