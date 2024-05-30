package com.example.recycleview.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.alarm.AlarmScheduler
import com.example.recycleview.data.mappers.toAlarmPlant
import com.example.recycleview.data.mappers.toAlarmPlantDeletion
import com.example.recycleview.data.mappers.toPlant
import com.example.recycleview.data.mappers.toPlantWateringSchedule
import com.example.recycleview.data.mappers.toPlantWateringScheduleEntity
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.domain.Plant
import com.example.recycleview.domain.PlantScheduleData
import com.example.recycleview.domain.PlantWateringSchedule
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

    private val _plantScheduleDataList = MutableStateFlow<MutableList<PlantWateringSchedule>>(mutableListOf())
    val plantScheduleDataList: StateFlow<MutableList<PlantWateringSchedule>> =
        _plantScheduleDataList
            .asStateFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                mutableListOf()
            )

    fun getPlant(id: Int) = viewModelScope.launch(Dispatchers.IO) {
            val plant = plantDao.getSinglePlant(id).toPlant()
            _plantData.value = plant
    }

    fun getPlantSchedule(id: Int) = viewModelScope.launch {
        plantScheduleDao.getPlantScheduleByPlantId(id).collect { plantScheduleEntityList ->
            val updatedList = plantScheduleEntityList.map { it.toPlantWateringSchedule() }
            _plantScheduleDataList.value = updatedList.toMutableList()
        }
    }


    fun scheduleWatering(
        item: PlantScheduleData,
        scheduleId: String,
        plantId: Int,
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

    fun cancelSchedule(item: PlantWateringSchedule) {
        val alarmPlant = item.toAlarmPlantDeletion()
        alarmScheduler.cancel(alarmPlant)
    }

    fun saveWateringSchedule(item: PlantScheduleData, scheduleId: String, plantId: Int) =
        viewModelScope.launch {
            val entity =
                item.toPlantWateringScheduleEntity(scheduleId = scheduleId, plantId = plantId)
            plantScheduleDao.insertPlantWateringSchedule(entity)
        }

    fun deleteSchedule(item: PlantWateringSchedule) = viewModelScope.launch {
        val plantWateringScheduleEntity = item.toPlantWateringScheduleEntity()
        plantScheduleDao.deleteSchedule(plantWateringScheduleEntity)
    }
}