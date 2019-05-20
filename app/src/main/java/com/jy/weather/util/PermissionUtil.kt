package com.jy.weather.util

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

object PermissionUtil {

    fun checkPermissionAndRequest(
        activity: Activity,
        permission: String,
        permissionGranted: () -> Unit,
        showPermissionHintDialog: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity, permission)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionHintDialog()
            } else {
                requestPermission(activity, permission)
            }
        } else {
            permissionGranted()
        }
    }

    fun requestPermission(activity: Activity, permission: String) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), 0)
    }

    fun onPermissionResult(
        grantResults: IntArray,
        permissionGranted: () -> Unit,
        permissionDenied: () -> Unit) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted()
        } else {
            permissionDenied()
        }
    }
}