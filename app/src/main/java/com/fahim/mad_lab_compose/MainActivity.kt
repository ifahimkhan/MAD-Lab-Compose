package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahim.mad_lab_compose.ui.theme.MADLabComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MADLabComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BasicCalculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCalculator(modifier: Modifier = Modifier) {
    val operations = listOf("+", "-", "*", "/")
    val selectedOperation = rememberSaveable { mutableStateOf(operations.first()) }
    val expanded = remember { mutableStateOf(false) }
    var number1 by rememberSaveable { mutableStateOf("") }
    var number2 by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterVertically
        )
    ) {

        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Enter number 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)), // Rounded corners
            shape = RoundedCornerShape(12.dp),    // Rounded outline
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Blue,
                focusedLabelColor = Color.Blue
            )
        )



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = { expanded.value = !expanded.value }
            ) {
                OutlinedTextField(
                    value = selectedOperation.value,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Operation") },
                    textStyle = TextStyle(fontSize = 20.sp),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)), // Rounded corners
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.Blue,
                        focusedLabelColor = Color.Blue
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded.value
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    operations.forEach { operation ->
                        DropdownMenuItem(
                            text = { Text(operation) },
                            onClick = {
                                selectedOperation.value = operation
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Enter number 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)), // Rounded corners
            shape = RoundedCornerShape(12.dp),    // Rounded outline
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                cursorColor = Color.Blue,
                focusedLabelColor = Color.Blue
            )
        )
        Button(
            enabled = number1.isNotEmpty() && number2.isNotEmpty(),
            onClick = {
                result = calculate(number1, number2, selectedOperation)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (number1.isNotEmpty() && number2.isNotEmpty()) {
                    Color.Black
                } else {
                    Color.LightGray
                },      // Background color
                contentColor = Color.White        // Text color
            ),
            shape = RoundedCornerShape(12.dp),   // Optional: match rounded style
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text("Calculate")
        }

        Text(text = "$result", style = TextStyle(fontSize = 24.sp));
    }

}

fun calculate(
    number1: String,
    number2: String,
    selectedOperation: MutableState<String>,
): String {
    val num1 = number1.toDoubleOrNull()
    val num2 = number2.toDoubleOrNull()

    if (num1 == null || num2 == null) {
        return "Invalid input"
    }

    return when (selectedOperation.value) {
        "+" -> (num1 + num2).toString()
        "-" -> (num1 - num2).toString()
        "*" -> (num1 * num2).toString()
        "/" -> if (num2 != 0.0) (num1 / num2).toString() else "Cannot divide by zero"
        else -> "Unknown operation"
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    MADLabComposeTheme {
        BasicCalculator()
    }
}