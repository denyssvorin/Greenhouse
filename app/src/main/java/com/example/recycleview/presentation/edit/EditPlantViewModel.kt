package com.example.recycleview.presentation.edit


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditPlantViewModel @Inject constructor(
    private val plantRepository: PlantRepository,
    private val plantImageManager: PlantImageManager,
) : ViewModel() {

    private val _plantImageUri = MutableStateFlow<String?>(null)
    val plantImageUri: StateFlow<String?> = _plantImageUri
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    var plantName by mutableStateOf("")
        private set
    var plantDescription by mutableStateOf("")
        private set


    fun getPlant(id: String) {
        viewModelScope.launch {
            val plant = plantRepository.getSinglePlant(id)

            if (plant != null) {
                _plantImageUri.value = plant.imagePath
                plantName = plant.name
                plantDescription = plant.description
            }
        }
    }

    fun savePlant(plant: Plant) = viewModelScope.launch {
        plantRepository.savePlant(plant)
    }

    fun updatePlantNameTextField(text: String) {
        plantName = text
    }

    fun updatePlantDescriptionTextField(text: String) {
        plantDescription = text
    }

    fun mapPhotos(imagePath: String) = viewModelScope.launch {
        val photo = plantImageManager.mapPhotosFromExternalStorage(imagePath)
        _plantImageUri.value = photo
    }
}