package com.example.recycleview.ui.home.additional

import com.example.recycleview.data.Plant

fun SharedPhotoEntity.toPlant(): Plant {

    return Plant(plantImagePath = contentUri.toString())
}