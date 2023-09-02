package com.example.recycleview.ui.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import com.example.recycleview.repo.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val repository: PlantRepository
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val _plants = searchQuery.flatMapLatest { query ->
        plantDao.getPlants(query)
    }
    val plants = _plants.asLiveData()




    private val _loadedPhoto = MutableLiveData<Uri>()
    val loadedPhoto = _loadedPhoto
    fun loadPhotoByContentUri(uri: Uri) = viewModelScope.launch {
        _loadedPhoto.value = repository.loadPhotoByContentUri(uri)?.contentUri
    }




    fun onPlantSelected(plant: Plant) = viewModelScope.launch {
        plantEventChannel.send(HomeEvent.NavigateToDetailsScreen(plant))
    }
    fun onAddNewPlant() = viewModelScope.launch {
        plantEventChannel.send(HomeEvent.NavigateToEditScreen)
    }

    private val plantEventChannel = Channel<HomeEvent>()
    val plantEvent = plantEventChannel.receiveAsFlow()

    sealed class HomeEvent {
        data class NavigateToDetailsScreen(val plant: Plant): HomeEvent()
        object NavigateToEditScreen: HomeEvent()
    }
}