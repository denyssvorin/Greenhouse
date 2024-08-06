package com.example.recycleview.domain.alarm

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmPlant)
    fun cancel(scheduleId: String)
}