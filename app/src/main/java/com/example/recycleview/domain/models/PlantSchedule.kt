package com.example.recycleview.domain.models

data class PlantSchedule(
    var id: String,
    var plantId: String,
    var notificationMessage: String,
    var time: String,
    var daysInterval: Int,
    var firstTriggerDate: String
)