package com.display.photos.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object BitmapUtils {

    /**
     * To decode the bitmap from file
     * @param file the image file
     * @param reqWidth The required width of the image
     * @param reqHeight The required height of the image
     *
     * @return Bitmap of the given image or null
     */
    fun decodeBitmapFromFile(file: File, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //to decode only bounds
        BitmapFactory.decodeFile(file.absolutePath, options)
        options.inSampleSize = calculateSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false //to decode full image
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    /**
     * To calculate the sample size of the bitmap
     */
    private fun calculateSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) {
                heightRatio
            } else {
                widthRatio
            }
        }
        return inSampleSize
    }
}