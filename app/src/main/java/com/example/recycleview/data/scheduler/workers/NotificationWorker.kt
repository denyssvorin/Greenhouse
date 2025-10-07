package com.example.recycleview.data.scheduler.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.scheduler.notification.NotificationManager
import com.example.recycleview.data.scheduler.notification.NotificationWorkerItem
import com.example.recycleview.data.scheduler.utils.toNotificationItem
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    private val plantDao: PlantDao,
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            val notificationItemString = parameters.inputData.getString(KEY_DATA)
            val notificationWorkerItem =
                Gson().fromJson(notificationItemString, NotificationWorkerItem::class.java)

            try {
                val plant =
                    plantDao.getSinglePlant(notificationWorkerItem.plantId)

                val notificationItem = plant.toNotificationItem(
                    scheduleId = notificationWorkerItem.scheduleId,
                    notificationMessage = notificationWorkerItem.message
                )

                val plantWateringNotification = NotificationManager(context)
                plantWateringNotification.createNotificationChannels()
                plantWateringNotification.createNotification(notificationItem)

                Result.success()
            } catch (e: Exception) {
                Log.e("NotificationWorker", "Error accessing database", e)
                if (e is CancellationException) throw e
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }
}