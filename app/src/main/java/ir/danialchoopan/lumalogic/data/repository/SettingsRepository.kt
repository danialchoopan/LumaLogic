package ir.danialchoopan.lumalogic.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Data class holding comprehensive user setting preferences.
 */
data class SettingsData(
    // Language Preference ("English" or "Persian")
    val language: String = "English",

    // Appearance
    val darkThemeMode: String = "System", // "System", "Light", "Dark"
    val beamGlow: Boolean = true,
    val gridVisibility: Boolean = true,
    val gridOpacity: Float = 0.3f,

    // Accessibility
    val highContrast: Boolean = false,
    val reduceMotion: Boolean = false,
    val textSize: String = "Normal", // "Small", "Normal", "Large", "Extra Large"
    val colorblindMode: Boolean = false,

    // Audio & Haptics
    val soundEnabled: Boolean = true,
    val sfxVolume: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val hapticIntensity: String = "Medium", // "Low", "Medium", "High"

    // Gameplay Controls & Rules
    val confirmRestart: Boolean = true,
    val confirmExit: Boolean = true,
    val autoAdvance: Boolean = true,
    val showHints: Boolean = true,
    val showTutorial: Boolean = true,
    val tutorialCompleted: Boolean = false,

    // HUD Customization
    val showMoveCounter: Boolean = true,
    val showEnergyCounter: Boolean = true,
    val showTimer: Boolean = true,
    val showScore: Boolean = true,
    val showStarRequirements: Boolean = true,

    // Visual Feedback
    val successEffects: Boolean = true,
    val failureEffects: Boolean = true,
    val animationsEnabled: Boolean = true
) {
    val isPersian: Boolean get() = language == "Persian" || language == "فارسی" || language == "fa"
}

/**
 * Local persistent repository for application settings using SharedPreferences.
 */
class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("lumalogic_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<SettingsData> = _settings

    private fun loadSettings(): SettingsData {
        return SettingsData(
            language = prefs.getString("language", "English") ?: "English",
            darkThemeMode = prefs.getString("dark_theme_mode", "System") ?: "System",
            beamGlow = prefs.getBoolean("beam_glow", true),
            gridVisibility = prefs.getBoolean("grid_visibility", true),
            gridOpacity = prefs.getFloat("grid_opacity", 0.3f),

            highContrast = prefs.getBoolean("high_contrast", false),
            reduceMotion = prefs.getBoolean("reduce_motion", false),
            textSize = prefs.getString("text_size", "Normal") ?: "Normal",
            colorblindMode = prefs.getBoolean("colorblind_mode", false),

            soundEnabled = prefs.getBoolean("sound_enabled", true),
            sfxVolume = prefs.getFloat("sfx_volume", 1.0f),
            hapticsEnabled = prefs.getBoolean("haptics_enabled", true),
            hapticIntensity = prefs.getString("haptic_intensity", "Medium") ?: "Medium",

            confirmRestart = prefs.getBoolean("confirm_restart", true),
            confirmExit = prefs.getBoolean("confirm_exit", true),
            autoAdvance = prefs.getBoolean("auto_advance", true),
            showHints = prefs.getBoolean("show_hints", true),
            showTutorial = prefs.getBoolean("show_tutorial", true),
            tutorialCompleted = prefs.getBoolean("tutorial_completed", false),

            showMoveCounter = prefs.getBoolean("show_move_counter", true),
            showEnergyCounter = prefs.getBoolean("show_energy_counter", true),
            showTimer = prefs.getBoolean("show_timer", true),
            showScore = prefs.getBoolean("show_score", true),
            showStarRequirements = prefs.getBoolean("show_star_reqs", true),

            successEffects = prefs.getBoolean("success_effects", true),
            failureEffects = prefs.getBoolean("failure_effects", true),
            animationsEnabled = prefs.getBoolean("animations_enabled", true)
        )
    }

    fun updateLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
        _settings.value = _settings.value.copy(language = lang)
    }

    fun updateDarkThemeMode(mode: String) {
        prefs.edit().putString("dark_theme_mode", mode).apply()
        _settings.value = _settings.value.copy(darkThemeMode = mode)
    }

    fun updateBeamGlow(enabled: Boolean) {
        prefs.edit().putBoolean("beam_glow", enabled).apply()
        _settings.value = _settings.value.copy(beamGlow = enabled)
    }

    fun updateGridVisibility(enabled: Boolean) {
        prefs.edit().putBoolean("grid_visibility", enabled).apply()
        _settings.value = _settings.value.copy(gridVisibility = enabled)
    }

    fun updateGridOpacity(opacity: Float) {
        prefs.edit().putFloat("grid_opacity", opacity).apply()
        _settings.value = _settings.value.copy(gridOpacity = opacity)
    }

    fun updateHighContrast(enabled: Boolean) {
        prefs.edit().putBoolean("high_contrast", enabled).apply()
        _settings.value = _settings.value.copy(highContrast = enabled)
    }

    fun updateReduceMotion(enabled: Boolean) {
        prefs.edit().putBoolean("reduce_motion", enabled).apply()
        _settings.value = _settings.value.copy(reduceMotion = enabled)
    }

    fun updateTextSize(size: String) {
        prefs.edit().putString("text_size", size).apply()
        _settings.value = _settings.value.copy(textSize = size)
    }

    fun updateColorblindMode(enabled: Boolean) {
        prefs.edit().putBoolean("colorblind_mode", enabled).apply()
        _settings.value = _settings.value.copy(colorblindMode = enabled)
    }

    fun updateSound(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _settings.value = _settings.value.copy(soundEnabled = enabled)
    }

    fun updateSfxVolume(vol: Float) {
        prefs.edit().putFloat("sfx_volume", vol).apply()
        _settings.value = _settings.value.copy(sfxVolume = vol)
    }

    fun updateHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _settings.value = _settings.value.copy(hapticsEnabled = enabled)
    }

    fun updateHapticIntensity(intensity: String) {
        prefs.edit().putString("haptic_intensity", intensity).apply()
        _settings.value = _settings.value.copy(hapticIntensity = intensity)
    }

    fun updateConfirmRestart(enabled: Boolean) {
        prefs.edit().putBoolean("confirm_restart", enabled).apply()
        _settings.value = _settings.value.copy(confirmRestart = enabled)
    }

    fun updateConfirmExit(enabled: Boolean) {
        prefs.edit().putBoolean("confirm_exit", enabled).apply()
        _settings.value = _settings.value.copy(confirmExit = enabled)
    }

    fun updateAutoAdvance(enabled: Boolean) {
        prefs.edit().putBoolean("auto_advance", enabled).apply()
        _settings.value = _settings.value.copy(autoAdvance = enabled)
    }

    fun updateShowHints(enabled: Boolean) {
        prefs.edit().putBoolean("show_hints", enabled).apply()
        _settings.value = _settings.value.copy(showHints = enabled)
    }

    fun updateShowTutorial(enabled: Boolean) {
        prefs.edit().putBoolean("show_tutorial", enabled).apply()
        _settings.value = _settings.value.copy(showTutorial = enabled)
    }

    fun setTutorialCompleted(completed: Boolean) {
        prefs.edit().putBoolean("tutorial_completed", completed).apply()
        _settings.value = _settings.value.copy(tutorialCompleted = completed)
    }

    fun updateShowMoveCounter(enabled: Boolean) {
        prefs.edit().putBoolean("show_move_counter", enabled).apply()
        _settings.value = _settings.value.copy(showMoveCounter = enabled)
    }

    fun updateShowEnergyCounter(enabled: Boolean) {
        prefs.edit().putBoolean("show_energy_counter", enabled).apply()
        _settings.value = _settings.value.copy(showEnergyCounter = enabled)
    }

    fun updateShowTimer(enabled: Boolean) {
        prefs.edit().putBoolean("show_timer", enabled).apply()
        _settings.value = _settings.value.copy(showTimer = enabled)
    }

    fun updateShowScore(enabled: Boolean) {
        prefs.edit().putBoolean("show_score", enabled).apply()
        _settings.value = _settings.value.copy(showScore = enabled)
    }

    fun updateShowStarRequirements(enabled: Boolean) {
        prefs.edit().putBoolean("show_star_reqs", enabled).apply()
        _settings.value = _settings.value.copy(showStarRequirements = enabled)
    }

    fun updateAnimations(enabled: Boolean) {
        prefs.edit().putBoolean("animations_enabled", enabled).apply()
        _settings.value = _settings.value.copy(animationsEnabled = enabled)
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }
}
