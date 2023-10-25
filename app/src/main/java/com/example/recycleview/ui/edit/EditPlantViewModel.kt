package com.example.recycleview.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import com.example.recycleview.repo.PlantRepositoryImpl
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
class EditPlantViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val repository: PlantRepositoryImpl,
) : ViewModel() {

    private val _plantData = MutableStateFlow<Plant?>(null)
    val plantData: StateFlow<Plant?> = _plantData.asStateFlow()

    fun getPlant(id: Int) {
        viewModelScope.launch {
            val plant = plantDao.getSinglePlant(id).first()
            _plantData.value = plant
        }
    }
    fun savePlant(plant: Plant) = viewModelScope.launch {
        plantDao.insertPlant(plant)
        editPlantEventChannel.send(EditPlantEvent.NavigateToHomeScreen)
    }

    private val _mappedPhotos = MutableLiveData<String>()
    val mappedPhotos: LiveData<String> = _mappedPhotos
    fun mapPhotos(imagePath: String) = viewModelScope.launch {
        val photos = repository.mapPhotosFromExternalStorage(imagePath)
        _mappedPhotos.postValue(photos)
    }

    fun updatePlant(plant: Plant) = viewModelScope.launch {
        plantDao.updatePlant(plant)
//        editPlantEventChannel.send(EditPlantEvent.NavigateToDetailsScreen)
    }

    private val editPlantEventChannel = Channel<EditPlantEvent>()
    val editPlantEvent = editPlantEventChannel.receiveAsFlow()

    sealed class EditPlantEvent {
        object NavigateToHomeScreen : EditPlantEvent()
//        object NavigateToDetailsScreen : EditPlantEvent()
    }
}