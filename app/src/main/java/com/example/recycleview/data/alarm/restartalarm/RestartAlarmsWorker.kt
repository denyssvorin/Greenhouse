package com.example.recycleview.data.alarm.restartalarm

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recycleview.data.alarm.AlarmScheduler
import com.example.recycleview.data.mappers.toAlarmPlant
import com.example.recycleview.data.realm.plant.PlantDao
import com.example.recycleview.data.realm.plantschedule.PlantScheduleDao
import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestartAlarmsWorker constructor(
    private val plantDao: PlantDao,
    private val plantScheduleDao: PlantScheduleDao,
    private val alarmScheduler: AlarmScheduler,

    // AssistedInject
    private val context: Context,
    private val parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Main) {

            try {
                Log.i(TAG, "doWork")
                Log.i(TAG, "doWork thread name ${Thread.currentThread().name}")
                Log.i(TAG, "doWork thread id${Thread.currentThread().id}")

                val scheduleList =
                    plantScheduleDao
                        .getAll()
                        .findAllAsync()

                Log.i(TAG, "scheduleList: $scheduleList")

                scheduleList.forEach { plantScheduleEntity: PlantScheduleEntity ->

                    val plant = plantDao.getSinglePlant(plantScheduleEntity.plant?._id.toString())

                    plant?.let {
                        alarmScheduler.schedule(
                            plantScheduleEntity.toAlarmPlant(
                                plantName = plant.plantName,
                                plantImagePath = plant.plantImagePath,
                            )
                        )
                    }
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