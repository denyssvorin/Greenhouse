package com.example.recycleview.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recycleview.data.mappers.toPlantEntity
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.domain.Plant
import com.example.recycleview.repo.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditPlantViewModel @Inject constructor(
    private val plantDao: PlantDao,
    private val repository: PlantRepository
) : ViewModel() {

    private val _plantImageUri = MutableStateFlow<String?>(null)
    val plantImageUri: StateFlow<String?> = _plantImageUri.asStateFlow()

    fun getPlant(id: Int) {
        viewModelScope.launch {
            val plant = plantDao.getSinglePlant(id).first()

            _plantImageUri.value = plant.plantImagePath
            plantName = plant.plantName
            plantDescription = plant.plantDescription
        }
    }

    fun savePlant(plant: Plant) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                plantDao.upsertPlant(plant.toPlantEntity())
            }
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

    fun mapPhotos(imagePath: String) = viewModelScope.launch {
        val photo = repository.mapPhotosFromExternalStorage(imagePath)
        _plantImageUri.value = photo
    }
}