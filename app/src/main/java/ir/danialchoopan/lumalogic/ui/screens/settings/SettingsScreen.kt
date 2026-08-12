package ir.danialchoopan.lumalogic.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onOpenDebugClick: () -> Unit = {}
) {
    val settingsState by AppContainer.settingsRepository.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LumaHeader(
                title = "Settings",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.testTag("settings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleLarge,
                color = AmberPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Theme Toggle
            SettingToggleRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Theme",
                subtitle = "Force sleek dark neon canvas",
                isChecked = settingsState.darkThemeMode != "Light",
                onCheckedChange = { isChecked ->
                    AppContainer.settingsRepository.updateDarkThemeMode(if (isChecked) "Dark" else "Light")
                },
                testTag = "theme_switch"
            )

            // Sound Toggle
            SettingToggleRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Audio Effects",
                subtitle = "Synthesized tone feedback on rotations and wins",
                isChecked = settingsState.soundEnabled,
                onCheckedChange = { isChecked ->
                    AppContainer.settingsRepository.updateSound(isChecked)
                },
                testTag = "sound_switch"
            )

            // Haptics Toggle
            SettingToggleRow(
                icon = Icons.Default.Vibration,
                title = "Haptic Feedback",
                subtitle = "Tactile vibration feedback on interactions",
                isChecked = settingsState.hapticsEnabled,
                onCheckedChange = { isChecked ->
                    AppContainer.settingsRepository.updateHaptics(isChecked)
                },
                testTag = "haptics_switch"
            )

            // Animations Toggle
            SettingToggleRow(
                icon = Icons.Default.Animation,
                title = "Beam Animations",
                subtitle = "Smooth light ray propagation transitions",
                isChecked = settingsState.animationsEnabled,
                onCheckedChange = { isChecked ->
                    AppContainer.settingsRepository.updateAnimations(isChecked)
                },
                testTag = "animations_switch"
            )

            // Developer Debug Mode Entry
            GlowingCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDebugClick() }
                    .testTag("debug_mode_settings_item")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Debug Mode",
                            tint = AmberPrimary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Light Engine Debugger",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step-by-step beam simulation inspector",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "OPEN >",
                        color = AmberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Reset Game Progress
            OutlinedButton(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_progress_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Reset All Level Progress", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Settings changes persist in local storage.",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Level Progress?", fontWeight = FontWeight.Bold) },
            text = { Text("This will clear all saved stars, high scores, and level completion records. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        AppContainer.levelProgressManager.clearAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset Progress")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("reset_progress_dialog")
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    GlowingCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AmberPrimary,
                    checkedTrackColor = AmberPrimary.copy(alpha = 0.4f)
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
