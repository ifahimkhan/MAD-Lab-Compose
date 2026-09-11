package com.fahim.mad_lab_compose

import android.net.Uri

/**
 * One MediaStore image row.
 *
 * [dateModified] is the MediaStore `DATE_MODIFIED` value (seconds). A row keeps the same [uri]
 * after an in-place edit, so the cache signature must come from this timestamp or Glide would
 * keep serving the stale disk-cached thumbnail.
 */
data class GalleryImage(
    val uri: Uri,
    val dateModified: Long,
)
