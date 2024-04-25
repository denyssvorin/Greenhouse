package com.example.recycleview.data.alarm

interface AlarmScheduler {
    fun schedule(alarmItem: AlarmPlant)
    fun cancel(alarmItem: AlarmPlant)
}