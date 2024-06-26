package com.example.recycleview.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R
import com.example.recycleview.domain.Plant
import com.example.recycleview.ui.ScreenNavigation

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlantItem(
    plantEntity: Plant?,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(
                    ScreenNavigation.DetailsScreen.withArgs(
                        plantEntity?.plantId.toString()
                    )
                )
            },
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            val plantImageData = plantEntity?.plantImagePath
            if (plantImageData != null) {
                GlideImage(
                    model = plantImageData,
                    contentDescription = stringResource(id = R.string.plant_image),
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            } else {
                GlideImage(
                    model = R.drawable.plant_placeholder_coloured,
                    contentDescription = stringResource(id = R.string.plant_image),
                    modifier = Modifier
                        .size(150.dp)
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }

            Text(
                text = plantEntity?.plantName ?: stringResource(id = R.string.plant),
                Modifier
                    .padding(8.dp),
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.15.sp
            )
        }
    }
}
