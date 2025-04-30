package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageComparisonScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun ImageComparisonScreen(innerPadding: PaddingValues) {
    var useGlide by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        // List takes all available space except what the button needs
        Box(
            modifier = Modifier.weight(1f)
        ) {
            if (useGlide) {
                GlideImageList()
            } else {
                CoilImageList()
            }
        }
        Button(
            onClick = { useGlide = !useGlide },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(if (useGlide) "Switch to Coil" else "Switch to Glide")
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageList() {
    val items = remember { (1..1000).map { it } }

    LazyColumn {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glide Image
                GlideImage(
                    model = "https://picsum.photos/200/200?image=${item}",
                    contentDescription = "Item $item",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                ) { request ->
                    request
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .placeholder(R.drawable.ic_launcher_background)
                }

                Spacer(Modifier.width(16.dp))

                Text("Item $item", style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider(thickness = 1.dp)

        }
    }
}

// Helper function to get mipmap resources
fun getMipmapResourceId(index: Int): Int {
    val resources = arrayOf(
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        // Add all your mipmap resources
    )
    return resources[index % resources.size]
}

@Composable
fun CoilImageList() {
    val items = remember { (1..1000).map { it } }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 72.dp)
    ) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coil Image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https://picsum.photos/200/200?image=${item}")
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_foreground)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Item $item",
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(16.dp))

                Column {
                    Text("Item $item", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "URL :https://picsum.photos/200/200?image=$item",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                }
            }
            HorizontalDivider(thickness = 1.dp)

        }
    }
}


@Preview
@Composable
private fun ImageComparisonScreenPreview() {
    ImageComparisonScreen(innerPadding = PaddingValues())
}