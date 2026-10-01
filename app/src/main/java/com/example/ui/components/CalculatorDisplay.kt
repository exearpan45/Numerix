package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.model.AngleMode
import com.example.ui.theme.NumerixBlue
import com.example.ui.theme.NumerixCyan
import com.example.ui.theme.RedDanger

@Composable
fun CalculatorDisplay(
    expression: String,
    previewResult: String?,
    finalResult: String?,
    isError: Boolean,
    errorMessage: String?,
    angleMode: AngleMode,
    hasMemory: Boolean,
    onToggleAngleMode: () -> Unit,
    modifier: Modifier = Modifier,
    onCopySuccess: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(expression) {
        if (expression.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("calculator_display_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = true,
                        onClick = onToggleAngleMode,
                        label = {
                            Text(
                                text = angleMode.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NumerixBlue.copy(alpha = 0.15f),
                            selectedLabelColor = NumerixCyan
                        ),
                        border = null,
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("angle_mode_badge")
                    )

                    if (hasMemory) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.testTag("memory_badge")
                        ) {
                            Text(
                                text = "M",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                val displayResult = finalResult ?: previewResult
                if (!displayResult.isNullOrBlank() && !isError) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(displayResult))
                                onCopySuccess?.invoke()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("copy_result_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy result",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val shareText = "Numerix\n\n$expression\n= $displayResult"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Calculation")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("share_result_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = "Share calculation",
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (expression.isEmpty()) "0" else expression,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (expression.length > 15) 24.sp else 32.sp,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End
                ),
                color = if (expression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .testTag("calculator_expression_text")
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isError) {
                Text(
                    text = errorMessage ?: "Invalid expression",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = RedDanger
                    ),
                    modifier = Modifier.testTag("calculator_error_text")
                )
            } else {
                val resultText = finalResult ?: previewResult
                AnimatedVisibility(
                    visible = !resultText.isNullOrBlank(),
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut()
                ) {
                    Text(
                        text = if (finalResult != null) "= $finalResult" else "≈ $previewResult",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = if (resultText != null && resultText.length > 10) 36.sp else 46.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (finalResult != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_result_text")
                    )
                }
            }
        }
    }
}
