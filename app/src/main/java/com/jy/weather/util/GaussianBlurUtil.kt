package com.jy.weather.util

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

object GaussianBlurUtil {

    private lateinit var renderScript: RenderScript

    public fun gaussianBlur(context: Context, radius: Float, origin: Bitmap): Bitmap {
        renderScript = RenderScript.create(context)
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