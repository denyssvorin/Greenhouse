package com.example.recycleview.domain.repository

import androidx.paging.PagingData
import com.example.recycleview.domain.datastore.SortOrder
import com.example.recycleview.domain.models.Plant
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getPagingPlants(searchQuery: String, sortOrder: SortOrder): Flow<PagingData<Plant>>

    suspend fun getSinglePlant(plantId: String): Plant?

    suspend fun savePlant(plant: Plant)

    suspend fun delete(plantId: String)
}