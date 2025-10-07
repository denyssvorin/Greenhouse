package com.example.recycleview.data.scheduler.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recycleview.data.scheduler.alarm.AlarmSchedulerImpl.Companion.EXTRA_NOTIFICATION_ALARM_ITEM
import com.example.recycleview.data.scheduler.workers.NotificationWorker
import com.example.recycleview.data.scheduler.notification.NotificationWorkerItem
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val notificationItem: NotificationWorkerItem =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent?.getParcelableExtra(
                        EXTRA_NOTIFICATION_ALARM_ITEM,
                        NotificationWorkerItem::class.java
                    )
                } else {
                    intent?.getParcelableExtra(EXTRA_NOTIFICATION_ALARM_ITEM)
                } ?: return

            Log.i("AlarmReceiver", "onReceive: Alarm triggered in broadcast receiver")

            if (context != null) {

                val workInputData = Data.Builder()
                    .putString(NotificationWorker.KEY_DATA, Gson().toJson(notificationItem)).build()

                val workConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()

                val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setConstraints(workConstraints)
                    .setInputData(workInputData)
                    .build()
                WorkManager.getInstance(context).enqueue(notificationWorkRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}