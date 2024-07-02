package com.example.recycleview.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.recycleview.data.mappers.toNotificationServiceItem
import javax.inject.Inject


class AlarmSchedulerImpl @Inject constructor(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(alarmItem: AlarmPlant) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_ALARM_ITEM, alarmItem.toNotificationServiceItem())
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmItem.firstTriggerTimeAndDateInMillis,
            alarmItem.repeatIntervalDaysInMillis,
            PendingIntent.getBroadcast(
                context,
                alarmItem.scheduleId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(scheduleId: Int) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                scheduleId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        const val EXTRA_NOTIFICATION_ALARM_ITEM = "EXTRA_NOTIFICATION_ALARM_ITEM"
    }
}