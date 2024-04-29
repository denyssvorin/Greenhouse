package com.example.recycleview.repo

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.plant.PlantDatabase
import com.example.recycleview.data.plant.PlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val context: Application,
    private val db: PlantDatabase
) : PlantRepository {
    override suspend fun mapPhotosFromExternalStorage(imagePath: String): String {
        return withContext(Dispatchers.IO) {
            val numericPart = imagePath.replace(Regex("[^0-9]"), "")
            var result = "mapDefault"

            val projection = arrayOf(
                MediaStore.Images.Media._ID
            )

            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(numericPart)

            context.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idColumn)
                    val currentContentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val byteArray =
                        readBytesFromContentUri(context.contentResolver, currentContentUri)
                    val localUriString = byteArray?.let {
                        saveBytesToLocalFile(context, it, "$numericPart.png")
                    }
                    result = localUriString
                        ?: "default"
                } else {
                    Log.e("TAG", "mapPhotosFromExternalStorage: error")
                }
                result
            } ?: "map.withContext"
        }
    }

    override fun readBytesFromContentUri(contentResolver: ContentResolver, contentUri: Uri): ByteArray? {
        return try {
            contentResolver.openInputStream(contentUri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

     override fun saveBytesToLocalFile(context: Context, byteArray: ByteArray, filename: String): String? {
        return try {
            val file = File(context.filesDir, filename)
            FileOutputStream(file).use { outputStream ->
                outputStream.write(byteArray)
            }
            file.toUri().toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

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
                PlantPagingSource(
                    dbLoader,
                    PAGE_SIZE
                )
            }
        ).flow
    }

    private suspend fun getPlants(
        limit: Int,
        offset: Int,
        searchQuery: String,
        sortOrder: SortOrder
    ): List<PlantEntity> =
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