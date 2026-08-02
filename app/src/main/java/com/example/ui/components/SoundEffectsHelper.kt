package com.example.ui.components

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SoundEffectsHelper {
    private var toneGenerator: ToneGenerator? = null
    private var bgmJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        getOrInitToneGenerator()
    }

    private fun getOrInitToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            } catch (e: Exception) {
                Log.e("SoundEffectsHelper", "Failed to initialize ToneGenerator", e)
            }
        }
        return toneGenerator
    }

    fun playClick() {
        try {
            getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (e: Exception) {
            Log.e("SoundEffectsHelper", "Error playing click", e)
        }
    }

    fun playBearGrowl() {
        scope.launch {
            try {
                // Low, deep, rumbling beep to simulate a friendly cartoon bear growl
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 250)
            } catch (e: Exception) {
                Log.e("SoundEffectsHelper", "Error playing bear growl", e)
            }
        }
    }

    fun playMotuMelody() {
        scope.launch {
            try {
                // Energetic, comical cartoon bounce/horn pattern for Motu
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 120)
                delay(100)
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 120)
                delay(100)
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 150)
            } catch (e: Exception) {
                Log.e("SoundEffectsHelper", "Error playing Motu melody", e)
            }
        }
    }

    fun playSuccess() {
        scope.launch {
            try {
                // Cheerful rising progression
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
                delay(150)
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
                delay(150)
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
            } catch (e: Exception) {
                Log.e("SoundEffectsHelper", "Error playing success chime", e)
            }
        }
    }

    fun playCorrect() {
        playSuccess()
    }

    fun playWrong() {
        scope.launch {
            try {
                getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_SUP_ERROR, 250)
            } catch (e: Exception) {
                Log.e("SoundEffectsHelper", "Error playing wrong sfx", e)
            }
        }
    }

    fun playNext() {
        try {
            getOrInitToneGenerator()?.startTone(ToneGenerator.TONE_PROP_ACK, 100)
        } catch (e: Exception) {
            Log.e("SoundEffectsHelper", "Error playing next chime", e)
        }
    }

    fun startBackgroundMusic() {
        stopBackgroundMusic()
        bgmJob = scope.launch {
            // A gentle, soft, occasional chime progression for kids
            val melody = listOf(
                ToneGenerator.TONE_PROP_ACK to 250L,
                ToneGenerator.TONE_CDMA_PIP to 350L,
                ToneGenerator.TONE_PROP_ACK to 250L,
                ToneGenerator.TONE_CDMA_PIP to 350L,
                ToneGenerator.TONE_SUP_CONFIRM to 500L,
                0 to 800L, // Silence rest
                ToneGenerator.TONE_PROP_ACK to 250L,
                ToneGenerator.TONE_CDMA_PIP to 350L,
                ToneGenerator.TONE_SUP_CONFIRM to 600L,
                0 to 1200L // Long rest
            )
            while (isActive) {
                val tg = getOrInitToneGenerator()
                for (note in melody) {
                    if (!isActive) break
                    if (note.first != 0) {
                        try {
                            tg?.startTone(note.first, 60) // Pleasant, slightly more audible volume (60 instead of 50)
                        } catch (e: Exception) {
                            Log.e("SoundEffectsHelper", "BGM note error", e)
                        }
                    }
                    delay(note.second)
                }
                // Rest for 4 seconds between cycles for a pleasant, ambient feel
                delay(4000)
            }
        }
    }

    fun stopBackgroundMusic() {
        bgmJob?.cancel()
        bgmJob = null
    }

    fun release() {
        stopBackgroundMusic()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SoundEffectsHelper", "Error releasing ToneGenerator", e)
        }
    }
}
