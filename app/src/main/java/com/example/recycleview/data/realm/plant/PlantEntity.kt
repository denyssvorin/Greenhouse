package com.example.recycleview.data.realm.plant

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PlantEntity: RealmObject() {
    @PrimaryKey
    var _id: String = ""
    var plantImagePath: String? = ""
    var plantName: String = ""
    var plantDescription: String = ""
}
