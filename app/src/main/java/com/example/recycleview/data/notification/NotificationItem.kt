package com.example.recycleview.data.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationItem(
    val scheduleId: String,
    val plantImagePath: String?,
    val plantName: String,
    val notificationMessage: String
): Parcelable