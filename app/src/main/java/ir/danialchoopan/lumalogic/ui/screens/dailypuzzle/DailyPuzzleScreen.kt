package ir.danialchoopan.lumalogic.ui.screens.dailypuzzle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.R
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.LumaButton
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.localization.toPersianDigits
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyPuzzleScreen(
    onBackClick: () -> Unit,
    onPlayClick: (String) -> Unit
) {
    val dailyRepo = remember { AppContainer.dailyPuzzleRepository }
    val progressManager = remember { AppContainer.levelProgressManager }
    val loc = currentLocalization()

    val todayLevel = remember { dailyRepo.getTodayPuzzle() }
    val dateString = remember { dailyRepo.getTodayDateString() }
    val progress = remember { progressManager.getProgress(todayLevel.levelId) }
    val isCompleted = progress?.completed == true

    Scaffold(
        topBar = {
            LumaHeader(
                title = stringResource(R.string.title_daily_puzzle),
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_puzzle_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .background(AmberPrimary.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateString.toPersianDigits(loc.isPersian),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = loc.getLevelName(todayLevel),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (loc.isPersian) "یک معمای نوری منحصربه‌فرد که هر روز به‌روزرسانی می‌شود. آن را حل کنید تا امتیاز ویژه بگیرید!" else "A unique optical puzzle refreshed daily. Solve it to boost your streak and earn bonus score!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (isCompleted && progress != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(TargetGreen.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TargetGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (loc.isPersian) "امروز با موفقیت حل شد! (${loc.formatNumber(progress.stars)} ⭐ • ${loc.formatNumber(progress.bestScore)} امتیاز)" else "Completed Today! (${progress.stars} ⭐ • ${progress.bestScore} pts)",
                                fontWeight = FontWeight.Bold,
                                color = TargetGreen,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    LumaButton(
                        text = if (isCompleted) (if (loc.isPersian) "بازی مجدد" else "PLAY AGAIN") else (if (loc.isPersian) "شروع معمای روزانه" else "START DAILY PUZZLE"),
                        onClick = { onPlayClick(todayLevel.levelId) },
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "play_daily_puzzle_button"
                    )
                }
            }
        }
    }
}
