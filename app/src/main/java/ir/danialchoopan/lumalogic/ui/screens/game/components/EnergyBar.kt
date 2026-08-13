package ir.danialchoopan.lumalogic.ui.screens.game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.domain.model.EnergyState
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen

@Composable
fun EnergyBar(
    energyState: EnergyState,
    modifier: Modifier = Modifier
) {
    val loc = currentLocalization()

    val progress = if (energyState.maximum > 0) {
        (energyState.remaining.toFloat() / energyState.maximum.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val barColor = when {
        energyState.isDepleted -> MaterialTheme.colorScheme.error
        energyState.isLow -> Color(0xFFFF9800) // Amber / Orange warning
        else -> TargetGreen
    }

    val transition = rememberInfiniteTransition(label = "low_energy_pulse")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("energy_bar")
            .semantics {
                contentDescription = "Energy remaining: ${energyState.remaining} of ${energyState.maximum}"
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (energyState.isLow || energyState.isDepleted) Icons.Default.Warning else Icons.Default.Bolt,
                    contentDescription = null,
                    tint = barColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (loc.isPersian) "انرژی" else "ENERGY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (energyState.isLow) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (loc.isPersian) "کم" else "LOW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.alpha(alphaAnim)
                    )
                } else if (energyState.isDepleted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (loc.isPersian) "تمام شد" else "DEPLETED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Text(
                text = "${loc.formatNumber(energyState.remaining)} / ${loc.formatNumber(energyState.maximum)}",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Bar Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(5.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}
