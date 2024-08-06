package com.example.recycleview.presentation.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.domain.imageconverter.PlantImageConverter
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.presentation.uitls.mappers.toPlantEntity
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
class EditPlantViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val imageConverter: PlantImageConverter
) : ViewModel() {

    private val _plantImageUri = MutableStateFlow<String?>(null)
    val plantImageUri: StateFlow<String?> = _plantImageUri
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun getPlant(plantId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val plant = plantDao.getSinglePlant(plantId)

            _plantImageUri.value = plant.plantImagePath
            plantName = plant.plantName
            plantDescription = plant.plantDescription
        }
    }

    fun savePlant(plant: Plant) {
        viewModelScope.launch(Dispatchers.IO) {
            plantDao.upsertPlant(plant.toPlantEntity())
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
        val photo = imageConverter.mapPhotosFromExternalStorage(imagePath)
        _plantImageUri.value = photo
    }
}