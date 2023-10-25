package com.example.recycleview.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val plantDao: PlantDao
) : ViewModel() {

    private val detailsEventChannel = Channel<DetailsEvent>()
    val detailsEvent = detailsEventChannel.receiveAsFlow()

    fun editPlant(plant: Plant) = viewModelScope.launch {
        detailsEventChannel.send(DetailsEvent.NavigateToEditScreen(plant))
    }

    private val _plantData = MutableLiveData<Plant>()
    val plantData: LiveData<Plant> = _plantData

    fun getPlant(id: Int) = viewModelScope.launch {
        val plant = plantDao.getSinglePlant(id).first()
        _plantData.postValue(plant)
    }

    private val _plantData1 = MutableStateFlow<Plant?>(null)
    val plantData1: StateFlow<Plant?> = _plantData1.asStateFlow()

    fun getPlant1(id: Int) {
        viewModelScope.launch {
            val plant = plantDao.getSinglePlant(id).first()
            _plantData1.value = plant
        }
    }

    sealed class DetailsEvent {
        data class NavigateToEditScreen(val plant: Plant) : DetailsEvent()
    }


}