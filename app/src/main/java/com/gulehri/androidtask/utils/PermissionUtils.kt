package com.gulehri.androidtask.utils

import android.Manifest
import android.os.Build
import com.gulehri.androidtask.ui.MainActivity
import com.permissionx.guolindev.PermissionX

object PermissionUtils {


    fun checkAndRequestPermissions(activity: MainActivity, onGrant: () -> Unit) {
        val hasStoragePermission =
            PermissionX.isGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val hasMediaImagesPermission =
            PermissionX.isGranted(activity, Manifest.permission.READ_MEDIA_IMAGES)
        val hasWritePermission =
            PermissionX.isGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val hasNotificationPermission =
            PermissionX.isGranted(activity, Manifest.permission.POST_NOTIFICATIONS)

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {

                if (!hasMediaImagesPermission && !hasNotificationPermission) {
                    PermissionX.init(activity).permissions(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.POST_NOTIFICATIONS,
                    ).onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Allow these Permission for the App to work Properly.",
                            "OK",
                            "Cancel"
                        )
                    }.onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "You need to allow necessary permissions in Settings manually",
                            "OK",
                            "Cancel"
                        )
                    }.request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            onGrant()
                        }
                    }

                } else onGrant()
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {

                if (!hasWritePermission && !hasStoragePermission) {
                    PermissionX.init(activity).permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ).onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Allow these Permission for the App to work Properly.",
                            "OK",
                            "Cancel"
                        )
                    }.onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "You need to allow necessary permissions in Settings manually",
                            "OK",
                            "Cancel"
                        )
                    }.request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            onGrant()
                        }
                    }
                } else onGrant()
            }

            else -> {
                if (!hasStoragePermission) {
                    PermissionX.init(activity)
                        .permissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ).onExplainRequestReason { scope, deniedList ->
                            scope.showRequestReasonDialog(
                                deniedList,
                                "Allow these Permission for the App to work Properly.",
                                "OK",
                                "Cancel"
                            )
                        }
                        .onForwardToSettings { scope, deniedList ->
                            scope.showForwardToSettingsDialog(
                                deniedList,
                                "You need to allow necessary permissions in Settings manually",
                                "OK",
                                "Cancel"
                            )
                        }
                        .request { allGranted, grantedList, deniedList ->
                            if (allGranted) {
                                onGrant()
                            }
                        }
                } else onGrant()
            }
        }
    }
}