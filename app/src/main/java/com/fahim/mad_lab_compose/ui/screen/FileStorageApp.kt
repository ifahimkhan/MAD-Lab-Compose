package com.fahim.mad_lab_compose.ui.screen

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.mad_lab_compose.interfaces.StorageType
import com.fahim.mad_lab_compose.viewmodel.FilesViewModel

@Composable
fun FileStorageApp(viewModel: FilesViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = viewModel.fileName.value,
            onValueChange = { viewModel.setFileName(it) },
            label = { Text("File Name") },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        )

        TextField(
            value = viewModel.fileContent.value,
            onValueChange = { if (it.isNotEmpty() && it.isNotBlank() && it.length>0) viewModel.setFileContent(it) },
            label = { Text("File Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color = Red)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Row {
                RadioButton(selected = viewModel.storageType.value == StorageType.INTERNAL,
                    onClick = { viewModel.setStorageType(StorageType.INTERNAL) })
                Text(
                    "Internal",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable {
                            viewModel.setStorageType(StorageType.INTERNAL)
                        },
                )
            }

            Row {
                RadioButton(selected = viewModel.storageType.value == StorageType.EXTERNAL,
                    onClick = { viewModel.setStorageType(StorageType.EXTERNAL) })
                Text(
                    "External",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable {
                            viewModel.setStorageType(StorageType.EXTERNAL)
                        },
                )
            }


        }


        PermissionHandler(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onPermissionGranted = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        viewModel.writeToFile(viewModel.fileContent.value)
                    }) {
                        Text("Write File")
                    }
                    Button(onClick = {
                        viewModel.readFromFile()
                    }) {
                        Text("Read File")
                    }
                }

            },
            onPermissionDenied = {
                Text("Permission denied. Cannot access external storage.")
            })
    }
}

@Preview
@Composable
private fun FileStorageAppPreview() {
    FileStorageApp()
}