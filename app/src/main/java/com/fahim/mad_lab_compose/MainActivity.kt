package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GroceryList(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}



val groceryItems = listOf(
    GroceryItem(
        1,
        "Apples",
        2.99,
        "https://images.pexels.com/photos/102104/pexels-photo-102104.jpeg"
    ),
    GroceryItem(
        2,
        "Bananas",
        1.50,
        "https://images.pexels.com/photos/61127/pexels-photo-61127.jpeg"
    ),
    GroceryItem(
        3,
        "Milk",
        3.49,
        "https://images.pexels.com/photos/248412/pexels-photo-248412.jpeg"
    ),
    GroceryItem(
        4,
        "Bread",
        2.25,
        "https://images.pexels.com/photos/209206/pexels-photo-209206.jpeg"
    ),
    GroceryItem(
        5,
        "Eggs",
        4.00,
        "https://images.pexels.com/photos/162712/egg-white-food-protein-162712.jpeg"
    )
)

@Composable
fun GroceryList(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(groceryItems.size) { index ->
            val item = groceryItems[index]
            ListItem(
                leadingContent = {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .width(48.dp)
                            .height(48.dp)
                    )
                },
                headlineContent = { Text(item.title) },
                supportingContent = { Text("Price: $${item.price}") },
                trailingContent = {
                    Image(imageVector = Icons.Default.Add, contentDescription = "")
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MADLabComposeTheme {
        GroceryList()
    }
}