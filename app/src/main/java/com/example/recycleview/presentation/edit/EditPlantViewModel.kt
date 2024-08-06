package com.example.recycleview.presentation.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.domain.imageconverter.PlantImageConverter
import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plant.PlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditPlantViewModel constructor(
    private val plantDao: PlantDao,
    private val repository: PlantImageConverter
) : ViewModel() {

    private val _plantImageUri = MutableStateFlow<String?>(null)
    val plantImageUri: StateFlow<String?> = _plantImageUri
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun getPlant(id: String) {
        viewModelScope.launch {
            val plant = plantDao.getSinglePlant(id)

            _plantImageUri.value = plant?.plantImagePath
            plantName = plant?.plantName ?: ""
            plantDescription = plant?.plantDescription ?: ""
        }
    }

    fun savePlant(plant: PlantEntity) {
        viewModelScope.launch {
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

    fun mapPhotos(imagePath: String) = viewModelScope.launch(Dispatchers.IO) {
        val photo = repository.mapPhotosFromExternalStorage(imagePath)
        _plantImageUri.value = photo
    }
}