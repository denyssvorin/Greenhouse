package com.example.recycleview.ui.home

import android.app.DownloadManager.Query
import android.net.Uri
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import com.example.recycleview.repo.PlantRepository
import com.example.recycleview.ui.home.additional.SharedPhotoEntity
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


    private val _loadedPhoto = MutableLiveData<SharedPhotoEntity?>()
    val loadedPhoto: LiveData<SharedPhotoEntity?> = _loadedPhoto
    fun loadPhotoByContentUri(uri: Uri) = viewModelScope.launch {
        val photo = repository.loadPhotoByContentUri(uri)
        _loadedPhoto.postValue(photo)
    }

    private val _loadedPhoto1 = MutableLiveData<List<SharedPhotoEntity?>>()
    val loadedPhoto1: LiveData<List<SharedPhotoEntity?>> = _loadedPhoto1
    fun loadPhotoByContentUri1(uri: Uri) = viewModelScope.launch {
        val photo1 = repository.loadPhotosFromExternalStorage(uri)
        _loadedPhoto1.postValue(photo1)
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