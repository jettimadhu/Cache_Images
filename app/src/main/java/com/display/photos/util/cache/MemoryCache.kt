package com.display.photos.util.cache

import android.graphics.Bitmap
import android.util.LruCache
import org.jetbrains.annotations.VisibleForTesting

/**
 * To cache the image in memory with given cache size and expiry time in millis by using LruCache.
 * Setting the default cache expiry time as 24 Hours.
 *
 * The cached data store in application memory and gets cleared with applications destroyed cycle.
 *
 * LruCache-When queue gets filled in size,least used cache data become visible to garbage collector
 */
class MemoryCache {

    private val cache: LruCache<String, CacheEntry>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        //Using 1/8th portion of the available memory for this memory cache with max 30k
        val cacheSize = CachedDataImpl.MAX_MEMORY_CACHE_SIZE.coerceAtMost(maxMemory / 8)
        cache = object : LruCache<String, CacheEntry>(cacheSize) {
            override fun sizeOf(key: String, value: CacheEntry): Int {
                return value.bitmap.byteCount / 1024
            }
        }
    }

    /**
     * get bitmap from the memory cache if exists otherwise null returns.
     * @param url Url of the image
     *
     * @return bitmap bitmap image from cache
     */
    fun getImage(url: String): Bitmap? {
        val cacheEntry = cache.get(url)
        cacheEntry?.let { entry ->
            return if (entry.isExpired()) {
                cache.remove(url) //remove from cache if expired
                null
            } else {
                entry.bitmap
            }
        }
        return null
    }

    /**
     * Save the image
     * @param url Url of the image
     * @param bitmap bitmap to save in cache
     */
    fun saveImage(url: String, bitmap: Bitmap) {
        cache.put(url, CacheEntry(bitmap))
    }

    /**
     * To get the size of the snapshots
     */
    fun size(): Long {
        return synchronized(this) {
            cache.snapshot().size.toLong()
        }
    }

    /**
     * clear the cache
     */
    fun clearCache() {
        cache.evictAll()
    }

    @VisibleForTesting
    fun getCache() = cache

    data class CacheEntry(
        val bitmap: Bitmap,
        private val timestamp: Long = System.currentTimeMillis()
    ) {
        /**
         * To check the validity of the cache data based on its saving time timestamp and
         * a maximum cache time.
         * @param maxCacheTimeMillis - added this for validating the expiry time for test cases
         * otherwise we directly use the constant while checking
         */
        fun isExpired(maxCacheTimeMillis: Long = CachedDataImpl.MAX_CACHE_VALIDITY_TIME_IN_MILLIS): Boolean {
            return System.currentTimeMillis() - timestamp > maxCacheTimeMillis
        }
    }
}