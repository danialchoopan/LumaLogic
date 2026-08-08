package ir.danialchoopan.lumalogic.ui.components

import androidx.compose.ui.graphics.Color
import ir.danialchoopan.lumalogic.data.model.LightColor

/**
 * Palette of game rendering colors used across Canvas components.
 */
object GameColors {
    val LaserYellow = Color(0xFFFFD54F)
    val TargetGreen = Color(0xFF4CAF50)
    val MirrorBlue = Color(0xFF2196F3)
    val BackgroundDark = Color(0xFF101014)
    val GridGray = Color(0xFF2A2A34)
    val GridBorder = Color(0xFF3F3F4E)
    val CellDark = Color(0xFF1E1E26)
    val BlockDark = Color(0xFF2C2C36)
    val WireGray = Color(0xFF616161)
    val BeamGlow = Color(0xFFFFF59D)
    val BeamOuter = Color(0x33FFD54F)
    val BeamMiddle = Color(0x66FFD54F)

    fun LightColor?.toComposeColor(): Color {
        return when (this) {
            LightColor.RED -> Color(0xFFFF5252)
            LightColor.BLUE -> Color(0xFF448AFF)
            LightColor.GREEN -> Color(0xFF69F0AE)
            LightColor.YELLOW -> Color(0xFFFFD740)
            LightColor.WHITE, null -> Color.White
        }
    }
}
