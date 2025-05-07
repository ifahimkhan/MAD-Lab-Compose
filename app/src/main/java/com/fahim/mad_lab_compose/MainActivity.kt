package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.fahim.mad_lab_compose.ui.CalculatorScreen
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CalculatorScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CalculatorAppPreview() {
    MADLabComposeTheme {
        CalculatorScreen()
    }
}
