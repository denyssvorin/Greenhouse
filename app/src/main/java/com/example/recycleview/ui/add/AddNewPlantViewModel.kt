package com.example.recycleview.ui.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import com.example.recycleview.repo.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddNewPlantViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val repository: PlantRepository
) : ViewModel() {

    fun savePlant(plant: Plant) = viewModelScope.launch {

        withContext(Dispatchers.IO) {
            plantDao.upsertPlant(plant)
        }
    }


    var plantName by mutableStateOf("")
        private set
    var plantDescription by mutableStateOf("")
        private set

    fun updatePlantNameTextField(text: String) {
        plantName = text
    }

    fun updatePlantDescriptionTextField(text: String) {
        plantDescription = text
    }

    private val _mappedPhotos = MutableStateFlow<String?>(null)
    val mappedPhotos: StateFlow<String?> = _mappedPhotos

    fun mapPhotos(imagePath: String) = viewModelScope.launch {
        val photos = async {
            repository.mapPhotosFromExternalStorage(imagePath)
        }

        val result = photos.await()
        _mappedPhotos.value = result
    }
}