package com.example.recycleview.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import com.example.recycleview.repo.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val repository: PlantRepository,
) : ViewModel() {

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

    private val editPlantEventChannel = Channel<EditPlantEvent>()
    val editPlantEvent = editPlantEventChannel.receiveAsFlow()

    sealed class EditPlantEvent {
        object NavigateToHomeScreen : EditPlantEvent()
    }
}