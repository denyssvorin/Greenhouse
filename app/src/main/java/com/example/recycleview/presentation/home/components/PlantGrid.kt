package com.example.recycleview.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.LazyPagingItems
import com.example.recycleview.domain.models.Plant
import com.example.recycleview.presentation.home.extensions.photoGridDragHandler
import com.example.recycleview.presentation.navigation.ScreenNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun PlantGrid(
    modifier: Modifier = Modifier,
    plantList: LazyPagingItems<Plant>,
    navController: NavHostController,
    selectedIds: MutableState<Set<String>>,
) {
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }

    val state = rememberLazyGridState()
    val autoScrollSpeed = remember { mutableStateOf(0f) }
    LaunchedEffect(autoScrollSpeed.value) {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.value)
                delay(10)
            }
        }
    }
    LazyVerticalGrid(
        state = state,
        columns = GridCells.Adaptive(minSize = 120.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .padding(8.dp)
            .photoGridDragHandler(
                lazyGridState = state,
                haptics = LocalHapticFeedback.current,
                selectedIds = selectedIds,
                autoScrollSpeed = autoScrollSpeed,
                autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
            )
    ) {
        items(
            count = plantList.itemCount,
            key = { index -> plantList[index]?.id ?: 0 }) { index ->

            val plant = plantList[index] ?: return@items
            val selected by remember { derivedStateOf { selectedIds.value.contains(plant.id) } }

            PlantItem(
                plantEntity = plant,
                isSelected = selected,
                isInSelectableMode = inSelectionMode,
                modifier = modifier
                    .semantics {
                        if (!inSelectionMode) {
                            onLongClick("Select") {
                                selectedIds.value += plant.id
                                true
                            }
                        }
                    }
                    .then(
                        if (inSelectionMode) {
                            modifier.toggleable(
                                value = selected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null, // do not show a ripple
                                onValueChange = {
                                    if (it) {
                                        selectedIds.value += plant.id
                                    } else {
                                        selectedIds.value -= plant.id
                                    }
                                }
                            )
                        } else modifier
                            .clickable {
                                navController.navigate(
                                    ScreenNavigation.DetailsScreen.withArgs(
                                        plant.id
                                    )
                                )
                            }
                    )
            )
        }
    }
}