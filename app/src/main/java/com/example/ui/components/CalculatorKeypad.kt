package com.example.ui.components

import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class KeyType {
    NUMBER,
    OPERATOR,
    ACTION,
    EQUALS,
    CLEAR,
    MEMORY,
    FUNCTION
}

@Composable
fun CalculatorKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    keyType: KeyType = KeyType.NUMBER,
    hapticEnabled: Boolean = true,
    soundEnabled: Boolean = false,
    contentDesc: String? = null,
    testTag: String = "key_$text",
    icon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        label = "key_scale_$text"
    )
    val view = LocalView.current
    val isDark = isSystemInDarkTheme()

    val (bgColor, textColor) = when (keyType) {
        KeyType.NUMBER -> if (isDark) Pair(DarkKeyNumber, DarkKeyNumberText) else Pair(LightKeyNumber, LightKeyNumberText)
        KeyType.OPERATOR -> if (isDark) Pair(DarkKeyOperator, DarkKeyOperatorText) else Pair(LightKeyOperator, LightKeyOperatorText)
        KeyType.ACTION -> if (isDark) Pair(DarkKeyAction, DarkKeyActionText) else Pair(LightKeyAction, LightKeyActionText)
        KeyType.EQUALS -> Pair(DarkKeyEquals, DarkKeyEqualsText)
        KeyType.CLEAR -> if (isDark) Pair(DarkKeyClear, DarkKeyClearText) else Pair(LightKeyClear, LightKeyClearText)
        KeyType.MEMORY -> if (isDark) Pair(DarkKeyAction.copy(alpha = 0.6f), NumerixCyan) else Pair(LightKeyAction.copy(alpha = 0.7f), NumerixBlue)
        KeyType.FUNCTION -> if (isDark) Pair(DarkKeyAction, DarkKeyActionText) else Pair(LightKeyAction, LightKeyActionText)
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticEnabled) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    }
                    if (soundEnabled) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    }
                    onClick()
                }
            )
            .semantics {
                contentDescription = contentDesc ?: "Button: $text"
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            icon()
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = when {
                        keyType == KeyType.MEMORY -> 15.sp
                        keyType == KeyType.FUNCTION -> 17.sp
                        text.length > 2 -> 18.sp
                        keyType == KeyType.OPERATOR || keyType == KeyType.EQUALS -> 26.sp
                        else -> 24.sp
                    },
                    fontWeight = when (keyType) {
                        KeyType.EQUALS, KeyType.OPERATOR -> FontWeight.Bold
                        KeyType.NUMBER -> FontWeight.Medium
                        else -> FontWeight.SemiBold
                    },
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun MemoryRow(
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val keys = listOf(
            Triple("MC", onMemoryClear, "Memory Clear"),
            Triple("MR", onMemoryRecall, "Memory Recall"),
            Triple("M+", onMemoryAdd, "Memory Add"),
            Triple("M-", onMemorySubtract, "Memory Subtract")
        )
        keys.forEach { (label, action, desc) ->
            CalculatorKey(
                text = label,
                onClick = action,
                keyType = KeyType.MEMORY,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = desc,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
            )
        }
    }
}

@Composable
fun StandardKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimal: () -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onPercentage: () -> Unit,
    onNegate: () -> Unit,
    onEquals: () -> Unit,
    hapticEnabled: Boolean,
    soundEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorKey(
                text = "AC",
                onClick = onClear,
                keyType = KeyType.CLEAR,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: All Clear",
                testTag = "key_ac",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
            CalculatorKey(
                text = "⌫",
                onClick = onDelete,
                keyType = KeyType.ACTION,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Delete",
                testTag = "key_delete",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Backspace,
                        contentDescription = "Button: Delete",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            CalculatorKey(
                text = "%",
                onClick = onPercentage,
                keyType = KeyType.ACTION,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Percentage",
                testTag = "key_percent",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
            CalculatorKey(
                text = "÷",
                onClick = { onOperator("÷") },
                keyType = KeyType.OPERATOR,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Divide",
                testTag = "key_divide",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("7", "8", "9").forEach { num ->
                CalculatorKey(
                    text = num,
                    onClick = { onDigit(num) },
                    keyType = KeyType.NUMBER,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    contentDesc = "Button: $num",
                    testTag = "key_$num",
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
            }
            CalculatorKey(
                text = "×",
                onClick = { onOperator("×") },
                keyType = KeyType.OPERATOR,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Multiply",
                testTag = "key_multiply",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("4", "5", "6").forEach { num ->
                CalculatorKey(
                    text = num,
                    onClick = { onDigit(num) },
                    keyType = KeyType.NUMBER,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    contentDesc = "Button: $num",
                    testTag = "key_$num",
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
            }
            CalculatorKey(
                text = "−",
                onClick = { onOperator("−") },
                keyType = KeyType.OPERATOR,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Minus",
                testTag = "key_minus",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("1", "2", "3").forEach { num ->
                CalculatorKey(
                    text = num,
                    onClick = { onDigit(num) },
                    keyType = KeyType.NUMBER,
                    hapticEnabled = hapticEnabled,
                    soundEnabled = soundEnabled,
                    contentDesc = "Button: $num",
                    testTag = "key_$num",
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                )
            }
            CalculatorKey(
                text = "+",
                onClick = { onOperator("+") },
                keyType = KeyType.OPERATOR,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Plus",
                testTag = "key_plus",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CalculatorKey(
                text = "±",
                onClick = onNegate,
                keyType = KeyType.ACTION,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Negate",
                testTag = "key_negate",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
            CalculatorKey(
                text = "0",
                onClick = { onDigit("0") },
                keyType = KeyType.NUMBER,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: 0",
                testTag = "key_0",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
            CalculatorKey(
                text = ".",
                onClick = onDecimal,
                keyType = KeyType.NUMBER,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Decimal",
                testTag = "key_decimal",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
            CalculatorKey(
                text = "=",
                onClick = onEquals,
                keyType = KeyType.EQUALS,
                hapticEnabled = hapticEnabled,
                soundEnabled = soundEnabled,
                contentDesc = "Button: Equals",
                testTag = "key_equals",
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            )
        }
    }
}
