package com.fahim.mad_lab_compose

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

class ImageHelper {

    companion object {
        fun getImages(context: Context): List<GalleryImage> {
            val images = mutableListOf<GalleryImage>()
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_MODIFIED,
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val modifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    images.add(GalleryImage(contentUri, cursor.getLong(modifiedColumn)))
                }
            }
            return images.toList()
        }

        fun getImageLoaderCache(context: Context): ImageLoader {
            val imageLoader = ImageLoader.Builder(context)
                .memoryCachePolicy(CachePolicy.ENABLED) // Enable memory cache
                .diskCachePolicy(CachePolicy.ENABLED)   // Enable disk cache
                .diskCache {
                    DiskCache.Builder()
                        .directory(context.cacheDir.resolve("image_cache")) // Set cache directory
                        .maxSizePercent(0.1) // Use 10% of available storage
                        .build()
                }
                .memoryCache {
                    MemoryCache.Builder(context)
                        .maxSizePercent(0.25).build()
                }
                .build()
            Coil.setImageLoader(imageLoader)
            return imageLoader
        }

    }
}
