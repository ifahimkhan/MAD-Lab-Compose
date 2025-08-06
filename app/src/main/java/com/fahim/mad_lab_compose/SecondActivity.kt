package com.fahim.mad_lab_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val message = intent.getStringExtra("firstName")
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        var lastname by remember { mutableStateOf("") }
                        TextField(
                            label = { Text("Last Name") },
                            placeholder = { Text("Enter your surname") },
                            value = lastname,
                            onValueChange = { lastname = it }
                        )
                        Text(text = message ?: "No Message", textAlign = TextAlign.Center)
                        Button(onClick = {
                            val intent = Intent()
                            intent.putExtra("lastname", lastname)
                            setResult(RESULT_OK, intent)
                            finish()
                        }) {
                            Text("Send Back")
                        }
                    }
                }
            }
        }
    }
}
