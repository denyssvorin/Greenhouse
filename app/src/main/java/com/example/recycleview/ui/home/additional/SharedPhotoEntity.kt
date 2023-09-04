package com.example.recycleview.ui.home.additional

import android.net.Uri

data class SharedPhotoEntity(
    val id: Long,
    val name: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri
)
