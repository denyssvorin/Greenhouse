package com.example.recycleview.data.plant

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.recycleview.data.datastore.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    fun getPlants(
        searchText: String, sortOrder: SortOrder,
        limit: Int, offset: Int
    ): List<PlantEntity> =
        when(sortOrder) {
            SortOrder.A2Z -> getPlantsSortedA2Z(searchText, limit, offset)
            SortOrder.Z2A -> getPlantsSortedZ2A(searchText, limit, offset)
        }
    @Query("SELECT * FROM plant_table WHERE plantName LIKE '%' || :searchText || '%' " +
            "ORDER BY plantName ASC " +
            "LIMIT :limit " +
            "OFFSET :offset")
    fun getPlantsSortedA2Z(searchText: String, limit: Int, offset: Int): List<PlantEntity>
    @Query("SELECT * FROM plant_table WHERE plantName LIKE '%' || :searchText || '%' " +
            "ORDER BY plantName DESC " +
            "LIMIT :limit " +
            "OFFSET :offset")
    fun getPlantsSortedZ2A(searchText: String, limit: Int, offset: Int): List<PlantEntity>

    @Query("SELECT * FROM plant_table WHERE plantId LIKE '%' || :id || '%'")
    fun getSinglePlant(id: Int): Flow<PlantEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlant(plantEntity: PlantEntity)

    @Upsert
    fun upsertPlant(plantEntity: PlantEntity)

    @Update
    fun updatePlant(plantEntity: PlantEntity)

    @Delete
    fun deletePlant(plantEntity: PlantEntity)
}