package com.fahim.mad_lab_compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.Dispatchers

class MyApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

    }
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // Use 25% of available heap for memory cache
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_cache"))
                    .maxSizeBytes(100L * 1024 * 1024) // 100 MB
                    .build()
            }
            // Limit decode thread pool to prevent CPU saturation during fling gestures
            .decoderDispatcher(Dispatchers.IO.limitedParallelism(4))
            .components {
                add(MediaStoreThumbnailFetcher.Factory(this@MyApplication))
            }
            .allowHardware(true)
            .build()
    }
}