package com.example.ui.components

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import kotlinx.coroutines.*
import kotlin.coroutines.resume

class TextToSpeechHelper(
    private val context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private val progressManager = com.example.data.ProgressManager(context)
    private var webSpeechHelper: WebSpeechTtsHelper? = null
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var onSpeechDoneCallback: (() -> Unit)? = null

    // Coroutine support for singing poems rhythmically
    private var poemJob: Job? = null
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private data class SingNote(val pitch: Float, val speedFactor: Float, val pauseMs: Long)

    // Melody cycle configuration: oscillates the deep bear voice in a 4-beat rhythm to simulate chanting/singing
    private val melodyCycle = listOf(
        SingNote(pitch = 0.75f, speedFactor = 0.72f, pauseMs = 600L),  // Cheerful, rising baritone melody
        SingNote(pitch = 0.58f, speedFactor = 0.65f, pauseMs = 700L),  // Majestic deep bass response
        SingNote(pitch = 0.85f, speedFactor = 0.76f, pauseMs = 600L),  // High singing crescendo
        SingNote(pitch = 0.52f, speedFactor = 0.58f, pauseMs = 1200L)  // Ultra deep, grand resolution cadence with long rest
    )

    private var isRetrying = false
    private val promptedLanguages = mutableSetOf<String>()

    init {
        if (progressManager.useWebSpeechApi) {
            initWebSpeechHelper()
        }
        initializeTts()
    }

    private fun initWebSpeechHelper() {
        if (webSpeechHelper == null) {
            webSpeechHelper = WebSpeechTtsHelper(context) { success ->
                Log.d("TTS", "WebSpeechTtsHelper initialized: $success")
                if (progressManager.useWebSpeechApi) {
                    onInitComplete(success)
                }
            }
        }
    }

    fun isReady(): Boolean {
        return if (progressManager.useWebSpeechApi) {
            if (webSpeechHelper == null) {
                initWebSpeechHelper()
            }
            webSpeechHelper?.isEngineReady == true
        } else {
            isInitialized
        }
    }

    private fun initializeTts() {
        try {
            Log.d("TTS", "Attempting to initialize TextToSpeech with Google TTS engine...")
            val ttsContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context.createAttributionContext("languageland_attribution")
            } else {
                context
            }
            tts = TextToSpeech(ttsContext, this, "com.google.android.tts")
        } catch (e: Exception) {
            Log.e("TTS", "Failed to initialize with Google TTS engine, falling back to default engine", e)
            fallbackToDefaultEngine()
        }
    }

    private fun fallbackToDefaultEngine() {
        if (isRetrying) {
            isInitialized = false
            onInitComplete(false)
            return
        }
        isRetrying = true
        try {
            Log.d("TTS", "Attempting to initialize default TextToSpeech engine...")
            val ttsContext = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                context.createAttributionContext("languageland_attribution")
            } else {
                context
            }
            tts = TextToSpeech(ttsContext, this)
        } catch (e: Exception) {
            Log.e("TTS", "Exception initializing default TextToSpeech engine.", e)
            isInitialized = false
            onInitComplete(false)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("TTS", "Speech started: $utteranceId")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d("TTS", "Speech finished: $utteranceId")
                    onSpeechDoneCallback?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    Log.e("TTS", "Speech error: $utteranceId")
                    onSpeechDoneCallback?.invoke()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    Log.e("TTS", "Speech error: $utteranceId, code: $errorCode")
                    onSpeechDoneCallback?.invoke()
                }

                override fun onStop(utteranceId: String?, interrupted: Boolean) {
                    Log.d("TTS", "Speech stopped/interrupted: $utteranceId")
                    onSpeechDoneCallback?.invoke()
                }
            })
            onInitComplete(true)
        } else {
            Log.e("TTS", "TextToSpeech initialization failed with status $status")
            if (!isRetrying) {
                fallbackToDefaultEngine()
            } else {
                isInitialized = false
                onInitComplete(false)
            }
        }
    }

    private fun isLongPronunciation(text: String, subtitle: String): Boolean {
        val lowerSub = subtitle.lowercase()
        if (lowerSub.contains("(long)")) return true
        
        // If the learning item is a full word rather than a single letter, speak it slower/longer
        if (text.length > 2) return true
        
        val hasDoubleVowels = lowerSub.contains("aa") || lowerSub.contains("ee") || 
                              lowerSub.contains("oo") || lowerSub.contains("ae") || 
                              lowerSub.contains("uu") || lowerSub.contains("ai") || 
                              lowerSub.contains("au") || lowerSub.contains("ii")
        if (hasDoubleVowels) return true
        
        val longChars = setOf(
            'ఆ', 'ఈ', 'ఊ', 'ఏ', 'ఓ', 'ఐ', 'ఔ',
            'ஆ', 'ஈ', 'ஊ', 'ஏ', 'ஐ', 'ஓ', 'ஔ',
            'आ', 'ई', 'ऊ', 'ए', 'ऐ', 'ओ', 'औ',
            'ಆ', 'ಈ', 'ಊ', 'ಏ', 'ಐ', 'ಓ', 'ಔ',
            'ആ', 'ഈ', 'ഊ', 'ഏ', 'ഐ', 'ഓ', 'ഔ'
        )
        
        val longSigns = setOf(
            'ా', 'ీ', 'ూ', 'ే', 'ో', 'ై', 'ౌ',
            'ா', 'ீ', 'ூ', 'ே', 'ோ', 'ை', 'ௌ',
            'ा', 'ी', 'ू', 'े', 'ो', 'ै', 'ौ',
            'ಾ', 'ೀ', 'ೂ', 'ೇ', 'ೋ', 'ೈ', 'ೌ',
            'ാ', 'ി', 'ൂ', 'ே', 'ോ', 'ൈ', 'ൌ'
        )
        
        for (char in text) {
            if (char in longChars || char in longSigns) {
                return true
            }
        }
        return false
    }

    fun speak(text: String, subtitle: String = "", langCode: String, speed: Float, isVoiceOn: Boolean, isPoem: Boolean = false, voiceCharacter: String = "bear", onDone: () -> Unit = {}) {
        poemJob?.cancel() // Cancel any ongoing poem recitation immediately
        
        val cleanedText = stripEmojis(text)
        
        if (progressManager.useWebSpeechApi) {
            if (webSpeechHelper == null) {
                initWebSpeechHelper()
            }
            if (isVoiceOn) {
                webSpeechHelper?.speak(cleanedText, langCode, speed, onDone)
            } else {
                onDone()
            }
            return
        }

        var textToSpeak = cleanedText
        if (langCode == "te") {
            // Apply custom TTS fixes for Telugu isolated pronunciation issues
            if (textToSpeak == "ఆ") {
                textToSpeak = "ఆఆ"
            } else if (textToSpeak == "ఈ") {
                textToSpeak = "ఈఈ"
            } else if (textToSpeak == "ఊ") {
                textToSpeak = "ఊఊ"
            } else if (textToSpeak == "ఏ") {
                textToSpeak = "ఏఏ"
            } else if (textToSpeak == "ఓ") {
                textToSpeak = "ఓఓ"
            } else if (textToSpeak == "ఒ") {
                textToSpeak = "ఒ"
            } else if (textToSpeak == "ఱ") {
                textToSpeak = "బండిరా"
            }
            
            // Fix "అః" (aha) and "ఒ" in composite strings for perfect pronunciation
            if (textToSpeak.contains("అః")) {
                textToSpeak = textToSpeak.replace("అః", "అహా")
            }
            if (textToSpeak.startsWith("ఒ ")) {
                textToSpeak = textToSpeak.replaceFirst("ఒ ", "ఒ ")
            }
        } else if (langCode == "ta") {
            // Apply custom TTS fixes for Tamil letters to speak clearly and naturally
            when (textToSpeak) {
                "க்" -> textToSpeak = "இக்"
                "ங்" -> textToSpeak = "இங்"
                "ச்" -> textToSpeak = "இச்"
                "ஞ்" -> textToSpeak = "இஞ்"
                "ட்" -> textToSpeak = "இட்"
                "ண்" -> textToSpeak = "இண்"
                "த்" -> textToSpeak = "இத்"
                "ந்" -> textToSpeak = "இந்"
                "ப்" -> textToSpeak = "இப்"
                "ம்" -> textToSpeak = "இம்"
                "ய்" -> textToSpeak = "இய்"
                "ர்" -> textToSpeak = "இர்"
                "ல்" -> textToSpeak = "இல்"
                "வ்" -> textToSpeak = "இவ்"
                "ழ்" -> textToSpeak = "இழ்"
                "ள்" -> textToSpeak = "இள்"
                "ற்" -> textToSpeak = "இற்"
                "ன்" -> textToSpeak = "இன்"
                "ஆ" -> textToSpeak = "ஆஆ"
                "ஈ" -> textToSpeak = "ஈஈ"
                "ஊ" -> textToSpeak = "ஊஊ"
                "ஏ" -> textToSpeak = "ஏஏ"
                "ஓ" -> textToSpeak = "ஓஓ"
                "ஃ" -> textToSpeak = "அக்கு"
            }
        } else if (langCode == "hi") {
            // Apply custom TTS fixes for Hindi letters to speak clearly and naturally
            when (textToSpeak) {
                "आ" -> textToSpeak = "आआ"
                "ई" -> textToSpeak = "ईई"
                "ऊ" -> textToSpeak = "ऊऊ"
                "ऋ" -> textToSpeak = "री"
                "ए" -> textToSpeak = "एए"
                "ऐ" -> textToSpeak = "ऐऐ"
                "ओ" -> textToSpeak = "ओओ"
                "औ" -> textToSpeak = "औऔ"
                "अः" -> textToSpeak = "अहा"
            }
            if (textToSpeak.contains("अः")) {
                textToSpeak = textToSpeak.replace("अः", "अहा")
            }
        } else if (langCode == "ar") {
            var tempText = textToSpeak.trim()
            
            // Map raw isolated Arabic letters to their clear pronunciation word names
            val rawLetterNames = mapOf(
                "ا" to "أَلِف", "ب" to "بَا", "ت" to "تَا", "ث" to "ثَا",
                "ج" to "جِيم", "ح" to "حَا", "خ" to "خَا", "د" to "دَال",
                "ذ" to "ذَال", "ر" to "رَا", "ز" to "زَاي", "س" to "سِين",
                "ش" to "شِين", "ص" to "صَاد", "ض" to "ضَاد", "ط" to "طَا",
                "ظ" to "ظَا", "ع" to "عَيْن", "غ" to "غَيْن", "ف" to "فَا",
                "ق" to "قَاف", "ك" to "كَاف", "ل" to "لَام", "م" to "مِيم",
                "ن" to "نُون", "هـ" to "هَا", "ه" to "هَا", "و" to "وَاو", "ي" to "يَا"
            )
            
            if (rawLetterNames.containsKey(tempText)) {
                tempText = rawLetterNames[tempText] ?: tempText
            } else {
                // Smooth out the glottal stops in letter names if they appear within larger compound strings
                tempText = tempText.replace("أَلِفْ", "أَلِف").replace("أَلِف", "أَلِف")
                tempText = tempText.replace("بَاءْ", "بَا").replace("بَاء", "بَا")
                tempText = tempText.replace("تَاءْ", "تَا").replace("تَاء", "تَا")
                tempText = tempText.replace("ثَاءْ", "ثَا").replace("ثَاء", "ثَا")
                tempText = tempText.replace("حَاءْ", "حَا").replace("حَاء", "حَا")
                tempText = tempText.replace("خَاءْ", "خَا").replace("خَاء", "خَا")
                tempText = tempText.replace("رَاءْ", "رَا").replace("رَاء", "رَا")
                tempText = tempText.replace("طَاءْ", "طَا").replace("طَاء", "طَا")
                tempText = tempText.replace("ظَاءْ", "ظَا").replace("ظَاء", "ظَا")
                tempText = tempText.replace("فَاءْ", "فَا").replace("فَاء", "فَا")
                tempText = tempText.replace("هَاءْ", "هَا").replace("هَاء", "هَا")
                tempText = tempText.replace("يَاءْ", "يَا").replace("يَاء", "يَا")
            }
            
            // Replace colons (from letter-word matching like "أَلِفْ: أَسَدْ") with an Arabic comma (،) and a space to force a natural breathing pause.
            tempText = tempText.replace(":", " ، ")
            tempText = tempText.replace("：", " ، ")
            
            // Strip the decorative Kashida/Tatweel (ـ) character which can confuse speech synthesis algorithms.
            tempText = tempText.replace("ـ", "")
            
            // Strip any trailing or intermediate Sukun (ْ) which can cause harsh stops or clipped voice output on some engines.
            tempText = tempText.replace("ْ", "")
            
            textToSpeak = tempText
        }
        
        if (isVoiceOn) {
            // Volume check to alert user if their device volume is turned to 0
            try {
                val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                val currentVolume = audioManager?.getStreamVolume(android.media.AudioManager.STREAM_MUSIC) ?: 0
                if (currentVolume == 0) {
                    android.widget.Toast.makeText(
                        context,
                        "⚠️ Sound is muted! Please turn up your media volume.\n⚠️ సౌండ్ ఆఫ్‌లో ఉంది! దయచేసి మీ ఫోన్ వాల్యూమ్ పెంచండి.",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("TTS", "Error checking audio stream volume", e)
            }
        }
        
        if (!isInitialized) {
            Log.w("TTS", "System TTS is not initialized. Falling back to WebSpeechTtsHelper.")
            if (webSpeechHelper == null) {
                initWebSpeechHelper()
            }
            if (isVoiceOn) {
                webSpeechHelper?.speak(textToSpeak, langCode, speed, onDone)
            } else {
                onDone()
            }
            return
        }
        
        if (!isVoiceOn) {
            onDone()
            return
        }

        val locale = getLocaleForLangCode(langCode)
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("TTS", "Language $langCode is not supported or missing voice data. Falling back to WebSpeechTtsHelper.")
            if (webSpeechHelper == null) {
                initWebSpeechHelper()
            }
            if (isVoiceOn) {
                webSpeechHelper?.speak(textToSpeak, langCode, speed, onDone)
            } else {
                onDone()
            }
            return
        } else {
            // Attempt to select a high quality, deep, authoritative localized voice (Teacher Bhalu Bear) or a realistic female professional voice (Language Guru)
            try {
                val availableVoices = tts?.voices
                if (!availableVoices.isNullOrEmpty()) {
                    val bestVoice = availableVoices.asSequence()
                        .filter { it.locale.language == locale.language }
                        .sortedWith(compareByDescending<android.speech.tts.Voice> { 
                            if (voiceCharacter == "guru") {
                                // Prefer network-based high-quality neural/WaveNet voices for realistic feeling
                                it.isNetworkConnectionRequired
                            } else {
                                !it.isNetworkConnectionRequired 
                            }
                        }.thenByDescending { 
                            val nameLower = it.name.lowercase()
                            if (voiceCharacter == "guru") {
                                // Highly robust match for Google TTS and system female/sweet/natural voices
                                nameLower.contains("female") || 
                                nameLower.contains("-f-") || 
                                nameLower.contains("f-local") || 
                                nameLower.contains("f-network") || 
                                nameLower.contains("tef") || 
                                nameLower.contains("taf") || 
                                nameLower.contains("hif") || 
                                nameLower.contains("knf") || 
                                nameLower.contains("mlf")
                            } else {
                                // Highly robust match for Google TTS and system male voices (e.g. tem, tam, him, knm, mlm, m-local, m-network, male)
                                nameLower.contains("male") || 
                                nameLower.contains("-m-") || 
                                nameLower.contains("m-local") || 
                                nameLower.contains("m-network") || 
                                nameLower.contains("tem") || 
                                nameLower.contains("tam") || 
                                nameLower.contains("him") || 
                                nameLower.contains("knm") || 
                                nameLower.contains("mlm")
                            }
                        })
                        .firstOrNull()

                    if (bestVoice != null) {
                        Log.d("TTS", "Setting optimized voice ($voiceCharacter): ${bestVoice.name}")
                        tts?.voice = bestVoice
                    }
                }
            } catch (e: Exception) {
                Log.e("TTS", "Error setting optimized voice", e)
            }
        }

        // If it is a poem, we recite it like a song by splitting it into rhythmic phrases with melody oscillations
        if (isPoem) {
            this.onSpeechDoneCallback = null // Handled internally per phrase
            poemJob = mainScope.launch {
                // Split by commas, question marks, exclamation marks, periods, semicolons, and newlines
                val phrases = text.split(Regex("[,?\\n.!;:]+"))
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                if (phrases.isEmpty()) {
                    onDone()
                    return@launch
                }

                for (index in phrases.indices) {
                    val phrase = phrases[index]
                    val note = melodyCycle[index % melodyCycle.size]
                    
                    // Oscillating pitch and pacing to create a musical cadence
                    val currentPitch = if (voiceCharacter == "motu") note.pitch + 0.45f else note.pitch
                    val currentSpeed = if (voiceCharacter == "motu") {
                        (speed * note.speedFactor * 1.15f).coerceIn(0.70f, 1.20f)
                    } else {
                        (speed * note.speedFactor).coerceIn(0.50f, 1.10f)
                    }

                    try {
                        speakPhrase(phrase, currentPitch, currentSpeed)
                        // Dynamic musical pause between verses
                        delay(note.pauseMs)
                    } catch (e: CancellationException) {
                        break
                    } catch (e: Exception) {
                        Log.e("TTS", "Error during poem recitation", e)
                    }
                }
                onDone()
            }
            return
        }

        this.onSpeechDoneCallback = onDone

        val isSentence = textToSpeak.contains(" ") || textToSpeak.contains("\n")

        // Adjust speed dynamically based on character - higher multipliers for native clarity
        var adjustedSpeed = if (voiceCharacter == "motu") {
            if (isSentence) {
                (speed * 0.88f).coerceIn(0.70f, 1.15f) // Comical but perfectly spoken for Motu
            } else {
                (speed * 0.95f).coerceIn(0.75f, 1.20f)
            }
        } else if (voiceCharacter == "guru") {
            if (isSentence) {
                (speed * 0.92f).coerceIn(0.80f, 1.15f) // Highly realistic natural teacher speed
            } else {
                (speed * 0.96f).coerceIn(0.85f, 1.20f)
            }
        } else {
            if (isSentence) {
                (speed * 0.85f).coerceIn(0.70f, 1.10f) // Highly clear, majestic and native pacing
            } else if (langCode == "ar") {
                (speed * 0.88f).coerceIn(0.75f, 1.15f) // Clear precise classic Arabic pronunciation pace
            } else {
                if (isLongPronunciation(textToSpeak, subtitle)) {
                    (speed * 0.85f).coerceIn(0.70f, 1.10f)
                } else {
                    (speed * 0.92f).coerceIn(0.80f, 1.25f)
                }
            }
        }

        tts?.setSpeechRate(adjustedSpeed)
        
        // Pitch tuning: Clear deep friendly pitch (0.95f) for bear, sweet professional pitch (1.02f) for Guru, standard natural pitch (1.0f) for Arabic, bubbly comical energetic pitch (1.15f) for Motu voice
        val pitch = if (voiceCharacter == "motu") {
            1.15f
        } else if (voiceCharacter == "guru") {
            1.02f // Extremely warm, realistic, professional voice pitch
        } else if (langCode == "ar") {
            1.0f
        } else {
            0.95f // Brightened and elevated to keep it extremely friendly, warm, and highly intelligible
        }
        tts?.setPitch(pitch)

        val params = android.os.Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "language_app_utterance")
        }

        val speakResult = tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "language_app_utterance")
        if (speakResult == TextToSpeech.ERROR) {
            Log.w("TTS", "System tts.speak returned ERROR. Falling back to WebSpeechTtsHelper.")
            if (webSpeechHelper == null) {
                initWebSpeechHelper()
            }
            if (isVoiceOn) {
                webSpeechHelper?.speak(textToSpeak, langCode, speed, onDone)
            } else {
                onDone()
            }
        }
    }

    private suspend fun speakPhrase(phrase: String, pitch: Float, speed: Float) = suspendCancellableCoroutine<Unit> { continuation ->
        this.onSpeechDoneCallback = {
            if (continuation.isActive) {
                continuation.resume(Unit)
            }
        }
        
        continuation.invokeOnCancellation {
            this.onSpeechDoneCallback = null
        }
        
        tts?.setPitch(pitch)
        tts?.setSpeechRate(speed)
        
        val params = android.os.Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "language_app_utterance")
        }
        
        val result = tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, params, "language_app_utterance")
        if (result == TextToSpeech.ERROR) {
            if (continuation.isActive) {
                continuation.resume(Unit)
            }
        }
    }

    fun stop() {
        poemJob?.cancel()
        poemJob = null
        webSpeechHelper?.stop()
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        poemJob?.cancel()
        poemJob = null
        webSpeechHelper?.shutdown()
        if (isInitialized) {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        }
    }

    private fun getLocaleForLangCode(langCode: String): Locale {
        return when (langCode) {
            "te" -> Locale("te", "IN") // Telugu
            "ta" -> Locale("ta", "IN") // Tamil
            "hi" -> Locale("hi", "IN") // Hindi
            "ar" -> Locale("ar", "SA") // Arabic
            "kn" -> Locale("kn", "IN") // Kannada
            "ml" -> Locale("ml", "IN") // Malayalam
            "bn" -> Locale("bn", "IN") // Bengali
            "mr" -> Locale("mr", "IN") // Marathi
            "gu" -> Locale("gu", "IN") // Gujarati
            else -> Locale.US          // English default
        }
    }

    private fun getLanguageName(langCode: String): String {
        return when (langCode) {
            "te" -> "Telugu (తెలుగు)"
            "ta" -> "Tamil (தமிழ்)"
            "hi" -> "Hindi (हिन्दी)"
            "ar" -> "Arabic (العربية)"
            "kn" -> "Kannada (ಕನ್ನಡ)"
            "ml" -> "Malayalam (മലയാളం)"
            "bn" -> "Bengali (বাংলা)"
            "mr" -> "Marathi (मराठी)"
            "gu" -> "Gujarati (ગુજરાતી)"
            else -> "English"
        }
    }

    private fun showMissingDataPrompt(langCode: String) {
        if (promptedLanguages.contains(langCode)) return
        promptedLanguages.add(langCode)

        val langName = getLanguageName(langCode)
        mainScope.launch {
            android.widget.Toast.makeText(
                context,
                "⚠️ $langName voice data is missing. Opening TTS settings to download it.\n⚠️ $langName వాయిస్ డేటా లేదు. డౌన్‌లోడ్ చేయడానికి TTS సెట్టింగ్స్ ఓపెన్ చేస్తున్నాము.",
                android.widget.Toast.LENGTH_LONG
            ).show()
            
            try {
                val intent = android.content.Intent().apply {
                    action = "com.android.settings.TTS_SETTINGS"
                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val installIntent = android.content.Intent().apply {
                        action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(installIntent)
                } catch (ex: Exception) {
                    Log.e("TTS", "Could not open TTS settings", ex)
                }
            }
        }
    }

    private fun stripEmojis(text: String): String {
        val sb = java.lang.StringBuilder()
        var i = 0
        while (i < text.length) {
            val codePoint = text.codePointAt(i)
            val charCount = Character.charCount(codePoint)
            
            val isEmoji = (codePoint in 0x1F000..0x1FBF9) ||
                    (codePoint in 0x1F300..0x1F5FF) ||
                    (codePoint in 0x1F600..0x1F64F) ||
                    (codePoint in 0x1F680..0x1F6FF) ||
                    (codePoint in 0x1F900..0x1F9FF) ||
                    (codePoint in 0x1FA00..0x1FAFF) ||
                    (codePoint in 0x2000..0x3300) || // Includes 0x2B50 (⭐), 0x2728 (✨), symbols
                    (codePoint in 0x2600..0x27BF) ||
                    (codePoint in 0x1F1E0..0x1F1FF) ||
                    (codePoint in 0x1F100..0x1F1FF) ||
                    (codePoint == 0x200D) ||
                    (codePoint == 0xFE0F) ||
                    (codePoint == 0xFE0E)
            
            if (!isEmoji) {
                sb.appendRange(text, i, i + charCount)
            }
            i += charCount
        }
        var result = sb.toString()
        val removeChars = listOf("⭐", "✨", "🎉", "❤️", "🎈", "🎓", "🥳", "🌈", "🏆", "🧸", "🦖", "🦕", "👏", "👍", "👑", "🚀", "🪄", "🧹", "🔥")
        for (rc in removeChars) {
            result = result.replace(rc, "")
        }
        // Remove markdown formatting characters so TTS doesn't read them out loud
        result = result.replace("**", "")
            .replace("*", "")
            .replace("__", "")
            .replace("_", "")
            .replace("`", "")
            .replace("#", "")
            .replace("~", "")
        return result.trim()
    }
}
