package ir.danialchoopan.lumalogic.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WorkspacePremium
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
import ir.danialchoopan.lumalogic.data.level.LevelRegistry
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.GlowingCard
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
    val currentLevelIndex = remember(nextPlayableLevel) {
        val idx = LevelRegistry.getAllLevels().indexOfFirst { it.levelId == nextPlayableLevel.levelId }
        if (idx >= 0) idx + 1 else 1
    }
    val currentChapter = remember(nextPlayableLevel) {
        val chapterId = nextPlayableLevel.tags.firstOrNull { it.startsWith("chapter_") }
        LevelRegistry.chapters.find { it.id == chapterId } ?: LevelRegistry.chapters.first()
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
                        AmberPrimary.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(width / 2f, height * 0.22f),
                    radius = width * 0.75f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Logo & Header Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlowingCard(
                        modifier = Modifier.size(86.dp),
                        borderColor = AmberPrimary.copy(alpha = 0.6f)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            drawCircle(
                                color = AmberPrimary,
                                radius = size.width / 3.4f,
                                center = center
                            )
                            drawLine(
                                color = Color.Cyan,
                                start = Offset(0f, size.height * 0.3f),
                                end = center,
                                strokeWidth = 7f
                            )
                            drawLine(
                                color = Color(0xFF00E676),
                                start = center,
                                end = Offset(size.width, size.height * 0.8f),
                                strokeWidth = 7f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LumaLogic",
                            style = MaterialTheme.typography.displayLarge,
                            color = AmberPrimary,
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (loc.isPersian) "موتور معمایی نور، شکست و اپتیک منطقی" else "Optic Routing & Logic Simulation Engine",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            // PRIMARY HERO 1: Start Game / Continue Game (Direct to Level)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(700)) + slideInVertically(initialOffsetY = { 20 })
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
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (hasStarted) Icons.Default.PlayArrow else Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
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
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (hasStarted) {
                                    "${if (loc.isPersian) "مرحله" else "Level"} ${loc.formatNumber(currentLevelIndex)} • ${loc.getLevelName(nextPlayableLevel)}"
                                } else {
                                    if (loc.isPersian) "ورود مستقیم به مرحله ۱ • پایه‌های نور" else "Direct Play Level 1 • Light Basics"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${if (loc.isPersian) "فصل" else "Chapter"} ${loc.formatNumber(currentChapter.number)}: ${loc.getChapterName(currentChapter)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(alpha = 0.65f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = AmberPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            // PRIMARY HERO 2: "دیدن مرحله‌ها" (View Levels / Chapter Select)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(750)) + slideInVertically(initialOffsetY = { 25 })
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00BCD4), Color(0xFF3F51B5), AmberPrimary)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            onChaptersClick()
                        }
                        .testTag("view_levels_button"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF00BCD4).copy(alpha = 0.25f), Color(0xFF3F51B5).copy(alpha = 0.25f))
                                        ),
                                        RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "View Levels",
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (loc.isPersian) "دیدن مرحله‌ها و فصل‌ها" else "VIEW ALL LEVELS",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.FormatListNumbered,
                                        contentDescription = null,
                                        tint = Color(0xFF00BCD4),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (loc.isPersian) "نقشه ۱۶ قلمرو و ۲۵۶ مرحله نوری" else "16 Realms & 256 Handcrafted Stages",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF00BCD4),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Progression Summary Card
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AmberPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.overall_progress),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberPrimary,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "${loc.formatNumber(stats.totalLevelsCompleted)} / ${loc.formatNumber(256)} ${if (loc.isPersian) "مرحله باز/تکمیل" else "Levels Done"}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFFFFC107).copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFC107),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${loc.formatNumber(stats.totalStars)} / ${loc.formatNumber(768)}",
                                    fontSize = 14.sp,
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

            // Grid Options Section with Rich Icons
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(850, delayMillis = 150)) + slideInVertically(initialOffsetY = { 35 })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Row 1: Daily Puzzle & Achievements
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_daily_puzzle),
                            subtitle = if (loc.isPersian) "معمای روزانه نوری" else "Today's Challenge",
                            icon = Icons.Default.CalendarMonth,
                            badgeColor = Color(0xFFE91E63),
                            onClick = onDailyPuzzleClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_daily_puzzle"
                        )
                        HomeGridTile(
                            title = stringResource(R.string.title_achievements),
                            subtitle = if (loc.isPersian) "مدال‌ها و افتخارات" else "Badges & Goals",
                            icon = Icons.Default.EmojiEvents,
                            badgeColor = Color(0xFFFF9800),
                            onClick = onAchievementsClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_achievements"
                        )
                    }

                    // Row 2: Profile & Level Editor
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_profile),
                            subtitle = if (loc.isPersian) "آمار و رکوردها" else "Profile & Stats",
                            icon = Icons.Default.Leaderboard,
                            badgeColor = Color(0xFF4CAF50),
                            onClick = onProfileClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_profile"
                        )
                        HomeGridTile(
                            title = stringResource(R.string.title_editor),
                            subtitle = if (loc.isPersian) "طراحی و ساخت مرحله" else "Level Studio",
                            icon = Icons.Default.DesignServices,
                            badgeColor = Color(0xFF9C27B0),
                            onClick = onLevelEditorClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_level_editor"
                        )
                    }

                    // Row 3: Settings & Quick Level List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HomeGridTile(
                            title = stringResource(R.string.title_settings),
                            subtitle = if (loc.isPersian) "تنظیمات، صدا و زبان" else "Audio & Display",
                            icon = Icons.Default.Tune,
                            badgeColor = Color(0xFF607D8B),
                            onClick = onSettingsClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_settings"
                        )
                        HomeGridTile(
                            title = if (loc.isPersian) "فهرست مراحل" else "Level List",
                            subtitle = if (loc.isPersian) "جستجو و مراحل سفارشی" else "Search & Custom",
                            icon = Icons.Default.FormatListNumbered,
                            badgeColor = Color(0xFF00BCD4),
                            onClick = onLevelSelectClick,
                            modifier = Modifier.weight(1f),
                            testTag = "tile_level_select"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Footer Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "v1.0.5".toPersianDigits(loc.isPersian),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(onClick = onAboutClick)
                        .testTag("about_link")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.title_about),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeGridTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color = AmberPrimary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(badgeColor.copy(alpha = 0.16f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(22.dp)
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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

