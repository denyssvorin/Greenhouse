package com.example.recycleview.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.recycleview.R

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
    onCancelDeleteClicked: () -> Unit,
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
