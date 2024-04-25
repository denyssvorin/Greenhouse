package com.example.recycleview.data.plantschedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantScheduleDao {

    @Query("SELECT * FROM plant_schedule_table WHERE plantId = :currentPlantId")
    fun getPlantScheduleByPlantId(currentPlantId: Int): Flow<List<PlantWateringScheduleEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantWateringSchedule(plantWateringScheduleEntity: PlantWateringScheduleEntity)

    @Delete
    suspend fun deleteSchedule(plantWateringScheduleEntity: PlantWateringScheduleEntity)
}