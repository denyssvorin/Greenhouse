package com.example.recycleview.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.domain.repository.PlantRepository
import com.example.recycleview.domain.repository.PlantScheduleRepository
import com.example.recycleview.presentation.details.models.AlarmItem
import com.example.recycleview.presentation.details.models.PlantScheduleData
import com.example.recycleview.presentation.utils.mappers.toAlarmPlant
import com.example.recycleview.presentation.utils.mappers.toPlantSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val plantScheduleRepository: PlantScheduleRepository,
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
    val plantScheduleDataList: StateFlow<List<PlantSchedule>> = _plantScheduleDataList
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _plantNotificationItem = MutableStateFlow<AlarmItem?>(null)
    val plantNotificationItem: StateFlow<AlarmItem?> = _plantNotificationItem

    fun getPlant(plantId: String) = viewModelScope.launch {
        val plant = plantRepository.getSinglePlant(plantId)
        _plantData.value = plant
    }

    fun getPlantSchedules(plantId: String) = viewModelScope.launch {
        plantScheduleRepository.getPlantSchedulesByPlantId(plantId).collect { plantScheduleList ->
            _plantScheduleDataList.value = plantScheduleList
        }
    }

    fun setPlantNotificationItem(alarmItem: AlarmItem?) {
        _plantNotificationItem.value = alarmItem
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

    fun saveWateringSchedule(plantScheduleData: PlantScheduleData, scheduleId: String, plantId: String) =
        viewModelScope.launch {
            plantScheduleRepository.insertPlantSchedule(plantScheduleData.toPlantSchedule(scheduleId, plantId))
        }

    fun deleteSchedule(plantSchedule: PlantSchedule) = viewModelScope.launch {
        plantScheduleRepository.deleteSchedule(plantSchedule)
    }

    fun deletePlant(plantId: String, scheduleList: List<PlantSchedule>) = viewModelScope.launch {
        plantRepository.delete(plantId)
        cancelScheduleList(scheduleList)
    }

    private fun cancelScheduleList(scheduleList: List<PlantSchedule>) {
        scheduleList.forEach { alarmScheduler.cancel(it.id) }
    }


    fun updateWateringSchedule(plantScheduleData: PlantScheduleData, scheduleId: String, plantId: String) = viewModelScope.launch {
        plantScheduleRepository.updatePlantSchedule(plantScheduleData.toPlantSchedule(scheduleId, plantId))
    }
}