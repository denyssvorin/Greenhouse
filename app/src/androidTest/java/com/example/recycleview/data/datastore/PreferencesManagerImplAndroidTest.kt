package com.example.recycleview.data.datastore

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.recycleview.domain.datastore.SortOrder
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PreferencesManagerImplAndroidTest {

    private lateinit var context: Context
    private lateinit var preferencesManager: PreferencesManagerImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        preferencesManager = PreferencesManagerImpl(context)
    }

    @Test
    fun saveSortOrder_updatesStoredValue() = runBlocking {
        preferencesManager.saveSortOrder(SortOrder.Z2A)

        val prefs = preferencesManager.preferencesFlow.first()
        assertEquals(SortOrder.Z2A, prefs.sortOrder)
    }
}