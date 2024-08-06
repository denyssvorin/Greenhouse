package com.example.recycleview.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plant.PlantEntity
import com.example.recycleview.data.realm.plantschedule.PlantScheduleDao
import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.models.PlantScheduleData
import com.example.recycleview.presentation.utils.mappers.toAlarmPlant
import com.example.recycleview.presentation.utils.mappers.toPlantScheduleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _plantData = MutableStateFlow<PlantEntity?>(null)
    val plantData: StateFlow<PlantEntity?> = _plantData
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private val _plantScheduleDataList = MutableStateFlow<List<PlantScheduleEntity>>(emptyList())
    val plantScheduleDataList: StateFlow<List<PlantScheduleEntity>> =
        _plantScheduleDataList
            .asStateFlow()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun getPlant(id: String) = viewModelScope.launch {
        val plant = plantDao.getSinglePlant(id)
        _plantData.value = plant
    }

    fun getPlantSchedules(id: String) = viewModelScope.launch {
        plantScheduleDao.getPlantSchedulesByPlantId(id).collect { plantScheduleEntityList ->
            _plantScheduleDataList.value = plantScheduleEntityList
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

    fun cancelSchedule(scheduleId: Int) {
        alarmScheduler.cancel(scheduleId)
    }

    fun saveWateringSchedule(item: PlantScheduleData, scheduleId: String, plantEntityId: String) =
        viewModelScope.launch {
            val entity = item.toPlantScheduleEntity(scheduleId)
            plantScheduleDao.insertPlantSchedule(entity, plantEntityId)
        }

    fun deleteSchedule(plantScheduleEntityId: String) = viewModelScope.launch {
        plantScheduleDao.deleteSchedule(plantScheduleEntityId)
    }

    fun deletePlant(plantId: String) = viewModelScope.launch {
        plantDao.deletePlant(plantId)
    }
}