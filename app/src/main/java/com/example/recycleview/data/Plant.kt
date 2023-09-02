package com.example.recycleview.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "plant_table")
@Parcelize
data class Plant(
    @PrimaryKey(autoGenerate = true) val plantId: Int = 0,
    val plantImagePath: String,
    val plantName: String = "plant_name",
    val plantDescription: String = "plant_description"
) : Parcelable
