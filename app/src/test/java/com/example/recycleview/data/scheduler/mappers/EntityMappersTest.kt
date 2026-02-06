package com.example.recycleview.data.scheduler.mappers

import com.example.recycleview.data.scheduler.utils.calculateNextNotificationDateLong
import com.example.recycleview.data.scheduler.utils.daysToMillis
import com.example.recycleview.data.scheduler.utils.toAlarmPlant
import com.example.recycleview.data.scheduler.utils.toNotificationItem
import com.example.recycleview.data.scheduler.utils.toNotificationServiceItem
import com.example.recycleview.testutils.createAlarmPlant
import com.example.recycleview.testutils.createPlantEntity
import com.example.recycleview.testutils.createPlantScheduleEntity
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class EntityMappersTest {

    @Before
    fun arrange() {
        mockkStatic(::daysToMillis)
        mockkStatic(::calculateNextNotificationDateLong)
    }

    @After
    fun clear() {
        unmockkStatic(::daysToMillis)
        unmockkStatic(::calculateNextNotificationDateLong)
    }

    @Test
    fun alarmPlantToNotificationServiceItemReturnsCorrectEntity() {
        val alarmPlant = createAlarmPlant()

        val notificationWorkerItem = alarmPlant.toNotificationServiceItem()

        assertEquals(alarmPlant.scheduleId, notificationWorkerItem.scheduleId)
        assertEquals(alarmPlant.plantId, notificationWorkerItem.plantId)
        assertEquals(alarmPlant.message, notificationWorkerItem.message)
    }

    @Test
    fun plantScheduleEntityToAlarmPlantIncludeParametersReturnsCorrectEntity() {
        val plantScheduleEntity = createPlantScheduleEntity()
        val expectedPlantName = "expectedPlantName"
        val expectedPlantImagePath = "expectedPlantImagePath"
        val expectedDays = 259200000L
        val expectedNotificationDate = 1761225600000L

        every { daysToMillis(any()) } returns expectedDays
        every { calculateNextNotificationDateLong(any(), any(), any()) } returns expectedNotificationDate

        val alarmPlant = plantScheduleEntity.toAlarmPlant(
            plantName = expectedPlantName,
            plantImagePath = expectedPlantImagePath
        )

        assertEquals(expectedPlantName, alarmPlant.plantName)
        assertEquals(expectedPlantImagePath, alarmPlant.plantImagePath)
        assertEquals(plantScheduleEntity.scheduleId, alarmPlant.scheduleId)
        assertEquals(plantScheduleEntity.plantId, alarmPlant.plantId)
        assertEquals(plantScheduleEntity.notificationMessage, alarmPlant.message)
        assertEquals(expectedNotificationDate, alarmPlant.firstTriggerTimeAndDateInMillis)
        assertEquals(expectedDays, alarmPlant.repeatIntervalDaysInMillis)
    }

    @Test
    fun plantScheduleEntityToAlarmPlantWithNullParameterReturnsCorrectEntity() {
        val plantScheduleEntity = createPlantScheduleEntity()
        val expectedPlantName = "expectedPlantName"
        val expectedPlantImagePath = null
        val expectedDays = 259200000L
        val expectedNotificationDate = 1761225600000L

        every { daysToMillis(any()) } returns expectedDays
        every { calculateNextNotificationDateLong(any(), any(), any()) } returns expectedNotificationDate

        val alarmPlant = plantScheduleEntity.toAlarmPlant(
            plantName = expectedPlantName,
            plantImagePath = expectedPlantImagePath
        )

        assertEquals(expectedPlantName, alarmPlant.plantName)
        assertEquals(expectedPlantImagePath, alarmPlant.plantImagePath)
        assertEquals(plantScheduleEntity.scheduleId, alarmPlant.scheduleId)
        assertEquals(plantScheduleEntity.plantId, alarmPlant.plantId)
        assertEquals(plantScheduleEntity.notificationMessage, alarmPlant.message)
        assertEquals(expectedNotificationDate, alarmPlant.firstTriggerTimeAndDateInMillis)
        assertEquals(expectedDays, alarmPlant.repeatIntervalDaysInMillis)
    }


    @Test
    fun plantEntityToNotificationItemIncludeParameterReturnsCorrectEntity() {
        val plantScheduleEntity = createPlantEntity()
        val expectedScheduleId = "expectedScheduleId"
        val expectedNotificationMessage = "expectedNotificationMessage"

        val notificationItem = plantScheduleEntity.toNotificationItem(
            scheduleId = expectedScheduleId,
            notificationMessage = expectedNotificationMessage
        )

        assertEquals(expectedScheduleId, notificationItem.scheduleId)
        assertEquals(expectedNotificationMessage, notificationItem.notificationMessage)
        assertEquals(plantScheduleEntity.plantId, notificationItem.plantId)
        assertEquals(plantScheduleEntity.plantImagePath, notificationItem.plantImagePath)
        assertEquals(plantScheduleEntity.plantName, notificationItem.plantName)
    }
}