package com.example.recycleview.data.imageconverter

import android.content.ContentResolver
import android.content.Context
import android.net.Uri

interface PlantImageConverter {
    suspend fun mapPhotosFromExternalStorage(imagePath: String): String
    fun readBytesFromContentUri(contentResolver: ContentResolver, contentUri: Uri): ByteArray?
    fun saveBytesToLocalFile(context: Context, byteArray: ByteArray, filename: String): String?
}