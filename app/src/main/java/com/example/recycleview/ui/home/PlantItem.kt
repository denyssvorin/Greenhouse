package com.example.recycleview.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.data.Plant
import com.example.recycleview.ui.ScreenNavigation

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlantItem(
    plant: Plant,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(
                    ScreenNavigation.DetailsScreen.withArgs(
                        plant.plantId.toString()
                    )
                )
            },
        elevation = 2.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GlideImage(
                model = plant.plantImagePath,
                contentDescription = "Plant image",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = plant.plantName,
                Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                letterSpacing = 0.15.sp
            )
        }
    }
}
