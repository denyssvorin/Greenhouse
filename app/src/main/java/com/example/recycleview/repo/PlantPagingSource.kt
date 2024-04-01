package com.example.recycleview.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.recycleview.data.Plant

typealias PlantReposDBPageLoader = suspend (limit: Int, offset: Int) -> List<Plant>

class PlantPagingSource(
    private val dbPageLoader: PlantReposDBPageLoader,
    private val pageSize: Int
): PagingSource<Int, Plant>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Plant> {
        val pageIndex = params.key ?: 1

        val offset = (pageIndex - 1) * pageSize
        val limit = if (offset == 0) {
            params.loadSize / 3
        } else {
            params.loadSize
        }
        return try {
            val dbResponse = dbPageLoader.invoke(limit, offset)

            LoadResult.Page(
                data = dbResponse,
                prevKey = if (pageIndex == 1) null else pageIndex.minus(1),
                nextKey = if (dbResponse.size == limit) pageIndex + (limit / pageSize) else null)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Plant>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)

        }
    }
}