package com.example.recycleview.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.recycleview.R

@Composable
fun DeleteDialog(
    title: String, text: String,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {

    AlertDialog(
        onDismissRequest = { onCancelClick() },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmClick() }
            ) {
                Text(text = stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCancelClick() }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}