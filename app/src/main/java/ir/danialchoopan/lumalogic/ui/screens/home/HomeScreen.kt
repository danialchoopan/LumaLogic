package ir.danialchoopan.lumalogic.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
import ir.danialchoopan.lumalogic.ui.components.LumaButton
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onPlayLevel: (String) -> Unit = {},
    onChaptersClick: () -> Unit = {},
    onLevelSelectClick: () -> Unit = {},
    onDailyPuzzleClick: () -> Unit = {},
    onAchievementsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLevelEditorClick: () -> Unit = {},
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val progressManager = remember { AppContainer.levelProgressManager }
    val stats = remember { progressManager.getPlayerStats() }
    val loc = currentLocalization()
    val hasStarted = remember { progressManager.hasStartedGame() }
    val nextPlayableLevel = remember { progressManager.getNextPlayableLevel() }
    val totalLevels = 256
    val currentLevelNumber = remember(nextPlayableLevel) {
        val idx = ir.danialchoopan.lumalogic.data.level.LevelRegistry.getAllLevels().indexOfFirst { it.levelId == nextPlayableLevel.levelId }
        if (idx >= 0) idx + 1 else 1
    }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // Ambient background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AmberPrimary.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(width / 2f, height * 0.25f),
                    radius = width * 0.7f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Logo & Header Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { -50 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlowingCard(
                        modifier = Modifier.size(90.dp),
                        borderColor = AmberPrimary.copy(alpha = 0.5f)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            drawCircle(
                                color = AmberPrimary,
                                radius = size.width / 3.5f,
                                center = center
                            )
                            drawLine(
                                color = Color.Cyan,
                                start = Offset(0f, size.height * 0.3f),
                                end = center,
                                strokeWidth = 6f
                            )
                            drawLine(
                                color = Color.Green,
                                start = center,
                                end = Offset(size.width, size.height * 0.8f),
                                strokeWidth = 6f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "LumaLogic",
                        style = MaterialTheme.typography.displayLarge,
                        color = AmberPrimary,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (loc.isPersian) "موتور معمایی نور و اپتیک منطقی" else "Precision Light Routing Engine",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Play / Continue Game Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(initialOffsetY = { 30 })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            onPlayLevel(nextPlayableLevel.levelId)
                        }
                        .testTag("main_play_button"),
                    colors = CardDefaults.cardColors(
                        containerColor = AmberPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasStarted) {
                                    if (loc.isPersian) "ادامه بازی" else "CONTINUE GAME"
                                } else {
                                    if (loc.isPersian) "شروع بازی" else "START GAME"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (hasStarted) {
                                    "${if (loc.isPersian) "مرحله" else "Level"} ${loc.formatNumber(currentLevelNumber)} • ${loc.getLevelName(nextPlayableLevel)}"
                                } else {
                                    if (loc.isPersian) "مرحله ۱ • پایه‌های نور" else "Level 1 • Light Basics"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.75f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = AmberPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progression Summary Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onProfileClick)
                        .testTag("home_progression_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.overall_progress),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberPrimary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "${loc.formatNumber(stats.totalLevelsCompleted)} / ${loc.formatNumber(256)} ${if (loc.isPersian) "مرحله" else "Levels"}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${loc.formatNumber(stats.totalStars)} / ${loc.formatNumber(768)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFC107)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { stats.totalLevelsCompleted.toFloat() / 256.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = AmberPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid Options Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_chapters),
                            subtitle = if (loc.isPersian) "۱۶ فصل ماجراجویی" else "16 Realms",
                            icon = Icons.Default.AutoAwesome,
                            onClick = onChaptersClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_chapters"
                        )
                        HomeGridTile(
                            title = stringResource(R.string.title_daily_puzzle),
                            subtitle = if (loc.isPersian) "چالش امروز" else "Today's Light",
                            icon = Icons.Default.CalendarToday,
                            onClick = onDailyPuzzleClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_daily_puzzle"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_achievements),
                            subtitle = if (loc.isPersian) "نشان‌ها و افتخارات" else "Badges & Goals",
                            icon = Icons.Default.EmojiEvents,
                            onClick = onAchievementsClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_achievements"
                        )
                        HomeGridTile(
                            title = stringResource(R.string.title_profile),
                            subtitle = if (loc.isPersian) "آمار و رکوردها" else "Profile & Metrics",
                            icon = Icons.Default.Person,
                            onClick = onProfileClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_profile"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_editor),
                            subtitle = if (loc.isPersian) "ویرایشگر و طراحی مرحله" else "Level Editor",
                            icon = Icons.Default.Edit,
                            onClick = onLevelEditorClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_level_editor"
                        )
                        HomeGridTile(
                            title = stringResource(R.string.title_settings),
                            subtitle = if (loc.isPersian) "تنظیمات و زبان" else "Audio & Settings",
                            icon = Icons.Default.Settings,
                            onClick = onSettingsClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_settings"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "v1.0.5".toPersianDigits(loc.isPersian),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                Text(
                    text = stringResource(R.string.title_about),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberPrimary,
                    modifier = Modifier.clickable(onClick = onAboutClick).testTag("about_link")
                )
            }
        }
    }
}

@Composable
fun HomeGridTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(AmberPrimary.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
