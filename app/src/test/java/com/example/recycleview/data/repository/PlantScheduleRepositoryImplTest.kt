package com.example.recycleview.data.repository

import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.data.repository.mappers.toPlantSchedule
import com.example.recycleview.data.repository.mappers.toPlantScheduleEntity
import com.example.recycleview.domain.models.PlantSchedule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PlantScheduleRepositoryImplTest {

    private lateinit var repository: PlantScheduleRepositoryImpl
    private val dao: PlantScheduleDao = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = PlantScheduleRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPlantSchedulesByPlantId should return mapped data`() = runTest {
        val entity = PlantScheduleEntity(
            scheduleId = "1",
            plantId = "plantId",
            notificationMessage = "Water me!",
            time = "08:00",
            daysInterval = 3,
            firstTriggerDate = "2025-04-15"
        )
        val expected = listOf(entity.toPlantSchedule())

        coEvery { dao.getPlantSchedulesByPlantId("plantId") } returns flowOf(listOf(entity))

        val result = repository.getPlantSchedulesByPlantId("plantId").first()

        assertEquals(expected, result)
    }

    @Test
    fun `getPlantSchedulesByPlantId should emit empty list on exception`() = runTest {
        coEvery { dao.getPlantSchedulesByPlantId(any()) } returns flow {
            throw RuntimeException("DB error")
        }

        val result = repository.getPlantSchedulesByPlantId("plantId").first()

        assertEquals(emptyList<PlantSchedule>(), result)
    }

    @Test
    fun `insertPlantSchedule should call dao insert method`() = runTest {
        val schedule = PlantSchedule(
            id = "1",
            plantId = "plantId",
            notificationMessage = "Water me!",
            time = "08:00",
            daysInterval = 3,
            firstTriggerDate = "2025-04-15"
        )

        repository.insertPlantSchedule(schedule)

        coVerify { dao.insertPlantSchedule(schedule.toPlantScheduleEntity()) }
    }

    @Test
    fun `deleteSchedule should call dao delete method`() = runTest {
        val schedule = PlantSchedule(
            id = "1",
            plantId = "plantId",
            notificationMessage = "Remove me!",
            time = "10:00",
            daysInterval = 2,
            firstTriggerDate = "2025-05-01"
        )

        repository.deleteSchedule(schedule)

        coVerify { dao.deleteSchedule(schedule.toPlantScheduleEntity()) }
    }

    @Test
    fun `updatePlantSchedule should call dao update method`() = runTest {
        val schedule = PlantSchedule(
            id = "1",
            plantId = "plantId",
            notificationMessage = "Update me!",
            time = "12:00",
            daysInterval = 5,
            firstTriggerDate = "2025-06-01"
        )

        repository.updatePlantSchedule(schedule)

        coVerify { dao.updatePlantSchedule(schedule.toPlantScheduleEntity()) }
    }
}
