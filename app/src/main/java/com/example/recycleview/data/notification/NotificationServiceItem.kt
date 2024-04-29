package com.example.recycleview.data.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationServiceItem(
    val scheduleId: String,
    val plantId: Int,
    val message: String
): Parcelable