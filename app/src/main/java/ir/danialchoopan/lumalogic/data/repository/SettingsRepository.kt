package ir.danialchoopan.lumalogic.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Data class holding user setting preferences.
 */
data class SettingsData(
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val darkThemeMode: String = "System",
    val language: String = "English"
)

/**
 * Local persistent repository for application settings using SharedPreferences.
 */
class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lumalogic_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SettingsData> = _settings

    private fun loadSettings(): SettingsData {
        return SettingsData(
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            hapticsEnabled = prefs.getBoolean("haptics_enabled", true),
            animationsEnabled = prefs.getBoolean("animations_enabled", true),
            darkThemeMode = prefs.getString("dark_theme_mode", "System") ?: "System",
            language = prefs.getString("language", "English") ?: "English"
        )
    }

    fun updateSound(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _settings.value = _settings.value.copy(soundEnabled = enabled)
    }

    fun updateHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _settings.value = _settings.value.copy(hapticsEnabled = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean("animations_enabled", enabled).apply()
        _settings.value = _settings.value.copy(animationsEnabled = enabled)
    }

    fun updateDarkThemeMode(mode: String) {
        prefs.edit().putString("dark_theme_mode", mode).apply()
        _settings.value = _settings.value.copy(darkThemeMode = mode)
    }

    fun updateLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _settings.value = _settings.value.copy(language = lang)
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }
}
