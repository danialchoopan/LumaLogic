package ir.danialchoopan.lumalogic.ui.screens.achievements

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.data.model.Achievement
import ir.danialchoopan.lumalogic.di.AppContainer
import ir.danialchoopan.lumalogic.ui.components.LumaHeader
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit
) {
    val achRepo = remember { AppContainer.achievementRepository }
    val progressManager = remember { AppContainer.levelProgressManager }

    var achievements by remember { mutableStateOf<List<Achievement>>(emptyList()) }

    LaunchedEffect(Unit) {
        val stats = progressManager.getPlayerStats()
        achRepo.checkAndUnlockAchievements(stats, null)
        achievements = achRepo.getAchievements()
    }

    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        topBar = {
            LumaHeader(
                title = "Achievements ($unlockedCount/${achievements.size})",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { ach ->
                AchievementCard(achievement = ach)
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val isUnlocked = achievement.isUnlocked

    val iconVector: ImageVector = when (achievement.iconName) {
        "Lightbulb" -> Icons.Default.Lightbulb
        "Star" -> Icons.Default.Star
        "Stars" -> Icons.Default.Stars
        "AutoAwesome" -> Icons.Default.AutoAwesome
        "WorkspacePremium" -> Icons.Default.WorkspacePremium
        "Bolt" -> Icons.Default.Bolt
        "AccountTree" -> Icons.Default.AccountTree
        "Psychology" -> Icons.Default.Psychology
        else -> Icons.Default.EmojiEvents
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_card_${achievement.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isUnlocked) AmberPrimary.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isUnlocked) iconVector else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) AmberPrimary else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isUnlocked) {
                        Text(
                            text = "UNLOCKED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TargetGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                if (!isUnlocked && achievement.targetCount > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { achievement.progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(CircleShape),
                            color = AmberPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${achievement.currentCount}/${achievement.targetCount}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
