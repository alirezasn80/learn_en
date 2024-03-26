package com.alirezasn80.learn_en.utill

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionState(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): PermissionState {
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsMap ->
        val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            onGranted()
        } else {
            onDenied()
        }

    }

    return PermissionState(
        context,
        launcher,
        onGranted
    )
}

class PermissionState(
    private val context: Context,
    private val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>,
    private val alreadyGranted: () -> Unit
) {

    fun requestStorage() {
        if (listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).all {
                ContextCompat.checkSelfPermission(
                    context,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            alreadyGranted()
        } else {
            // Request Permissions
            launcher.launch(listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).toTypedArray())
        }
    }

    fun requestNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (listOf(Manifest.permission.POST_NOTIFICATIONS).all {
                    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                }) {
                alreadyGranted()
            } else {
                // Request Permission
                launcher.launch(listOf(Manifest.permission.POST_NOTIFICATIONS).toTypedArray())
            }
        }

    }


}





