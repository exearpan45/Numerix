package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.components.CreatorFooter
import com.example.ui.components.NumerixAppBar
import com.example.viewmodel.GstMode
import com.example.viewmodel.GstUiState
import com.example.viewmodel.TipUiState

@Composable
fun EverydayToolsScreen(
    tipState: TipUiState,
    gstState: GstUiState,
    onUpdateTipBill: (String) -> Unit,
    onUpdateTipPercent: (String) -> Unit,
    onUpdatePeopleCount: (Int) -> Unit,
    onSetGstMode: (GstMode) -> Unit,
    onUpdateGstAmount: (String) -> Unit,
    onUpdateGstRate: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTool by remember { mutableStateOf(0) }
    val tools = listOf("Tip & Split Bill", "GST / Tax")
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            NumerixAppBar(
                title = "Everyday Tools",
                onBack = onBack
            )
        },
        bottomBar = {
            CreatorFooter()
        },
        modifier = modifier.testTag("everyday_tools_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TabRow(
                selectedTabIndex = selectedTool,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.clip(RoundedCornerShape(14.dp)).testTag("tools_tabs")
            ) {
                tools.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTool == index,
                        onClick = { selectedTool = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTool == index) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    )
                }
            }

            if (selectedTool == 0) {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_tip_calculator"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Bill Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = tipState.billInput,
                            onValueChange = onUpdateTipBill,
                            label = { Text("Bill Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_tip_bill"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "Tip Percentage",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("5", "10", "15", "20").forEach { preset ->
                                val isSelected = tipState.tipPercentInput == preset
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateTipPercent(preset) },
                                    label = { Text("$preset%") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = tipState.tipPercentInput,
                            onValueChange = onUpdateTipPercent,
                            label = { Text("Custom Tip (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_custom_tip"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "Split Between People",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledIconButton(
                                onClick = { onUpdatePeopleCount(tipState.peopleCount - 1) },
                                enabled = tipState.peopleCount > 1,
                                modifier = Modifier.size(44.dp).testTag("button_decrease_people")
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease people")
                            }

                            Text(
                                text = "${tipState.peopleCount} ${if (tipState.peopleCount == 1) "person" else "people"}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.testTag("text_people_count")
                            )

                            FilledIconButton(
                                onClick = { onUpdatePeopleCount(tipState.peopleCount + 1) },
                                modifier = Modifier.size(44.dp).testTag("button_increase_people")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase people")
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tip Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(tipState.tipAmount, fontWeight = FontWeight.SemiBold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total Bill", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(tipState.totalBill, fontWeight = FontWeight.Bold)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Amount Per Person",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        tipState.perPersonTotal,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.testTag("result_tip_per_person")
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Tip Per Person", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(tipState.perPersonTip, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("card_gst_calculator"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "GST Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilterChip(
                                selected = gstState.mode == GstMode.ADD,
                                onClick = { onSetGstMode(GstMode.ADD) },
                                label = { Text("+ Add GST") },
                                modifier = Modifier.weight(1f).testTag("gst_mode_add")
                            )
                            FilterChip(
                                selected = gstState.mode == GstMode.REMOVE,
                                onClick = { onSetGstMode(GstMode.REMOVE) },
                                label = { Text("− Remove GST") },
                                modifier = Modifier.weight(1f).testTag("gst_mode_remove")
                            )
                        }

                        OutlinedTextField(
                            value = gstState.amountInput,
                            onValueChange = onUpdateGstAmount,
                            label = { Text(if (gstState.mode == GstMode.ADD) "Base Amount" else "Gross Amount (inc. GST)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_gst_amount"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text(
                            text = "GST Rate (%)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("5", "12", "18", "28").forEach { rate ->
                                val isSelected = gstState.gstRateInput == rate
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onUpdateGstRate(rate) },
                                    label = { Text("$rate%") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = gstState.gstRateInput,
                            onValueChange = onUpdateGstRate,
                            label = { Text("Custom GST Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_custom_gst_rate"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Net Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(gstState.netAmount, fontWeight = FontWeight.SemiBold)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("CGST (${(gstState.gstRateInput.toDoubleOrNull() ?: 0.0) / 2}%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(gstState.cgstAmount, style = MaterialTheme.typography.bodyMedium)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("SGST (${(gstState.gstRateInput.toDoubleOrNull() ?: 0.0) / 2}%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(gstState.sgstAmount, style = MaterialTheme.typography.bodyMedium)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total GST Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(gstState.gstAmount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total Amount",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        gstState.totalAmount,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier.testTag("result_gst_total")
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
