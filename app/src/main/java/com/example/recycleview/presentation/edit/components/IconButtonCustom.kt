package com.example.recycleview.presentation.edit.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun IconButtonCustom(
    onClick: () -> Unit,
    painterIcon: Painter,
    iconContentDescription: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onClick()
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        modifier = modifier
            .padding(8.dp)
            .size(64.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(
            painter = painterIcon,
            contentDescription = iconContentDescription,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = modifier
                .fillMaxSize()
        )
    }
}