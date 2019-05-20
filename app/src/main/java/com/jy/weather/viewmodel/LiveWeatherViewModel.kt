package com.jy.weather.viewmodel

import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jy.weather.JYApplication
import com.jy.weather.R
import com.jy.weather.navigator.LiveWeatherNavigator
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GaussianBlurUtil
import java.lang.ref.WeakReference

class LiveWeatherViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    private lateinit var navigator: WeakReference<LiveWeatherNavigator>

    val bgResBitmap: ObservableField<Bitmap> =
        ObservableField(GaussianBlurUtil.gaussianBlur(
            context,
            25F,
            BitmapFactory.decodeResource(context.resources, DrawableUtil.getBackground(db.condCode))
        ))

    val ALBUM_MODE = 0
    val SHOOT_MODE = 1
    val choosePhotoMode: Array<String> by lazy {
        context.resources.getStringArray(R.array.choose_photo_mode)
    }

    fun start(navigator: LiveWeatherNavigator) {
        this.navigator = WeakReference(navigator)
    }
}