package com.example.recycleview.data.plantschedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantScheduleDao {

    @Query("SELECT * FROM plant_schedule_table WHERE plantId = :currentPlantId")
    fun getPlantSchedulesByPlantId(currentPlantId: String): Flow<List<PlantScheduleEntity>>

    @Query("SELECT * FROM plant_schedule_table")
    fun getAllSchedules(): List<PlantScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlantSchedule(plantScheduleEntity: PlantScheduleEntity)

    @Update
    suspend fun updatePlantSchedule(plantScheduleEntity: PlantScheduleEntity)

    @Delete
    suspend fun deleteSchedule(plantScheduleEntity: PlantScheduleEntity)
}