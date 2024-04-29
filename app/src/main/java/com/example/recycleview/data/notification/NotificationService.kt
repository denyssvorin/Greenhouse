package com.example.recycleview.data.notification

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.recycleview.data.mappers.toNotificationItem
import com.example.recycleview.data.mappers.toPlant
import com.example.recycleview.data.plant.PlantDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {

    private var job: Job? = null

    @Inject
    lateinit var plantDao: PlantDao

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationServiceItem: NotificationServiceItem? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(
                    EXTRA_PLANT,
                    NotificationServiceItem::class.java
                )
            } else {
                intent?.getParcelableExtra(EXTRA_PLANT)
            }

        notificationServiceItem?.let { item ->
            job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val plant =
                        plantDao.getSinglePlant(item.plantId).first().toPlant()
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
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        const val EXTRA_PLANT = "EXTRA_PLANT"
    }
}