package com.fahim.mad_lab_compose.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permission: String,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(permission)

    LaunchedEffect(permissionState) {
        if (!permissionState.hasPermission) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.hasPermission) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}