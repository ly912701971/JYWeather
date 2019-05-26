package com.jy.weather.viewmodel

import android.content.ContentUris
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.entity.LiveWeather
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GaussianBlurUtil
import com.jy.weather.util.SnackbarObj
import com.jy.weather.util.UserUtil
import java.lang.ref.WeakReference

class LiveWeatherViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private lateinit var navigator: WeakReference<LiveWeatherNavigator>

    val bgResBitmap: ObservableField<Bitmap> =
        ObservableField(GaussianBlurUtil.gaussianBlur(
            25F,
            BitmapFactory.decodeResource(context.resources, DrawableUtil.getBackground(db.condCode))
        ))
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()
    val portraitUrl: ObservableField<String> = ObservableField("")
    val liveWeathers: ObservableList<LiveWeather> = ObservableArrayList()

    val choosePhotoMode: Array<String> by lazy {
        context.resources.getStringArray(R.array.choose_photo_mode)
    }
    val loginType: Array<String> by lazy {
        context.resources.getStringArray(R.array.login_type)
    }

    fun start(navigator: LiveWeatherNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun autoLogin() {
        UserUtil.autoLogin {
            NetworkInterface.queryQQUserInfo(
                UserUtil.accessToken,
                UserUtil.openId,
                {
                    portraitUrl.set(it)
                }
            )
        }
    }

    fun queryLiveWeather() {
        NetworkInterface.queryLiveWeather(
            {
                liveWeathers.addAll(it)
            }
        )
    }

    fun requestPermission() = navigator.get()?.requestPermission()

    fun onPermissionGranted() {
        if (UserUtil.hasLogin()) {
            navigator.get()?.showChooseImageDialog()
        } else {
            navigator.get()?.showLoginHintDialog()
        }
    }

    fun showLoginDialog() =
        if (!UserUtil.hasLogin()) {
            navigator.get()?.showLoginDialog()
        } else {
            navigator.get()?.showLogoutDialog()
        }

    fun onLoginSucceed() {
        NetworkInterface.queryQQUserInfo(
            UserUtil.accessToken,
            UserUtil.openId,
            {
                portraitUrl.set(it)
                NetworkInterface.uploadUserInfo(
                    UserUtil.openId,
                    UserUtil.nickname,
                    UserUtil.portraitUrl
                )
            }
        )
    }

    fun onLoginFailed() =
        snackbarObj.set(SnackbarObj(context.getString(R.string.login_failed)))

    fun loginViaWX() =
        snackbarObj.set(SnackbarObj(context.getString(R.string.function_under_developing)))

    fun logout() {
        portraitUrl.set("")
    }

    fun getPhotoPath(uri: Uri) =
        when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                when (uri.authority) {
                    "com.android.providers.media.documents" -> {
                        val id = docId.split(Regex(":"))[1]// 解析出数字格式id
                        val selection = "${MediaStore.Images.Media._ID}=$id"
                        getImagePath(uri, selection)
                    }
                    "com.android.providers.downloads.documents" -> {
                        val contentUri =
                            ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                        getImagePath(contentUri, null)
                    }
                    else -> ""
                }
            }
            uri.scheme.equals("content", true) -> getImagePath(uri, null)
            uri.scheme.equals("file", true) -> uri.path ?: ""
            else -> ""
        }

    private fun getImagePath(uri: Uri, selection: String?): String {
        val cursor = context.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
        }
        cursor?.close()
        return ""
    }
}