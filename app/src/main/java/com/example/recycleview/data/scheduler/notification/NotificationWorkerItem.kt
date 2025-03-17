package com.example.recycleview.data.scheduler.notification

import java.io.Serializable

data class NotificationWorkerItem(
    val scheduleId: String,
    val plantId: String,
    val message: String
): Serializable