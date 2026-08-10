@file:Suppress("DEPRECATION")

package com.fahim.mad_lab_compose.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun SimpleWebViewScreen(
    initialUrl: String = "https://google.com"
) {
    // ---- 1. State management ----
    // Holds loading state, current URL, page title, error state, etc.
    val state = rememberWebViewState(url = initialUrl)

    // ---- 2. Navigation control ----
    // Lets you call navigator.navigateBack(), navigateForward(), reload(), etc.
    val navigator = rememberWebViewNavigator()

    // Track a text field for a simple address bar (optional but handy)
    var urlText by rememberSaveable { mutableStateOf(initialUrl) }

    Scaffold(
        topBar = {
            Column {

                // Simple address bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("URL") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        // Navigate to whatever the user typed
                        navigator.loadUrl(urlText)
                    }) {
                        Text("Go")
                    }
                }

                // Loading progress indicator
                when (val loadingState = state.loadingState) {
                    is LoadingState.Loading -> {
                        LinearProgressIndicator(
                            progress = loadingState.progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        urlText = state.lastLoadedUrl ?: ""
                    }

                    else -> Unit
                }
            }
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { navigator.navigateBack(); },
                        enabled = navigator.canGoBack
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }

                    IconButton(
                        onClick = {
                            navigator.navigateForward()
                        },
                        enabled = navigator.canGoForward
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Forward")
                    }

                    IconButton(onClick = { navigator.reload() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reload")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

            // ---- 3. Custom settings ----
            WebView(
                state = state,
                navigator = navigator,
                modifier = Modifier.fillMaxSize(),
                onCreated = { webView ->
                    webView.settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        // Allow mixed content if you need http resources on an https page
                        // mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                }
            )

            // ---- 4. Error handling ----
            val errorState = state.errorsForCurrentRequest.lastOrNull()
            if (errorState != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Failed to load page")
                        Text(errorState.error.description?.toString() ?: "Unknown error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { navigator.reload() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SimpleWebViewScreenPreview() {
    SimpleWebViewScreen()
}