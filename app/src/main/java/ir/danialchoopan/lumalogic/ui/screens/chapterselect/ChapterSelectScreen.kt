package ir.danialchoopan.lumalogic.ui.screens.chapterselect

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.data.model.Chapter
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterSelectScreen(
    onBackClick: () -> Unit,
    onChapterSelected: (String) -> Unit
) {
    val chapterRepo = remember { AppContainer.chapterRepository }
    val progressManager = remember { AppContainer.levelProgressManager }
    val loc = currentLocalization()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val chapters = chapterRepo.getChapters()
    val allStats = remember { progressManager.getPlayerStats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (loc.isPersian) "نقشه فصول و قلمروها" else stringResource(R.string.title_chapters),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AmberPrimary
                        )
                        Text(
                            text = "${loc.formatNumber(allStats.totalLevelsCompleted)}/${loc.formatNumber(256)} ${if (loc.isPersian) "مرحله باز شده" else "Levels"} • ${loc.formatNumber(allStats.totalStars)}/${loc.formatNumber(768)} ⭐",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("chapter_select_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AmberPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            // Ambient Star Glow Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(AmberPrimary.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(width * 0.8f, height * 0.2f),
                        radius = width * 0.6f
                    )
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(chapters) { chapter ->
                    val isUnlocked = progressManager.isChapterUnlocked(chapter.id)
                    val chapterLevels = chapterRepo.getLevelsForChapter(chapter.id)
                    val completedCount = chapterLevels.count { progressManager.isLevelCompleted(it.levelId) }
                    val starsEarned = chapterLevels.sumOf { progressManager.getProgress(it.levelId)?.stars ?: 0 }

                    ChapterCard(
                        chapter = chapter,
                        isUnlocked = isUnlocked,
                        completedCount = completedCount,
                        starsEarned = starsEarned,
                        onClick = {
                            if (isUnlocked) {
                                onChapterSelected(chapter.id)
                            } else {
                                val prevChapterNum = chapter.number - 1
                                val msg = if (loc.isPersian) {
                                    "این فصل قفل است! ابتدا حداقل ۱۲ مرحله از فصل ${loc.formatNumber(prevChapterNum)} را تکمیل کنید."
                                } else {
                                    "Chapter is locked! Complete at least 12 levels of Chapter $prevChapterNum first."
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterCard(
    chapter: Chapter,
    isUnlocked: Boolean,
    completedCount: Int,
    starsEarned: Int,
    onClick: () -> Unit
) {
    val loc = currentLocalization()
    val isMastered = completedCount == 16
    val isStarted = completedCount > 0 && !isMastered

    val chapterColor = remember(chapter.accentColorHex) {
        try {
            Color(android.graphics.Color.parseColor(chapter.accentColorHex))
        } catch (_: Exception) {
            AmberPrimary
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale = if (isStarted && isUnlocked) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        ).value
    } else 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(if (isStarted) pulseScale else 1f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .border(
                width = if (isMastered) 2.dp else if (isStarted) 1.5.dp else 1.dp,
                color = when {
                    !isUnlocked -> Color.White.copy(alpha = 0.08f)
                    isMastered -> TargetGreen.copy(alpha = 0.8f)
                    isStarted -> chapterColor.copy(alpha = 0.8f)
                    else -> chapterColor.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("chapter_card_${chapter.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            }
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            // Header Row: Chapter Number, Title, Difficulty & Lock Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (isUnlocked) chapterColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = if (isUnlocked) chapterColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        if (isUnlocked) {
                            Text(
                                text = loc.formatNumber(chapter.number),
                                fontWeight = FontWeight.Black,
                                color = chapterColor,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (loc.isPersian) "فصل ${loc.formatNumber(chapter.number)}" else "CHAPTER ${chapter.number}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUnlocked) chapterColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = loc.getChapterName(chapter),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                // Status Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        !isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        isMastered -> TargetGreen.copy(alpha = 0.2f)
                        isStarted -> chapterColor.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isMastered) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TargetGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (loc.isPersian) "تکمیل ۱۰۰٪" else "MASTERED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TargetGreen
                            )
                        } else if (!isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (loc.isPersian) "قفل" else "LOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = loc.getDifficultyLabel(chapter.difficulty),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = chapterColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle & Description
            Text(
                text = loc.getChapterDescription(chapter),
                fontSize = 13.sp,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isUnlocked) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = chapterColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${loc.formatNumber(completedCount)}/${loc.formatNumber(16)} ${if (loc.isPersian) "مرحله" else "Levels"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${loc.formatNumber(starsEarned)}/${loc.formatNumber(48)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFC107)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { completedCount.toFloat() / 16.0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = if (isMastered) TargetGreen else chapterColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            } else {
                val prevChapterNum = chapter.number - 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (loc.isPersian) {
                            "قفل • تکمیل ۱۲ مرحله از فصل ${loc.formatNumber(prevChapterNum)}"
                        } else {
                            "Locked • Complete 12 levels in Chapter $prevChapterNum"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
