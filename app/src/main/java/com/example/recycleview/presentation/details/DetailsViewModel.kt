package com.example.recycleview.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.domain.models.PlantScheduleData
import com.example.recycleview.presentation.uitls.mappers.toAlarmPlant
import com.example.recycleview.presentation.uitls.mappers.toPlant
import com.example.recycleview.presentation.uitls.mappers.toPlantEntity
import com.example.recycleview.presentation.uitls.mappers.toPlantSchedule
import com.example.recycleview.presentation.uitls.mappers.toPlantScheduleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val plantScheduleDao: PlantScheduleDao,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _plantData = MutableStateFlow<Plant?>(null)
    val plantData: StateFlow<Plant?> = _plantData
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private val _plantScheduleDataList = MutableStateFlow<List<PlantSchedule>>(emptyList())
    val plantScheduleDataList: StateFlow<List<PlantSchedule>> =
        _plantScheduleDataList
            .asStateFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun getPlant(plantId: String) = viewModelScope.launch(Dispatchers.IO) {
        val plant = plantDao.getSinglePlant(plantId).toPlant()
        _plantData.value = plant
    }

    fun getPlantSchedules(plantId: String) = viewModelScope.launch(Dispatchers.IO) {
        plantScheduleDao.getPlantSchedulesByPlantId(plantId).collect { plantScheduleEntityList ->
            val updatedList = plantScheduleEntityList.map { it.toPlantSchedule() }
            _plantScheduleDataList.value = updatedList.toMutableList()
        }
    }

    fun scheduleWatering(
        item: PlantScheduleData,
        scheduleId: String,
        plantId: String,
        plantName: String,
        plantImagePath: String?,
    ) {
        val alarmPlant = item.toAlarmPlant(
            scheduleId = scheduleId,
            plantId = plantId,
            plantName = plantName,
            plantImagePath = plantImagePath,
        )
        alarmScheduler.schedule(alarmPlant)
    }

    fun cancelSchedule(scheduleId: String) {
        alarmScheduler.cancel(scheduleId)
    }

    fun saveWateringSchedule(item: PlantScheduleData, scheduleId: String, plantEntityId: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val entity = item.toPlantScheduleEntity(scheduleId, plantEntityId)
            plantScheduleDao.insertPlantSchedule(entity)
        }

    fun deleteSchedule(plantSchedule: PlantSchedule) = viewModelScope.launch(Dispatchers.IO) {
        plantScheduleDao.deleteSchedule(plantSchedule.toPlantScheduleEntity())
    }

    fun deletePlant(plant: Plant) = viewModelScope.launch(Dispatchers.IO) {
        plantDao.deletePlant(plant.toPlantEntity())
    }
}