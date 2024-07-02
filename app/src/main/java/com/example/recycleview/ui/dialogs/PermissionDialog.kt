package com.example.recycleview.ui.dialogs

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recycleview.R

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Column {
                Divider()
                Text(
                    text = if (isPermanentlyDeclined) {
                        stringResource(R.string.grant_permission)
                    } else {
                        stringResource(R.string.OK)
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isPermanentlyDeclined) {
                                onGoToAppSettingsClick()
                            } else {
                                onOkClick()
                            }
                        }
                        .padding(16.dp)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.permission_required))
        },
        text = {
            Box(modifier = modifier.fillMaxWidth()) {
                Text(
                    text = permissionTextProvider.getDescription(
                        isPermanentlyDeclined = isPermanentlyDeclined,
                        context = context
                    )
                )
            }

        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String
}

class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_notification_permission)
        } else {
            context.getString(R.string.this_app_needs_access_to_post_notifications)
        }
    }
}

class ReadMediaImagesPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_media_permission)
        } else {
            context.getString(R.string.this_app_needs_access_to_images)
        }
    }
}

class ReadExternalStoragePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean, context: Context): String {
        return if (isPermanentlyDeclined) {
            context.getString(R.string.it_seems_you_permanently_declined_media_permission)
        } else {
            context.getString(R.string.this_app_needs_access_to_images)
        }
    }
}