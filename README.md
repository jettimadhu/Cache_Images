# Cache_Images
Cache the images using Memory and Disk Cache - 


**Setup App Development Environment**
1. Download and install [Android Studio](https://developer.android.com/studio)
2. Download/clone this repository to a folder on your computer
3. Start Android Studio, open your source code folder, and check if the Gradle build will be successful using the Build/Make Module "App". You might have to install the right Android SDK via Tools/SDK Manager first.
4. Run ./gradlew from the root directory of the project you checkout to build all debug versions, this might take a while.
5. If the build is successful, you can run the app by doing the following: click Run -> Run 'app'.
6. Connect your phone or create a new virtual device following on-screen instruction

**Overview**:
The main intention of this project is to use the traditional way of data handling/caching without using any third-party libraries.
Displaying the images from [Unsplash](https://unsplash.com/developers) in a Staggered Grid. 
For demo purposes, we can request a maximum of 50 requests in an hour.

**Features**: 
1. Image Loading - Loads images asynchronously using traditional Handlers and Threads.
2. Asynchronously load more images based on the scrolling behavior
3. Save in Memory/Disk Cache - Saves the images in memory and/or disk based on the build config parameter CACHE_TYPE. 
 The values for cache type are like
   1. MEMORY - 1, 
   2. DISK - 2,
   3. MEMORY_AND_DISK - 3 
4. If the image is read from disk, updating the same to the memory cache. 
5. Caching the images with maximum size and expiry time.
   1. Validating the expiry time (24 Hours) before returning the requested image from both memory and disk cache. 
   2. Saving the images in the Disk cache by checking the available space in the cache.
6. Added error handling for basic operations.

**Advantages of caching** 
1. Improved performance
2. Improved responsiveness
3. Reduce data usage
4. Enhance user experience