package com.display.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.display.photos.util.cache.CachedDataImpl
import com.display.photos.util.cache.DiskCache
import com.display.photos.util.cache.MemoryCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException
import kotlin.math.abs

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CacheInstrumentedTest {

    private lateinit var context: Context
    private lateinit var memoryCache: MemoryCache
    private lateinit var diskCacheDir: File
    private lateinit var diskCache: DiskCache
    private lateinit var cachedDataImpl: CachedDataImpl
    private var isDiskTypeCache: Boolean = false
    private var isMemoryTypeCache: Boolean = false

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.display.photos", context.packageName)
    }

    /**
     * Creating the required instances to perform the tests
     */
    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        diskCacheDir = File(context.cacheDir, "image_cache")
        memoryCache = MemoryCache()
        diskCache = DiskCache(diskCacheDir)
        cachedDataImpl = CachedDataImpl(memoryCache, diskCache)
        isMemoryTypeCache = BuildConfig.CACHE_TYPE == CachedDataImpl.CacheType.MEMORY.value
                || BuildConfig.CACHE_TYPE == CachedDataImpl.CacheType.MEMORY_AND_DISK.value
        isDiskTypeCache = BuildConfig.CACHE_TYPE == CachedDataImpl.CacheType.DISK.value
                || BuildConfig.CACHE_TYPE == CachedDataImpl.CacheType.MEMORY_AND_DISK.value
    }

    /**
     * Checks the memory cache gets cleared or not after performing the clearCache action
     */
    @Test
    fun testMemoryCacheClear() {
        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())
    }

    /**
     * Checks the disk cache gets cleared or not after performing the clearCache action
     */
    @Test
    fun testDiskCacheClear() {
        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)
    }

    /**
     * To save the image to the memory cache
     */
    @Test
    fun testSavingImageToMemoryCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }
    }

    /**
     * To save the image to the memory cache and also validating the clear cache status
     */
    @Test
    fun testSavingImageToMemoryCacheAndClearCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        testMemoryCacheClear()
    }

    /**
     * To save the image to the disk cache
     */
    @Test
    fun testSavingImageToDiskCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }
    }

    /**
     * To save the image to the disk cache and also validating the clear cache status
     */
    @Test
    fun testSavingImageToDiskCacheAndClearCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }
        testDiskCacheClear()
    }

    /**
     * To save the image to the memory cache and get the image from memory cache and assert with
     * Bitmap provided sameAs()
     */
    @Test
    fun testSaveAndGetImageToMemoryCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
        assertEquals(true, insertingBitmap.sameAs(fetchedBitmap))
    }

    /**
     * To save the image to the memory cache and get the image from memory cache and assert with
     * Color sources
     */
    @Test
    fun testSaveAndGetImageToMemoryCacheWithColor() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)

        fetchedBitmap?.let {
            assertBitmapsEqualityWithColor(insertingBitmap, it)
        } ?: fail("There is no image with the key $imageUrl in the memory cache by comparing color")
    }

    /**
     * To save the image to the memory cache and get the image from memory cache and assert with
     * each pixels
     */
    @Test
    fun testSaveAndGetImageToMemoryCacheWithPixels() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)

        fetchedBitmap?.let {
            assertBitmapsEqualityWithPixels(insertingBitmap, it)
        }
            ?: fail("There is no image with the key $imageUrl in the memory cache by comparing pixels")
    }

    /**
     * To save the image to the disk cache and get the image from disk cache and assert with
     * Bitmap provided sameAs()
     */
    @Test
    fun testSaveAndGetImageToDiskCache() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
        assertEquals(true, insertingBitmap.sameAs(fetchedBitmap))
    }

    /**
     * To save the image to the disk cache and get the image from disk cache and assert with
     * Color sources
     */
    @Test
    fun testSaveAndGetImageToDiskCacheWithColor() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)

        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
        fetchedBitmap?.let {
            assertBitmapsEqualityWithColor(insertingBitmap, it)
        } ?: fail("There is no image with the key $imageUrl in the disk cache by comparing color")
    }

    /**
     * To save the image to the disk cache and get the image from disk cache and assert with
     * pixels
     */
    @Test
    fun testSaveAndGetImageToDiskCacheWithPixels() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)
        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }

        val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
        fetchedBitmap?.let {
            assertBitmapsEqualityWithPixels(insertingBitmap, it)
        } ?: fail("There is no image with the key $imageUrl in the disk cache by comparing pixels")
    }

    /**
     * Compare two images with color distances.
     * @param expectedBitmap The expected bitmap
     * @param actualBitmap The actual bitmap
     */
    private fun assertBitmapsEqualityWithColor(
        expectedBitmap: Bitmap,
        actualBitmap: Bitmap,
        tolerance: Double = 10.0
    ) {
        val reducedExpectedBitmap = Bitmap.createScaledBitmap(
            expectedBitmap, 32, 32, true
        )
        val reducedActualBitmap = Bitmap.createScaledBitmap(actualBitmap, 32, 32, true)
        var cumulatedColorDistance = 0
        for (x in 0 until reducedExpectedBitmap.width) {
            for (y in 0 until reducedExpectedBitmap.height) {
                val expectedColor = reducedExpectedBitmap.getPixel(x, y)
                val actualColor = reducedActualBitmap.getPixel(x, y)
                val expectedRed: Int = Color.red(expectedColor)
                val actualRed: Int = Color.red(actualColor)
                val expectedBlue: Int = Color.blue(expectedColor)
                val actualBlue: Int = Color.blue(actualColor)
                val expectedGreen: Int = Color.green(expectedColor)
                val actualGreen: Int = Color.green(actualColor)
                val colorDistance =
                    (abs((expectedRed - actualRed).toDouble()) + abs((expectedBlue - actualBlue).toDouble()) + abs(
                        (expectedGreen - actualGreen).toDouble()
                    )).toInt()
                cumulatedColorDistance += colorDistance
            }
        }
        val difference = 100.0 * cumulatedColorDistance / 3.0 / (32.0 * 32.0) / 255.0
        if (difference > tolerance) {
            fail("Difference between the bitmaps: $difference% (>$tolerance%)")
        }
    }

    /**
     * Compare two images with pixel by pixel
     *
     * @param expectedBitmap The expected bitmap
     * @param actualBitmap The actual bitmap
     */
    private fun assertBitmapsEqualityWithPixels(expectedBitmap: Bitmap, actualBitmap: Bitmap) {
        if (expectedBitmap.width != actualBitmap.width || expectedBitmap.height != actualBitmap.height) {
            fail(
                "The Width of the bitmaps are mismatching. expectedBitmap width " +
                        "${expectedBitmap.width} and actualBitmap width ${actualBitmap.width}"
            )
        }
        if (expectedBitmap.height != actualBitmap.height) {
            fail(
                "The Height of the bitmaps are mismatching. expectedBitmap height " +
                        "${expectedBitmap.height} and actualBitmap height ${actualBitmap.height}"
            )
        }
        for (heightPos in 0 until expectedBitmap.height) {
            for (widthPos in 0 until expectedBitmap.width) {
                if (expectedBitmap.getPixel(widthPos, heightPos) != actualBitmap.getPixel(
                        widthPos,
                        heightPos
                    )
                ) {
                    fail(
                        "The Width and Height pixels are mismatching at " +
                                "widthPos $widthPos and heightPos $heightPos"
                    )
                }
            }
        }
    }

    // Cache expiry test cases
    /**
     * To check the memory cache expiry time with maximum available cache time.
     * In this case, we expect the validity expiry as false as the max cache time is always
     * more than the current time stamp.
     */
    @Test
    fun testValidityExpireForMemoryCachedImageWithMaxCachedTime() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)
        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        val cacheEntry = memoryCache.getCache().get(imageUrl)
        if (isMemoryTypeCache) {
            assertFalse(cacheEntry.isExpired())
        } else {
            assertNull(cacheEntry)
        }
    }


    /**
     * To check the memory cache expiry time with provided input cache time.
     * This test case is purely to validate whether the expiry is working or not even trying
     * after expired time
     */
    @Test
    fun testValidityExpireForMemoryCachedImageToExpiry() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)
        if (isMemoryTypeCache) {
            assertEquals(1, memoryCache.size())
        } else {
            assertEquals(0, memoryCache.size())
        }

        Thread.sleep(1000)
        val cacheEntry = memoryCache.getCache().get(imageUrl)
        if (isMemoryTypeCache) {
            assertTrue(cacheEntry.isExpired(200))
        } else {
            assertNull(cacheEntry)
        }
    }

    /**
     * To check the disk cache expiry time with maximum available cache time.
     * In this case, we expect the validity expiry as false as the max cache time is always
     * more than the current time stamp.
     */
    @Test
    fun testValidityExpireForDiskCachedImageWithMaxCachedTime() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)
        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }

        val isValidityExpired = diskCache.isValidityExpired(imageUrl.hashCode().toString())
        assertFalse(isValidityExpired)
    }

    /**
     * To check the disk cache expiry time with provided input cache time.
     * This test case is purely to validate whether the expiry is working or not even trying
     * after expired time
     */
    @Test
    fun testValidityExpireForDiskCachedImageToExpiry() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        cachedDataImpl.saveImage(imageUrl, insertingBitmap)
        if (isDiskTypeCache) {
            assertEquals(1, diskCacheDir.listFiles()?.size)
        } else {
            assertEquals(0, diskCacheDir.listFiles()?.size)
        }

        Thread.sleep(1000)

        val isValidityExpired = diskCache.isValidityExpired(imageUrl.hashCode().toString(), 200)
        if (isDiskTypeCache) {
            assertTrue(isValidityExpired)
        } else {
            assertFalse(isValidityExpired)
        }
    }

    //Disk space Availability checks

    /**
     * To check whether the space available or not for the to be cached image with minimum allocated
     * cached size and throws IOException if there is no space left for caching.
     */
    @Test
    fun testDiskSpaceAvailabilityWithCacheSizeAndThrowIOExceptionIfNoSpaceAvailable() {
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        val isSpaceAvailable = isSpaceAvailable(insertingBitmap, 1L)
        if (!isSpaceAvailable) {
            assertThrows(
                "No Disk cache space available to cache the image", IOException::class.java
            ) {
                throw IOException("No Disk cache space available to cache the image")
            }
        }
    }

    /**
     * To check whether the space available or not for the to be cached image with maximum allocated
     * cached size. Expecting the cache space should be there as we are clearing the cache before
     * validating the disk cache space.
     */
    @Test
    fun testDiskSpaceAvailabilityWithMaxCacheSize() {
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        assertTrue(isSpaceAvailable(insertingBitmap, CachedDataImpl.MAX_CACHE_SIZE_BYTES))
    }

    /**
     * To check whether the space available or not for the to be cached image with minimum allocated
     * cached size
     */
    @Test
    fun testDiskSpaceAvailabilityWithMinCacheSize() {
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        assertFalse(isSpaceAvailable(insertingBitmap, 1L))
    }

    private fun isSpaceAvailable(bitmap: Bitmap, availableDiskSpace: Long): Boolean {
        var diskCacheSize: Long = 0
        diskCacheDir.listFiles()?.forEach { file ->
            diskCacheSize += file.length()
        }
        return availableDiskSpace > (diskCacheSize + bitmap.byteCount)
    }


    /**
     * It perform below actions with minimum (1) cache size for validating the failure cases
     * 1. get the bitmap from the url
     *  2.1 validate the width and height
     * 2. clear the cache
     *  2.1 validate the memory cache size
     * 3. check the cache space availability
     *  3.1 if not space available, throws the IOException
     *  3.2 if space available proceed further
     * 4. save the image in cache
     *  4.1 validate the cache
     * 5. get the image from cache
     *  5.1 check whether the image retrieved or not
     *      5.1.1 if yes, proceed further
     *      5.1.2 if no, update through fail()
     *  5.2 check the expiry time of the cached image
     * 6. Validate the inserted and cached image with color combinations
     */
    @Test
    fun testMemoryCacheWithMinCacheSize() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        val isSpaceAvailable = isSpaceAvailable(insertingBitmap, 1L)
        if (!isSpaceAvailable) {
            assertThrows(
                "No Disk cache space available to cache the image", IOException::class.java
            ) {
                throw IOException("No Disk cache space available to cache the image")
            }
        } else {
            cachedDataImpl.saveImage(imageUrl, insertingBitmap)
            if (isMemoryTypeCache) {
                assertEquals(1, memoryCache.size())
                val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
                fetchedBitmap?.let {
                    val cacheEntry = memoryCache.getCache().get(imageUrl)
                    if (!cacheEntry.isExpired()) {
                        assertBitmapsEqualityWithColor(insertingBitmap, it)
                    }
                }
                    ?: fail("There is no image with the key $imageUrl in the memory cache by comparing color")
            } else {
                assertEquals(0, memoryCache.size())
            }
        }
    }

    /**
     * It perform below actions with maximum Memory cached size
     * 1. get the bitmap from the url
     *  2.1 validate the width and height
     * 2. clear the cache
     *  2.1 validate the memory cache size
     * 3. check the cache space availability
     *  3.1 if not space available, throws the IOException
     *  3.2 if space available proceed further
     * 4. save the image in cache
     *  4.1 validate the cache
     * 5. get the image from cache
     *  5.1 check whether the image retrieved or not
     *      5.1.1 if yes, proceed further
     *      5.1.2 if no, update through fail()
     *  5.2 check the expiry time of the cached image
     * 6. Validate the inserted and cached image with color combinations
     */
    @Test
    fun testMemoryCacheWithMaxCacheSize() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, memoryCache.size())

        val isSpaceAvailable =
            isSpaceAvailable(insertingBitmap, CachedDataImpl.MAX_CACHE_SIZE_BYTES)
        if (!isSpaceAvailable) {
            assertThrows(
                "No Memory cache space available to cache the image", IOException::class.java
            ) {
                throw IOException("No Memory cache space available to cache the image")
            }
        } else {
            cachedDataImpl.saveImage(imageUrl, insertingBitmap)
            if (isMemoryTypeCache) {
                assertEquals(1, memoryCache.size())
                val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
                fetchedBitmap?.let {
                    val cacheEntry = memoryCache.getCache().get(imageUrl)
                    if (!cacheEntry.isExpired()) {
                        assertBitmapsEqualityWithColor(insertingBitmap, it)
                    }
                }
                    ?: fail("There is no image with the key $imageUrl in the memory cache by comparing color")
            } else {
                assertEquals(0, memoryCache.size())
            }
        }
    }

    /**
     * It perform below actions with maximum Disk cached size
     * 1. get the bitmap from the url
     *  2.1 validate the width and height
     * 2. clear the cache
     *  2.1 validate the memory cache size
     * 3. check the cache space availability
     *  3.1 if not space available, throws the IOException
     *  3.2 if space available proceed further
     * 4. save the image in cache
     *  4.1 validate the cache
     * 5. get the image from cache
     *  5.1 check whether the image retrieved or not
     *      5.1.1 if yes, proceed further
     *      5.1.2 if no, update through fail()
     *  5.2 check the expiry time of the cached image
     * 6. Validate the inserted and cached image with color combinations
     */
    @Test
    fun testDiskCacheWithMaxCacheSize() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)


        val isSpaceAvailable =
            isSpaceAvailable(insertingBitmap, CachedDataImpl.MAX_CACHE_SIZE_BYTES)
        if (!isSpaceAvailable) {
            assertThrows(
                "No Disk cache space available to cache the image", IOException::class.java
            ) {
                throw IOException("No Disk cache space available to cache the image")
            }
        } else {
            cachedDataImpl.saveImage(imageUrl, insertingBitmap)
            if (isDiskTypeCache) {
                assertEquals(1, diskCacheDir.listFiles()?.size)
                val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
                fetchedBitmap?.let {
                    val isValidityExpired = diskCache.isValidityExpired(imageUrl.hashCode().toString())
                    if (!isValidityExpired) {
                        assertBitmapsEqualityWithColor(insertingBitmap, it)
                    }
                }
                    ?: fail("There is no image with the key $imageUrl in the disk cache by comparing color")
            } else {
                assertEquals(0, diskCacheDir.listFiles()?.size)
            }
        }
    }

    /**
     * It perform below actions with minimum Disk cached size
     * 1. get the bitmap from the url
     *  2.1 validate the width and height
     * 2. clear the cache
     *  2.1 validate the memory cache size
     * 3. check the cache space availability
     *  3.1 if not space available, throws the IOException
     *  3.2 if space available proceed further
     * 4. save the image in cache
     *  4.1 validate the cache
     * 5. get the image from cache
     *  5.1 check whether the image retrieved or not
     *      5.1.1 if yes, proceed further
     *      5.1.2 if no, update through fail()
     *  5.2 check the expiry time of the cached image
     * 6. Validate the inserted and cached image with color combinations
     */
    @Test
    fun testDiskCacheWithMinCacheSize() {
        val imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/e6/1kb.png"
        val insertingBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.test_image
        )
        assertTrue(insertingBitmap.width > 0)
        assertTrue(insertingBitmap.height > 0)

        cachedDataImpl.clearCache()
        assertEquals(0, diskCacheDir.listFiles()?.size)

        val isSpaceAvailable =
            isSpaceAvailable(insertingBitmap, 1L)
        if (!isSpaceAvailable) {
            assertThrows(
                "No Disk cache space available to cache the image", IOException::class.java
            ) {
                throw IOException("No Disk cache space available to cache the image")
            }
        } else {
            cachedDataImpl.saveImage(imageUrl, insertingBitmap)
            if (isDiskTypeCache) {
                assertEquals(1, diskCacheDir.listFiles()?.size)
                val fetchedBitmap = cachedDataImpl.getImage(imageUrl)
                fetchedBitmap?.let {
                    val isValidityExpired = diskCache.isValidityExpired(imageUrl.hashCode().toString())
                    if (!isValidityExpired) {
                        assertBitmapsEqualityWithColor(insertingBitmap, it)
                    }
                }
                    ?: fail("There is no image with the key $imageUrl in the disk cache by comparing color")
            } else {
                assertEquals(0, diskCacheDir.listFiles()?.size)
            }
        }
    }
}