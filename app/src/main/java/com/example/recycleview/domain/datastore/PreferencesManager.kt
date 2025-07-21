package com.example.recycleview.domain.datastore

import com.example.recycleview.data.datastore.FilterPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    val preferencesFlow: Flow<FilterPreferences>
    suspend fun saveSortOrder(sortOrder: SortOrder)
}