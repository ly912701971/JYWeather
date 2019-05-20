package com.jy.weather.viewmodel

import android.databinding.ObservableField
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jy.weather.JYApplication
import com.jy.weather.util.DrawableUtil
import com.jy.weather.util.GaussianBlurUtil

class CommentViewModel {

    private val context = JYApplication.INSTANCE
    private val db = JYApplication.cityDB

    val bgResBitmap: ObservableField<Bitmap> =
        ObservableField(GaussianBlurUtil.gaussianBlur(
            25F,
            BitmapFactory.decodeResource(context.resources, DrawableUtil.getBackground(db.condCode))
        ))
    val imageUri: ObservableField<String> = ObservableField()

    fun start() {

    }

    fun setImageUri(imageUri: String) {
        this.imageUri.set(imageUri)
    }
}