package com.example.recycleview.data.repository.mappers

import com.example.recycleview.testutils.createPlant
import com.example.recycleview.testutils.createPlantEntity
import com.example.recycleview.testutils.createPlantSchedule
import com.example.recycleview.testutils.createPlantScheduleEntity
import junit.framework.TestCase.assertEquals
import org.junit.Test

class EntityMappersTest {

    @Test
    fun plantScheduleToPlantScheduleEntityWithoutParametersReturnsCorrectEntity() {
        val plantSchedule = createPlantSchedule()

        val plantScheduleEntity = plantSchedule.toPlantScheduleEntity()

        assertEquals(plantSchedule.id, plantScheduleEntity.scheduleId)
        assertEquals(plantSchedule.plantId, plantScheduleEntity.plantId)
        assertEquals(plantSchedule.notificationMessage, plantScheduleEntity.notificationMessage)
        assertEquals(plantSchedule.time, plantScheduleEntity.time)
        assertEquals(plantSchedule.daysInterval, plantScheduleEntity.daysInterval)
        assertEquals(plantSchedule.firstTriggerDate, plantScheduleEntity.firstTriggerDate)

    }

    @Test
    fun plantScheduleToPlantScheduleEntityIncludeParametersReturnsCorrectEntity() {
        val plantSchedule = createPlantSchedule()
        val expectedScheduleId = "123"
        val expectedPlantId = "456"

        val plantScheduleEntity = plantSchedule.toPlantScheduleEntity(
            scheduleId = expectedScheduleId,
            plantId = expectedPlantId
        )

        assertEquals(expectedScheduleId, plantScheduleEntity.scheduleId)
        assertEquals(expectedPlantId, plantScheduleEntity.plantId)
        assertEquals(plantSchedule.notificationMessage, plantScheduleEntity.notificationMessage)
        assertEquals(plantSchedule.time, plantScheduleEntity.time)
        assertEquals(plantSchedule.daysInterval, plantScheduleEntity.daysInterval)
        assertEquals(plantSchedule.firstTriggerDate, plantScheduleEntity.firstTriggerDate)
    }

    @Test
    fun plantScheduleEntityToPlantScheduleReturnsCorrectEntity() {
        val plantScheduleEntity = createPlantScheduleEntity()

        val schedule = plantScheduleEntity.toPlantSchedule()

        assertEquals(plantScheduleEntity.scheduleId, schedule.id)
        assertEquals(plantScheduleEntity.plantId, schedule.plantId)
        assertEquals(plantScheduleEntity.notificationMessage, schedule.notificationMessage)
        assertEquals(plantScheduleEntity.time, schedule.time)
        assertEquals(plantScheduleEntity.daysInterval, schedule.daysInterval)
        assertEquals(plantScheduleEntity.firstTriggerDate, schedule.firstTriggerDate)
    }

    @Test
    fun plantToPlantEntityReturnsCorrectEntity() {
        val plant = createPlant()

        val plantEntity = plant.toPlantEntity()

        assertEquals(plant.id, plantEntity.plantId)
        assertEquals(plant.imagePath, plantEntity.plantImagePath)
        assertEquals(plant.name, plantEntity.plantName)
        assertEquals(plant.description, plantEntity.plantDescription)
    }

    @Test
    fun plantEntityToPlantReturnsCorrectEntity() {
        val plantEntity = createPlantEntity()

        val plant = plantEntity.toPlant()

        assertEquals(plantEntity.plantId, plant.id)
        assertEquals(plantEntity.plantImagePath, plant.imagePath)
        assertEquals(plantEntity.plantName, plant.name)
        assertEquals(plantEntity.plantDescription, plant.description)
    }
}