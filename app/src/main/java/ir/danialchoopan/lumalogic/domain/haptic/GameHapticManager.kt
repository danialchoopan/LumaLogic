package ir.danialchoopan.lumalogic.domain.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Local haptic feedback manager for tactical touch feedback.
 */
class GameHapticManager(context: Context) {

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (e: Exception) {
        null
    }

    var isHapticsEnabled: Boolean = true

    fun performRotate() {
        vibrate(30)
    }

    fun performInvalidMove() {
        vibratePattern(longArrayOf(0, 40, 40, 40))
    }

    fun performTargetActivation() {
        vibrate(80)
    }

    fun performLevelComplete() {
        vibratePattern(longArrayOf(0, 80, 50, 120))
    }

    fun performLevelFailed() {
        vibratePattern(longArrayOf(0, 150))
    }

    private fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore vibration exceptions
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        if (!isHapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            // Ignore vibration exceptions
        }
    }
}
