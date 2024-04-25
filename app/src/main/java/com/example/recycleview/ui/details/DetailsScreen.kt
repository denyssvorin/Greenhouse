package com.example.recycleview.ui.details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R
import com.example.recycleview.domain.PlantScheduleData
import com.example.recycleview.domain.PlantWateringSchedule
import com.example.recycleview.ui.ScreenNavigation
import com.example.recycleview.ui.details.dialogs.AlarmScheduleDialog
import com.example.recycleview.utils.ScheduleDateUtils
import com.example.recycleview.utils.days
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    plantId: Int,
    navController: NavHostController,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val plant by viewModel.plantData.collectAsState()
    val plantScheduleList by viewModel.plantScheduleDataList.collectAsState()

    val context = LocalContext.current

    val openAlertDialog = rememberSaveable { mutableStateOf(false) }

    val plantImageData =
        plant?.plantImagePath ?: R.drawable.plant_placeholder_coloured


    LaunchedEffect(plantId) {
        viewModel.getPlant(plantId)
        viewModel.getPlantSchedule(plantId)
    }

    if (openAlertDialog.value) {
        AlarmScheduleDialog(
            onDismissRequest = { openAlertDialog.value = false },
            onConfirmation = { plantScheduleData: PlantScheduleData ->
                openAlertDialog.value = false

                val generatesUUID = UUID.randomUUID().toString()

                viewModel.scheduleWatering(
                    item = plantScheduleData,
                    scheduleId = generatesUUID,
                    plantId = plant?.plantId ?: plantId,
                    plantName = plant?.plantName ?: context.getString(R.string.plant),
                    plantImagePath = plant?.plantImagePath,
                )

                viewModel.saveWateringSchedule(
                    item = plantScheduleData,
                    scheduleId = generatesUUID,
                    plantId = plant?.plantId ?: plantId
                )

                Toast.makeText(context, "Scheduled!", Toast.LENGTH_SHORT).show()
            },
            dialogTitle = stringResource(R.string.set_up_notification),
            icon = painterResource(id = R.drawable.ic_schedule)
        )
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                title = {
                    Text(
                        text = stringResource(R.string.details),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.search),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        })
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        ScreenNavigation.EditScreen.withArgs(
                            plant?.plantId.toString()
                        )
                    )
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(id = R.string.edit),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                })
        },
        content = { padding ->
            Surface(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                if (plant != null) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)

                    ) {
                        Card(
                            modifier = modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            GlideImage(
                                model = plantImageData,
                                contentDescription = stringResource(id = R.string.plant_image),
                                modifier = modifier
                                    .size(height = 250.dp, width = Dp.Unspecified)
                                    .align(Alignment.CenterHorizontally),
                                contentScale = ContentScale.Inside,
                                alignment = Alignment.Center
                            )
                        }

                        Card(
                            modifier = modifier
                                .fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = plant?.plantName.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = plant?.plantDescription.toString(),
                                modifier = modifier.padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    top = 12.dp,
                                    bottom = 8.dp
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        var removeState by remember { mutableStateOf(false) }
                        Column(
                            modifier = modifier
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            plantScheduleList.forEach { plantWateringSchedule ->
                                var showConfirmDialog by remember { mutableStateOf(false) }

                                AnimatedVisibility(
                                    visible = !showConfirmDialog,
                                    enter = if (removeState) {
                                        EnterTransition.None
                                    } else {
                                        slideInHorizontally(
                                            initialOffsetX = { it }
                                        )
                                    },
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { it },
                                        animationSpec = tween(durationMillis = 300)
                                    ) + shrinkVertically(
                                        animationSpec = tween(delayMillis = 300)
                                    )
                                ) {
                                    WateringNotificationItem(
                                        plantWateringSchedule = plantWateringSchedule,
                                        onDeleteClicked = {
                                            removeState = false
                                            showConfirmDialog = true
                                        },
                                        modifier = Modifier
                                    )
                                }

                                if (showConfirmDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showConfirmDialog = false },
                                        title = { Text(stringResource(R.string.confirmation_of_removal)) },
                                        text = { Text(stringResource(R.string.do_you_really_want_to_remove_this_item)) },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                removeState = true
                                                plantScheduleList.remove(plantWateringSchedule)

                                                viewModel.cancelSchedule(plantWateringSchedule)

                                                viewModel.deleteSchedule(plantWateringSchedule)
                                                showConfirmDialog = false
                                            }) {
                                                Text(stringResource(id = R.string.remove))
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = {
                                                showConfirmDialog = false
                                            }) {
                                                Text(stringResource(id = R.string.cancel))
                                            }
                                        }
                                    )
                                }
                            }
                        }


                        Button(
                            onClick = {
                                openAlertDialog.value = true
                            },
                            modifier = modifier.align(Alignment.Start)
                        ) {
                            Text(text = stringResource(R.string.add_a_reminder))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WateringNotificationItem(
    plantWateringSchedule: PlantWateringSchedule,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val time: String = plantWateringSchedule.time

    val days: String = plantWateringSchedule.daysInterval.days()

    val startDate: String = plantWateringSchedule.firstTriggerDate

    val interval: Int = plantWateringSchedule.daysInterval

    val nextWateringDate: String =
        ScheduleDateUtils().calculateNextNotificationDate(
            startDate = startDate,
            notificationTime = time,
            interval = interval.toLong(),
        )

    Card(
        modifier = modifier
            .fillMaxSize(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = modifier.padding(4.dp)) {
                    Text(
                        text = plantWateringSchedule.notificationMessage,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Text(
                        text = "Repeat every: $days",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Next watering: $nextWateringDate",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = stringResource(R.string.cancel_watering),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                    modifier = modifier.clickable { onDeleteClicked() }
                )
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
            )
        }
    }
}