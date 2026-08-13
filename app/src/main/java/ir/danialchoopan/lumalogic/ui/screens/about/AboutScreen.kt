package ir.danialchoopan.lumalogic.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

@Composable
fun AboutScreen(
    onBackClick: () -> Unit
) {
    val loc = currentLocalization()

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_about),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("about_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // App Brand Card
            GlowingCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AmberPrimary.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LumaLogic",
                        style = MaterialTheme.typography.displayLarge,
                        color = AmberPrimary,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (loc.isPersian) "ساخته‌شده توسط دانیال چوپان" else "Created by Danial Choopan",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Detail Rows
            AboutInfoCard(
                icon = Icons.Default.Info,
                label = if (loc.isPersian) "نام برنامه" else "App Name",
                value = "LumaLogic"
            )

            AboutInfoCard(
                icon = Icons.Default.DeveloperMode,
                label = if (loc.isPersian) "توسعه‌دهنده" else "Developer",
                value = if (loc.isPersian) "دانیال چوپان" else "Danial Choopan"
            )

            // Technical Package Name strictly LTR
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = if (loc.isPersian) "نام پکیج" else "Package Name",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "ir.danialchoopan.lumalogic",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            AboutInfoCard(
                icon = Icons.Default.Layers,
                label = if (loc.isPersian) "نسخه" else "Version",
                value = "1.0.5".toPersianDigits(loc.isPersian)
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = stringResource(R.string.title_about),
                        fontWeight = FontWeight.Bold,
                        color = AmberPrimary,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.about_desc),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Clean Architecture • Jetpack Compose • Kotlin",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun AboutInfoCard(
    icon: ImageVector,
    label: String,
    value: String
) {
    GlowingCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
