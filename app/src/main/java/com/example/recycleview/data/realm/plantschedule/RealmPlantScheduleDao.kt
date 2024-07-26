package com.example.recycleview.data.realm.plantschedule

import android.util.Log
import com.example.recycleview.data.realm.RealmDao
import com.example.recycleview.data.realm.plant.PlantEntity
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

interface PlantScheduleDao : RealmDao<PlantScheduleEntity> {

    suspend fun getPlantSchedulesByPlantId(id: String): Flow<List<PlantScheduleEntity>> {
        return withContext(Dispatchers.Main) {
            channelFlow {
                val schedules = getListOfItemsByIdAsync("plant._id", id)
                val changeListener =
                    OrderedRealmCollectionChangeListener<RealmResults<PlantScheduleEntity>> { updatedResults, _ ->
                        val unmanagedDataList = realm.copyFromRealm(updatedResults).toList()
                        trySend(unmanagedDataList).isSuccess
                    }
                schedules.addChangeListener(changeListener)
                awaitClose {
                    schedules.removeChangeListener(changeListener)
                }
            }
        }
    }

    suspend fun insertPlantSchedule(scheduleEntity: PlantScheduleEntity, plantEntityId: String) {
        Log.i("TAG", "scheduleEntity._id = ${scheduleEntity._id} ")

        realm.executeTransactionAsync({ realm ->
            val plantEntity = realm.where(PlantEntity::class.java)
                .equalTo("_id", plantEntityId)
                .findFirst()

            val entity = scheduleEntity.apply {
                plant = plantEntity
            }

            realm.insertOrUpdate(entity)
        }, {
            // onSuccess callback
            Log.i("RealmTransaction", "Transaction was successful!")

        }, { error ->
            // onError callback
            Log.e("RealmTransaction", "Transaction failed: ${error.message}")
            error.printStackTrace()
        })
    }

    suspend fun deleteSchedule(plantScheduleEntityId: String) {
        delete("_id", plantScheduleEntityId)
    }
}

class RealmPlantScheduleDaoImpl(
    private val r: Realm
) : PlantScheduleDao {
    override val realm: Realm
        get() = r
    override val clazz: Class<PlantScheduleEntity>
        get() = PlantScheduleEntity::class.java

}