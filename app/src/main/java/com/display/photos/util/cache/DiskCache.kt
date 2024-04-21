package com.display.photos.util.cache

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import com.display.photos.util.BitmapUtils
import org.jetbrains.annotations.VisibleForTesting
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

/**
 *  To cache the image in disk cache.
 *  The cached data store in device storage and does not clear with application destroyed cycle.
 */
class DiskCache(private val cacheDir: File) {

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    /**
     * get bitmap from the disk cache if exists otherwise null returns.
     * @param url Url of the image
     *
     * @return bitmap bitmap image from cache
     */
    fun getImage(url: String): Bitmap? {
        val file = getFileForKey(url)

        if (file.exists()) {
            if (isValidityExpired(url.hashCode().toString())) {
                deleteImage(url) //delete the image as expiry time is over
            } else {
                try {
                    return BitmapUtils.decodeBitmapFromFile(file, 100, 100)
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
        }
        return null
    }

    /**
     * Saves a bitmap to the disk cache.
     * @param url Url of the image
     * @param bitmap bitmap to save in cache
     */
    @SuppressLint("UsableSpace")
    fun saveImage(url: String, bitmap: Bitmap) {
        val file = getFileForKey(url)
        //if we don't have max cache size then we simply use bitmap.byteCount >= file.usableSpace.
        val availableDiskSpace = min(CachedDataImpl.MAX_CACHE_SIZE_BYTES, cacheDir.usableSpace)
        if (cacheSize() + bitmap.byteCount > availableDiskSpace) {
            //Either we can clear the cache or not backup after the limit reached.
            // Here, clearing the cache for new images.
            clearCache()
        }

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
                out.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        }
    }


    /**
     * To delete all cache files.
     */
    fun clearCache() {
        cacheDir.listFiles()?.forEach { file ->
            file.delete()
        }
    }

    /**
     * To get the file with name as hashcode of the url
     */
    private fun getFileForKey(url: String): File {
        return File(cacheDir, url.hashCode().toString())
    }


    /**
     * To delete the cached image
     */
    private fun deleteImage(url: String) {
        val file = getFileForKey(url)
        if (file.exists()) {
            file.delete()
        }
    }

    /**
     * To calculate the cache size of total saved data.
     */
    private fun cacheSize(): Long {
        var size: Long = 0
        cacheDir.listFiles()?.forEach { file ->
            size += file.length()
        }
        return size
    }

    /**
     * To check the validity of the cache data based on its last modified timestamp and a maximum
     * cache time.
     */
    @VisibleForTesting
    fun isValidityExpired(
        key: String,
        maxCacheTimeMillis: Long = CachedDataImpl.MAX_CACHE_VALIDITY_TIME_IN_MILLIS
    ): Boolean {
        val file = File(cacheDir, key)
        if (file.exists()) {
            val lastModified = file.lastModified()
            val currentTime = System.currentTimeMillis()
            return currentTime - lastModified > maxCacheTimeMillis
        }
        return false
    }

    companion object {
        private val TAG: String = DiskCache::class.java.simpleName
    }
}