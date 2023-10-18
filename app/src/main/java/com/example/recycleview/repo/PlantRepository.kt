package com.example.recycleview.repo

import androidx.paging.PagingData
import com.example.recycleview.data.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun mapPhotosFromExternalStorage(imagePath: String): String
    fun getPagingPlants(searchQuery: String): Flow<PagingData<Plant>>
}