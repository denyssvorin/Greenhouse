package com.example.recycleview.presentation.activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun `onPermissionResult adds permission to queue if not granted`() {
        // Act
        viewModel.onPermissionResult("android.permission.CAMERA", isGranted = false)

        // Assert
        val result = viewModel.visiblePermissionDialogQueue.value
        assertEquals(listOf("android.permission.CAMERA"), result)
    }

    @Test
    fun `dismissDialog removes the first permission from queue`() {
        // Arrange
        viewModel.onPermissionResult("android.permission.CAMERA", isGranted = false)
        viewModel.onPermissionResult("android.permission.READ_CONTACTS", isGranted = false)

        // Act
        viewModel.dismissDialog()

        // Assert
        val result = viewModel.visiblePermissionDialogQueue.value
        assertEquals(listOf("android.permission.READ_CONTACTS"), result)
    }

    @Test
    fun `dismissDialog does nothing if queue is empty`() {
        // Act
        viewModel.dismissDialog()

        // Assert
        val result = viewModel.visiblePermissionDialogQueue.value
        assertNull(result)
    }
}