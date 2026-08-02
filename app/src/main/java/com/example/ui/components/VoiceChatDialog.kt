package com.example.ui.components

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceChatDialog(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val activeLang by viewModel.selectedLanguage.collectAsState()
    val langCode = activeLang?.code ?: "en"

    val isAvatarFemale by viewModel.isAvatarFemale.collectAsState()
    val avatarType by viewModel.avatarType.collectAsState()
    val avatarExpression by viewModel.avatarExpression.collectAsState()
    val avatarIsSpeaking by viewModel.avatarIsSpeaking.collectAsState()
    val avatarAction by viewModel.avatarAction.collectAsState()
    val avatarSize by viewModel.avatarSize.collectAsState()
    val avatarText by viewModel.avatarText.collectAsState()
    val isAvatarMuted by viewModel.isAvatarMuted.collectAsState()

    val isPremium by viewModel.isPremium.collectAsState()
    val diamonds by viewModel.diamonds.collectAsState()
    val aiQuestionsCountToday by viewModel.aiQuestionsCountToday.collectAsState()
    var showLimitReachedDialog by remember { mutableStateOf(false) }
    var pendingPromptText by remember { mutableStateOf("") }
    var bypassLimitCheck by remember { mutableStateOf(false) }

    var isWatchingAd by remember { mutableStateOf(false) }
    var adCountdown by remember { mutableStateOf(5) }

    var chatInput by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var isSimulatingMic by remember { mutableStateOf(false) }
    var previousResponse by remember { mutableStateOf("") }

    val currentItem = viewModel.getCurrentItem()

    // Pre-defined kid-friendly quick buttons
    val quickPhrases = when (langCode) {
        "te" -> listOf(
            "ఇది ఏమిటి? 🤔" to "ఇది ఏమిటి?",
            "ఇంగ్లీష్ లో ఏమంటారు? 🇬🇧" to "How do you say this in English?",
            "మళ్లీ చెప్పు 🔊" to "మళ్లీ చెప్పు",
            "నాకో కథ చెప్పు 📖" to "నాకో చిన్న కథ చెప్పు",
            "మరో పదం చెప్పు ✨" to "మరో కొత్త పదం నేర్పించు"
        )
        "ta" -> listOf(
            "இது என்ன? 🤔" to "இது என்ன?",
            "ஆங்கிலத்தில் எப்படி சொல்வது? 🇬🇧" to "How do you say this in English?",
            "மீண்டும் கூறு 🔊" to "மீண்டும் கூறு",
            "ஒரு கதை கூறு 📖" to "ஒரு குட்டிக் கதை கூறு",
            "மற்றொரு வார்த்தை ✨" to "மற்றொரு புதிய வார்த்தையைக் கற்றுக்கொடு"
        )
        "hi" -> listOf(
            "यह क्या है? 🤔" to "यह क्या है?",
            "अंग्रेजी में क्या कहेंगे? 🇬🇧" to "How do you say this in English?",
            "फिर से बोलो 🔊" to "फिर से बोलो",
            "कहानी सुनाओ 📖" to "मुझे एक छोटी कहानी सुनाओ",
            "दूसरा शब्द ✨" to "मुझे एक और नया शब्द सिखाओ"
        )
        "ar" -> listOf(
            "ما هذا؟ 🤔" to "ما هذا؟",
            "كيف بالإنجليزية؟ 🇬🇧" to "How do you say this in English?",
            "أعد مجدداً 🔊" to "أعد مجدداً",
            "احكِ لي قصة 📖" to "احكِ لي قصة قصيرة",
            "كلمة أخرى ✨" to "علمني كلمة جديدة أخرى"
        )
        "kn" -> listOf(
            "ಇದು ಏನು? 🤔" to "ಇದು ಏನು?",
            "ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಹೇಗೆ ಹೇಳುವುದು? 🇬🇧" to "How do you say this in English?",
            "ಮತ್ತೊಮ್ಮೆ ಹೇಳು 🔊" to "ಮತ್ತೊಮ್ಮೆ ಹೇಳು",
            "ಒಂದು ಕಥೆ ಹೇಳು 📖" to "ನನಗೊಂದು ಸಣ್ಣ ಕಥೆ ಹೇಳು",
            "ಮತ್ತೊಂದು ಪದ ✨" to "ನನಗೆ ಮತ್ತೊಂದು ಹೊಸ ಪದ ಕಲಿಸು"
        )
        "ml" -> listOf(
            "ಇതെന്താണ്? 🤔" to "ಇതെന്താണ്?",
            "ഇംഗ്ലീഷിൽ എങ്ങനെ പറയും? 🇬🇧" to "How do you say this in English?",
            "ഒന്നുകൂടി പറയൂ 🔊" to "ഒന്നുകൂടി പറയൂ",
            "ഒരു കഥ പറയൂ 📖" to "എനിക്ക് ഒരു കുട്ടി കഥ പറയൂ",
            "മറ്റൊരു വാക്ക് ✨" to "എനിക്ക് മറ്റൊരു പുതിയ വാക്ക് പഠിപ്പിച്ചു തരൂ"
        )
        else -> listOf(
            "What is this? 🤔" to "What is this item?",
            "How in Telugu? 🇮🇳" to "How do you say this in Telugu?",
            "Say it again 🔊" to "Say again",
            "Tell a story 📖" to "Tell me a short story",
            "Another word ✨" to "Teach me another new word"
        )
    }

    // Call Gemini API
    fun askGemini(promptText: String) {
        if (isThinking) return
        viewModel.playClickSound()
        
        // Handle "Say again" or local equivalents offline
        val sayAgainTriggers = setOf(
            "Say again", "Say it again 🔊", "మళ్లీ చెప్పు", "మళ్లీ చెప్పు 🔊",
            "மீண்டும் கூறு", "மீண்டும் கூறு 🔊", "फिर से बोलो", "फिर से बोलो 🔊",
            "أعد مجدداً", "أعد مجدداً 🔊", "ಮತ್ತೊಮ್ಮೆ ಹೇಳು", "ಮತ್ತೊಮ್ಮೆ ಹೇಳು 🔊",
            "ഒന്നുകൂടി പറയൂ", "ഒന്നുകൂടി പറയൂ 🔊"
        )
        val isSayAgain = sayAgainTriggers.contains(promptText) || sayAgainTriggers.contains(promptText.trim())

        if (!isSayAgain && !bypassLimitCheck) {
            val currentCount = viewModel.getAiQuestionsCountToday()
            if (currentCount >= 5) {
                pendingPromptText = promptText
                showLimitReachedDialog = true
                return
            }
        }

        if (bypassLimitCheck) {
            bypassLimitCheck = false
        }

        if (!isSayAgain) {
            viewModel.incrementAiQuestionsCountToday()
        }

        if (isSayAgain) {
            if (previousResponse.isNotEmpty()) {
                responseText = previousResponse
                viewModel.speakCustomText(previousResponse)
            } else {
                val fallback = getVoiceChatText("fallback_no_speech", langCode)
                responseText = fallback
                viewModel.speakCustomText(fallback)
            }
            return
        }

        isThinking = true
        responseText = getVoiceChatText("thinking", langCode)
        
        // Update avatar animation to thinking
        // Expose direct variable changes if supported, or via speaking flows
        
        coroutineScope.launch {
            try {
                // Enrich prompt context with the current item
                val contextEnricher = if (currentItem != null) {
                    "The current item being learned is '${currentItem.display}' (pronunciation/translation: '${currentItem.subtitle}'). "
                } else ""

                val languageInstruction = when (langCode) {
                    "te" -> "The child's language is Telugu. You MUST write your response strictly and entirely in Telugu script (తెలుగు లిపి). Do not write English sentences. Speak to the child with warm encouragement in simple Telugu script. For example: 'విజయం ని ఇంగ్లీష్ లో Success అంటారు, బంగారం!'. Do not use English characters unless inside quotes as translations."
                    "ta" -> "The child's language is Tamil. You MUST write your response strictly and entirely in Tamil script (தமிழ்). Do not write English sentences. Speak with warm encouragement in simple Tamil."
                    "hi" -> "The child's language is Hindi. You MUST write your response strictly and entirely in Hindi script (हिन्दी). Do not write English sentences. Speak with warm encouragement in simple Hindi."
                    "ar" -> "The child's language is Arabic. You MUST write your response strictly and entirely in Arabic script (العربية). Do not write English sentences. Speak with warm encouragement in simple Arabic."
                    "kn" -> "The child's language is Kannada. You MUST write your response strictly and entirely in Kannada script (ಕನ್ನಡ). Do not write English sentences. Speak with warm encouragement in simple Kannada."
                    "ml" -> "The child's language is Malayalam. You MUST write your response strictly and entirely in Malayalam script (മലയാളം). Do not write English sentences. Speak with warm encouragement in simple Malayalam."
                    else -> "The child's language is English. Write your response in sweet, simple, encouraging English for kids."
                }

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                val textObj = JSONObject().apply {
                                    put("text", "System: You are a magical cartoon school teacher AI. Keep your answers extremely short (1 to 2 sentences maximum), friendly, colorful, and loaded with cute emojis! Do NOT use markdown symbols like double asterisks (**) or single asterisks (*) to bold text, just write standard clean text. " + languageInstruction + "\n\n" + contextEnricher + "User child says: " + promptText)
                                }
                                put(textObj)
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("VoiceChat", "Gemini error", e)
                        coroutineScope.launch {
                            isThinking = false
                            responseText = getVoiceChatText("brain_offline", langCode)
                            viewModel.speakCustomText(responseText)
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use { res ->
                            val body = res.body?.string() ?: ""
                            coroutineScope.launch {
                                isThinking = false
                                if (res.isSuccessful && body.isNotEmpty()) {
                                    try {
                                        val jsonRes = JSONObject(body)
                                        val candidates = jsonRes.optJSONArray("candidates")
                                        val firstCandidate = candidates?.optJSONObject(0)
                                        val content = firstCandidate?.optJSONObject("content")
                                        val parts = content?.optJSONArray("parts")
                                        val firstPart = parts?.optJSONObject(0)
                                        val text = firstPart?.optString("text") ?: ""
                                        
                                        if (text.isNotEmpty()) {
                                            responseText = text
                                            previousResponse = text
                                            // Make avatar speak response
                                            viewModel.speakCustomText(text)
                                        } else {
                                            responseText = getVoiceChatText("not_hear", langCode)
                                        }
                                    } catch (e: Exception) {
                                        responseText = "Error reading response: ${e.message}"
                                    }
                                } else {
                                    responseText = getVoiceChatText("silly_response", langCode)
                                }
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                isThinking = false
                responseText = "Request failure: ${ex.message}"
            }
        }
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember {
        try {
            SpeechRecognizer.createSpeechRecognizer(context)
        } catch (e: Exception) {
            Log.e("VoiceChat", "SpeechRecognizer creation failed", e)
            null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (e: Exception) {
                Log.e("VoiceChat", "Error destroying speech recognizer", e)
            }
        }
    }

    val recognitionListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VoiceChat", "onReadyForSpeech")
                isListening = true
                isSimulatingMic = true
                responseText = getVoiceChatText("listening", langCode)
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceChat", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("VoiceChat", "onEndOfSpeech")
                isListening = false
                isSimulatingMic = false
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech input detected"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input timeout"
                    else -> "Speech recognition error"
                }
                Log.e("VoiceChat", "Speech error code: $error ($errorMsg)")
                isListening = false
                isSimulatingMic = false
                
                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    responseText = when (langCode) {
                        "te" -> "నేను వినలేకపోయాను. దయచేసి మళ్లీ మైక్ బటన్ నొక్కి మాట్లాడండి లేదా టైప్ చేయండి! 🎙️✍️"
                        "ta" -> "என்னால் கேட்க முடியவில்லை. மீண்டும் மைக்கை அழுத்தவும் அல்லது தட்டச்சு செய்யவும்! 🎙️✍️"
                        "hi" -> "मैं सुन नहीं पाया। कृपया फिर से माइक दबाएं या टाइप करें! 🎙️✍️"
                        "ar" -> "لم أتمكن من السماع. يرجى الضغط على الميكروفون مجدداً أو الكتابة! 🎙️✍️"
                        "kn" -> "ನನಗೆ ಕೇಳಿಸಲಿಲ್ಲ. ದಯವಿಟ್ಟು ಮತ್ತೊಮ್ಮೆ ಮೈಕ್ ಒತ್ತಿ ಅಥವಾ ಟೈಪ್ ಮಾಡಿ! 🎙️✍️"
                        "ml" -> "എനിക്ക് കേൾക്കാൻ കഴിഞ്ഞില്ല. ദയവായി വീണ്ടും മൈക്ക് അമർക്കുക അല്ലെങ്കിൽ ടൈപ്പ് ചെയ്യുക! 🎙️✍️"
                        else -> "I didn't catch that. Please tap the mic again to speak, or write your question! 🎙️✍️"
                    }
                } else {
                    responseText = "Error listening: $errorMsg. Please try again!"
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val speechResult = matches[0]
                    Log.d("VoiceChat", "Speech result: $speechResult")
                    chatInput = speechResult
                    isListening = false
                    isSimulatingMic = false
                    askGemini(speechResult)
                } else {
                    responseText = getVoiceChatText("not_hear", langCode)
                    isListening = false
                    isSimulatingMic = false
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0]
                    chatInput = partialText
                    responseText = "Listening: \"$partialText\"... 🎤"
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    val startListeningSpeech = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            
            val speechLocale = when (langCode) {
                "te" -> "te-IN"
                "ta" -> "ta-IN"
                "hi" -> "hi-IN"
                "ar" -> "ar-SA"
                "kn" -> "kn-IN"
                "ml" -> "ml-IN"
                else -> "en-US"
            }
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLocale)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, speechLocale)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, speechLocale)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            
            // Adjust silence thresholds generously to let the child finish speaking without being interrupted early
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 10000L) // 10s min speech window
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000L) // 4s complete silence window
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000L) // 4s possible silence window
        }
        
        try {
            speechRecognizer?.setRecognitionListener(recognitionListener)
            speechRecognizer?.startListening(intent)
            isListening = true
            isSimulatingMic = true
            chatInput = ""
        } catch (e: Exception) {
            Log.e("VoiceChat", "Error starting speech recognizer", e)
            responseText = "Could not start mic listener: ${e.message}"
            isListening = false
            isSimulatingMic = false
        }
    }

    val stopListeningSpeech = {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("VoiceChat", "Error stopping speech recognizer", e)
        }
        isListening = false
        isSimulatingMic = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            startListeningSpeech()
        } else {
            responseText = when (langCode) {
                "te" -> "దయచేసి మైక్రోఫోన్ అనుమతి ఇవ్వండి! 🎙️"
                "ta" -> "தயவுசெய்து மைக்ரோஃபோன் அனுமதியை வழங்கவும்! 🎙️"
                "hi" -> "कृपया माइक्रोफ़ोन की अनुमति दें! 🎙️"
                "ar" -> "يرجى السماح بالوصول إلى الميكروفون! 🎙️"
                "kn" -> "దయవిట్టు ಮೈಕ್ರోಫೋನ್ ಅನುಮತಿ ನೀಡಿ! 🎙️"
                "ml" -> "ദയവായി മൈക്രോഫോൺ അനുമതി നൽകുക! 🎙️"
                else -> "Please grant microphone permission to talk! 🎙️"
            }
        }
    }

    Dialog(
        onDismissRequest = {
            viewModel.stopSpeech()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            AnimalBackgroundContainer(showAnimals = true) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = getVoiceChatText("title", langCode),
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.stopSpeech()
                                onDismiss()
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    
                    // Welcome Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = getVoiceChatText("welcome", langCode),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Animated Avatar Box
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        InteractiveAIAvatar(
                            isFemale = isAvatarFemale,
                            avatarType = avatarType,
                            expression = avatarExpression,
                            isSpeaking = avatarIsSpeaking,
                            action = avatarAction,
                            avatarSize = avatarSize,
                            spokenText = avatarText,
                            isMuted = isAvatarMuted,
                            onToggleMute = { viewModel.toggleAvatarMuted() },
                            onToggleGender = { viewModel.toggleAvatarGender() },
                            modifier = Modifier.size(180.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Speech output bubble
                    if (responseText.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                            ),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = responseText,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = getVoiceChatText("instruction", langCode),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick buttons LazyRow
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getVoiceChatText("quick_ask", langCode),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(quickPhrases) { pair ->
                                val (label, phrase) = pair
                                Card(
                                    modifier = Modifier
                                        .clickable { askGemini(phrase) }
                                        .testTag("quick_phrase_$label"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Custom input area (Mic simulation and TextField)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = {
                                Text(getVoiceChatText("placeholder", langCode))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .testTag("voice_chat_text_input"),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 2
                        )

                        // Send typed input
                        IconButton(
                            onClick = {
                                if (chatInput.isNotBlank()) {
                                    val userText = chatInput
                                    chatInput = ""
                                    askGemini(userText)
                                }
                            },
                            enabled = chatInput.isNotBlank() && !isThinking,
                            modifier = Modifier
                                .background(
                                    if (chatInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                                .testTag("voice_chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = if (chatInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Big Mic Recording button (Uses Android's SpeechRecognizer or fallback if unavailable)
                        IconButton(
                            onClick = {
                                if (!isThinking) {
                                    if (speechRecognizer == null || !SpeechRecognizer.isRecognitionAvailable(context)) {
                                        // Fallback toggle for mock simulation (configured with generous timeouts)
                                        if (isSimulatingMic) {
                                            isSimulatingMic = false
                                        } else {
                                            isSimulatingMic = true
                                            responseText = getVoiceChatText("listening", langCode)
                                            coroutineScope.launch {
                                                // 8-second delay to give them ample time to speak
                                                var timer = 0
                                                while (timer < 80 && isSimulatingMic) {
                                                    delay(100)
                                                    timer++
                                                }
                                                if (isSimulatingMic) {
                                                    isSimulatingMic = false
                                                    val mockVoiceResult = if (currentItem != null) {
                                                        getVoiceChatText("mock_story", langCode, currentItem.display)
                                                    } else {
                                                        getVoiceChatText("mock_joke", langCode)
                                                    }
                                                    askGemini(mockVoiceResult)
                                                }
                                            }
                                        }
                                    } else {
                                        // Real Speech-To-Text Recognition
                                        if (isListening) {
                                            stopListeningSpeech()
                                        } else {
                                            if (hasPermission) {
                                                startListeningSpeech()
                                            } else {
                                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .background(
                                    if (isSimulatingMic) Color.Red else MaterialTheme.colorScheme.errorContainer,
                                    CircleShape
                                )
                                .border(
                                    if (isSimulatingMic) BorderStroke(3.dp, Color.White) else BorderStroke(0.dp, Color.Transparent),
                                    CircleShape
                                )
                                .testTag("voice_chat_mic_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Speak now",
                                tint = if (isSimulatingMic) Color.White else MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            }
        }

        if (showLimitReachedDialog) {
            AlertDialog(
                onDismissRequest = { showLimitReachedDialog = false },
                title = {
                    Text(
                        text = "Today's free AI Teacher limit reached.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "To unlock another session with your AI Teacher, please select one of the safe options below:",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "(Current Diamonds: $diamonds 💎)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0288D1)
                        )
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (viewModel.spendDiamonds(10)) {
                                    showLimitReachedDialog = false
                                    bypassLimitCheck = true
                                    askGemini(pendingPromptText)
                                } else {
                                    viewModel.playWrongSound()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            enabled = diamonds >= 10,
                            modifier = Modifier.fillMaxWidth().testTag("use_diamonds_button")
                        ) {
                            Text("💎 Option 1: Use 10 Diamonds", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                isWatchingAd = true
                                adCountdown = 5
                                coroutineScope.launch {
                                    var currentContext = context
                                    var activity: Activity? = null
                                    while (currentContext is android.content.ContextWrapper) {
                                        if (currentContext is Activity) {
                                            activity = currentContext
                                            break
                                        }
                                        currentContext = currentContext.baseContext
                                    }

                                    if (activity != null) {
                                        com.example.data.AdMobHelper.showRewardedAd(
                                            activity = activity,
                                            onRewardEarned = {
                                                bypassLimitCheck = true
                                            },
                                            onAdClosed = {
                                                isWatchingAd = false
                                                showLimitReachedDialog = false
                                                viewModel.playSuccessSound()
                                                askGemini(pendingPromptText)
                                            }
                                        )
                                    } else {
                                        while (adCountdown > 0) {
                                            delay(1000)
                                            adCountdown--
                                        }
                                        isWatchingAd = false
                                        showLimitReachedDialog = false
                                        bypassLimitCheck = true
                                        viewModel.playSuccessSound()
                                        askGemini(pendingPromptText)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6)),
                            modifier = Modifier.fillMaxWidth().testTag("watch_ad_button")
                        ) {
                            Text("🎥 Option 2: Watch One Rewarded Ad", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        TextButton(
                            onClick = { showLimitReachedDialog = false },
                            modifier = Modifier.fillMaxWidth().testTag("cancel_limit_button")
                        ) {
                            Text("❌ Cancel", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {}
            )
        }

        if (isWatchingAd) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🎥🍿", fontSize = 80.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Safe Kids Advertisement 🧩\n(Google AdMob Auto-Optimized)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Show family-safe, child-appropriate categories.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator(
                        progress = adCountdown.toFloat() / 5f,
                        color = Color(0xFFFF9100),
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rewards unlock in $adCountdown seconds...",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

fun getVoiceChatText(key: String, langCode: String, currentItemDisplay: String? = null): String {
    return when (key) {
        "title" -> when (langCode) {
            "te" -> "🎙️ AI తో మాటలు"
            "ta" -> "🎙️ AI உடன் குரல் அரட்டை"
            "hi" -> "🎙️ AI के साथ वॉयस चैट"
            "ar" -> "🎙️ محادثة صوتية مع الذكاء الاصطناعي"
            "kn" -> "🎙️ AI ನೊಂದಿಗೆ ಧ್ವನಿ ಚಾಟ್"
            "ml" -> "🎙️ AI-യുമായി വോയ്‌സ് ചാറ്റ്"
            else -> "🎙️ Voice Chat with AI"
        }
        "thinking" -> when (langCode) {
            "te" -> "ఆలోచిస్తున్నాను... 🧠✨"
            "ta" -> "யோசிக்கிறேன்... 🧠✨"
            "hi" -> "सोच रहा हूँ... 🧠✨"
            "ar" -> "أفكر... 🧠✨"
            "kn" -> "ಯೋಚಿಸುತ್ತಿದ್ದೇನೆ... 🧠✨"
            "ml" -> "ആലോചിക്കുന്നു... 🧠✨"
            else -> "Thinking... 🧠✨"
        }
        "welcome" -> when (langCode) {
            "te" -> "నీకు నచ్చిన ప్రశ్న అడుగు, నేను జవాబు చెప్తాను! 🥰"
            "ta" -> "உங்களுக்கு பிடித்த கேள்வியைக் கேளுங்கள், நான் பதில் சொல்கிறேன்! 🥰"
            "hi" -> "मुझसे कोई भी सवाल पूछें! मैं आपको सिखाने के लिए यहाँ हूँ! 🥰"
            "ar" -> "اسألني أي سؤال! أنا هنا لأعلمك! 🥰"
            "kn" -> "ನಿನಗೆ ಇಷ್ಟವಾದ ಪ್ರಶ್ನೆ ಕೇಳು, ನಾನು ಉತ್ತರ ಹೇಳುವೆ! 🥰"
            "ml" -> "നിങ്ങൾക്ക് ഇഷ്ടമുള്ള ചോദ്യം ചോദിക്കൂ, ഞാൻ മറുപടി പറയാം! 🥰"
            else -> "Ask me any question! I'm here to teach you! 🥰"
        }
        "instruction" -> when (langCode) {
            "te" -> "నాతో మాట్లాడటానికి మైక్రోఫోన్ నొక్కండి! 👇"
            "ta" -> "என்னுடன் பேச மைக்ரோஃபோனைத் தட்டவும்! 👇"
            "hi" -> "मुझसे बात करने के लिए नीचे दिए गए माइक पर टैप करें! 👇"
            "ar" -> "اضغط على الميكروفون أدناه للتحدث معي! 👇"
            "kn" -> "ನನ್ನೊಂದಿಗೆ ಮಾತನಾಡಲು ಮೈಕ್ರೋಫೋನ್ ಟ್ಯಾಪ್ ಮಾಡಿ! 👇"
            "ml" -> "എന്നോട് സംസാരിക്കാൻ താഴെയുള്ള മൈക്രോഫോൺ ടാപ്പ് ചെയ്യുക! 👇"
            else -> "Tap the microphone below to talk to me! 👇"
        }
        "quick_ask" -> when (langCode) {
            "te" -> "💬 త్వరగా అడుగు:"
            "ta" -> "💬 விரைவாகக் கேள்:"
            "hi" -> "💬 तुरंत पूछें:"
            "ar" -> "💬 أسئلة سريعة:"
            "kn" -> "💬 ಶೀಘ್ರವಾಗಿ ಕೇಳಿ:"
            "ml" -> "💬 വേഗത്തിൽ ചോദിക്കൂ:"
            else -> "💬 Ask Quick:"
        }
        "placeholder" -> when (langCode) {
            "te" -> "ప్రశ్న టైప్ చేయి..."
            "ta" -> "கேள்வியைத் தட்டச்சு செய்க..."
            "hi" -> "सवाल टाइप करें..."
            "ar" -> "اكتب سؤالاً..."
            "kn" -> "ಪ್ರಶ್ನೆ ಟೈಪ್ ಮಾಡಿ..."
            "ml" -> "ചോദ്യം ടൈപ്പ് ചെയ്യുക..."
            else -> "Type a question..."
        }
        "listening" -> when (langCode) {
            "te" -> "నిన్ను వింటున్నాను... 🎤🔊"
            "ta" -> "உங்களைக் கேட்டுக்கொண்டிருக்கிறேன்... 🎤🔊"
            "hi" -> "सुन रहा हूँ... 🎤🔊"
            "ar" -> "أسمعك الآن... 🎤🔊"
            "kn" -> "ನಿಮ್ಮನ್ನು ಕೇಳುತ್ತಿದ್ದೇನೆ... 🎤🔊"
            "ml" -> "കേൾക്കുന്നുണ്ട്... 🎤🔊"
            else -> "Listening to you... 🎤🔊"
        }
        "fallback_no_speech" -> when (langCode) {
            "te" -> "నేను ఇంకా ఏమీ మాట్లాడలేదు ప్రియమైన స్నేహితుడా!"
            "ta" -> "நான் இன்னும் எதுவும் பேசவில்லை என் அன்பு நண்பரே!"
            "hi" -> "मैंने अभी तक कुछ नहीं बोला है, मेरे प्रिय मित्र!"
            "ar" -> "لم أقل أي شيء بعد يا صديقي العزيز!"
            "kn" -> "ನಾನು ಇನ್ನು ಏನೂ ಮಾತನಾಡಿಲ್ಲ ನನ್ನ ಪ್ರೀತಿಯ ಗೆಳೆಯ!"
            "ml" -> "ഞാൻ ഇതുവരെ ഒന്നും സംസാരിച്ചിട്ടില്ല എന്റെ പ്രിയ കൂട്ടുകാരാ!"
            else -> "I haven't said anything yet, my dear friend!"
        }
        "brain_offline" -> when (langCode) {
            "te" -> "క్షమించు, నా బ్రెయిన్ కనెక్ట్ అవ్వలేదు!"
            "ta" -> "மன்னிக்கவும், எனது மூளை இணைக்கப்படவில்லை!"
            "hi" -> "क्षमा करें, मेरा दिमाग ऑफ़लाइन है!"
            "ar" -> "معذرةً! عقلي غير متصل بالإنترنت حالياً!"
            "kn" -> "ಕ್ಷಮಿಸಿ, ನನ್ನ ಮೆದುಳು ಆಫ್‌ಲೈನ್ ಆಗಿದೆ!"
            "ml" -> "ಕ್ಷಮಿಕണം, എന്റെ തലച്ചോറ് ഓഫ്‌ലൈനാണ്!"
            else -> "Oops! My brain is offline right now!"
        }
        "mock_story" -> {
            val item = currentItemDisplay ?: ""
            when (langCode) {
                "te" -> "మనం ఇప్పుడు నేర్చుకుంటున్న '$item' గురించి చెప్పు!"
                "ta" -> "நாம் இப்போது கற்றுக் கொள்ளும் '$item' பற்றி கூறுங்கள்!"
                "hi" -> "हम अभी जो '$item' सीख रहे हैं, उसके बारे में बताएं!"
                "ar" -> "حدثني عن '$item' الذي نتعلمه الآن!"
                "kn" -> "ನಾವು ಈಗ ಕಲಿಯುತ್ತಿರುವ '$item' ಬಗ್ಗೆ ಹೇಳಿ!"
                "ml" -> "നമ്മൾ ഇപ്പോൾ പഠിച്ചുകൊണ്ടിരിക്കുന്ന '$item'-നെ കുറിച്ച് പറയൂ!"
                else -> "Tell me a fun story about '$item'!"
            }
        }
        "mock_joke" -> when (langCode) {
            "te" -> "నాకు ఒక పిల్లల జోక్ చెప్పు!"
            "ta" -> "எனக்கு ஒரு குழந்தைகளுக்கான நகைச்சுவை கூறுங்கள்!"
            "hi" -> "मुझे बच्चों का कोई चुटकुला सुनाओ!"
            "ar" -> "أخبرني بنكتة مضحكة للأطفال!"
            "kn" -> "ನನಗೊಂದು ಮಕ್ಕಳ ಜೋಕ್ ಹೇಳಿ!"
            "ml" -> "എനിക്കൊരു കുട്ടികളുടെ തമാശ പറയൂ!"
            else -> "Tell me a silly kids joke!"
        }
        "not_hear" -> when (langCode) {
            "te" -> "నేను నిన్ను సరిగ్గా వినలేదు!"
            "ta" -> "நான் உங்களை சரியாகக் கேட்கவில்லை!"
            "hi" -> "मैं आपकी बात ठीक से सुन नहीं पाया!"
            "ar" -> "لم أسمعك جيداً!"
            "kn" -> "ನಮಗೆ ಸರಿಯಾಗಿ ಕೇಳಿಸಲಿಲ್ಲ!"
            "ml" -> "എനിക്ക് വ്യക്തമായി കേൾക്കാൻ പറ്റിയില്ല!"
            else -> "I didn't quite hear you!"
        }
        "silly_response" -> when (langCode) {
            "te" -> "సర్వర్ నుండి తప్పుడు సమాధానం వచ్చింది!"
            "ta" -> "சேவையகத்திலிருந்து ஒரு தவறான பதில் வந்தது!"
            "hi" -> "सर्वर से गड़बड़ उत्तर मिला!"
            "ar" -> "تلقيت ردًا غريبًا من الخادم!"
            "kn" -> "ಸರ್ವರ್‌ನಿಂದ ತಪ್ಪಾದ ಪ್ರತಿಕ್ರಿಯೆ ಬಂದಿದೆ!"
            "ml" -> "സർവറിൽ നിന്ന് തെറ്റായ മറുപടി ലഭിച്ചു!"
            else -> "Got a silly response from the server!"
        }
        else -> ""
    }
}
