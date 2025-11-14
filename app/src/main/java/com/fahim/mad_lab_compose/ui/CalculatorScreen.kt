package com.fahim.mad_lab_compose.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

@Composable
fun CalculatorScreen() {
    var displayValue by remember { mutableStateOf("0") }
    var firstOperand by remember { mutableStateOf(0.0) }
    var currentOperation by remember { mutableStateOf<Operation?>(null) }
    var resetDisplay by remember { mutableStateOf(false) }
    var previousEquation by remember { mutableStateOf("") }

    // Moved these functions inside remember to make them stable
    val calculatorActions = remember {
        object {
            fun appendNumber(number: String) {
                if (resetDisplay) {
                    displayValue = if (number == ".") "0." else number
                    resetDisplay = false
                } else {
                    if (displayValue == "0" && number != ".") {
                        displayValue = number
                    } else {
                        displayValue += number
                    }
                }
            }

            fun formatResult(result: Double): String {
                val df = DecimalFormat("#.######")
                return if (result.isNaN()) {
                    "Error"
                } else if (result % 1 == 0.0) {
                    result.toLong().toString()
                } else {
                    df.format(result)
                }
            }

            fun performOperation(): Double? {
                displayValue.replace("+", "")
                displayValue.replace("-", "")
                displayValue.replace("x", "")
                displayValue.replace("÷", "")
                displayValue.replace("%", "")
                val currentValue = displayValue.toDoubleOrNull() ?: return null

                return when (currentOperation) {
                    Operation.ADD -> firstOperand + currentValue
                    Operation.SUBTRACT -> firstOperand - currentValue
                    Operation.MULTIPLY -> firstOperand * currentValue
                    Operation.DIVIDE -> if (currentValue != 0.0) firstOperand / currentValue else Double.NaN
                    null -> currentValue
                }
            }

            fun createPreviousEquation(currentOperation: Operation): String {
                return when (currentOperation) {
                    Operation.ADD -> "${formatResult(firstOperand)} + ${formatResult(displayValue.toDouble())}"
                    Operation.SUBTRACT -> "${formatResult(firstOperand)} - ${formatResult(displayValue.toDouble())}"
                    Operation.MULTIPLY -> "${formatResult(firstOperand)} * ${formatResult(displayValue.toDouble())}"
                    Operation.DIVIDE -> "${formatResult(firstOperand)} / ${formatResult(displayValue.toDouble())}"
                    null -> displayValue
                }
            }

            fun handleOperation(op: Operation) {
                performOperation()?.let {
                    displayValue = formatResult(it)
                    firstOperand = it
                }
                currentOperation = op
                resetDisplay = true
            }

            fun handleEquals() {
                performOperation()?.let {
                    previousEquation = createPreviousEquation(currentOperation!!) ?: ""
                    displayValue = formatResult(it)
                    firstOperand = it
                }
                currentOperation = null
                resetDisplay = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .padding(8.dp),
            contentAlignment = Alignment.BottomEnd
        ) {

            Column {
                // Previous equation (top)
                Text(
                    text = previousEquation,
                    color = Color.LightGray.copy(alpha = 0.7f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = displayValue,
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Row 1: AC, +/-, %, ÷
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("AC", Color(0xFFA5A5A5)) {
                    displayValue = "0"
                    firstOperand = 0.0
                    currentOperation = null
                    previousEquation = ""
                }
                CalculatorButton("+/-", Color(0xFFA5A5A5)) {
                    displayValue = if (displayValue.startsWith("-")) {
                        displayValue.substring(1)
                    } else {
                        "-$displayValue"
                    }
                }
                CalculatorButton("%", Color(0xFFA5A5A5)) {
                    displayValue = (displayValue.toDouble() / 100).toString()
                }
                CalculatorButton("÷", Color(0xFFFF9500)) {
                    calculatorActions.handleOperation(Operation.DIVIDE)
//                    calculatorActions.appendNumber("÷")
                }
            }

            // Row 2: 7, 8, 9, ×
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("7", Color(0xFF333333)) {
                    calculatorActions.appendNumber("7")
                }
                CalculatorButton("8", Color(0xFF333333)) {
                    calculatorActions.appendNumber("8")
                }
                CalculatorButton("9", Color(0xFF333333)) {
                    calculatorActions.appendNumber("9")
                }
                CalculatorButton("×", Color(0xFFFF9500)) {
                    calculatorActions.handleOperation(Operation.MULTIPLY)
//                    calculatorActions.appendNumber("x")

                }
            }

            // Row 3: 4, 5, 6, -
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("4", Color(0xFF333333)) {
                    calculatorActions.appendNumber("4")
                }
                CalculatorButton("5", Color(0xFF333333)) {
                    calculatorActions.appendNumber("5")
                }
                CalculatorButton("6", Color(0xFF333333)) {
                    calculatorActions.appendNumber("6")
                }
                CalculatorButton("-", Color(0xFFFF9500)) {
                    calculatorActions.handleOperation(Operation.SUBTRACT)
//                    calculatorActions.appendNumber("-")
                }
            }

            // Row 4: 1, 2, 3, +
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton("1", Color(0xFF333333)) {
                    calculatorActions.appendNumber("1")
                }
                CalculatorButton("2", Color(0xFF333333)) {
                    calculatorActions.appendNumber("2")
                }
                CalculatorButton("3", Color(0xFF333333)) {
                    calculatorActions.appendNumber("3")
                }
                CalculatorButton("+", Color(0xFFFF9500)) {
                    calculatorActions.handleOperation(Operation.ADD)
//                    calculatorActions.appendNumber("+")
                }
            }

            // Row 5: 0, ., =
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalculatorButton(
                    "0",
                    Color(0xFF333333),
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .weight(2.0f)
                        .height(84.dp)
                ) {
                    calculatorActions.appendNumber("0")
                }
                CalculatorButton(".", Color(0xFF333333)) {
                    if (!displayValue.contains(".")) {
                        calculatorActions.appendNumber(".")
                    }
                }
                CalculatorButton("=", Color(0xFFFF9500)) {
                    calculatorActions.handleEquals()
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier.size(84.dp),
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedAlpha: Float by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "alpha"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(if (isPressed) 2.dp else 4.dp, CircleShape)
            .clip(CircleShape)
            .graphicsLayer { alpha = animatedAlpha }
            .background(color)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

enum class Operation {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

// Rest of the code remains the same (CalculatorButton, Operation enum, etc.)

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    MaterialTheme {
        CalculatorScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorButtonPreview() {
    MaterialTheme {
        CalculatorButton("10", Color(0xFFA5A5A5)) {

        }
    }
}