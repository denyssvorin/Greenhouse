package com.example.recycleview.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AddNewPlantScreen(

) {
    Column {
        GlideImage(
            model = R.drawable.plant_placeholder_coloured,
            contentDescription = stringResource(R.string.plant),
            modifier = Modifier.size(250.dp)
        )
        val blankPlantName = rememberSaveable { mutableStateOf("") }
        TextField(
            value = blankPlantName.value,
            onValueChange = { blankPlantName.value = it },
            label = { Text(stringResource(R.string.label)) }
        )
        val blankPlantDescription = rememberSaveable { mutableStateOf("") }
        TextField(
            value = blankPlantDescription.value,
            onValueChange = { blankPlantDescription.value = it },
            label = { Text(stringResource(R.string.description)) }
        )
    }
}