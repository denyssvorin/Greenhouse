package com.example.recycleview.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Query("SELECT * FROM plant_table WHERE plantName LIKE '%' || :searchText || '%' " +
            "ORDER BY plantName LIMIT :limit OFFSET :offset")
    fun getPlants(searchText: String, limit: Int, offset: Int): List<Plant>

    @Query("SELECT * FROM plant_table WHERE plantId LIKE '%' || :id || '%'")
    fun getSinglePlant(id: Int): Flow<Plant>

    @Upsert
    fun upsertPlant(plant: Plant)

    @Delete
    fun deletePlant(plant: Plant)
}