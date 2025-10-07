package com.example.recycleview.data.scheduler.alarm

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.recycleview.data.scheduler.utils.toNotificationServiceItem
import com.example.recycleview.data.scheduler.workers.NotificationWorker
import com.example.recycleview.data.scheduler.workers.OneTimeAlarmWorker
import com.example.recycleview.domain.alarm.AlarmPlant
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    override fun schedule(alarmItem: AlarmPlant) {
        try {

            val delay = alarmItem.firstTriggerTimeAndDateInMillis - System.currentTimeMillis()
            val notificationItemWorkInputData = Gson().toJson(alarmItem.toNotificationServiceItem())

            val workRequest = OneTimeWorkRequestBuilder<OneTimeAlarmWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        NotificationWorker.KEY_DATA to notificationItemWorkInputData,
                        OneTimeAlarmWorker.ALARM_DATA_KEY to alarmItem.repeatIntervalDaysInMillis
                    )
                )
                .addTag(alarmItem.scheduleId)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override fun cancel(scheduleId: String) {
        try {
            WorkManager.getInstance(context).cancelAllWorkByTag(scheduleId)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION_ALARM_ITEM = "EXTRA_NOTIFICATION_ALARM_ITEM"
    }
}