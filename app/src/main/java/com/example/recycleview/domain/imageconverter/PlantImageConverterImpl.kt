package com.example.recycleview.domain.imageconverter

import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class PlantImageConverterImpl @Inject constructor(
    private val context: Application,
) : PlantImageConverter {
    override fun mapPhotosFromExternalStorage(imagePath: String): String {
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
        }
        return result
    }

    private fun readBytesFromContentUri(contentResolver: ContentResolver, contentUri: Uri): ByteArray? {
        return try {
            contentResolver.openInputStream(contentUri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveBytesToLocalFile(context: Context, byteArray: ByteArray, filename: String): String? {
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
}