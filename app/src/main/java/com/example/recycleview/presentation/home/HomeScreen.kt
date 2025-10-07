package com.example.recycleview.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.recycleview.R
import com.example.recycleview.presentation.dialogs.DeleteDialog
import com.example.recycleview.presentation.home.components.HomeTopBar
import com.example.recycleview.presentation.home.components.PlantGrid
import com.example.recycleview.presentation.home.components.SearchAppBar
import com.example.recycleview.presentation.navigation.ScreenNavigation

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val plantList = viewModel.plantPagingFlow.collectAsLazyPagingItems()
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
                    viewModel.deleteSelectedPlantsById(plantId)
                }
                selectedItemsIds.value = emptySet()

                openDeleteDialog = false

                plantList.refresh()
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
                animationSpec = tween(durationMillis = 500),
                label = ""
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
                when {
                    plantList.loadState.refresh is LoadState.Loading -> {
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
                    }

                    plantList.itemCount == 0 -> {
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

                    else -> {
                        PlantGrid(
                            plantList = plantList,
                            navController = navController,
                            selectedIds = selectedItemsIds,
                        )
                    }
                }
            }
        }
    )
}