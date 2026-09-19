package com.fahim.mad_lab_compose

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import kotlinx.coroutines.Dispatchers

class GalleryApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("coil_image_cache"))
                    .maxSizeBytes(100L * 1024 * 1024)
                    .build()
            }
            // Limit decoding threads to prevent starving the UI thread or flooding allocations
            .decoderDispatcher(Dispatchers.IO.limitedParallelism(4))
            .components {
                add(MediaStoreThumbnailFetcher.Factory(this@GalleryApplication))
            }
            .allowHardware(true)
            .build()
    }
}
