package com.example.recycleview.domain.alarm

import com.example.recycleview.data.scheduler.alarm.AlarmPlant

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmPlant)
    fun cancel(scheduleId: Int)
}