package com.fahim.mad_lab_compose.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.fahim.mad_lab_compose.SecondActivity
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

@Composable
fun FirstScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> lastname = result.data?.getStringExtra("LastNameKey")!! }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(value = firstName, onValueChange = { firstName = it })
        Button(onClick = {
            val intent = Intent(context, SecondActivity::class.java)
            intent.putExtra("FirstNameKey", firstName)
            launcher.launch(intent)
        }) {
            Text("Send")
        }
        Text("FullName: $firstName $lastname")
    }
}

@Preview(showBackground = true)
@Composable
private fun FirstScreenPreview() {
    MADLabComposeTheme() {
        FirstScreen()
    }
}
