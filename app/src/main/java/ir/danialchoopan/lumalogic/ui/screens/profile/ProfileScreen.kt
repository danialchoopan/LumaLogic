package ir.danialchoopan.lumalogic.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val progressManager = remember { AppContainer.levelProgressManager }
    val achRepo = remember { AppContainer.achievementRepository }
    val favRepo = remember { AppContainer.favoriteLevelRepository }
    val loc = currentLocalization()

    val achievements = remember { achRepo.getAchievements() }
    val unlockedAchCount = achievements.count { it.isUnlocked }
    val favCount = favRepo.getFavoriteLevelIds().size

    val stats = remember { progressManager.getPlayerStats(unlockedAchCount, achievements.size, favCount) }

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_profile),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("profile_hero_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .background(AmberPrimary.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AmberPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (loc.isPersian) "استاد اپتیک لوما" else "Luma Logic Optician",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = if (loc.isPersian) "متخصص مهندسی پرتوها و مدارهای نوری" else "Master of Beams and Optical Circuits",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Completion Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.overall_progress),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${loc.formatNumber(stats.totalLevelsCompleted)} / ${loc.formatNumber(256)} (${String.format("%.1f", stats.completionPercentage).toPersianDigits(loc.isPersian)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { stats.totalLevelsCompleted.toFloat() / 256.0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = AmberPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            // Stat Cards Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            icon = Icons.Default.Star,
                            label = if (loc.isPersian) "مجموع ستاره‌ها" else "Total Stars",
                            value = "${loc.formatNumber(stats.totalStars)} / ${loc.formatNumber(768)}",
                            color = Color(0xFFFFC107),
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            icon = Icons.Default.WorkspacePremium,
                            label = if (loc.isPersian) "مجموع امتیازات" else "Total Score",
                            value = loc.formatNumber(stats.totalScore),
                            color = TargetGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            icon = Icons.Default.AutoAwesome,
                            label = if (loc.isPersian) "فصل‌های کامل‌شده" else "Chapters Done",
                            value = "${loc.formatNumber(stats.chaptersCompleted)} / ${loc.formatNumber(16)}",
                            color = Color(0xFF2196F3),
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            icon = Icons.Default.EmojiEvents,
                            label = stringResource(R.string.title_achievements),
                            value = "${loc.formatNumber(stats.achievementsUnlocked)} / ${loc.formatNumber(stats.totalAchievements)}",
                            color = Color(0xFFE91E63),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            icon = Icons.Default.Timer,
                            label = if (loc.isPersian) "زمان بازی" else "Play Time",
                            value = formatPlayTime(stats.totalPlayTimeSeconds, loc.isPersian),
                            color = Color(0xFF00BCD4),
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            icon = Icons.Default.Lightbulb,
                            label = if (loc.isPersian) "راهنمایی‌های مصرفی" else "Hints Used",
                            value = loc.formatNumber(stats.hintsUsed),
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("stat_box_${label.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatPlayTime(seconds: Long, isPersian: Boolean): String {
    if (seconds <= 0) return if (isPersian) "۰ دقیقه" else "0m"
    val minutes = seconds / 60
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    val hStr = if (isPersian) "ساعت" else "h"
    val mStr = if (isPersian) "دقیقه" else "m"

    val formatNum = { n: Long -> if (isPersian) n.toString().map {
        when (it) {
            '0' -> '۰'; '1' -> '۱'; '2' -> '۲'; '3' -> '۳'; '4' -> '۴'; '5' -> '۵'; '6' -> '۶'; '7' -> '۷'; '8' -> '۸'; '9' -> '۹'; else -> it
        }
    }.joinToString("") else n.toString() }

    return if (hours > 0) "${formatNum(hours)}$hStr ${formatNum(remainingMinutes)}$mStr" else "${formatNum(minutes)}$mStr"
}
