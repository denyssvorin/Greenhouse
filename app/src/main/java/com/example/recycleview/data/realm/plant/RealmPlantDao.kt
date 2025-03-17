package com.example.recycleview.data.realm.plant

import android.util.Log
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.realm.RealmDao
import io.realm.Case
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

interface PlantDao : RealmDao<PlantEntity> {

    suspend fun getAllPlants(
        searchText: String,
        sortOrder: SortOrder
    ): Flow<List<PlantEntity>> = channelFlow {
        val results = withContext(Dispatchers.Main) {

            Log.i("TAG", "getAll thread name ${Thread.currentThread().name}")
            Log.i("TAG", "getAll thread id ${Thread.currentThread().id}")

            getAll()
                .contains("plantName", searchText, Case.INSENSITIVE)
                .sort(
                    "plantName", when (sortOrder) {
                        SortOrder.A2Z -> io.realm.Sort.ASCENDING
                        SortOrder.Z2A -> io.realm.Sort.DESCENDING
                    }
                )
                .findAllAsync()
        }

        val listener =
            OrderedRealmCollectionChangeListener<RealmResults<PlantEntity>> { updatedResults, _ ->
                val unmanagedDataList = realm.copyFromRealm(updatedResults).toList()
                trySend(unmanagedDataList).isSuccess
            }
        results.addChangeListener(listener)
        awaitClose {
            results.removeChangeListener(listener)
        }
    }


    suspend fun getSinglePlant(id: String): PlantEntity? {
        return getById("_id", id)?.let { realm.copyFromRealm(it) }

    }

    suspend fun upsertPlant(plantEntity: PlantEntity) {
        upsert(plantEntity)
    }

    suspend fun deletePlant(plantId: String) {
        delete("_id", plantId)
    }
}

class RealmPlantDaoImpl constructor(
    private val r: Realm
) : PlantDao {
    override val realm: Realm
        get() = r
    override val clazz: Class<PlantEntity>
        get() = PlantEntity::class.java
}