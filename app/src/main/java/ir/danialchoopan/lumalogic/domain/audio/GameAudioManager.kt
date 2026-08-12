package ir.danialchoopan.lumalogic.domain.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

/**
 * Local audio manager generating crisp synthesized tones for game interactions.
 * Does NOT require microphone or internet permissions.
 */
class GameAudioManager {

    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.e("GameAudioManager", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playButtonClick() {
        playTone(ToneGenerator.TONE_PROP_BEEP, 30)
    }

    fun playMirrorRotate() {
        playTone(ToneGenerator.TONE_PROP_ACK, 40)
    }

    fun playComponentMove() {
        playTone(ToneGenerator.TONE_PROP_BEEP2, 35)
    }

    fun playBeamActivation() {
        playTone(ToneGenerator.TONE_DTMF_A, 50)
    }

    fun playTargetActivation() {
        playTone(ToneGenerator.TONE_DTMF_0, 100)
    }

    fun playSplitter() {
        playTone(ToneGenerator.TONE_DTMF_5, 60)
    }

    fun playFilterBlocked() {
        playTone(ToneGenerator.TONE_PROP_NACK, 80)
    }

    fun playGateActivation() {
        playTone(ToneGenerator.TONE_DTMF_8, 70)
    }

    fun playLevelComplete() {
        playTone(ToneGenerator.TONE_CDMA_HIGH_L, 250)
    }

    fun playLevelFailed() {
        playTone(ToneGenerator.TONE_CDMA_LOW_PBX_L, 250)
    }

    fun playError() {
        playTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 120)
    }

    private fun playTone(toneType: Int, durationMs: Int) {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(toneType, durationMs)
        } catch (e: Exception) {
            // Ignore audio generation failures gracefully
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // Ignore release errors
        }
    }
}
