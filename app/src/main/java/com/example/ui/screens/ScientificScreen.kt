package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.calculator.model.AngleMode
import com.example.ui.components.*
import com.example.viewmodel.CalculatorUiState

@Composable
fun ScientificScreen(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onPercentage: () -> Unit,
    onNegate: () -> Unit,
    onEquals: () -> Unit,
    onFunction: (String) -> Unit,
    onParenthesis: () -> Unit,
    onToggleAngleMode: () -> Unit,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onOpenHistory: () -> Unit,
    onBack: () -> Unit,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    var is2ndMode by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            NumerixAppBar(
                title = "Scientific",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("scientific_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "View History",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            CreatorFooter()
        },
        modifier = modifier.testTag("scientific_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CalculatorDisplay(
                expression = uiState.expression,
                previewResult = uiState.previewResult,
                finalResult = uiState.finalResult,
                isError = uiState.isError,
                errorMessage = uiState.errorMessage,
                angleMode = uiState.angleMode,
                hasMemory = uiState.hasMemory,
                onToggleAngleMode = onToggleAngleMode,
                modifier = Modifier.weight(1f, fill = false)
            )

            MemoryRow(
                onMemoryClear = onMemoryClear,
                onMemoryRecall = onMemoryRecall,
                onMemoryAdd = onMemoryAdd,
                onMemorySubtract = onMemorySubtract,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalculatorKey(
                        text = if (is2ndMode) "1st" else "2nd",
                        onClick = { is2ndMode = !is2ndMode },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = uiState.angleMode.name,
                        onClick = onToggleAngleMode,
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = if (is2ndMode) "sin⁻¹" else "sin",
                        onClick = { onFunction(if (is2ndMode) "sin⁻¹" else "sin") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = if (is2ndMode) "cos⁻¹" else "cos",
                        onClick = { onFunction(if (is2ndMode) "cos⁻¹" else "cos") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = if (is2ndMode) "tan⁻¹" else "tan",
                        onClick = { onFunction(if (is2ndMode) "tan⁻¹" else "tan") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalculatorKey(
                        text = "ln",
                        onClick = { onFunction("ln") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "log",
                        onClick = { onFunction("log") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = if (is2ndMode) "³√" else "√",
                        onClick = { onFunction(if (is2ndMode) "³√" else "√") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "x²",
                        onClick = { onFunction("x²") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "xʸ",
                        onClick = { onFunction("xʸ") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalculatorKey(
                        text = "π",
                        onClick = { onFunction("π") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "e",
                        onClick = { onFunction("e") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "!",
                        onClick = { onFunction("!") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = "(",
                        onClick = { onFunction("(") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                    CalculatorKey(
                        text = ")",
                        onClick = { onFunction(")") },
                        keyType = KeyType.FUNCTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(46.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalculatorKey(
                        text = "AC",
                        onClick = onClear,
                        keyType = KeyType.CLEAR,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                    CalculatorKey(
                        text = "⌫",
                        onClick = onDelete,
                        keyType = KeyType.ACTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp),
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = "Delete",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                    CalculatorKey(
                        text = "%",
                        onClick = onPercentage,
                        keyType = KeyType.ACTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                    CalculatorKey(
                        text = "÷",
                        onClick = { onOperator("÷") },
                        keyType = KeyType.OPERATOR,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("7", "8", "9").forEach { num ->
                        CalculatorKey(
                            text = num,
                            onClick = { onDigit(num) },
                            keyType = KeyType.NUMBER,
                            hapticEnabled = hapticEnabled,
                            soundEnabled = soundEnabled,
                            modifier = Modifier.weight(1f).height(52.dp)
                        )
                    }
                    CalculatorKey(
                        text = "×",
                        onClick = { onOperator("×") },
                        keyType = KeyType.OPERATOR,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("4", "5", "6").forEach { num ->
                        CalculatorKey(
                            text = num,
                            onClick = { onDigit(num) },
                            keyType = KeyType.NUMBER,
                            hapticEnabled = hapticEnabled,
                            soundEnabled = soundEnabled,
                            modifier = Modifier.weight(1f).height(52.dp)
                        )
                    }
                    CalculatorKey(
                        text = "−",
                        onClick = { onOperator("−") },
                        keyType = KeyType.OPERATOR,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("1", "2", "3").forEach { num ->
                        CalculatorKey(
                            text = num,
                            onClick = { onDigit(num) },
                            keyType = KeyType.NUMBER,
                            hapticEnabled = hapticEnabled,
                            soundEnabled = soundEnabled,
                            modifier = Modifier.weight(1f).height(52.dp)
                        )
                    }
                    CalculatorKey(
                        text = "+",
                        onClick = { onOperator("+") },
                        keyType = KeyType.OPERATOR,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalculatorKey(
                        text = "±",
                        onClick = onNegate,
                        keyType = KeyType.ACTION,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                    CalculatorKey(
                        text = "0",
                        onClick = { onDigit("0") },
                        keyType = KeyType.NUMBER,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                    CalculatorKey(
                        text = ".",
                        onClick = onDecimal,
                        keyType = KeyType.NUMBER,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                    CalculatorKey(
                        text = "=",
                        onClick = { onEquals() },
                        keyType = KeyType.EQUALS,
                        hapticEnabled = hapticEnabled,
                        soundEnabled = soundEnabled,
                        modifier = Modifier.weight(1f).height(52.dp)
                    )
                }
            }
        }
    }
}
