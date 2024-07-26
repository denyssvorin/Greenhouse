package com.example.recycleview.data.notification

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recycleview.data.mappers.toNotificationItem
import com.example.recycleview.data.realm.plant.PlantDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker constructor(
    private val plantDao: PlantDao,

    // AssistedInject
    private val context: Context,
    private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Main) {

            val notificationItemString = parameters.inputData.getString(KEY_DATA)
            val notificationWorkerItem =
                Gson().fromJson(notificationItemString, NotificationWorkerItem::class.java)

            try {
                val plant =
                    plantDao.getSinglePlant(notificationWorkerItem.plantId)

                if (plant != null) {
                    val notificationItem = plant.toNotificationItem(
                        scheduleId = notificationWorkerItem.scheduleId,
                        notificationMessage = notificationWorkerItem.message
                    )

                    val plantWateringNotification = NotificationManager(context)
                    plantWateringNotification.createNotificationChannels()
                    plantWateringNotification.createNotification(notificationItem)

                    Result.success()
                } else {
                    Result.failure()
                }

            } catch (e: Exception) {
                Log.e("NotificationWorker", "Error accessing database", e)
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_DATA = "KEY_DATA"
    }
}

