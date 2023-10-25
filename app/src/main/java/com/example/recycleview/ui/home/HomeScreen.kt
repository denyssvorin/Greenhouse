package com.example.recycleview.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.recycleview.ui.ScreenNavigation

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val plantList = viewModel.plantPagingFlow.collectAsLazyPagingItems()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        ScreenNavigation.AddNewPlantScreen.route
                    )
                },
                content = {
                    Icon(Icons.Filled.Add, "Floating action button.")
                }
            )
        },
        content = { padding ->
            Surface(modifier = Modifier.padding(padding)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(plantList.itemCount) { index ->
                        PlantItem(
                            plant = plantList[index]!!,
                            navController = navController
                        )
                    }
                }
            }
        }
    )
}
