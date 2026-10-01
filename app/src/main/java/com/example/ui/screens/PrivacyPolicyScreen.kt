package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CreatorFooter
import com.example.ui.components.NumerixAppBar

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val policySections = listOf(
        "1. Introduction" to "Numerix is created by Arpan Goswami as an offline, private calculator and mathematics utility. We respect your privacy and are committed to protecting it. This Privacy Policy explains our practices regarding your data.",
        "2. Information We Collect" to "Numerix does NOT collect, store, transmit, or share any personally identifiable information (PII). We do not collect names, email addresses, phone numbers, location data, or device identifiers.",
        "3. Information Stored Locally" to "All data generated within Numerix—including calculation history, user settings (theme preferences, angle units, haptic feedback toggle)—is stored exclusively on your device's local internal storage using Android Room and DataStore. This data never leaves your device.",
        "4. Internet Access" to "Numerix operates 100% offline. The application does not require, request, or utilize internet connectivity for any of its calculation or converter functions.",
        "5. Third-Party Services" to "Numerix does not integrate third-party SDKs, marketing trackers, cloud sync services, or external APIs.",
        "6. Analytics" to "Numerix contains zero analytics frameworks. We do not track what expressions you calculate, which tools you use, or how frequently you launch the application.",
        "7. Advertising" to "Numerix is 100% ad-free. No advertising networks, trackers, or behavioral profiling mechanisms are present in the app.",
        "8. Children's Privacy" to "Because Numerix does not collect any personal information whatsoever, it is safe for users of all ages, including children under 13.",
        "9. Data Security" to "Your calculation history and settings reside safely within Android's sandboxed private app storage, inaccessible to other applications unless your device is compromised.",
        "10. Data Deletion" to "You have complete control over your data. You can delete your entire calculation history at any time via Settings > Clear History or within the History screen. Uninstalling the app permanently purges all stored data.",
        "11. Changes to This Policy" to "If any features are updated in future versions, this Privacy Policy will be updated accordingly within the app release.",
        "12. Contact" to "If you have questions regarding this Privacy Policy or Numerix, you can contact the creator, Arpan Goswami."
    )

    Scaffold(
        topBar = {
            NumerixAppBar(
                title = "Privacy Policy",
                onBack = onBack
            )
        },
        bottomBar = {
            CreatorFooter()
        },
        modifier = modifier.testTag("privacy_policy_container")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Summary: Numerix is 100% offline, contains zero ads, zero trackers, and never transmits your data anywhere.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }

            policySections.forEach { (heading, content) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = heading,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
