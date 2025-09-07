package com.example.recycleview.data.scheduler.notification

data class NotificationItem(
    val scheduleId: String,
    val plantId: String,
    val plantImagePath: String?,
    val plantName: String?,
    val notificationMessage: String?
)