package com.example.recycleview.data.repo

import androidx.paging.PagingData
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.plant.PlantEntity
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    fun getPagingPlants(searchQuery: String, sortOrder: SortOrder): Flow<PagingData<PlantEntity>>

    fun delete(plantId: String)
}