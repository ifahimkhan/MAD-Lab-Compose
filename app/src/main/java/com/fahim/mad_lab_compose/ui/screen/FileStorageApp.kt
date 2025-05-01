package com.fahim.mad_lab_compose.ui.screen

import android.Manifest
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fahim.mad_lab_compose.interfaces.Storage
import com.fahim.mad_lab_compose.interfaces.StorageType
import com.fahim.mad_lab_compose.model.ExternalStorage
import com.fahim.mad_lab_compose.model.InternalStorage

@Composable
fun FileStorageApp() {
    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var fileData by rememberSaveable { mutableStateOf("") }
    var fileName by remember { mutableStateOf("example.txt") }
    var storageType by remember { mutableStateOf(StorageType.INTERNAL) }
    val internalStorage = InternalStorage()
    val externalStorage = ExternalStorage()
    var storage: Storage? = null

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = fileName,
            onValueChange = { fileName = it },
            label = { Text("File Name") },
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
        )

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("File Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(color = Red)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Row {
                RadioButton(selected = storageType == StorageType.INTERNAL,
                    onClick = { storageType = StorageType.INTERNAL })
                Text(
                    "Internal",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable {
                            storageType = StorageType.INTERNAL
                        },
                )
            }

            Row {
                RadioButton(selected = storageType == StorageType.EXTERNAL,
                    onClick = { storageType = StorageType.EXTERNAL })
                Text(
                    "External",
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clickable {
                            storageType = StorageType.EXTERNAL
                        },
                )
            }


        }


        PermissionHandler(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onPermissionGranted = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (storageType == StorageType.INTERNAL)
                                storage = internalStorage
                            else if (storageType == StorageType.EXTERNAL)
                                storage = externalStorage
                            storage?.writeToFile(
                                context = context, fileName = fileName, data = text
                            )
                        }) {
                            Text("Write File")
                        }
                        Button(onClick = {
                            if (storageType == StorageType.INTERNAL)
                                storage = internalStorage
                            else if (storageType == StorageType.EXTERNAL)
                                storage = externalStorage
                            fileData = storage?.readFromFile(context, fileName) ?: ""
                        }) {
                            Text("Read File")
                        }
                    }
                    Text(
                        text = fileData,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle
                    )
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