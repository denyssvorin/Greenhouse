package com.example.recycleview.domain.notification

data class NotificationItem(
    val scheduleId: String,
    val plantImagePath: String?,
    val plantName: String?,
    val notificationMessage: String?
)