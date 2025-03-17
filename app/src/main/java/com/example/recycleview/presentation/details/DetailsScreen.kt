package com.example.recycleview.presentation.details

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.recycleview.R
import com.example.recycleview.data.realm.plantschedule.PlantScheduleEntity
import com.example.recycleview.domain.models.PlantScheduleData
import com.example.recycleview.presentation.details.dialogs.AlarmItem
import com.example.recycleview.presentation.details.dialogs.AlarmScheduleDialog
import com.example.recycleview.presentation.dialogs.DeleteDialog
import com.example.recycleview.presentation.navigation.ScreenNavigation
import com.example.recycleview.presentation.utils.days
import com.example.recycleview.presentation.utils.mappers.calculateNextNotificationDate
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalGlideComposeApi::class
)
@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    plantId: String,
    navController: NavHostController,
    viewModel: DetailsViewModel = koinViewModel(),
) {
    val plant by viewModel.plantData.collectAsStateWithLifecycle()
    val plantScheduleList by viewModel.plantScheduleDataList.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var openAlertDialog by rememberSaveable { mutableStateOf(false) }

    val plantImageData =
        plant?.plantImagePath ?: R.drawable.plant_placeholder_coloured

    var showPopupMenu by rememberSaveable { mutableStateOf(false) }
    var openDeleteDialog by rememberSaveable { mutableStateOf(false) }

    val selectedAlarmItem by viewModel.plantNotificationItem.collectAsStateWithLifecycle()

    LaunchedEffect(plantId) {
        viewModel.getPlant(plantId)
        viewModel.getPlantSchedules(plantId)
    }

    if (selectedAlarmItem != null) {
        AlarmScheduleDialog(
            onDismissRequest = { viewModel.setPlantNotificationItem(null) },
            onConfirmation = { plantScheduleData: PlantScheduleData ->
                viewModel.setPlantNotificationItem(null)

                val scheduleId = selectedAlarmItem!!.id

                viewModel.cancelSchedule(scheduleId)

                viewModel.updateWateringSchedule(
                    item = plantScheduleData,
                    scheduleId = scheduleId,
                    plantId = plantId
                )

                viewModel.scheduleWatering(
                    item = plantScheduleData,
                    scheduleId = scheduleId,
                    plantId = plant?._id.toString(),
                    plantName = plant?.plantName ?: context.getString(R.string.plant),
                    plantImagePath = plant?.plantImagePath,
                )

                Toast.makeText(
                    context,
                    context.getString(R.string.notification_updated),
                    Toast.LENGTH_SHORT
                ).show()
            },
            dialogTitle = stringResource(R.string.set_up_notification),
            icon = painterResource(id = R.drawable.ic_schedule),
            alarmItem = AlarmItem(
                id = selectedAlarmItem!!.id,
                days = selectedAlarmItem!!.days,
                startDate = selectedAlarmItem!!.startDate,
                startTime = selectedAlarmItem!!.startTime,
                message = selectedAlarmItem!!.message
            )
        )
    }
    if (openAlertDialog) {
        AlarmScheduleDialog(
            onDismissRequest = { openAlertDialog = false },
            onConfirmation = { plantScheduleData: PlantScheduleData ->
                openAlertDialog = false

                val generatesUUID = UUID.randomUUID().toString()

                viewModel.scheduleWatering(
                    item = plantScheduleData,
                    scheduleId = generatesUUID,
                    plantId = plant?._id.toString(),
                    plantName = plant?.plantName ?: context.getString(R.string.plant),
                    plantImagePath = plant?.plantImagePath,
                )

                viewModel.saveWateringSchedule(
                    item = plantScheduleData,
                    scheduleId = generatesUUID,
                    plantEntityId = plant?._id ?: ""
                )

                Toast.makeText(
                    context,
                    context.getString(R.string.notification_scheduled),
                    Toast.LENGTH_SHORT
                ).show()
            },
            dialogTitle = stringResource(R.string.set_up_notification),
            icon = painterResource(id = R.drawable.ic_schedule),
            alarmItem = null
        )
    }
    if (openDeleteDialog) {
        DeleteDialog(
            title = stringResource(R.string.delete_item),
            text = stringResource(R.string.are_you_sure_you_want_to_delete_the_item),
            onConfirmClick = {
                viewModel.deletePlant(plant?._id ?: plantId)
                showPopupMenu = false
                openDeleteDialog = false
                navController.navigateUp()
            },
            onCancelClick = {
                openDeleteDialog = false
            })
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
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
                            navController.navigateUp()
                        },
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.search),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        })
                },
                actions = {
                    Box {
                        IconButton(onClick = {
                            showPopupMenu = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.MoreVert,
                                contentDescription = stringResource(R.string.more)
                            )
                        }

                        DropdownMenu(
                            expanded = showPopupMenu,
                            onDismissRequest = { showPopupMenu = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = stringResource(
                                            id = R.string.delete
                                        )
                                    )
                                },
                                text = {
                                    Text(
                                        stringResource(id = R.string.delete),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                onClick = { openDeleteDialog = true }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        ScreenNavigation.EditScreen.withArgs(
                            plant?._id.toString()
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
                    var removeState by remember { mutableStateOf(false) }

                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            modifier = modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Card(
                                    modifier = modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    )
                                ) {
                                    GlideImage(
                                        model = plantImageData,
                                        contentDescription = stringResource(id = R.string.plant_image),
                                        modifier = modifier
                                            .size(height = 250.dp, width = Dp.Unspecified)
                                            .align(Alignment.CenterHorizontally),
                                        contentScale = if (plantImageData != R.drawable.plant_placeholder_coloured) {
                                            ContentScale.Crop
                                        } else ContentScale.Inside,
                                        alignment = Alignment.Center
                                    )
                                }

                                Spacer(modifier = modifier.height(8.dp))

                                Card(
                                    modifier = modifier
                                        .fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    )
                                ) {
                                    Text(
                                        text = plant?.plantName.toString(),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center
                                        ),
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
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                            items(plantScheduleList) { item: PlantScheduleEntity ->
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
                                        plantScheduleEntity = item,
                                        onDeleteClicked = {
                                            removeState = false
                                            showConfirmDialog = true
                                        },
                                        modifier = Modifier,
                                        onItemClick = {
                                            viewModel.setPlantNotificationItem(
                                                AlarmItem(
                                                    id = item._id,
                                                    days = item.daysInterval.toString(),
                                                    startDate = item.firstTriggerDate,
                                                    startTime = item.time,
                                                    message = item.notificationMessage
                                                )
                                            )
                                        }
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
                                                viewModel.cancelSchedule(item._id)

                                                viewModel.deleteSchedule(item._id)
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

                            item {
                                OutlinedButton(
                                    onClick = { openAlertDialog = true },
                                ) {
                                    Text(text = stringResource(R.string.add_a_reminder))
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = modifier.fillMaxSize()) {
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
    plantScheduleEntity: PlantScheduleEntity,
    onDeleteClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    val context = LocalContext.current
    val time: String = plantScheduleEntity.time

    val days: String = plantScheduleEntity.daysInterval?.let {
        it.days(context)
    } ?: context.getString(R.string.days)

    val startDate: String = plantScheduleEntity.firstTriggerDate

    val interval: Int = plantScheduleEntity.daysInterval ?: 1

    val nextWateringDate = calculateNextNotificationDate(
        startDate = startDate,
        notificationTime = time,
        interval = interval.toLong(),
    )


    Box(
        modifier = modifier
            .fillMaxSize()
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
                        if (plantScheduleEntity.notificationMessage.isNotBlank()
                            || plantScheduleEntity.notificationMessage.isNotEmpty()
                        ) {
                            Text(
                                text = plantScheduleEntity.notificationMessage,
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