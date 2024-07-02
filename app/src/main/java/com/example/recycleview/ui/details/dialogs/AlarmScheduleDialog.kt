@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.recycleview.ui.details.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recycleview.R
import com.example.recycleview.domain.PlantScheduleData
import com.example.recycleview.utils.formatDays
import com.example.recycleview.utils.localDateToMilliseconds
import com.example.recycleview.utils.localDateToString
import com.example.recycleview.utils.localTimeToString
import com.example.recycleview.utils.millisToLocalDate
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.Calendar

@Composable
fun AlarmScheduleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (PlantScheduleData) -> Unit,
    dialogTitle: String,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    var pickedDays by rememberSaveable {
        mutableStateOf("-1")
    }

    var isInitialDaysInput by rememberSaveable {
        mutableStateOf(true)
    }

    val isDaysInputIncorrect by remember {
        derivedStateOf {
            pickedDays.isEmpty()
        }
    }

    var pickedTime by rememberSaveable {
        mutableStateOf(LocalTime.NOON)
    }

    val formattedTimeString by remember {
        derivedStateOf {
            localTimeToString(pickedTime)
        }
    }

    var pickedDate by rememberSaveable {
        mutableStateOf(LocalDate.now())
    }

    val pickedDateString by remember {
        derivedStateOf {
            localDateToString(pickedDate)
        }
    }

    val pickedDateInMillis by remember {
        derivedStateOf {
            localDateToMilliseconds(pickedDate)
        }
    }

    val openTimePickerDialog = remember { mutableStateOf(false) }
    val openDatePickerDialog = remember { mutableStateOf(false) }

    var notificationMessage by rememberSaveable {
        mutableStateOf("")
    }

    if (openTimePickerDialog.value) {
        ScheduleTimePicker(
            initialTime = pickedTime,
            onCancel = { openTimePickerDialog.value = false },
            onConfirm = { calendar: Calendar ->

                val calendarTime = calendar.timeInMillis
                val calendarZoneId = calendar.timeZone.toZoneId()
                val zonedDateTime =
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(calendarTime), calendarZoneId)

                pickedTime = zonedDateTime.toLocalTime()

                openTimePickerDialog.value = false
            },
            modifier = modifier
        )
    }

    if (openDatePickerDialog.value) {
        ScheduleDatePicker(
            initialDateInMillis = pickedDateInMillis,
            onCancel = { openDatePickerDialog.value = false },
            onConfirm = { date: DatePickerState ->

                pickedDate = millisToLocalDate(
                    date.selectedDateMillis
                        ?: localDateToMilliseconds(LocalDate.now())
                )

                openDatePickerDialog.value = false
            },
        )
    }

    AlertDialog(
        modifier = modifier,
        icon = {
            Icon(icon, contentDescription = stringResource(R.string.schedule_icon))
        },
        title = {
            Text(text = dialogTitle, textAlign = TextAlign.Center)
        },
        text = {
            Column(modifier = modifier.fillMaxWidth()) {
                DaysInputPart(
                    onTextChange = { text: String ->
                        pickedDays = text
                        isInitialDaysInput = false
                    },
                    isInputIncorrect = isDaysInputIncorrect,
                    modifier = modifier
                )

                Spacer(
                    modifier = modifier
                        .fillMaxWidth()
                        .size(40.dp)
                )

                DatePickerPart(
                    date = pickedDateString,
                    openDatePickerDialog = {
                        openDatePickerDialog.value = true
                    },
                    modifier = modifier
                )
                Spacer(
                    modifier = modifier
                        .fillMaxWidth()
                        .size(40.dp)
                )

                TimePickerPart(
                    time = formattedTimeString,
                    openTimePickerDialog = {
                        openTimePickerDialog.value = true
                    },
                    modifier = modifier
                )
                Spacer(
                    modifier = modifier
                        .fillMaxWidth()
                        .size(40.dp)
                )
                ScheduleMessageInputPart(
                    onTextChange = { text: String ->
                        notificationMessage = text
                    },
                    modifier = modifier
                )
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isDaysInputIncorrect && !isInitialDaysInput) {
                        onConfirmation(
                            PlantScheduleData(
                                notificationMessage = notificationMessage,
                                time = pickedTime,
                                daysInterval = pickedDays.toInt(),
                                firstTriggerDate = pickedDate
                            )
                        )
                    } else {
                        pickedDays = ""
                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}

@Composable
fun DaysInputPart(
    onTextChange: (String) -> Unit,
    isInputIncorrect: Boolean,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by rememberSaveable {
        mutableStateOf("")
    }

    val numberRegex = """\b(?:[1-9]|[1-9]\d{1,2}|999)\b""".toRegex()
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.repeat_every),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = modifier.size(8.dp))
        OutlinedTextField(
            value = textFieldValue,
            isError = isInputIncorrect,
            onValueChange = { newValue ->
                // user can enter only integers
                val newText = newValue.filter { it.isDigit() }

                // apply regex to limit to numbers from 1 to 999
                val filteredText = numberRegex.find(newText)?.value ?: ""

                textFieldValue = if (filteredText.length > 3) {
                    filteredText.substring(0, 3)
                } else {
                    filteredText
                }
                onTextChange(textFieldValue)
            },
            maxLines = 1,
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.End),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = modifier.weight(1f),

            )
        Spacer(modifier = modifier.size(8.dp))
        Text(
            text = formatDays(context, textFieldValue),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun DatePickerPart(
    modifier: Modifier = Modifier,
    date: String,
    openDatePickerDialog: () -> Unit
) {
    Row {
        Text(
            text = stringResource(R.string.start_on),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = modifier.size(8.dp))

        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.End,
                modifier = modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        openDatePickerDialog()
                    }
            )
        }
    }
}

@Composable
fun TimePickerPart(
    modifier: Modifier = Modifier,
    time: String,
    openTimePickerDialog: () -> Unit
) {
    Row {
        Text(
            text = stringResource(R.string.choose_time),
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = modifier.size(8.dp))
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleMedium.copy(
                    textDecoration = TextDecoration.Underline
                ),
                textAlign = TextAlign.End,
                modifier = modifier
                    .align(Alignment.CenterEnd)
                    .clickable {
                        openTimePickerDialog()
                    }
            )
        }
    }
}


@Composable
fun ScheduleMessageInputPart(
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textFieldValue by rememberSaveable {
        mutableStateOf("")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = textFieldValue,
            label = {
                Text(
                    text = stringResource(R.string.notification_message)
                )
            },
            onValueChange = { newValue ->
                textFieldValue = newValue
                onTextChange(textFieldValue)
            },
            singleLine = true,
            maxLines = 1,
            modifier = modifier.weight(1f),
        )
    }
}

@Preview(showSystemUi = false)
@Composable
fun PreviewAlarmScheduleDialog() {
    AlarmScheduleDialog(
        onDismissRequest = { /* No action */ },
        onConfirmation = { /* No action */ },
        dialogTitle = "Set-up alarm",
        icon = painterResource(id = R.drawable.ic_schedule)
    )
}