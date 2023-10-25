package com.example.recycleview.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditScreen(
    plantId: Int,
    viewModel: EditPlantViewModel = hiltViewModel()
) {
    val plant by viewModel.plantData.collectAsState()
    LaunchedEffect(plantId) {
        viewModel.getPlant(plantId)
    }
    Column {
        GlideImage(
            model = plant?.plantImagePath,
            contentDescription = plant?.plantName
        )
        val plantName = rememberSaveable { mutableStateOf(plant?.plantName) }
        plantName.value?.let { name ->
            TextField(
                value = name,
                onValueChange = { newValue -> plantName.value = newValue },
                label = { Text(stringResource(R.string.label)) }
            )
        }
        val plantDescription = rememberSaveable { mutableStateOf(plant?.plantDescription) }
        plantDescription.value?.let { desc ->
            TextField(
                value = desc,
                onValueChange = { newValue -> plantDescription.value = newValue },
                label = { Text(stringResource(R.string.description)) }
            )
        }
    }
}
