package com.jy.weather.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Editable
import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.data.remote.NetworkInterface
import com.jy.weather.navigator.CommentNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GaussianBlurUtil
import com.jy.weather.util.GpsUtil
import com.jy.weather.util.LocationUtil
import com.jy.weather.util.NetworkUtil
import com.jy.weather.util.SnackbarObj
import com.jy.weather.util.UserUtil
import java.lang.ref.WeakReference

class PublishLiveViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private var hasGranted = false
    private var liveText = ""
    private val locationUnknown: String by lazy {
        context.getString(R.string.locate_unknown)
    }

    private lateinit var navigator: WeakReference<CommentNavigator>

    val bgResBitmap: ObservableField<Bitmap> =
        ObservableField(GaussianBlurUtil.gaussianBlur(
            25F,
            BitmapFactory.decodeResource(context.resources, DrawableUtil.getBackground(db.condCode))
        ))
    val imageUri: ObservableField<String> = ObservableField()
    val location: ObservableField<String> = ObservableField(context.getString(R.string.locating))
    val locationCheck: ObservableBoolean = ObservableBoolean(true)
    val snackbarObj: ObservableField<SnackbarObj> = ObservableField()
    val textNumber: ObservableField<String> = ObservableField("(0/160)")

    fun start(navigator: CommentNavigator) {
        this.navigator = WeakReference(navigator)
    }

    fun setImageUri(imageUri: String) {
        this.imageUri.set(imageUri)
    }

    fun afterTextChanged(editable: Editable) {
        textNumber.set("(${editable.length}/160)")
        liveText = editable.toString()
    }

    fun onPermissionGranted() {
        hasGranted = true
        locate()
    }

    fun onPermissionDenied() = setLocation(locationUnknown)

    fun onLocationClick() {
        if (!hasGranted) {
            snackbarObj.set(SnackbarObj(context.getString(R.string.permission_denied)))
        } else {
            locate()
        }
    }

    fun onPublishClick() =
        if (!NetworkUtil.isNetworkAvailable()) {
            snackbarObj.set(SnackbarObj(context.getString(R.string.network_unavailable)))
        } else {
            NetworkInterface.uploadLiveWeather(
                UserUtil.openId,
                liveText,
                if (locationCheck.get()) location.get()!! else locationUnknown,
                imageUri.get()!!,
                {
                    navigator.get()?.exitActivity(it)
                },
                {
                    navigator.get()?.exitActivity("failed")
                }
            )
        }

    fun onCheckChangedListener(button: CompoundButton, checked: Boolean) =
        locationCheck.set(checked)

    fun locate() {
        if (GpsUtil.isOpen(context)) {
            LocationUtil.locate {
                location.set(it.address.address.replace(Regex("中国"), ""))
            }
        } else {
            setLocation(locationUnknown)
            showGpsNotOpen()
        }
    }

    private fun showGpsNotOpen() =
        snackbarObj.set(
            SnackbarObj(
                context.getString(R.string.locate_failed),
                context.getString(R.string.goto_open)
            ) {
                navigator.get()?.startOpenGpsActivity()
            }
        )

    private fun setLocation(address: String) = location.set(address)
}