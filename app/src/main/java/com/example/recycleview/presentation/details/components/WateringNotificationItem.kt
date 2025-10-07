package com.example.recycleview.presentation.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.recycleview.R
import com.example.recycleview.domain.models.PlantSchedule
import com.example.recycleview.presentation.utils.days
import com.example.recycleview.presentation.utils.mappers.calculateNextNotificationDate

@Composable
fun WateringNotificationItem(
    plantSchedule: PlantSchedule,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    val context = LocalContext.current
    val time: String = plantSchedule.time

    val days: String = plantSchedule.daysInterval?.let {
        it.days(context)
    } ?: context.getString(R.string.days)

    val startDate: String = plantSchedule.firstTriggerDate

    val interval: Int = plantSchedule.daysInterval ?: 1

    val nextWateringDate = calculateNextNotificationDate(
        startDate = startDate,
        notificationTime = time,
        interval = interval.toLong(),
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onItemClick()
            }
    ) {
        Card(
            modifier = modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .zIndex(2f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = modifier.padding(4.dp),
                    ) {
                        if (plantSchedule.notificationMessage.isNotBlank()
                            || plantSchedule.notificationMessage.isNotEmpty()
                        ) {
                            Text(
                                text = plantSchedule.notificationMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = modifier.zIndex(2f)
                            )
                        }

                        Text(
                            text = time,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                        )
                        Text(
                            text = "${stringResource(id = R.string.repeat_every)} $days",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                        )
                        Text(
                            text = stringResource(R.string.next_triggering, nextWateringDate),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                        )
                    }
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_schedule),
                    contentDescription = stringResource(id = R.string.schedule),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = modifier
                        .alpha(0.2f)
                        .rotate(25f)
                        .align(Alignment.CenterEnd)
                        .padding(end = 30.dp)
                        .size(120.dp)
                        .clipToBounds()
                        .zIndex(1f)
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = stringResource(R.string.remove),
            tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
            modifier = Modifier
                .clickable { onDeleteClicked() }
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )
    }
}