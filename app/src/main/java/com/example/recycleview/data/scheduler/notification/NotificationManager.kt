package com.example.recycleview.data.scheduler.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import com.example.recycleview.R
import java.io.InputStream

class NotificationManager(private val context: Context) {

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    fun createNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_PLANT_WATERING_NOTIFICATION) == null) {
            val channelPlantWatering = NotificationChannel(
                CHANNEL_PLANT_WATERING_NOTIFICATION,
                context.getString(R.string.plant_watering_notifications),
                NotificationManager.IMPORTANCE_HIGH
            )
            channelPlantWatering.description =
                context.getString(R.string.notification_channel_description)

            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channelPlantWatering)
        }
    }

    fun createNotification(item: NotificationItem) {
        val intent = Intent(Intent.ACTION_VIEW, "greenhouse://plant/${item.plantId}".toUri())
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val bitmap: Bitmap = try {
            val imageUri = Uri.parse(item.plantImagePath)
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, R.drawable.plant_placeholder_coloured)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_PLANT_WATERING_NOTIFICATION)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle(item.notificationMessage)
            .setContentText(item.plantName)

            .setLargeIcon(bitmap)

            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap)
                    .bigLargeIcon(null as Bitmap?)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH) // On Android 7.1 (API level 25) and lower
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()


        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(item.scheduleId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_PLANT_WATERING_NOTIFICATION = "CHANNEL_PLANT_WATERING_NOTIFICATION"
    }
}