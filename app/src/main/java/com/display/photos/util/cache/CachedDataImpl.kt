package com.display.photos.util.cache

import android.graphics.Bitmap
import com.display.photos.BuildConfig

/**
 *  To cache the image in the memory/disk cache and perform actions on cached data.
 */
class CachedDataImpl(private val memoryCache: MemoryCache, private val diskCache: DiskCache) :
    ICacheData {

    enum class CacheType(val value: Int) {
        MEMORY(1),
        DISK(2),
        MEMORY_AND_DISK(3),
    }

    /**
     * Saves a bitmap to the disk cache.
     * @param url Url of the image
     * @param bitmap bitmap to save in cache
     */
    override fun saveImage(imageUrl: String, bitmap: Bitmap) {
        when (BuildConfig.CACHE_TYPE) {
            CacheType.MEMORY.value -> {
                memoryCache.saveImage(imageUrl, bitmap)
            }

            CacheType.DISK.value -> {
                diskCache.saveImage(imageUrl, bitmap)
            }

            CacheType.MEMORY_AND_DISK.value -> {
                memoryCache.saveImage(imageUrl, bitmap)
                diskCache.saveImage(imageUrl, bitmap)
            }
        }
    }

    /**
     * get bitmap from the disk cache if exists otherwise null returns.
     * @param imageUrl Url of the image
     *
     * @return bitmap bitmap image from cache
     */
    override fun getImage(imageUrl: String): Bitmap? {
        return when (BuildConfig.CACHE_TYPE) {
            CacheType.MEMORY.value -> {
                memoryCache.getImage(imageUrl)
            }

            CacheType.DISK.value -> {
                diskCache.getImage(imageUrl)
            }

            else -> {
                memoryCache.getImage(imageUrl) ?: run {
                    val bitmap = diskCache.getImage(imageUrl)
                    bitmap?.let {
                        memoryCache.saveImage(imageUrl, it)
                    }
                    bitmap
                }
            }
        }
    }

    /**
     * To delete all cache files.
     */
    override fun clearCache() {
        memoryCache.clearCache()
        diskCache.clearCache()
    }

    companion object {
        internal const val MAX_CACHE_VALIDITY_TIME_IN_MILLIS: Long = 24 * 60 * 60 * 1000 // 1 day
        internal const val MAX_CACHE_SIZE_BYTES: Long = 200 * 1024 * 1024 // 200 MB
        internal const val MAX_MEMORY_CACHE_SIZE: Int = 30000
    }
}