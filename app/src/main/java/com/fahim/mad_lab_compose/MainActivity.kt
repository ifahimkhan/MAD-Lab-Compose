package com.fahim.mad_lab_compose

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

val TAG: String? = MainActivity::class.java.name

class MainActivity : ComponentActivity() {
    private lateinit var galleryViewModel: GalleryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryViewModel = GalleryViewModel(application)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    GalleryApp(galleryViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryApp(galleryViewModel: GalleryViewModel = viewModel()) {
    Log.e(TAG, "GalleryApp: ")
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(android.Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        listOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    val permissionState = rememberPermissionState(permission[0])
    if (permissionState.hasPermission) {
        GalleryContent(viewModel = galleryViewModel)
    } else {
        LaunchedEffect(Unit) {
            Log.e(TAG, "GalleryApp: LaunchEffect")
            permissionState.launchPermissionRequest()
        }
        PermissionDeniedContent()
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GalleryContent(viewModel: GalleryViewModel = viewModel()) {
//    val context = LocalContext.current
//    val images = remember { ImageHelper.getImages(context) }
    val images by viewModel.images.collectAsStateWithLifecycle(initialValue = emptyList())

    LazyVerticalGrid(
        columns = GridCells.Fixed(3)
    ) {
        items(images) { uri ->
            Log.e(TAG, "GalleryContent: $uri", )
            GlideImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            /*Image(
                painter = rememberAsyncImagePainter(
                    model = uri,
                    imageLoader = ImageHelper.getImageLoaderCache(LocalContext.current)
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )*/
        }
    }
}


@Composable
fun PermissionDeniedContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Permission to access storage is required to display images.")
    }
}
