package com.example.recycleview.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.recycleview.domain.datastore.PreferencesManager
import com.example.recycleview.domain.datastore.SortOrder
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "PreferenceManager"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class FilterPreferences(val sortOrder: SortOrder)

@Singleton
class PreferencesManagerImpl @Inject constructor(context: Context) :
    PreferencesManager {

    private val dataStore = context.dataStore

    override val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortOrder.A2Z.name
            )
            FilterPreferences(sortOrder)
        }

    override suspend fun saveSortOrder(sortOrder: SortOrder) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SORT_ORDER] = sortOrder.name
            }
        } catch (e: CancellationException) {
            e.printStackTrace()
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }
}