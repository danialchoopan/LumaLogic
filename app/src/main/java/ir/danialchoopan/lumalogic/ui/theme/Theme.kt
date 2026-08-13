package ir.danialchoopan.lumalogic.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.danialchoopan.lumalogic.data.repository.SettingsData
import ir.danialchoopan.lumalogic.ui.localization.LocalLocalization
import ir.danialchoopan.lumalogic.ui.localization.LocalizationManager

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = AmberOnPrimary,
    primaryContainer = AmberContainer,
    onPrimaryContainer = OnAmberContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569)
)

private val HighContrastColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF242424),
    onSurfaceVariant = Color.White
)

val LumaShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp)
)

@Composable
fun LumaLogicTheme(
    settingsData: SettingsData = SettingsData(),
    content: @Composable () -> Unit
) {
    val isDark = when (settingsData.darkThemeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        settingsData.highContrast -> HighContrastColorScheme
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    val layoutDirection = if (settingsData.isPersian) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    val localizationManager = LocalizationManager(settingsData.isPersian)

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalLocalization provides localizationManager
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = LumaShapes,
            content = content
        )
    }
}
