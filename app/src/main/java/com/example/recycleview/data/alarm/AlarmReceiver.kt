package com.example.recycleview.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.recycleview.data.alarm.AlarmSchedulerImpl.Companion.EXTRA_NOTIFICATION_ALARM_ITEM
import com.example.recycleview.data.notification.NotificationService
import com.example.recycleview.data.notification.NotificationServiceItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationItem: NotificationServiceItem =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(
                    EXTRA_NOTIFICATION_ALARM_ITEM,
                    NotificationServiceItem::class.java
                )
            } else {
                intent?.getParcelableExtra(EXTRA_NOTIFICATION_ALARM_ITEM)
            } ?: return

        println("Alarm triggered in broadcast receiver")

        val notificationIntent = Intent(context, NotificationService::class.java).also { notificationIntent ->
            notificationIntent.putExtra(NotificationService.EXTRA_PLANT, notificationItem)
        }
        context?.startService(notificationIntent)
    }
}