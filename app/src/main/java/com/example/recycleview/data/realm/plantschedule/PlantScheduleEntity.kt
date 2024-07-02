package com.example.recycleview.data.realm.plantschedule

import com.example.recycleview.data.realm.plant.PlantEntity
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PlantScheduleEntity: RealmObject() {
    @PrimaryKey
    var _id: String = ""
    var plant: PlantEntity? = null
    var notificationMessage: String = ""
    var time: String = ""
    var daysInterval: Int? = null
    var firstTriggerDate: String = ""
}

