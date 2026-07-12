package com.fahim.mad_lab_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    BasicCalculator(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

private val operations = listOf("+", "-", "*", "/")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCalculator(modifier: Modifier = Modifier) {
    // 1. State: values that trigger recomposition when they change.
    var number1 by rememberSaveable { mutableStateOf("") }
    var number2 by rememberSaveable { mutableStateOf("") }
    var operation by rememberSaveable { mutableStateOf(operations.first()) }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var result by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // 2. Inputs: two numbers.
        OutlinedTextField(
            value = number1,
            onValueChange = { number1 = it },
            label = { Text("Enter number 1") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Operation picker: dropdown of "+ - * /".
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = operation,
                onValueChange = {},
                readOnly = true,
                label = { Text("Operation") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                operations.forEach { op ->
                    DropdownMenuItem(
                        text = { Text(op) },
                        onClick = { operation = op; expanded = false }
                    )
                }
            }
        }

        OutlinedTextField(
            value = number2,
            onValueChange = { number2 = it },
            label = { Text("Enter number 2") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Action: compute result on click.
        val inputsFilled = number1.isNotEmpty() && number2.isNotEmpty()
        Button(
            enabled = inputsFilled,
            onClick = { result = calculate(number1, number2, operation) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (inputsFilled) Color.Black else Color.LightGray,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Calculate")
        }

        Text(text = result, fontSize = 24.sp)
    }
}

// 5. Logic: pure function, easy to test on its own.
fun calculate(number1: String, number2: String, operation: String): String {
    val num1 = number1.toDoubleOrNull() ?: return "Invalid input"
    val num2 = number2.toDoubleOrNull() ?: return "Invalid input"

    return when (operation) {
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
