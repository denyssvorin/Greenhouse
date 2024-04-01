package com.example.recycleview.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.recycleview.R
import com.example.recycleview.ui.ui.theme.PlantTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(this) {
                        checkPermissions()
                    }
                    StartWithCheckPermission()
                }
            }
        }
    }

    private fun checkPermissions() {
        val mediaPermission: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (isPermissionGranted(mediaPermission)) {
            return
        } else {
            requestPermission(mediaPermission)
        }
    }

    private fun requestPermission(permission: String) {
        val permissions = mutableListOf<String>()

        permissions.add(permission)

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                MEDIA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED


    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun StartWithCheckPermission() {
        val mediaPermissionState = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberPermissionState(
                permission = Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            rememberPermissionState(
                permission = Manifest.permission.READ_EXTERNAL_STORAGE
            )
        })

        if (mediaPermissionState.status.isGranted) {
            Navigation()
        } else {
            RequestPermissionAgain(mediaPermissionState)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun RequestPermissionAgain(
        mediaPermissionState: PermissionState,
        modifier: Modifier = Modifier
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.explanationOfTheImportanceOfPermission),
                textAlign = TextAlign.Center
            )
            Button(
                modifier = modifier.padding(vertical = 16.dp),
                onClick = { mediaPermissionState.launchPermissionRequest() }) {
                Text(text = stringResource(id = R.string.requestPermissions))
            }
        }
    }
    companion object {
        const val MEDIA_PERMISSION_REQUEST_CODE = 0
    }
}