package com.fahim.mad_lab_compose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListButtons(
                        innerPadding
                    )
                }
            }
        }
    }
}

@Composable
fun ListButtons(innerPadding: PaddingValues) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            MapButton()
            MarketButton()
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceAround,
        ) {

            DialerButton()
            EmailButton()
        }
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceAround,
        ) {

            CameraButton()
        }


    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MADLabComposeTheme {
        ListButtons(innerPadding = PaddingValues())
    }
}


@Composable
fun MapButton() {
    val context = LocalContext.current
    Button(onClick = {
        val gmmIntentUri = Uri.parse("geo:0,0?q=Mukesh patel NMIMS")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        context.startActivity(mapIntent)
    }) {
        Text("Open Map")
    }
}

@Composable
fun MarketButton() {
    val context = LocalContext.current
    Button(onClick = {
        val appPackageName = "com.gdscmpstme.mpstme_ontrack"
        val uri = Uri.parse("market://details?id=$appPackageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }) {
        Text("Open Play Store")
    }
}

@Composable
fun DialerButton() {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:1234567890")
        context.startActivity(intent)
    }) {
        Text("Dial Number")
    }
}

@Composable
fun EmailButton() {
    val context = LocalContext.current
    Button(onClick = {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:android@gmail.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Hello from Compose")
        intent.putExtra(Intent.EXTRA_TEXT, "This is a test email.")
        context.startActivity(intent)
    }) {
        Text("Compose Email")
    }
}

@Composable
fun CameraButton() {
    val context = LocalContext.current
    Button(onClick = {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.startActivity(cameraIntent)
    }) {
        Text("Open Camera")
    }
}


