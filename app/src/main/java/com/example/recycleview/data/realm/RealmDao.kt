package com.example.recycleview.data.realm

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults

interface RealmDao<T : RealmObject> {
    val realm: Realm
    val clazz: Class<T>

    suspend fun upsert(entity: T) {
        realm.executeTransactionAsync {
            it.copyToRealmOrUpdate(entity)
        }
    }

    suspend fun getAll(): RealmQuery<T> {
        return realm.where(clazz)
    }

    suspend fun getById(field: String, id: String): T? {
        return realm.where(clazz).equalTo(field, id).findFirst()
    }

    suspend fun getListOfItemsByIdAsync(field: String, id: String): RealmResults<T> {
        return realm.where(clazz)
            .equalTo(field, id)
            .findAllAsync()
    }

    suspend fun delete(field: String, id: String) {
        realm.executeTransactionAsync {
            it
                .where(clazz)
                .equalTo(field, id)
                .findFirst()
                ?.deleteFromRealm()
        }
    }
}