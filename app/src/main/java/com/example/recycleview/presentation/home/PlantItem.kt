package com.example.recycleview.presentation.home

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R
import com.example.recycleview.domain.models.Plant

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlantItem(
    modifier: Modifier = Modifier,
    plantEntity: Plant?,
    isSelected: Boolean,
    isInSelectableMode: Boolean,
) {
    Card(
        modifier = modifier
            .fillMaxSize(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            val transition = updateTransition(isSelected, label = "selected")
            val padding by transition.animateDp(label = "padding") { selected ->
                if (selected) 10.dp else 0.dp
            }
            val roundedCornerShape by transition.animateDp(label = "corner") { selected ->
                if (selected) 16.dp else 0.dp
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.align(Alignment.Center)
            ) {
                val plantImageData = plantEntity?.imagePath
                if (plantImageData != null) {
                    GlideImage(
                        model = plantImageData,
                        contentDescription = stringResource(id = R.string.plant_image),
                        modifier = modifier
                            .size(150.dp)
                            .padding(padding)
                            .clip(RoundedCornerShape(roundedCornerShape)),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                } else {
                    GlideImage(
                        model = R.drawable.plant_placeholder_coloured,
                        contentDescription = stringResource(id = R.string.plant_image),
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 16.dp)
                            .padding(padding),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Text(
                    text = plantEntity?.name ?: stringResource(id = R.string.plant),
                    Modifier
                        .padding(8.dp),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.15.sp
                )
            }

            if (isInSelectableMode) {
                if (isSelected) {
                    val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    Icon(
                        Icons.Outlined.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor, CircleShape)
                            .clip(CircleShape)
                            .background(bgColor)
                    )
                } else {
                    Icon(
                        Icons.Outlined.AddCircle,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}