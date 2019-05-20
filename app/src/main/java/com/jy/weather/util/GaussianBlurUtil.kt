package com.jy.weather.util

import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.jy.weather.JYApplication

object GaussianBlurUtil {

    private val renderScript: RenderScript by lazy {
        RenderScript.create(JYApplication.INSTANCE)
    }

    fun gaussianBlur(radius: Float, origin: Bitmap): Bitmap {
        val input = Allocation.createFromBitmap(renderScript, origin)
        val output = Allocation.createTyped(renderScript, input.type)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(origin)
        return origin
    }
}