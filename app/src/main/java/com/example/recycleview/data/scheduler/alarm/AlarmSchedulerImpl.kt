package com.example.recycleview.data.scheduler.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.recycleview.data.scheduler.utils.toNotificationServiceItem
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.alarm.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(alarmItem: AlarmPlant) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override fun cancel(scheduleId: String) {
        try {
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    context,
                    scheduleId.hashCode(),
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION_ALARM_ITEM = "EXTRA_NOTIFICATION_ALARM_ITEM"
    }
}