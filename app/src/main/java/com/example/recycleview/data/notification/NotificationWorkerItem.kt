package com.example.recycleview.data.notification

import java.io.Serializable

data class NotificationWorkerItem(
    val scheduleId: String,
    val plantId: Int,
    val message: String
): Serializable