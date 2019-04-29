package com.jy.weather.util

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

object AlertDialogUtil {

    @JvmOverloads
    fun showDialog(
        context: Context,
        message: String,
        positiveButtonCallback: () -> Unit = {},
        negativeButtonCallback: () -> Unit = {},
        positiveButtonText: String = "确认",
        negativeButtonText: String = "取消",
        theme: Int = android.R.style.Theme_DeviceDefault_Dialog_Alert,
        cancelable: Boolean = false
    ) {
        AlertDialog.Builder(context, theme)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveButtonCallback()
                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeButtonCallback()
                dialog.dismiss()
            }
            .setCancelable(cancelable)
            .create()
            .show()
    }
}