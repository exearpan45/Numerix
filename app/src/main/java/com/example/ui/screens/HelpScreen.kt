package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CreatorFooter
import com.example.ui.components.NumerixAppBar

data class HelpItem(
    val category: String,
    val question: String,
    val answer: String
)

@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val faqList = remember {
        listOf(
            HelpItem(
                category = "Standard Calculator",
                question = "How does expression precedence work?",
                answer = "Numerix strictly respects standard mathematical operator precedence (BODMAS / PEMDAS). Expressions with multiplication and division (×, ÷) are calculated before addition and subtraction (+, −), unless grouped inside parentheses ( )."
            ),
            HelpItem(
                category = "Standard Calculator",
                question = "How do the Memory keys (MC, MR, M+, M-) work?",
                answer = "• MC (Memory Clear): Clears the saved memory back to 0.\n• MR (Memory Recall): Inserts the value stored in memory into your active expression.\n• M+ (Memory Add): Adds the current result to memory.\n• M- (Memory Subtract): Subtracts the current result from memory.\nA subtle 'M' badge indicates when memory contains a non-zero value."
            ),
            HelpItem(
                category = "Scientific Calculator",
                question = "How do I switch between Degrees (DEG) and Radians (RAD)?",
                answer = "In the Scientific Calculator, tap the DEG / RAD button in the top badge area or on the scientific keypad. You can also configure the default angle mode in Settings."
            ),
            HelpItem(
                category = "Scientific Calculator",
                question = "How do I access inverse trig functions (sin⁻¹, cos⁻¹, tan⁻¹)?",
                answer = "Tap the '2nd' key in the top-left of the scientific functions keypad to toggle between standard trig (sin, cos, tan) and inverse trig (sin⁻¹, cos⁻¹, tan⁻¹), as well as square root (√) and cube root (³√)."
            ),
            HelpItem(
                category = "Percentage",
                question = "What calculations does the Percentage tool support?",
                answer = "1. 'Percent Of': Computes what X% of Y is (e.g., 20% of 500 = 100).\n2. 'Increase/Decrease': Calculates the percentage change from initial to final value (e.g., 500 to 600 = +20%).\n3. 'Discount': Calculates final discounted price and exact amount saved."
            ),
            HelpItem(
                category = "Unit Converter",
                question = "Does the Unit Converter work without an internet connection?",
                answer = "Yes! The entire Unit Converter is 100% offline. All conversion factors for Length, Weight, Temperature, Area, Data, and Time are computed locally on device with zero internet latency."
            ),
            HelpItem(
                category = "Everyday Tools",
                question = "How do I calculate GST with Add GST or Remove GST?",
                answer = "In Everyday Tools > GST, select '+ Add GST' to add tax to a net base price, or '− Remove GST' to extract the original price from a tax-inclusive amount. You can choose standard presets (5%, 12%, 18%, 28%) or input a custom GST rate."
            ),
            HelpItem(
                category = "Everyday Tools",
                question = "How does the Tip & Split Bill calculator work?",
                answer = "Enter your bill total, select or type a tip percentage, and use the +/- buttons to set how many people are splitting the bill. Numerix instantly shows the total tip, overall bill, and per-person split amounts."
            ),
            HelpItem(
                category = "History",
                question = "How do I reuse or clear saved calculations?",
                answer = "Tap on any history card to instantly load the calculation into your active calculator. Use the copy icon to copy the result to your clipboard, or tap the trash icon to clear your history. History is saved locally and never shared."
            ),
            HelpItem(
                category = "Troubleshooting",
                question = "Why does the calculator show 'Cannot divide by zero' or 'Domain error'?",
                answer = "These friendly messages indicate mathematically invalid operations, such as dividing by 0, taking the square root of a negative number, or calculating log(0). Check your expression and try again."
            )
        )
    }

    Scaffold(
        topBar = {
            NumerixAppBar(
                title = "Help & Guide",
                onBack = onBack
            )
        },
        bottomBar = {
            CreatorFooter()
        },
        modifier = modifier.testTag("help_screen_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            faqList.forEach { item ->
                FaqExpandableCard(item = item)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FaqExpandableCard(item: HelpItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.question,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
