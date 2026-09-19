package com.fahim.mad_lab_compose

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import coil.size.pxOrElse

/**
 * High-performance fetcher for local MediaStore content URIs.
 *
 * Instead of reading the full 10MB-40MB camera JPEG through BitmapFactory (which causes massive
 * byte-buffer allocations, EXIF rotation re-allocations, and frequent GC pauses), this fetcher
 * asks the Android OS for the hardware-accelerated, pre-rendered thumbnail directly.
 */
class MediaStoreThumbnailFetcher(
    private val context: Context,
    private val uri: Uri,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val width = options.size.width.pxOrElse { 300 }
        val height = options.size.height.pxOrElse { 300 }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return try {
                val bitmap = context.contentResolver.loadThumbnail(uri, Size(width, height), null)
                DrawableResult(
                    drawable = BitmapDrawable(context.resources, bitmap),
                    isSampled = true,
                    dataSource = DataSource.DISK
                )
            } catch (e: Exception) {
                null // Fallback to standard Coil pipeline if thumbnail is missing
            }
        } else {
            return try {
                val id = uri.lastPathSegment?.toLongOrNull()
                if (id != null) {
                    @Suppress("DEPRECATION")
                    val bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                        context.contentResolver,
                        id,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                    )
                    if (bitmap != null) {
                        DrawableResult(
                            drawable = BitmapDrawable(context.resources, bitmap),
                            isSampled = true,
                            dataSource = DataSource.DISK
                        )
                    } else null
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    class Factory(private val context: Context) : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (data.scheme == ContentResolver.SCHEME_CONTENT &&
                (data.authority == "media" || data.authority?.contains("media") == true)
            ) {
                return MediaStoreThumbnailFetcher(context, data, options)
            }
            return null
        }
    }
}