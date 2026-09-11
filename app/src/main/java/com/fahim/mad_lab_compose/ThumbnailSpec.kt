package com.fahim.mad_lab_compose

import kotlin.math.roundToInt

/**
 * Fixed thumbnail geometry shared by every grid cell.
 *
 * Glide's bitmap pool can only hand a recycled bitmap to a new decode when the sizes line up,
 * so every gallery request must ask for the same target rectangle. Keeping the maths here, rather
 * than inline at the call site, means the Glide module and the UI cannot drift apart.
 */
object ThumbnailSpec {
    /** Height of every grid cell, in dp. */
    const val HEIGHT_DP = 150

    /** Converts [HEIGHT_DP] to physical pixels for the given screen [density]. */
    fun heightPx(density: Float): Int {
        require(density > 0f) { "density must be positive, was $density" }
        return (HEIGHT_DP * density).roundToInt().coerceAtLeast(1)
    }

    /**
     * Width of one cell when [gridWidthPx] is split evenly into [columns].
     * Matching the decode target to the real cell width keeps thumbnails sharp on wide screens
     * instead of upscaling a fixed square.
     */
    fun cellWidthPx(gridWidthPx: Int, columns: Int): Int {
        require(gridWidthPx > 0) { "gridWidthPx must be positive, was $gridWidthPx" }
        require(columns > 0) { "columns must be positive, was $columns" }
        return (gridWidthPx / columns).coerceAtLeast(1)
    }
}
