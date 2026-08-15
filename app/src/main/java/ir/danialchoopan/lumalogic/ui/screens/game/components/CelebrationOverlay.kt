package ir.danialchoopan.lumalogic.ui.screens.game.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.danialchoopan.lumalogic.ui.localization.currentLocalization
import ir.danialchoopan.lumalogic.ui.theme.AmberPrimary
import ir.danialchoopan.lumalogic.ui.theme.TargetGreen
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkleParticle(
    val angle: Float,
    val speed: Float,
    val radius: Float,
    val color: Color
)

/**
 * Visual particle burst and glowing banner celebration overlay displayed immediately upon level completion.
 */
@Composable
fun CelebrationOverlay(
    modifier: Modifier = Modifier
) {
    val loc = currentLocalization()

    val scaleAnim = remember { Animatable(0f) }
    val glowAnim = remember { Animatable(0f) }
    val particleProgress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(AmberPrimary, TargetGreen, Color(0xFF00E5FF), Color(0xFFFF4081), Color.White)
        List(40) {
            SparkleParticle(
                angle = Random.nextFloat() * 360f,
                speed = 80f + Random.nextFloat() * 220f,
                radius = 3f + Random.nextFloat() * 6f,
                color = colors[it % colors.size]
            )
        }
    }

    LaunchedEffect(Unit) {
        launch {
            scaleAnim.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            )
            scaleAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            )
        }
        launch {
            glowAnim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            particleProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(1500, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Particle canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val progress = particleProgress.value

            // Radiant expanding shockwave ring
            drawCircle(
                color = TargetGreen.copy(alpha = (1f - progress).coerceIn(0f, 1f) * 0.45f),
                radius = progress * size.width * 0.45f,
                center = center,
                style = Stroke(width = 6f * (1f - progress))
            )

            // Sparkle particles
            particles.forEach { p ->
                val rad = Math.toRadians(p.angle.toDouble())
                val dist = p.speed * progress * 2.5f
                val x = center.x + (cos(rad) * dist).toFloat()
                val y = center.y + (sin(rad) * dist).toFloat()
                val alpha = (1f - progress).coerceIn(0f, 1f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius * (1f - progress * 0.5f),
                    center = Offset(x, y)
                )
            }
        }

        // Center Celebration Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Success Star",
                tint = AmberPrimary,
                modifier = Modifier
                    .size(64.dp)
                    .scale(1f + glowAnim.value * 0.15f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (loc.isPersian) "مرحله با موفقیت کامل شد!" else "LEVEL COMPLETED!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TargetGreen,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (loc.isPersian) "درخشش عالی! در حال آماده‌سازی..." else "Brilliant! Preparing results...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
