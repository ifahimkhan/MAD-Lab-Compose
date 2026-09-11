package com.fahim.mad_lab_compose

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Glide configuration for the gallery.
 *
 * The grid recycles hundreds of thumbnails while scrolling. Without an explicit bitmap pool
 * every decode allocates a fresh bitmap on the Java heap, which is what triggers the constant
 * "Background concurrent copying GC" lines in logcat. Sizing the pool from
 * [MemorySizeCalculator] lets Glide feed recycled bitmaps back into `BitmapFactory.inBitmap`,
 * so scrolling reuses memory instead of allocating it.
 *
 * Pool reuse only pays off when decoded bitmaps share dimensions, so callers must pass a fixed
 * `override(width, height)` (see [ThumbnailSpec]) and must not request hardware bitmaps -
 * hardware bitmaps live outside the Java heap and can never be recycled through the pool.
 */
@GlideModule
class GalleryGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val calculator = MemorySizeCalculator.Builder(context)
            .setBitmapPoolScreens(BITMAP_POOL_SCREENS)
            .setMemoryCacheScreens(MEMORY_CACHE_SCREENS)
            .build()

        builder.setBitmapPool(LruBitmapPool(calculator.bitmapPoolSize.toLong()))
        builder.setArrayPool(LruArrayPool(calculator.arrayPoolSizeInBytes))
        builder.setMemoryCache(LruResourceCache(calculator.memoryCacheSize.toLong()))
        builder.setDiskCache(
            InternalCacheDiskCacheFactory(context, DISK_CACHE_DIR, DISK_CACHE_SIZE_BYTES)
        )

        builder.setDefaultRequestOptions(
            RequestOptions()
                // Keep the default ARGB_8888 on purpose. On API 26+ Glide's Downsampler asks the
                // pool for the decoder's reported config (ARGB_8888) but PREFER_RGB_565 makes the
                // decoded bitmap RGB_565, so returned bitmaps never match later requests and the
                // pool degrades to 0% hits (measured on API 33). A homogeneous ARGB_8888 pool
                // costs 2x bytes per thumbnail but actually gets reused.
                .format(DecodeFormat.PREFER_ARGB_8888)
                // HARDWARE bitmaps bypass the pool entirely, so pooling requires opting out.
                .disallowHardwareConfig()
        )
    }

    /** Manifest-declared modules are unused here; skipping the parse speeds up startup. */
    override fun isManifestParsingEnabled(): Boolean = false

    companion object {
        private const val BITMAP_POOL_SCREENS = 4f
        private const val MEMORY_CACHE_SCREENS = 2f
        private const val DISK_CACHE_DIR = "glide_image_cache"
        private const val DISK_CACHE_SIZE_BYTES = 100L * 1024 * 1024
    }
}
