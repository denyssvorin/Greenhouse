package com.example.recycleview.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.recycleview.data.alarm.AlarmSchedulerImpl.Companion.EXTRA_NOTIFICATION_ALARM_ITEM
import com.example.recycleview.data.notification.NotificationItem
import com.example.recycleview.data.notification.NotificationManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationItem: NotificationItem =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(
                    EXTRA_NOTIFICATION_ALARM_ITEM,
                    NotificationItem::class.java
                )
            } else {
                intent?.getParcelableExtra(EXTRA_NOTIFICATION_ALARM_ITEM)
            } ?: return

        println("Alarm triggered in broadcast receiver")

        val plantWateringNotification = context?.let {
            NotificationManager(it)
        }
        plantWateringNotification?.createNotificationChannels()
        plantWateringNotification?.createNotification(notificationItem)
    }
}