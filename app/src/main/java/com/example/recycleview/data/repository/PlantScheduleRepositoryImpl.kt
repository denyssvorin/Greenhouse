package com.example.recycleview.data.repository

import android.util.Log
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.repository.mappers.toPlantSchedule
import com.example.recycleview.data.repository.mappers.toPlantScheduleEntity
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.domain.repository.PlantScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class PlantScheduleRepositoryImpl @Inject constructor(
    private val plantScheduleDao: PlantScheduleDao,
) : PlantScheduleRepository {

    override suspend fun getPlantSchedulesByPlantId(plantId: String): Flow<List<PlantSchedule>> =
        plantScheduleDao.getPlantSchedulesByPlantId(plantId)
            .map { entityList -> entityList.map { it.toPlantSchedule() } }
            .catch { e ->
                e.printStackTrace()
                if (e is CancellationException) throw e
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)

    override suspend fun insertPlantSchedule(plantSchedule: PlantSchedule) {
        withContext(Dispatchers.IO) {
            try {
                val plantScheduleEntity = plantSchedule.toPlantScheduleEntity()

                plantScheduleDao.insertPlantSchedule(plantScheduleEntity)
            } catch (e: Exception) {
                Log.e("PlantScheduleRepositoryImpl", "insertPlantSchedule: ${e.message}")
            }
        }
    }


    override suspend fun deleteSchedule(plantSchedule: PlantSchedule) {
        withContext(Dispatchers.IO) {
            try {
                val plantScheduleEntity = plantSchedule.toPlantScheduleEntity()

                plantScheduleDao.deleteSchedule(plantScheduleEntity)
            } catch (e: Exception) {
                Log.e("PlantScheduleRepositoryImpl", "delete: ${e.message}")
            }
        }
    }

    override suspend fun updatePlantSchedule(plantSchedule: PlantSchedule) {
        withContext(Dispatchers.IO) {
            try {
                val entity = plantSchedule.toPlantScheduleEntity()

                plantScheduleDao.updatePlantSchedule(entity)
            } catch (e: Exception) {
                Log.e("PlantScheduleRepositoryImpl", "updatePlantSchedule: ${e.message}")
            }
        }
    }
}