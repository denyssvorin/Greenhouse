package com.example.recycleview.data.alarm.restartalarm

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.recycleview.data.alarm.AlarmScheduler
import com.example.recycleview.data.mappers.toAlarmPlant
import com.example.recycleview.data.plant.PlantDao
import com.example.recycleview.data.plantschedule.PlantScheduleDao
import com.example.recycleview.data.plantschedule.PlantWateringScheduleEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RestartAlarmsService : JobIntentService() {

    private var job: Job? = null

    @Inject
    lateinit var plantScheduleDao: PlantScheduleDao

    @Inject
    lateinit var plantDao: PlantDao

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onHandleWork(intent: Intent) {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduleList: List<PlantWateringScheduleEntity> =
                    plantScheduleDao.getAllSchedules()
                Log.i("TAG", "scheduleList: $scheduleList")

                scheduleList.forEach { plantWateringScheduleEntity: PlantWateringScheduleEntity ->

                    val plant = plantDao.getSinglePlant(plantWateringScheduleEntity.plantId)

                    alarmScheduler.schedule(
                        plantWateringScheduleEntity.toAlarmPlant(
                            plantName = plant.plantName,
                            plantImagePath = plant.plantImagePath,
                        )
                    )
                }


            } catch (e: Exception) {
                Log.e("RestartAlarmsService", "Error accessing database", e)
            } finally {
                stopSelf()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        const val JOB_ID = 1002

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, RestartAlarmsService::class.java, JOB_ID, work)
        }
    }
}