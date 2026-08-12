package ir.danialchoopan.lumalogic.ui.screens.game.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ir.danialchoopan.lumalogic.domain.model.GameCompletionResult
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import kotlinx.coroutines.delay

@Composable
fun WinDialog(
    result: GameCompletionResult,
    onRetry: () -> Unit,
    onNextLevel: () -> Unit,
    onLevelSelect: () -> Unit,
    onHome: () -> Unit
) {
    var animatedStarsCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(result.stars) {
        animatedStarsCount = 0
        for (i in 1..result.stars) {
            delay(250)
            animatedStarsCount = i
        }
    }

    Dialog(
        onDismissRequest = { /* Require button action */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, TargetGreen, RoundedCornerShape(28.dp))
                .padding(4.dp)
                .testTag("win_screen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LEVEL COMPLETE!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TargetGreen,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = result.levelName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Animated Stars Row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Rating: ${result.stars} out of 3 stars"
                        }
                ) {
                    for (i in 1..3) {
                        val isFilled = i <= animatedStarsCount
                        val scale by animateFloatAsState(
                            targetValue = if (isFilled) 1.2f else 0.9f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "star_scale_$i"
                        )

                        Icon(
                            imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (isFilled) AmberPrimary else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier
                                .size(48.dp)
                                .scale(scale)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                // Total Score Banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = TargetGreen.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TOTAL SCORE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TargetGreen,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "${result.scoreResult.totalScore}",
                            fontSize = 32.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = AmberPrimary
                        )
                    }
                }

                // Stats Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScoreStatRow("Completion Base", "+${result.scoreResult.baseCompletionScore}")
                    ScoreStatRow("Energy Bonus (${result.energyState.remaining} remaining)", "+${result.scoreResult.energyBonus}")
                    ScoreStatRow("Move Efficiency (${result.movesCount} moves)", "+${result.scoreResult.moveBonus}")
                    ScoreStatRow("Time Bonus (${result.timeSeconds}s)", "+${result.scoreResult.timeBonus}")
                    if (result.optionalTargetsActivated > 0) {
                        ScoreStatRow("Optional Targets (${result.optionalTargetsActivated})", "+${result.scoreResult.optionalTargetBonus}")
                    }
                    if (result.scoreResult.hintPenalty > 0) {
                        ScoreStatRow("Hints Used Penalty (${result.hintsUsed})", "-${result.scoreResult.hintPenalty}", isNegative = true)
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onHome,
                        modifier = Modifier.testTag("win_home_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(
                        onClick = onLevelSelect,
                        modifier = Modifier.testTag("win_level_select_button")
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Level Select", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OutlinedButton(
                        onClick = onRetry,
                        modifier = Modifier.testTag("win_retry_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retry")
                    }
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(containerColor = TargetGreen, contentColor = Color.Black),
                        modifier = Modifier.testTag("win_next_level_button")
                    ) {
                        Text("Next", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreStatRow(label: String, value: String, isNegative: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (isNegative) MaterialTheme.colorScheme.error else AmberPrimary
        )
    }
}
