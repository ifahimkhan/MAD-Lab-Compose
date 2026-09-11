package com.fahim.mad_lab_compose

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val TAG: String? = MainActivity::class.java.name

private const val GRID_COLUMNS = 3
private val CELL_PADDING = 4.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerpadding ->
                    GalleryApp(innerpadding)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryApp(innerpadding: PaddingValues) {
    Log.e(TAG, "GalleryApp: ")
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val permissionState = rememberPermissionState(permission[0])
    if (permissionState.hasPermission) {
        Box(modifier = Modifier.padding(innerpadding)) {
            GalleryContent()
        }
    } else {
        LaunchedEffect(Unit) {
            Log.e(TAG, "GalleryApp: LaunchEffect")
            permissionState.launchPermissionRequest()
        }
        PermissionDeniedContent()
    }
}

@Composable
fun GalleryContent() {
    val context = LocalContext.current
    // MediaStore query walks every image row; keep it off the main thread so first frame is not blocked.
    val images by produceState(initialValue = emptyList<GalleryImage>(), context) {
        value = withContext(Dispatchers.IO) { ImageHelper.getImages(context) }
    }
    var useGlide by remember { mutableStateOf(false) }

    Column {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center
        ) {
            // One decode target for every cell, sized to the real grid so pooled bitmaps are
            // interchangeable and thumbnails stay sharp on wide screens.
            val density = LocalDensity.current
            val cellSize = IntSize(
                width = ThumbnailSpec.cellWidthPx(
                    gridWidthPx = with(density) { maxWidth.roundToPx() },
                    columns = GRID_COLUMNS,
                ),
                height = ThumbnailSpec.heightPx(density.density),
            )
            LazyVerticalGrid(columns = GridCells.Fixed(GRID_COLUMNS)) {
                // Stable keys let the grid reuse cell state on scroll instead of restarting loads.
                items(images, key = { it.uri }) { image ->
                    if (useGlide) {
                        GlideThumbnail(image = image, cellSize = cellSize)
                    } else {
                        CoilThumbnail(uri = image.uri, context = context)
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            onClick = { useGlide = !useGlide }) {
            Text(text = if (useGlide) "Switch to Coil" else "Switch to Glide")
        }
    }
}

/**
 * Glide cell tuned for bitmap-pool reuse (see [GalleryGlideModule]).
 *
 * `override(cellSize)` fixes the decode target so every thumbnail is the same bitmap size and
 * recycled pool entries can be handed straight to `BitmapFactory.inBitmap`. `centerCrop` runs
 * through the pool too, and `RESOURCE` caching stores that small crop on disk instead of copying
 * the full-resolution original. The `DATE_MODIFIED` signature invalidates that cached crop when
 * a photo is edited in place, because its `content://` URI does not change.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun GlideThumbnail(image: GalleryImage, cellSize: IntSize) {
    GlideImage(
        model = image.uri,
        contentDescription = null,
        modifier = thumbnailModifier(),
        contentScale = ContentScale.Crop
    ) { requestBuilder ->
        requestBuilder
            .override(cellSize.width, cellSize.height)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .signature(ObjectKey(image.dateModified))
            .placeholder(R.drawable.ic_launcher_background)
    }
}

@Composable
private fun CoilThumbnail(uri: Uri, context: Context) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(uri)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .build(),
        contentDescription = null,
        modifier = thumbnailModifier(),
        contentScale = ContentScale.Crop
    )
}

private fun thumbnailModifier(): Modifier = Modifier
    .fillMaxWidth()
    .height(ThumbnailSpec.HEIGHT_DP.dp)
    .padding(CELL_PADDING)

@Composable
fun PermissionDeniedContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Permission to access storage is required to display images.")
    }
}
