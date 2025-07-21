package com.example.recycleview.presentation.home

import com.example.recycleview.data.datastore.FilterPreferences
import com.example.recycleview.domain.datastore.PreferencesManager
import com.example.recycleview.domain.datastore.SortOrder
import com.example.recycleview.domain.repository.PlantRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description


class HomeViewModelTest {

    @get:Rule
    val coroutinesTestRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private val repository: PlantRepository = mockk(relaxed = true)
    private val preferencesManager: PreferencesManager = mockk(relaxed = true)

    private val fakePreferencesFlow = MutableStateFlow(FilterPreferences(SortOrder.A2Z))

    @Before
    fun setup() {
        every { preferencesManager.preferencesFlow } returns fakePreferencesFlow

        viewModel = HomeViewModel(repository, preferencesManager)
    }

    @Test
    fun `updateSearchQuery updates searchText`() = runTest {
        viewModel.updateSearchQuery("lavender")
        advanceUntilIdle()
        assertEquals("lavender", viewModel.searchText)
    }


    @Test
    fun `CloseIconClicked hides search bar`() = runTest {
        viewModel.onAction(UserAction.CloseIconClicked)
        assertFalse(viewModel.topAppBarState.value.isSearchBarVisible)
    }

    @Test
    fun `SortMenuDismiss hides sort menu`() = runTest {
        viewModel.onAction(UserAction.SortMenuDismiss)
        assertFalse(viewModel.topAppBarState.value.isSortMenuVisible)
    }

    @Test
    fun `SortItemClicked saves sort order and hides menu`() = runTest {
        coEvery { preferencesManager.saveSortOrder(SortOrder.Z2A) } just Runs

        viewModel.onAction(UserAction.SortItemClicked(SortType.Z2A))
        advanceUntilIdle()

        coVerify { preferencesManager.saveSortOrder(SortOrder.Z2A) }
        assertFalse(viewModel.topAppBarState.value.isSortMenuVisible)
    }
}

@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}