package com.example.recycleview.domain.alarm.restartalarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.alarm.AlarmScheduler
import com.example.recycleview.domain.utils.toAlarmPlant
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class RestartAlarmsWorker @AssistedInject constructor(
    private val plantDao: PlantDao,
    private val plantScheduleDao: PlantScheduleDao,
    private val alarmScheduler: AlarmScheduler,
    @Assisted private val context: Context,
    @Assisted private val parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            try {
                Log.i(TAG, "doWork")
                val scheduleList: List<PlantScheduleEntity> =
                    plantScheduleDao.getAllSchedules()
                Log.i(TAG, "scheduleList: $scheduleList")

                scheduleList.forEach { plantScheduleEntity: PlantScheduleEntity ->

                    val plant = plantDao.getSinglePlant(plantScheduleEntity.plantId)

                    alarmScheduler.schedule(
                        plantScheduleEntity.toAlarmPlant(
                            plantName = plant.plantName,
                            plantImagePath = plant.plantImagePath,
                        )
                    )
                }
                Result.success()

            } catch (e: Exception) {
                Log.e("RestartAlarmsService", "Error accessing database", e)
                Result.failure()
            }

        }
    }

    companion object {
        private val TAG = RestartAlarmsWorker::class.qualifiedName
    }
}