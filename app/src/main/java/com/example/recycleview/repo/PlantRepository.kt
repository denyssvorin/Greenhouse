package com.example.recycleview.repo

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlantRepository @Inject constructor(
    private val context: Application
) {
    suspend fun mapPhotosFromExternalStorage(imagePath: String): String {
        return withContext(Dispatchers.IO) {
            val numericPart = imagePath.replace(Regex("[^0-9]"), "")
            var result = "null"

            val projection = arrayOf(
                MediaStore.Images.Media._ID
            )

            val selection = "${MediaStore.Images.Media._ID} = ?"
            val selectionArgs = arrayOf(numericPart)
            Log.d("TAG", "EditPlantFragment: selectionArgs: $selectionArgs")

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
                    result = currentContentUri.toString()

                } else {
                    Log.d("TAG", "mapPhotosFromExternalStorage: error")
                }
                result
            } ?: "null"
        }
    }
}