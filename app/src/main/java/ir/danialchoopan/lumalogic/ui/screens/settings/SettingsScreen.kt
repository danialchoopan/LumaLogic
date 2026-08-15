package ir.danialchoopan.lumalogic.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onOpenDebugClick: () -> Unit = {},
    onAboutClick: () -> Unit = {}
) {
    val settingsState by AppContainer.settingsRepository.settings.collectAsState()
    val loc = currentLocalization()
    val uriHandler = LocalUriHandler.current
    val githubUrl = "https://github.com/danialchoopan"

    var showResetProgressDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showResetCustomDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_settings),
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
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Selection Section
            SectionTitle(stringResource(R.string.language_setting))
            GlowingCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = AmberPrimary, modifier = Modifier.padding(end = 12.dp))
                        Text(
                            text = stringResource(R.string.language_setting),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LanguageChoiceChip(
                            label = "English",
                            isSelected = !settingsState.isPersian,
                            onClick = { AppContainer.settingsRepository.updateLanguage("English") },
                            modifier = Modifier.weight(1f)
                        )

                        LanguageChoiceChip(
                            label = "فارسی",
                            isSelected = settingsState.isPersian,
                            onClick = { AppContainer.settingsRepository.updateLanguage("Persian") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Appearance Section
            SectionTitle(stringResource(R.string.appearance_section))

            SettingToggleRow(
                icon = Icons.Default.DarkMode,
                title = stringResource(R.string.theme_mode),
                subtitle = when (settingsState.darkThemeMode) {
                    "Light" -> stringResource(R.string.theme_light)
                    "Dark" -> stringResource(R.string.theme_dark)
                    else -> stringResource(R.string.theme_system)
                },
                isChecked = settingsState.darkThemeMode != "Light",
                onCheckedChange = { isChecked ->
                    AppContainer.settingsRepository.updateDarkThemeMode(if (isChecked) "Dark" else "Light")
                },
                testTag = "theme_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.Palette,
                title = stringResource(R.string.beam_glow),
                subtitle = if (loc.isPersian) "نمایش افکت درخشش ردیابی نور" else "Enhanced laser trace bloom & glow effects",
                isChecked = settingsState.beamGlow,
                onCheckedChange = { AppContainer.settingsRepository.updateBeamGlow(it) },
                testTag = "beam_glow_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.GridOn,
                title = stringResource(R.string.grid_visibility),
                subtitle = if (loc.isPersian) "نمایش خطوط شبکه ماتریس" else "Display grid matrix background gridlines",
                isChecked = settingsState.gridVisibility,
                onCheckedChange = { AppContainer.settingsRepository.updateGridVisibility(it) },
                testTag = "grid_vis_switch"
            )

            // Accessibility Section
            SectionTitle(stringResource(R.string.accessibility_section))

            SettingToggleRow(
                icon = Icons.Default.Contrast,
                title = stringResource(R.string.high_contrast),
                subtitle = if (loc.isPersian) "کنتراست بالا برای وضوح بیشتر" else "Maximize contrast for better element visibility",
                isChecked = settingsState.highContrast,
                onCheckedChange = { AppContainer.settingsRepository.updateHighContrast(it) },
                testTag = "high_contrast_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.Speed,
                title = stringResource(R.string.reduce_motion),
                subtitle = if (loc.isPersian) "کاهش انیمیشن‌ها و جابه‌جایی‌ها" else "Minimize non-essential screen transition animations",
                isChecked = settingsState.reduceMotion,
                onCheckedChange = { AppContainer.settingsRepository.updateReduceMotion(it) },
                testTag = "reduce_motion_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.Accessibility,
                title = stringResource(R.string.colorblind_mode),
                subtitle = if (loc.isPersian) "نمایش نماد و حروف روی رنگ‌های پرتو" else "Display letter & shape symbols alongside beam colors",
                isChecked = settingsState.colorblindMode,
                onCheckedChange = { AppContainer.settingsRepository.updateColorblindMode(it) },
                testTag = "colorblind_switch"
            )

            // Audio & Haptics Section
            SectionTitle(stringResource(R.string.audio_haptics_section))

            SettingToggleRow(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = stringResource(R.string.sound_effects),
                subtitle = if (loc.isPersian) "صدای چرخش قطعات و پیروزی" else "Synthesized tone feedback on rotations & wins",
                isChecked = settingsState.soundEnabled,
                onCheckedChange = { AppContainer.settingsRepository.updateSound(it) },
                testTag = "sound_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.Vibration,
                title = stringResource(R.string.haptic_feedback),
                subtitle = if (loc.isPersian) "بازخورد لمسی در تماس با دکمه‌ها" else "Tactile vibration feedback on interactions",
                isChecked = settingsState.hapticsEnabled,
                onCheckedChange = { AppContainer.settingsRepository.updateHaptics(it) },
                testTag = "haptics_switch"
            )

            // Gameplay Rules & Controls Section
            SectionTitle(stringResource(R.string.gameplay_section))

            SettingToggleRow(
                icon = Icons.Default.RestartAlt,
                title = stringResource(R.string.confirm_restart),
                subtitle = if (loc.isPersian) "تأییدیه قبل از شروع مجدد مرحله" else "Prompt confirmation dialog before restarting grid",
                isChecked = settingsState.confirmRestart,
                onCheckedChange = { AppContainer.settingsRepository.updateConfirmRestart(it) },
                testTag = "confirm_restart_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.SportsEsports,
                title = stringResource(R.string.auto_advance),
                subtitle = if (loc.isPersian) "ورود مستقیم به مرحله بعدی بعد از برد" else "Automatically jump to next puzzle after victory",
                isChecked = settingsState.autoAdvance,
                onCheckedChange = { AppContainer.settingsRepository.updateAutoAdvance(it) },
                testTag = "auto_advance_switch"
            )

            // HUD Display Options
            SectionTitle(stringResource(R.string.hud_section))

            SettingToggleRow(
                icon = Icons.Default.TextFields,
                title = stringResource(R.string.show_move_counter),
                subtitle = if (loc.isPersian) "نمایش حرکت‌های انجام‌شده روی صفحه" else "Show move counter chip on gameplay screen",
                isChecked = settingsState.showMoveCounter,
                onCheckedChange = { AppContainer.settingsRepository.updateShowMoveCounter(it) },
                testTag = "hud_moves_switch"
            )

            SettingToggleRow(
                icon = Icons.Default.Animation,
                title = stringResource(R.string.show_energy_counter),
                subtitle = if (loc.isPersian) "نمایش میزان انرژی باقی‌مانده" else "Show energy gauge chip on gameplay screen",
                isChecked = settingsState.showEnergyCounter,
                onCheckedChange = { AppContainer.settingsRepository.updateShowEnergyCounter(it) },
                testTag = "hud_energy_switch"
            )

            // Light Engine Debugger Entry
            SectionTitle(if (loc.isPersian) "اشکال‌زدایی" else "Developer Tools")
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
                        Icon(Icons.Default.BugReport, contentDescription = "Debug", tint = AmberPrimary, modifier = Modifier.padding(end = 12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.title_debug),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (loc.isPersian) "بررسی گام‌به‌گام پرتوهای نور" else "Step-by-step beam trace simulator",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = if (loc.isPersian) "ورود >" else "OPEN >",
                        color = AmberPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // About & Developer Section
            SectionTitle(if (loc.isPersian) "درباره برنامه و سازنده" else "About & Developer")

            GlowingCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = AmberPrimary.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeveloperMode,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (loc.isPersian) "طراحی شده توسط دانیال چوپان" else "Designed by Danial Choopan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = if (loc.isPersian) "بازی فکری و پازل اپتیک و منطق نوری لوما‌لاجیک" else "LumaLogic - Optical & Logic Puzzle Game",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // GitHub Profile Link Button
                    Button(
                        onClick = {
                            try {
                                uriHandler.openUri(githubUrl)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_github_button")
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (loc.isPersian) "مشاهده گیت‌هاب دانیال چوپان (danialchoopan)" else "Visit GitHub (danialchoopan)",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    }

                    // Open full about page button
                    OutlinedButton(
                        onClick = onAboutClick,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_about_button")
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AmberPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (loc.isPersian) "صفحه کامل درباره برنامه" else "View Full About Page",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Data & Reset Section
            SectionTitle(stringResource(R.string.data_section))

            OutlinedButton(
                onClick = { showResetProgressDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_progress_button")
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(stringResource(R.string.reset_progress), fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { showResetSettingsDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_settings_button")
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.padding(4.dp))
                Text(stringResource(R.string.reset_settings), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.padding(12.dp))
        }
    }

    if (showResetProgressDialog) {
        AlertDialog(
            onDismissRequest = { showResetProgressDialog = false },
            title = { Text(stringResource(R.string.confirm_reset_progress_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.confirm_reset_progress_msg)) },
            confirmButton = {
                Button(
                    onClick = {
                        AppContainer.levelProgressManager.clearAllProgress()
                        showResetProgressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetProgressDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("reset_progress_dialog")
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(stringResource(R.string.confirm_reset_settings_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.confirm_reset_settings_msg)) },
            confirmButton = {
                Button(
                    onClick = {
                        AppContainer.settingsRepository.resetSettings()
                        showResetSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.testTag("reset_settings_dialog")
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = AmberPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun LanguageChoiceChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AmberPrimary else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
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
    GlowingCard(modifier = Modifier.fillMaxWidth()) {
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
