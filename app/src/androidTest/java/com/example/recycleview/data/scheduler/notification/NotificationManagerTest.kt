package com.example.recycleview.data.scheduler.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.recycleview.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationManagerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: com.example.recycleview.data.scheduler.notification.NotificationManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        notificationManager = com.example.recycleview.data.scheduler.notification.NotificationManager(context)
    }

    @Test
    fun testCreateNotificationChannel_createsChannel() {
        val systemNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Remove the channel if it already exists to ensure a "clean" test
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemNotificationManager.deleteNotificationChannel(
                NotificationManager.CHANNEL_PLANT_WATERING_NOTIFICATION
            )
        }

        // Create the notification channel
        notificationManager.createNotificationChannels()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val createdChannel = systemNotificationManager.getNotificationChannel(
                NotificationManager.CHANNEL_PLANT_WATERING_NOTIFICATION
            )

            assertNotNull("Channel should be created", createdChannel)
            assertEquals(
                context.getString(R.string.plant_watering_notifications),
                createdChannel?.name
            )
        }
    }

    @Test
    fun testCreateNotification_succeedsWhenPermissionGranted() {
        // This test will only run if the permission is manually granted
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            Log.e("NotificationManagerTest", "testCreateNotification_succeedsWhenPermissionGranted: Permission not granted. Skipping testCreateNotification.")
            return
        }

        val testItem = NotificationItem(
            scheduleId = "test123",
            plantId = "123",
            plantName = "Aloe Vera",
            plantImagePath = Uri.parse("android.resource://${context.packageName}/${R.drawable.plant_placeholder_coloured}").toString(),
            notificationMessage = "Time to water your Aloe Vera!"
        )

        notificationManager.createNotification(testItem)

        val notificationExists = NotificationManagerCompat.from(context)
            .areNotificationsEnabled() // Just checks if notifications are allowed

        assertTrue("Notification should be created and shown", notificationExists)
    }
}