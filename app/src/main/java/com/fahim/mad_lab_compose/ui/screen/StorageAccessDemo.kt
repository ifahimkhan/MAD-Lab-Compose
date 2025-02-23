package com.fahim.mad_lab_compose.ui.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.mad_lab_compose.SAFViewModel

@Composable
fun StorageAccessDemo(viewModel: SAFViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedDirectoryUri by remember { mutableStateOf<Uri?>(null) }
    var fileContent by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button to open a file
        Button(onClick = {
            viewModel.openFile(context) { uri ->
                selectedFileUri = uri
                fileContent = viewModel.readFileContent(context, uri)
                Log.e("TAG", "StorageAccessDemo:$fileContent ", )
            }
        }) {
            Text("Open File")
        }

        // Button to save a file
        Button(onClick = {
            viewModel.saveFile(context, "example.txt", fileContent.toByteArray())
        }) {
            Text("Save File")
        }

        // Button to select a directory
        Button(onClick = {
            viewModel.selectDirectory(context) { uri ->
                selectedDirectoryUri = uri
            }
        }) {
            Text("Select Directory")
        }

        // Display selected file content
        if (fileContent.isNotEmpty()) {
            Text(
                text = "File Content: $fileContent",
                modifier = Modifier.padding(16.dp)
            )
        }

        // Display selected directory URI
        if (selectedDirectoryUri != null) {
            Text(
                text = "Selected Directory: $selectedDirectoryUri",
                modifier = Modifier.padding(16.dp)
            )
        }
    }


}