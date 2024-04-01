package com.example.recycleview.data.datastore

import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    val preferencesFlow: Flow<FilterPreferences>
    suspend fun saveSortOrder(sortOrder: SortOrder)
}