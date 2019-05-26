package com.jy.weather.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

object ImageUtil {

    fun compress(imagePath: String): String {
        val oldFile = File(imagePath)
        val targetPath = "${oldFile.parent}/compress_${oldFile.name}"
        val quality = 50
        val bitmap = getSmallBitmap(imagePath)

        val outputFile = File(targetPath)
        try {
            if (!outputFile.exists()) {
                outputFile.parentFile.mkdirs()
            } else {
                outputFile.delete()
            }
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return imagePath
        }
        return outputFile.path
    }

    private fun getSmallBitmap(imagePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        options.inSampleSize = calcInSampleSize(options.outWidth, options.outHeight, 480, 800)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(imagePath, options)
    }

    private fun calcInSampleSize(
        width: Int,
        height: Int,
        targetWidth: Int,
        targetHeight: Int
    ): Int {
        var inSampleSize = 1
        if (width > targetWidth || height > targetHeight) {
            val widthRatio = width / targetHeight
            val heightRatio = height / targetHeight
            inSampleSize = if (widthRatio > heightRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }
}