package com.example.recycleview.data.scheduler.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.recycleview.domain.alarm.AlarmPlant
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class AlarmSchedulerImplTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var alarmScheduler: AlarmSchedulerImpl

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        alarmManager = mockk(relaxed = true)

        every { context.packageName } returns "com.example.test"
        every { context.getSystemService(AlarmManager::class.java) } returns alarmManager

        alarmScheduler = AlarmSchedulerImpl(context)
    }

    @Test
    fun `schedule should call alarmManager with correct parameters`() {
        val alarmItem = AlarmPlant(
            scheduleId = "schedule123",
            plantId = "plantABC",
            plantName = "Ficus",
            plantImagePath = null,
            message = "Полий свою рослину",
            firstTriggerTimeAndDateInMillis = 1_700_000_000_000,
            repeatIntervalDaysInMillis = AlarmManager.INTERVAL_DAY
        )

        mockkConstructor(Intent::class)
        mockkStatic(PendingIntent::class)
        val mockPendingIntent = mockk<PendingIntent>()
        every {
            PendingIntent.getBroadcast(
                any(),
                any(),
                any(),
                any()
            )
        } returns mockPendingIntent

        // Creating мок for AlarmManager and AlarmSchedulerImpl
        alarmManager = mockk(relaxed = true)
        every { context.getSystemService(AlarmManager::class.java) } returns alarmManager
        alarmScheduler = AlarmSchedulerImpl(context)

        // Launch method
        alarmScheduler.schedule(alarmItem)

        // Verify call
        verify {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmItem.firstTriggerTimeAndDateInMillis,
                alarmItem.repeatIntervalDaysInMillis,
                mockPendingIntent
            )
        }

        unmockkAll()
    }



    @Test
    fun `cancel should call alarmManager cancel with PendingIntent`() {
        val scheduleId = "schedule123"
        val pendingIntent = mockk<PendingIntent>()

        mockkStatic(PendingIntent::class)
        every {
            PendingIntent.getBroadcast(
                context,
                scheduleId.hashCode(),
                any(),
                any()
            )
        } returns pendingIntent

        alarmScheduler.cancel(scheduleId)

        verify { alarmManager.cancel(pendingIntent) }

        unmockkStatic(PendingIntent::class)
    }
}

