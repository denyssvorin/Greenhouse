package com.example.recycleview.domain.models

import java.time.LocalDate
import java.time.LocalTime

data class PlantScheduleData(
    val notificationMessage: String,
    val time: LocalTime,
    val daysInterval: Int,
    val firstTriggerDate: LocalDate
)