package com.example.recycleview.ui.details

import androidx.lifecycle.ViewModel
import com.example.recycleview.data.Plant
import com.example.recycleview.data.PlantDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val plantDao: PlantDao
) : ViewModel() {

    private val detailsEventChannel = Channel<DetailsEvent>()
    val detailsEvent = detailsEventChannel.receiveAsFlow()

    sealed class DetailsEvent {
        data class NavigateToEditScreen(val plant: Plant): DetailsEvent()
    }


}