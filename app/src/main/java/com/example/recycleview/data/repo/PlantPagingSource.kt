package com.example.recycleview.data.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recycleview.data.plant.PlantEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

typealias PlantReposDBPageLoader = suspend (limit: Int, offset: Int) -> Flow<List<PlantEntity>>

class PlantPagingSource(
    private val dbPageLoader: PlantReposDBPageLoader,
    private val pageSize: Int
): PagingSource<Int, PlantEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlantEntity> {
        val pageIndex = params.key ?: 1

        val offset = (pageIndex - 1) * pageSize
        val limit = if (offset == 0) {
            params.loadSize / 3
        } else {
            params.loadSize
        }
        return try {
            val dbResponse = dbPageLoader.invoke(limit, offset).first()

            LoadResult.Page(
                data = dbResponse,
                prevKey = if (pageIndex == 1) null else pageIndex.minus(1),
                nextKey = if (dbResponse.size == limit) pageIndex + (limit / pageSize) else null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, PlantEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }
}