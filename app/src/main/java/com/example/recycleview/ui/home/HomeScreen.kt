package com.example.recycleview.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.recycleview.R
import com.example.recycleview.ui.ScreenNavigation

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val plantList = viewModel.plantPagingFlow.collectAsLazyPagingItems()

    val state = viewModel.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Crossfade(
                targetState = state.value.isSearchBarVisible,
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
                        isSortMenuVisible = state.value.isSortMenuVisible,
                        onSortItemA2ZClicked = {
                            viewModel.onAction(
                                UserAction.SortItemClicked(SortType.A2Z)
                            )
                        },
                        onSortItemZ2AClicked = {
                            viewModel.onAction(
                                UserAction.SortItemClicked(SortType.Z2A)
                            )
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
                if (plantList.loadState.refresh is LoadState.Loading) {

                    Box(modifier = modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                } else {
                    if (plantList.itemCount != 0) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = modifier
                                .background(MaterialTheme.colorScheme.background)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(plantList.itemCount) { index ->
                                PlantItem(
                                    plantEntity = plantList[index],
                                    navController = navController
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Column(
                                modifier = modifier.fillMaxSize().padding(8.dp),
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
                                    text = if (!state.value.isSearchBarVisible) {
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
    isSortMenuVisible: Boolean
) {
    TopAppBar(
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
            Text(
                text = stringResource(R.string.main),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        actions = {
            IconButton(onClick = onSearchIconClicked) {
                Icon(
                    imageVector = Icons.Filled.Search,
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
    )
}