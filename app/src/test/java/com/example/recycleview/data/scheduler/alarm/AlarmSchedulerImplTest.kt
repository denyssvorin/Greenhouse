package com.example.recycleview.data.scheduler.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.recycleview.domain.alarm.AlarmPlant
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class AlarmSchedulerImplTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmScheduler: AlarmSchedulerImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        val realAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager = spyk(realAlarmManager)

        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        val contextSpy = spyk(context)
        every { contextSpy.getSystemService(Context.ALARM_SERVICE) } returns alarmManager

        alarmScheduler = AlarmSchedulerImpl(contextSpy)

        mockkStatic(PendingIntent::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(PendingIntent::class)
    }

    @Test
    fun `schedule should call alarmManager with correct parameters`() {
        // Arrange
        val alarmItem = AlarmPlant(
            scheduleId = "schedule123",
            plantId = "plantABC",
            plantName = "Ficus",
            plantImagePath = null,
            message = "Полий свою рослину",
            firstTriggerTimeAndDateInMillis = 1_700_000_000_000,
            repeatIntervalDaysInMillis = AlarmManager.INTERVAL_DAY
        )

        val mockPendingIntent = mockk<PendingIntent>()
        every {
            PendingIntent.getBroadcast(any(), any(), any(), any())
        } returns mockPendingIntent

        // Act
        alarmScheduler.schedule(alarmItem)

        // Assert
        verify {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmItem.firstTriggerTimeAndDateInMillis,
                alarmItem.repeatIntervalDaysInMillis,
                any()
            )
        }
    }

    @Test
    fun `cancel should call alarmManager cancel with PendingIntent`() {
        // Arrange
        val scheduleId = "schedule123"
        val mockPendingIntent = mockk<PendingIntent>()

        every {
            PendingIntent.getBroadcast(any(), any(), any(), any())
        } returns mockPendingIntent

        // Act
        alarmScheduler.cancel(scheduleId)

        // Assert
        verify { alarmManager.cancel(any<PendingIntent>()) }
    }
}
