package com.example.recycleview.data.mappers

import com.example.recycleview.data.alarm.AlarmPlant
import com.example.recycleview.data.notification.NotificationItem
import com.example.recycleview.data.notification.NotificationServiceItem
import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.plantschedule.PlantWateringScheduleEntity
import com.example.recycleview.domain.Plant
import com.example.recycleview.domain.PlantScheduleData
import com.example.recycleview.domain.PlantWateringSchedule
import com.example.recycleview.utils.ScheduleDateUtils

fun PlantScheduleData.toPlantWateringScheduleEntity(
    scheduleId: String,
    plantId: Int
): PlantWateringScheduleEntity {
    return PlantWateringScheduleEntity(
        scheduleId = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = ScheduleDateUtils().localTimeToString(time),
        daysInterval = daysInterval,
        firstTriggerDate = ScheduleDateUtils().localDateToString(firstTriggerDate)
    )
}

fun PlantWateringScheduleEntity.toPlantWateringSchedule(): PlantWateringSchedule {
    return PlantWateringSchedule(
        scheduleId = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = time,
        daysInterval = daysInterval,
        firstTriggerDate = firstTriggerDate
    )
}

fun PlantWateringScheduleEntity.toAlarmPlant(
    plantName: String,
    plantImagePath: String?,
): AlarmPlant {
    return AlarmPlant(
        scheduleId = scheduleId,
        plantId = plantId,
        plantName = plantName,
        plantImagePath = plantImagePath,
        message = notificationMessage,

        firstTriggerTimeAndDateInMillis = ScheduleDateUtils().calculateNextNotificationDateLong(
            startDate = firstTriggerDate,
            notificationTime = time,
            interval = daysInterval.toLong(),
        ),
        repeatIntervalDaysInMillis = ScheduleDateUtils().daysToMillis(
            daysInterval
        )
    )
}
fun PlantWateringSchedule.toPlantWateringScheduleEntity(): PlantWateringScheduleEntity {
    return PlantWateringScheduleEntity(
        scheduleId = scheduleId,
        plantId = plantId,
        notificationMessage = notificationMessage,
        time = time,
        daysInterval = daysInterval,
        firstTriggerDate = firstTriggerDate
    )
}

fun PlantWateringSchedule.toAlarmPlantDeletion(): AlarmPlant {
    return AlarmPlant(
        scheduleId = scheduleId,
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
        plantId = plantId,
        plantImagePath = plantImagePath,
        plantName = plantName,
        plantDescription = plantDescription
    )
}

fun Plant.toPlantEntity(): PlantEntity {
    return PlantEntity(
        plantId = plantId,
        plantImagePath = plantImagePath,
        plantName = plantName,
        plantDescription = plantDescription
    )
}

fun PlantScheduleData.toAlarmPlant(
    scheduleId: String,
    plantId: Int,
    plantName: String,
    plantImagePath: String?,
): AlarmPlant {
    val triggerTimeInMillis =
        ScheduleDateUtils().localTimeToMilliseconds(time)
    val firstTriggerDateInMillis =
        ScheduleDateUtils().localDateToMilliseconds(firstTriggerDate)

    val firstTriggerMillis = ScheduleDateUtils().combineDateAndTime(
        dateInMillis = firstTriggerDateInMillis,
        timeInMillis = triggerTimeInMillis
    )

    val daysRepeatIntervalInMillis =
        ScheduleDateUtils().daysToMillis(daysInterval)

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

fun AlarmPlant.toNotificationServiceItem(): NotificationServiceItem {
    return NotificationServiceItem(
        scheduleId = scheduleId,
        plantId = plantId,
        message = message
    )
}

fun Plant.toNotificationItem(scheduleId: String, notificationMessage: String): NotificationItem {
    return NotificationItem(
        scheduleId = scheduleId,
        plantImagePath = plantImagePath,
        plantName = plantName,
        notificationMessage = notificationMessage
    )
}