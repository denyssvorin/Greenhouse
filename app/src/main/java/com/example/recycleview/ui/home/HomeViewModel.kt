package com.example.recycleview.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.recycleview.data.Plant
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.repo.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    val plantPagingFlow: Flow<PagingData<Plant>> = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        repository.getPagingPlants(query, filterPreferences.sortOrder)
    }.debounce(100)

    var searchText by mutableStateOf(searchQuery.value)
        private set

    val state = MutableStateFlow(PlantScreenState())

    init {
        viewModelScope.launch {
            searchQuery.collect { newValue ->
                searchText = newValue
            }
        }
    }

    fun updateSearchQuery(text: String) {
        searchQuery.value = text
    }

    fun onAction(userAction: UserAction) {
        when (userAction) {
            UserAction.CloseIconClicked -> {
                state.value = state.value.copy(isSearchBarVisible = false)
            }

            UserAction.SearchIconClicked -> {
                state.value = state.value.copy(isSearchBarVisible = true)
            }

            UserAction.SortIconClicked -> {
                state.value = state.value.copy(isSortMenuVisible = true)
            }

            UserAction.SortMenuDismiss -> {
                state.value = state.value.copy(isSortMenuVisible = false)
            }

            is UserAction.SortItemClicked -> {
                when (userAction.type) {
                    SortType.A2Z -> sortPlantList(SortOrder.A2Z)
                    SortType.Z2A -> sortPlantList(SortOrder.Z2A)
                }
            }
        }
    }

    private fun sortPlantList(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.saveSortOrder(sortOrder)
        state.value = state.value.copy(
            isSortMenuVisible = false
        )
    }
}

sealed class UserAction {
    object SearchIconClicked : UserAction()
    object CloseIconClicked : UserAction()
    object SortIconClicked : UserAction()
    object SortMenuDismiss : UserAction()
//    data class TextFieldInput(val text: String) : UserAction()
    data class SortItemClicked(val type: SortType) : UserAction()
}

enum class SortType {
    A2Z,
    Z2A
}

data class PlantScreenState(
    val isSearchBarVisible: Boolean = false,
    val isSortMenuVisible: Boolean = false,
)