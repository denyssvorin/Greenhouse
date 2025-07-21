package com.example.recycleview.data.imagemanager

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PlantImageManagerImplTest {

    @MockK
    private lateinit var context: Application

    @MockK
    private lateinit var contentResolver: ContentResolver

    private lateinit var imageManager: PlantImageManagerImpl
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        every { context.contentResolver } returns contentResolver
        imageManager = PlantImageManagerImpl(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `mapPhotosFromExternalStorage should return mapwithContext when contentResolver query returns null`() = testScope.runTest {
        val imagePath = "prefix101suffix"
        val numericPart = "101"

        every { contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID),
            "${MediaStore.Images.Media._ID} = ?",
            arrayOf(numericPart),
            null
        ) } returns null

        val result = imageManager.mapPhotosFromExternalStorage(imagePath)
        assertEquals("map.withContext", result)
    }


    @Test
    fun `mapPhotosFromExternalStorage should rethrow CancellationException`() = testScope.runTest {
        val imagePath = "prefix666suffix"

        every { contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            any(),
            any(),
            any(),
            any()
        ) } throws CancellationException("Coroutine cancelled")

        try {
            imageManager.mapPhotosFromExternalStorage(imagePath)
            assert(false) // Should not reach here
        } catch (e: CancellationException) {
            assertEquals("Coroutine cancelled", e.message)
        }
    }
}