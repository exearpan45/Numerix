package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.components.*
import com.example.viewmodel.CalculatorUiState

@Composable
fun CalculatorScreen(
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
    onOpenScientific: () -> Unit,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            NumerixAppBar(
                title = "Calculator",
                actions = {
                    IconButton(
                        onClick = onOpenScientific,
                        modifier = Modifier.testTag("calculator_toggle_scientific_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Science,
                            contentDescription = "Scientific Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("calculator_history_button")
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
        modifier = modifier.testTag("calculator_screen_container")
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

            StandardKeypad(
                onDigit = onDigit,
                onOperator = onOperator,
                onDecimal = onDecimal,
                onClear = onClear,
                onDelete = onDelete,
                onPercentage = onPercentage,
                onNegate = onNegate,
                onEquals = onEquals,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled
            )
        }
    }
}
