package com.example.recycleview.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recycleview.R
import com.example.recycleview.ui.dialogs.NotificationPermissionTextProvider
import com.example.recycleview.ui.dialogs.PermissionDialog
import com.example.recycleview.ui.dialogs.ReadExternalStoragePermissionTextProvider
import com.example.recycleview.ui.dialogs.ReadMediaImagesPermissionTextProvider
import com.example.recycleview.ui.ui.theme.PlantTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantTheme {
                StartWithPermissionsCheck(this)
            }
        }
    }
}

@Composable
fun StartWithPermissionsCheck(activity: Activity) {
    val readMediaPermission: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    val postNotificationPermission: String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        }

    val permissionsToRequest = arrayOf(
        readMediaPermission,
        postNotificationPermission
    ).filter { it.isNotBlank() }.toTypedArray()

    val context = LocalContext.current

    val viewModel = viewModel<MainViewModel>()
    val dialogQueue by viewModel.visiblePermissionDialogQueue.observeAsState(emptyList())

    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            permissionsToRequest.forEach { permission ->
                if (perms[permission] == true || perms[permission] == false) {
                    viewModel.onPermissionResult(
                        permission = permission,
                        isGranted = perms[permission] == true
                    )
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        multiplePermissionResultLauncher.launch(permissionsToRequest)
    }

    val arePermissionsGranted = checkPermissions(
        context = context,
        notificationPermission = postNotificationPermission,
        mediaPermission = readMediaPermission
    )

    val lifecycle = LocalLifecycleOwner.current

    val monitorPermissionsGrantedState = remember {
        monitorWhetherPermissionsGranted(
            mediaPermission = readMediaPermission,
            notificationPermission = postNotificationPermission,
            context = context,
            lifecycle
        )
    }

    val monitorPermissionsGranted by monitorPermissionsGrantedState.collectAsStateWithLifecycle()

    if (arePermissionsGranted) {
        Navigation()
    } else {
        if (monitorPermissionsGranted) {
            Navigation()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.explanationOfTheImportanceOfPermission),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.size(16.dp))

                Button(onClick = {
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                }) {
                    Text(text = stringResource(id = R.string.requestPermissions))
                }
            }

            dialogQueue
                .reversed()
                .forEach { permission ->
                    PermissionDialog(
                        permissionTextProvider = when (permission) {
                            Manifest.permission.POST_NOTIFICATIONS -> {
                                NotificationPermissionTextProvider()
                            }

                            Manifest.permission.READ_MEDIA_IMAGES -> {
                                ReadMediaImagesPermissionTextProvider()
                            }

                            Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                ReadExternalStoragePermissionTextProvider()
                            }

                            else -> return@forEach
                        },
                        isPermanentlyDeclined = !activity.shouldShowRequestPermissionRationale(
                            permission
                        ),
                        onDismiss = viewModel::dismissDialog,
                        onOkClick = {
                            viewModel.dismissDialog()
                            multiplePermissionResultLauncher.launch(
                                arrayOf(permission)
                            )
                        },
                        onGoToAppSettingsClick = {
                            openAppSettings(activity = activity)
                            viewModel.dismissDialog()
                        }
                    )
                }
        }
    }
}

fun monitorWhetherPermissionsGranted(
    mediaPermission: String,
    notificationPermission: String,
    context: Context,
    lifecycle: LifecycleOwner
): StateFlow<Boolean> {
    val permissionsGrantedState = MutableStateFlow(false)

    lifecycle.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (!permissionsGrantedState.value) {
                permissionsGrantedState.value = checkPermissions(
                    context = context,
                    notificationPermission = notificationPermission,
                    mediaPermission = mediaPermission
                )
                delay(2000) // check permissions every 2 sec
            }
        }
    }
    return permissionsGrantedState
}

fun checkPermissions(
    context: Context,
    notificationPermission: String,
    mediaPermission: String
): Boolean {
    val isPostNotificationPermissionGranted =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                notificationPermission
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // if SDK < 33, permission is granted by default
        }

    val isReadMediaPermissionGranted = ActivityCompat.checkSelfPermission(
        context,
        mediaPermission
    ) == PackageManager.PERMISSION_GRANTED

    return isPostNotificationPermissionGranted && isReadMediaPermissionGranted
}

fun openAppSettings(activity: Activity) {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", activity.packageName, null)
    ).also(activity::startActivity)
}