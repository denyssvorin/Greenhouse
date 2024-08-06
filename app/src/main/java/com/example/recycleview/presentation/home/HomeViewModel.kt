package com.example.recycleview.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.recycleview.data.datastore.PreferencesManager
import com.example.recycleview.data.datastore.SortOrder
import com.example.recycleview.data.plant.PlantEntity
import com.example.recycleview.data.repo.PlantRepository
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.presentation.uitls.mappers.toPlant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlantRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val preferencesFlow = preferencesManager.preferencesFlow

    @OptIn(FlowPreview::class)
    private val _plantEntityPagingFlow: Flow<PagingData<PlantEntity>> = combine(
        searchQuery.debounce(300),
        preferencesFlow,
    ) { query, filterPreferences, ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        repository.getPagingPlants(query, filterPreferences.sortOrder)
    }

    val plantPagingFlow: Flow<PagingData<Plant>> =
        _plantEntityPagingFlow.map { pagingData: PagingData<PlantEntity> ->
            pagingData.map { data: PlantEntity ->
                data.toPlant()
            }
        }

    var searchText by mutableStateOf(searchQuery.value)
        private set

    private val _topAppBarState = MutableStateFlow(PlantScreenTopAppBarState())
    val topAppBarState: StateFlow<PlantScreenTopAppBarState> = _topAppBarState
        .asStateFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PlantScreenTopAppBarState()
        )

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
                _topAppBarState.value = _topAppBarState.value.copy(isSearchBarVisible = false)
            }

            UserAction.SearchIconClicked -> {
                _topAppBarState.value = _topAppBarState.value.copy(isSearchBarVisible = true)
            }

            UserAction.SortIconClicked -> {
                _topAppBarState.value = _topAppBarState.value.copy(isSortMenuVisible = true)
            }

            UserAction.SortMenuDismiss -> {
                _topAppBarState.value = _topAppBarState.value.copy(isSortMenuVisible = false)
            }

            is UserAction.SortItemClicked -> {
                when (userAction.type) {
                    SortType.A2Z -> sortPlantList(SortOrder.A2Z)
                    SortType.Z2A -> sortPlantList(SortOrder.Z2A)
                }
            }
        }
    }

    private fun sortPlantList(sortOrder: SortOrder) = viewModelScope.launch(Dispatchers.IO) {
        preferencesManager.saveSortOrder(sortOrder)
        _topAppBarState.value = _topAppBarState.value.copy(
            isSortMenuVisible = false
        )
    }

    fun deleteSelectedPlantsById(plantId: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(plantId)
    }
}

sealed class UserAction {
    object SearchIconClicked : UserAction()
    object CloseIconClicked : UserAction()
    object SortIconClicked : UserAction()
    object SortMenuDismiss : UserAction()
    data class SortItemClicked(val type: SortType) : UserAction()
}

enum class SortType {
    A2Z,
    Z2A
}

data class PlantScreenTopAppBarState(
    val isSearchBarVisible: Boolean = false,
    val isSortMenuVisible: Boolean = false,
)

class OnChange<T>(var value: T)