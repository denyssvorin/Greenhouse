package com.example.recycleview.presentation.edit

import com.example.recycleview.domain.imagemanager.PlantImageManager
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.domain.repository.PlantRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class EditPlantViewModelTest {

    private lateinit var viewModel: EditPlantViewModel
    private val plantRepository: PlantRepository = mockk()
    private val plantImageManager: PlantImageManager = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // use UnconfinedTestDispatcher for coroutine tests
        viewModel = EditPlantViewModel(plantRepository, plantImageManager)
    }

    @Test
    fun `test getPlant() updates plant details`() = runTest {
        // Arrange
        val plant = Plant("1", "image/path", "Plant Name", "Plant Description")
        coEvery { plantRepository.getSinglePlant("1") } returns plant

        // Act
        viewModel.getPlant("1")

        // Assert
        assertEquals("Plant Name", viewModel.plantName)
        assertEquals("Plant Description", viewModel.plantDescription)
        assertEquals("image/path", viewModel.plantImageUri.value)
    }

    @Test
    fun `test savePlant() calls repository save method`() = runTest {
        // Arrange
        val plant = Plant("1", "Plant Name", "Plant Description", "image/path")
        coEvery { plantRepository.savePlant(plant) } just Runs

        // Act
        viewModel.savePlant(plant)

        // Assert
        coVerify { plantRepository.savePlant(plant) }
    }

    @Test
    fun `test updatePlantNameTextField updates plantName`() {
        // Act
        viewModel.updatePlantNameTextField("New Plant Name")

        // Assert
        assertEquals("New Plant Name", viewModel.plantName)
    }

    @Test
    fun `test mapPhotos updates plantImageUri`() = runTest {
        // Arrange
        val imagePath = "path/to/image"
        val newPhotoPath = "path/to/mapped/image"
        coEvery { plantImageManager.mapPhotosFromExternalStorage(imagePath) } returns newPhotoPath

        // Act
        viewModel.mapPhotos(imagePath)

        // Assert
        assertEquals(newPhotoPath, viewModel.plantImageUri.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // main dispatcher
    }
}
