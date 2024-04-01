package com.example.recycleview.repo

import androidx.paging.PagingData
import com.example.recycleview.data.Plant
import com.example.recycleview.data.datastore.SortOrder
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun mapPhotosFromExternalStorage(imagePath: String): String
    fun getPagingPlants(searchQuery: String, sortOrder: SortOrder): Flow<PagingData<Plant>>
}