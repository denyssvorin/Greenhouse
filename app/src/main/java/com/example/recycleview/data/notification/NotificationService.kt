package com.example.recycleview.data.notification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.recycleview.data.mappers.toNotificationItem
import com.example.recycleview.data.mappers.toPlant
import com.example.recycleview.data.plant.PlantDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : JobIntentService() {

    private var job: Job? = null

    @Inject
    lateinit var plantDao: PlantDao

    override fun onHandleWork(intent: Intent) {
        Log.i("NotificationService", "onHandleWork: ")

        val notificationServiceItem: NotificationServiceItem? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(
                    EXTRA_PLANT,
                    NotificationServiceItem::class.java
                )
            } else {
                intent.getParcelableExtra(EXTRA_PLANT)
            }

        notificationServiceItem?.let { item ->
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val plant =
                        plantDao.getSinglePlant(item.plantId).toPlant()
                    val notificationItem = plant.toNotificationItem(
                        scheduleId = item.scheduleId,
                        notificationMessage = item.message
                    )

                    val plantWateringNotification = NotificationManager(this@NotificationService)
                    plantWateringNotification.createNotificationChannels()
                    plantWateringNotification.createNotification(notificationItem)
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error accessing database", e)
                } finally {
                    stopSelf()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        const val EXTRA_PLANT = "EXTRA_PLANT"
        const val JOB_ID = 1001

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, NotificationService::class.java, JOB_ID, work)
        }
    }
}