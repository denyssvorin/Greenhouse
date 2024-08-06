package com.example.recycleview.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.plant.PlantDatabase
import com.example.recycleview.data.plant.PlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val db: PlantDatabase,
) : PlantRepository {

    override fun getPagingPlants(
        searchQuery: String,
        sortOrder: SortOrder
    ): Flow<PagingData<PlantEntity>> {
        val dbLoader: PlantReposDBPageLoader = { limit, offset ->
            getPlants(limit, offset, searchQuery, sortOrder)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PlantPagingSource(dbLoader, PAGE_SIZE)
            }
        ).flow
    }

    override fun delete(plantId: String) {
        db.plantDao().getSinglePlant(plantId).also {
            db.plantDao().deletePlant(it)
        }
    }

    private suspend fun getPlants(
        limit: Int,
        offset: Int,
        searchQuery: String,
        sortOrder: SortOrder
    ): Flow<List<PlantEntity>> =
        withContext(Dispatchers.IO) {
            val list = db.plantDao().getPlants(
                limit = limit,
                offset = offset,
                searchText = searchQuery,
                sortOrder = sortOrder
            )
            return@withContext list
        }

    companion object {
        const val PAGE_SIZE = 20
    }
}