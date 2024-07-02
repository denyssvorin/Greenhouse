package com.example.recycleview.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.recycleview.R
import com.example.recycleview.data.realm.plant.PlantEntity
import com.example.recycleview.ui.ScreenNavigation
import com.example.recycleview.ui.dialogs.DeleteDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val plantList = viewModel.plantFlow.collectAsStateWithLifecycle().value

    val topAppBarState = viewModel.topAppBarState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val selectedItemsIds = rememberSaveable { mutableStateOf(emptySet<String>()) }
    val inSelectionMode by remember {
        derivedStateOf {
            selectedItemsIds.value.isNotEmpty()
        }
    }

    var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

    if (openDeleteDialog) {
        DeleteDialog(
            title = stringResource(R.string.delete_items),
            text = stringResource(R.string.are_you_sure_you_want_to_delete_the_items),
            onConfirmClick = {
                selectedItemsIds.value.forEach { plantId ->
                    viewModel.deleteSelectedPlants(plantId)
                }
                selectedItemsIds.value = emptySet()
                openDeleteDialog = false
            },
            onCancelClick = {
                openDeleteDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Crossfade(
                targetState = topAppBarState.value.isSearchBarVisible,
                animationSpec = tween(durationMillis = 500), label = ""
            ) { searchIsOpen ->
                if (searchIsOpen) {
                    SearchAppBar(
                        onCloseIconClicked = {
                            viewModel.onAction(UserAction.CloseIconClicked)
                        },
                        onInputValueChange = { newText ->
                            viewModel.updateSearchQuery(newText)
                        },
                        text = viewModel.searchText,
                        onSearchClicked = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    )
                } else {
                    HomeTopBar(
                        onSearchIconClicked = {
                            viewModel.onAction(UserAction.SearchIconClicked)
                        },
                        onSortIconClicked = {
                            viewModel.onAction(UserAction.SortIconClicked)
                        },
                        onSortMenuDismiss = {
                            viewModel.onAction(UserAction.SortMenuDismiss)
                        },
                        isSortMenuVisible = topAppBarState.value.isSortMenuVisible,
                        onSortItemA2ZClicked = {
                            viewModel.onAction(
                                UserAction.SortItemClicked(SortType.A2Z)
                            )
                        },
                        onSortItemZ2AClicked = {
                            viewModel.onAction(
                                UserAction.SortItemClicked(SortType.Z2A)
                            )
                        },
                        inSelectionMode = inSelectionMode,
                        onDeleteClicked = {
                            openDeleteDialog = true
                        },
                        onCancelDeleteClicked = {
                            selectedItemsIds.value = emptySet()
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        ScreenNavigation.EditScreen.route
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(id = R.string.add),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            )
        },
        content = { padding ->
            Surface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)

            ) {
                if (plantList == null) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                } else if (plantList.isNotEmpty()) {
                    PlantGrid(
                        plantEntityList = plantList,
                        navController = navController,
                        selectedIds = selectedItemsIds,
                    )
                } else {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.plant_image),
                                contentDescription = stringResource(R.string.empty_list_image),
                                alpha = 0.8f,
                                modifier = modifier.scale(0.9f)
                            )
                            Spacer(modifier = modifier.size(8.dp))
                            Text(
                                text = if (!topAppBarState.value.isSearchBarVisible) {
                                    stringResource(R.string.your_personal_list_is_empty)
                                } else {
                                    stringResource(R.string.no_results)
                                },
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    onCloseIconClicked: () -> Unit,
    onInputValueChange: (String) -> Unit,
    text: String,
    onSearchClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        value = text,
        shape = RoundedCornerShape(25),
        onValueChange = {
            onInputValueChange(it)
        },
        textStyle = TextStyle(
            color = MaterialTheme.typography.bodyLarge.color,
            fontSize = 18.sp
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.medium),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = MaterialTheme.colorScheme.primary.copy(
                    alpha = ContentAlpha.medium
                )
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (text.isNotEmpty()) {
                        onInputValueChange("")
                    } else {
                        onCloseIconClicked()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(
                alpha = ContentAlpha.medium
            ),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchClicked() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onSearchIconClicked: () -> Unit,
    onSortIconClicked: () -> Unit,
    onSortMenuDismiss: () -> Unit,
    onSortItemA2ZClicked: () -> Unit,
    onSortItemZ2AClicked: () -> Unit,
    isSortMenuVisible: Boolean,
    inSelectionMode: Boolean,
    onDeleteClicked: () -> Unit,
    onCancelDeleteClicked: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            if (inSelectionMode) {
                Text(
                    text = stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = stringResource(R.string.main),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        },
        navigationIcon = {
            if (inSelectionMode) {
                IconButton(onClick = onCancelDeleteClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        },
        actions = {
            if (inSelectionMode) {
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                IconButton(onClick = onSearchIconClicked) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                IconButton(onClick = onSortIconClicked) {
                    Icon(
                        painter = painterResource(R.drawable.filter_list),
                        contentDescription = stringResource(R.string.sort_icon),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                    DropdownMenu(
                        expanded = isSortMenuVisible,
                        onDismissRequest = onSortMenuDismiss,
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        DropdownMenuItem(
                            onClick = onSortItemA2ZClicked,
                            text = {
                                Text(text = stringResource(R.string.sort_a_z))
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        DropdownMenuItem(
                            onClick = onSortItemZ2AClicked,
                            text = {
                                Text(text = stringResource(R.string.sort_z_a))
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun PlantGrid(
    modifier: Modifier = Modifier,
    plantEntityList: List<PlantEntity>,
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
        items(plantEntityList, key = { it._id }) { plantEntity ->
            val selected by remember { derivedStateOf { selectedIds.value.contains(plantEntity._id) } }

            PlantItem(
                plantEntity = plantEntity,
                isSelected = selected,
                isInSelectableMode = inSelectionMode,
                modifier = modifier
                    .semantics {
                        if (!inSelectionMode) {
                            onLongClick("Select") {
                                selectedIds.value += plantEntity._id
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
                                        selectedIds.value += plantEntity._id
                                    } else {
                                        selectedIds.value -= plantEntity._id
                                    }
                                }
                            )
                        } else modifier
                            .clickable {
                                navController.navigate(
                                    ScreenNavigation.DetailsScreen.withArgs(
                                        plantEntity._id
                                    )
                                )
                            }
                    )
            )
        }
    }
}

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