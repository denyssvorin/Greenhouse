package com.example.recycleview.repo

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.paging.PagingData
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.plant.PlantEntity
import kotlinx.coroutines.flow.Flow

interface PlantRepository {
    suspend fun mapPhotosFromExternalStorage(imagePath: String): String
    fun readBytesFromContentUri(contentResolver: ContentResolver, contentUri: Uri): ByteArray?
    fun saveBytesToLocalFile(context: Context, byteArray: ByteArray, filename: String): String?
    fun getPagingPlants(searchQuery: String, sortOrder: SortOrder): Flow<PagingData<PlantEntity>>
}