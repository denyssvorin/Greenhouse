package com.example.recycleview.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.repository.mappers.toPlant
import com.example.recycleview.data.repository.mappers.toPlantEntity
import com.example.recycleview.domain.datastore.SortOrder
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.repository.PlantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class PlantRepositoryImpl @Inject constructor(
    private val plantDao: PlantDao,
) : PlantRepository {

    override fun getPagingPlants(
        searchQuery: String,
        sortOrder: SortOrder,
    ): Flow<PagingData<Plant>> {
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
        ).flow.map { pagingData: PagingData<PlantEntity> ->
            pagingData.map { it.toPlant() }
        }
    }

    private fun getPlants(
        limit: Int,
        offset: Int,
        searchQuery: String,
        sortOrder: SortOrder,
    ): Flow<List<PlantEntity>> {
        return plantDao.getPlants(
            limit = limit,
            offset = offset,
            searchText = searchQuery,
            sortOrder = sortOrder
        ).catch { e ->
            if (e is CancellationException) throw e
            e.printStackTrace()
            emit(emptyList())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getSinglePlant(plantId: String): Plant? = withContext(Dispatchers.IO) {
        return@withContext try {
            plantDao.getSinglePlant(plantId).toPlant()
        } catch (e: CancellationException) {
            e.printStackTrace()
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun savePlant(plant: Plant) {
        withContext(Dispatchers.IO) {
            try {
                plantDao.upsertPlant(plant.toPlantEntity())
            } catch (e: CancellationException) {
                e.printStackTrace()
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun delete(plantId: String) {
        withContext(Dispatchers.IO) {
            try {
                plantDao.getSinglePlant(plantId).also {
                    plantDao.deletePlant(it)
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}