package com.example.recycleview.data.scheduler.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class OneTimeAlarmWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val repeatInterval = parameters.inputData.getLong(ALARM_DATA_KEY, 0L)

                val workConstraints = Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()

                val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                    repeatInterval, TimeUnit.MILLISECONDS
                )
                    .setConstraints(workConstraints)
                    .setInputData(parameters.inputData)
                    .build()
                WorkManager.getInstance(context).enqueue(notificationWorkRequest)

                Result.success()
            } catch (e: Exception) {
                Log.e("RestartAlarmsService", "Error accessing database", e)
                if (e is CancellationException) throw e
                Result.failure()
            }
        }
    }

    companion object {
        const val ALARM_DATA_KEY = "ALARM_DATA_KEY"
    }
}