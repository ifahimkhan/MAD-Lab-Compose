package com.fahim.mad_lab_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fahim.mad_lab_compose.ui.screen.StorageAccessDemo
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val OPEN_FILE_REQUEST_CODE = 100
        const val SAVE_FILE_REQUEST_CODE = 101
        const val SELECT_DIRECTORY_REQUEST_CODE = 102
    }

    private lateinit var viewModel: SAFViewModel

    val activityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            // Handle the result here
            if (result.resultCode == OPEN_FILE_REQUEST_CODE) {
                // Handle the selected file URI
                result.data?.data?.let { uri ->
                    val stringValue= viewModel.readFileContent(this@MainActivity, uri)

                }
            } else if (result.resultCode == SAVE_FILE_REQUEST_CODE) {
                // Handle the saved file URI
            } else if (result.resultCode == SELECT_DIRECTORY_REQUEST_CODE) {
                // Handle the selected directory URI
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StorageAccessDemo()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADLabComposeTheme {
        Greeting("Android")
    }
}