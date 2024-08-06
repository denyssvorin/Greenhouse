package com.example.recycleview.domain.imageconverter

interface PlantImageConverter {
    fun mapPhotosFromExternalStorage(imagePath: String): String
}