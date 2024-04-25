package com.example.recycleview.domain

data class PlantWateringSchedule(
    val scheduleId: String,
    val plantId: Int,
    val notificationMessage: String,
    val time: String,
    val daysInterval: Int,
    val firstTriggerDate: String,
)
