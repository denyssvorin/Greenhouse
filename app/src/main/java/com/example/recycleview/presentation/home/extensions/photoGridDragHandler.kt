package com.example.recycleview.presentation.home.extensions

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect

fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedIds: MutableState<Set<String>>,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float,
): Modifier = this.pointerInput(Unit) {
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): String? =
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? String

    var initialKey: String? = null
    var currentKey: String? = null
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                if (!selectedIds.value.contains(key)) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    initialKey = key
                    currentKey = key
                    selectedIds.value += key
                }
            }
        },
        onDragCancel = { initialKey = null; autoScrollSpeed.value = 0f },
        onDragEnd = { initialKey = null; autoScrollSpeed.value = 0f },
        onDrag = { change, _ ->
            if (initialKey != null) {
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y
                autoScrollSpeed.value = when {
                    distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                    distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                    else -> 0f
                }
                lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                    if (currentKey != key) {
                        currentKey = key
                        if (!selectedIds.value.contains(key)) {
                            selectedIds.value += key
                        } else {
                            selectedIds.value -= key
                        }
                    }
                }
            }
        }
    )
}