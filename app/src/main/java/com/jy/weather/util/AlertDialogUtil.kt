package com.jy.weather.util

import android.app.AlertDialog
import android.content.Context
import android.os.Build

object AlertDialogUtil {

    @JvmOverloads
    fun showDialog(
        context: Context,
        message: String,
        positiveButtonCallback: () -> Unit = {},
        negativeButtonCallback: () -> Unit = {},
        positiveButtonText: String = "确认",
        negativeButtonText: String = "取消",
        theme: Int = getTheme(),
        cancelable: Boolean = false
    ) {
        AlertDialog.Builder(context, theme)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveButtonCallback()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeButtonCallback()
            }
            .setCancelable(cancelable)
            .create()
            .show()
    }

    fun getTheme() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            android.R.style.Theme_DeviceDefault_Dialog_Alert
        } else {
            AlertDialog.THEME_DEVICE_DEFAULT_DARK
        }
}