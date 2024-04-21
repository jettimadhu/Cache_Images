package com.display.photos.util.cache

import android.graphics.Bitmap

interface ICacheData {
    fun saveImage(imageUrl: String, bitmap: Bitmap)
    fun getImage(imageUrl: String): Bitmap?
    fun clearCache()
}