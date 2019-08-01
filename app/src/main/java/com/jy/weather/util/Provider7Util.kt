package com.jy.weather.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

object Provider7Util {

    fun getUriForFile(context: Context, file: File): Uri? {
        var fileUri: Uri? = null
        fileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriForFile24(context, file)
        } else {
            Uri.fromFile(file)
        }
        return fileUri
    }

    private fun getUriForFile24(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileProvider",
            file)
    }

    fun setIntentDataAndType(
        context: Context,
        intent: Intent,
        type: String,
        file: File,
        writeAble: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.apply {
                setDataAndType(getUriForFile(context, file), type)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (writeAble) {
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
        } else {
            intent.setDataAndType(Uri.fromFile(file), type)
            chmod("777", file.absolutePath)// apk放在cache文件中，需要获取读写权限
        }
    }

    fun chmod(permission: String, path: String) {
        try {
            Runtime.getRuntime().exec("chmod $permission $path")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun setIntentData(
        context: Context,
        intent: Intent,
        file: File,
        writeAble: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.apply {
                data = getUriForFile(context, file)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (writeAble) {
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }
        } else {
            intent.data = Uri.fromFile(file)
        }
    }

    fun grantPermissions(
        context: Context,
        intent: Intent,
        uri: Uri,
        writeAble: Boolean
    ) {
        var flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (writeAble) {
            flag = flag or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        intent.addFlags(flag)
        val resInfoList = context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context.grantUriPermission(packageName, uri, flag)
        }
    }
}
