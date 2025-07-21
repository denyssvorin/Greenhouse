package com.example.recycleview.domain.repository

import com.example.recycleview.domain.models.PlantSchedule
import kotlinx.coroutines.flow.Flow

interface PlantScheduleRepository {

    suspend fun getPlantSchedulesByPlantId(plantId: String): Flow<List<PlantSchedule>>

    suspend fun insertPlantSchedule(plantSchedule: PlantSchedule)

    suspend fun deleteSchedule(plantSchedule: PlantSchedule)

    suspend fun updatePlantSchedule(plantSchedule: PlantSchedule)
}