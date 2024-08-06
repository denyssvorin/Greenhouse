package com.example.recycleview.domain.alarm

data class AlarmPlant (
    val scheduleId: String,
    val plantId: String,
    val plantName: String,
    val plantImagePath: String?,
    val message: String,
    val firstTriggerTimeAndDateInMillis: Long,
    val repeatIntervalDaysInMillis: Long
)