package com.example.recycleview.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.ui.ScreenNavigation

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun DetailsScreen(
    plantId: Int,
    navController: NavHostController,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val plant by viewModel.plantData1.collectAsState()
    LaunchedEffect(plantId) {
        viewModel.getPlant1(plantId)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        ScreenNavigation.EditScreen.withArgs(
                            plant?.plantId.toString()
                        )
                    )
                },
                content = {
                    Icon(Icons.Filled.Edit, "Floating action button.")
                })
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlideImage(
                        model = plant?.plantImagePath,
                        contentDescription = plant?.plantName,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(250.dp),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                    Text(
                        text = plant?.plantName.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = plant?.plantDescription.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }
        }
    )
}