package com.fahim.mad_lab_compose

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var firstName = intent.getStringExtra("FirstNameKey")
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = firstName!!,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalActivity.current

    var lastname by remember { mutableStateOf("") }
    Column(modifier= Modifier.fillMaxSize()) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        OutlinedTextField(value = lastname, onValueChange = { lastname = it })
        Button(onClick = {
            val intent = Intent()
            intent.putExtra("LastNameKey",lastname)
            context?.setResult(RESULT_OK,intent)
            context?.finish()

        }) { Text("Send Back lastname") }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADLabComposeTheme {
        Greeting("Android")
    }
}