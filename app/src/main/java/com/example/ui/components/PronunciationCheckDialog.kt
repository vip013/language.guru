package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.BuildConfig
import com.example.data.LearningItem
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

enum class PronounceState {
    IDLE,
    RECORDING,
    UPLOADING,
    RESULT_SUCCESS,
    RESULT_ERROR
}

enum class PracticeMode {
    REALTIME_BROWSER,
    AI_EVALUATION
}

fun calculateAccuracy(target: String, spoken: String): Int {
    val cleanTarget = target.trim().lowercase().replace(Regex("[^\\w\\s\\p{L}]"), "")
    val cleanSpoken = spoken.trim().lowercase().replace(Regex("[^\\w\\s\\p{L}]"), "")
    
    if (cleanTarget.isEmpty() || cleanSpoken.isEmpty()) return 0
    if (cleanTarget == cleanSpoken) return 100
    
    val targetWords = cleanTarget.split(Regex("\\s+")).filter { it.isNotEmpty() }
    val spokenWords = cleanSpoken.split(Regex("\\s+")).filter { it.isNotEmpty() }
    
    var matchedWordsCount = 0
    for (tw in targetWords) {
        if (spokenWords.contains(tw)) {
            matchedWordsCount++
        }
    }
    
    val basePercentage = (matchedWordsCount.toFloat() / targetWords.size.toFloat() * 100).toInt()
    
    val lenT = cleanTarget.length
    val lenS = cleanSpoken.length
    val dp = Array(lenT + 1) { IntArray(lenS + 1) }
    for (i in 0..lenT) dp[i][0] = i
    for (j in 0..lenS) dp[0][j] = j
    
    for (i in 1..lenT) {
        for (j in 1..lenS) {
            val cost = if (cleanTarget[i - 1] == cleanSpoken[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,
                dp[i][j - 1] + 1,
                dp[i - 1][j - 1] + cost
            )
        }
    }
    val distance = dp[lenT][lenS]
    val maxLen = maxOf(lenT, lenS)
    val editPercentage = (((maxLen - distance).toFloat() / maxLen.toFloat()) * 100).toInt()
    
    return maxOf(basePercentage, editPercentage).coerceIn(0, 100)
}

@Composable
fun PronunciationCheckDialog(
    viewModel: LearningViewModel,
    targetItem: LearningItem,
    langCode: String,
    voiceCharacter: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var practiceMode by remember { mutableStateOf(PracticeMode.REALTIME_BROWSER) }
    var browserTranscript by remember { mutableStateOf("") }
    var browserAccuracyScore by remember { mutableStateOf(0) }
    var isListeningBrowser by remember { mutableStateOf(false) }
    var isRecognizerReady by remember { mutableStateOf(false) }
    var browserPlaySuccessSoundTriggered by remember { mutableStateOf(false) }

    val recognizerHelper = remember {
        WebSpeechRecognizerHelper(
            context = context,
            onReady = {
                isRecognizerReady = true
            },
            onListeningStarted = {
                isListeningBrowser = true
            },
            onListeningStopped = {
                isListeningBrowser = false
            },
            onResult = { finalText, interimText ->
                val transcript = if (finalText.isNotEmpty()) finalText else interimText
                if (transcript.isNotEmpty()) {
                    browserTranscript = transcript
                    val score = calculateAccuracy(targetItem.display, transcript)
                    browserAccuracyScore = score
                    if (score >= 80 && !browserPlaySuccessSoundTriggered) {
                        browserPlaySuccessSoundTriggered = true
                        viewModel.playCorrectSound()
                    }
                }
            },
            onError = { error ->
                Log.e("PronounceCheck", "Browser speech error: $error")
                isListeningBrowser = false
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            recognizerHelper.shutdown()
        }
    }

    var pronounceState by remember { mutableStateOf(PronounceState.IDLE) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    var feedbackText by remember { mutableStateOf("") }
    var isSpeakingFeedback by remember { mutableStateOf(false) }
    var accuracyScore by remember { mutableStateOf(100) }

    // MediaRecorder & file management
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordFile by remember { mutableStateOf<File?>(null) }
    var recordingDuration by remember { mutableStateOf(0) } // in seconds

    val isMotu = voiceCharacter == "motu"

    // Animation for mic pulse
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Stop recording helper
    fun stopRecording(isCancelled: Boolean = false) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("PronounceCheck", "Error stopping media recorder", e)
        }
        mediaRecorder = null

        if (isCancelled) {
            pronounceState = PronounceState.IDLE
            recordFile?.delete()
            recordFile = null
        }
    }

    // Auto-stop recording after 4 seconds
    LaunchedEffect(pronounceState) {
        if (pronounceState == PronounceState.RECORDING) {
            recordingDuration = 0
            while (recordingDuration < 4) {
                delay(1000)
                recordingDuration++
            }
            if (pronounceState == PronounceState.RECORDING) {
                stopRecording(isCancelled = false)
                pronounceState = PronounceState.UPLOADING
            }
        }
    }

    // Start recording function
    fun startRecording() {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        try {
            val file = File(context.cacheDir, "pronunciation_practice.3gp")
            if (file.exists()) file.delete()
            recordFile = file

            val recorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            pronounceState = PronounceState.RECORDING
            feedbackText = ""
            viewModel.playClickSound()
        } catch (e: Exception) {
            Log.e("PronounceCheck", "Failed to start MediaRecorder", e)
            feedbackText = "Failed to start voice recorder. Please check mic permissions!"
            pronounceState = PronounceState.IDLE
        }
    }

    // Call Gemini API to analyze the audio
    fun checkPronunciationWithGemini() {
        val file = recordFile
        if (file == null || !file.exists()) {
            pronounceState = PronounceState.IDLE
            return
        }

        pronounceState = PronounceState.UPLOADING

        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Read 3gp audio bytes and base64 encode
                val audioBytes = file.readBytes()
                val base64Audio = Base64.encodeToString(audioBytes, Base64.NO_WRAP)

                val characterName = if (isMotu) "Motu (your jolly companion)" else "Bhalu Bear (your school teacher)"
                
                val promptText = """
                    You are acting as $characterName, a highly encouraging kids language tutor.
                    A kid is trying to pronounce/speak the word/letter "${targetItem.display}" (which is pronounced as/associated with "${targetItem.subtitle}").
                    Listen to the attached audio recording of the kid's voice.
                    
                    Evaluate their pronunciation:
                    - Be supportive but realistic and precise. Rate their pronunciation on an honest scale of 0 to 100 for "accuracyScore", based on how clearly and correctly they pronounced the target "${targetItem.display}".
                    - If the "accuracyScore" is 70 or higher, set "isCorrect" to true.
                    - If the "accuracyScore" is less than 70, you MUST set "isCorrect" to false.
                    
                    You MUST respond with a valid JSON object containing exactly these keys:
                    {
                      "isCorrect": true or false,
                      "feedback": "A short, cheerful, encouraging sentence of feedback with cute emojis. The selected kid's language is '$langCode'. You MUST write the feedback entirely in '$langCode' (e.g. if 'te', write in sweet Telugu; if 'ta', write in Tamil; if 'hi', write in Hindi; if 'kn', write in Kannada; if 'ml', write in Malayalam; if 'ar', write in Arabic; if 'bn', write in Bengali; if 'mr', write in Marathi; if 'gu', write in Gujarati; if 'en', write in English). If they scored below 70, gently ask them to try again entirely in '$langCode' to reach at least 70%. Do NOT use any markdown symbols like double asterisks (**) or single asterisks (*) anywhere in the feedback string.",
                      "accuracyScore": an integer from 0 to 100 based on pronunciation accuracy
                    }
                    
                    Strictly return ONLY the raw JSON object inside triple backticks (```json ... ```). No other conversational text.
                """.trimIndent()

                val requestJson = JSONObject().apply {
                    val contentsArray = org.json.JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = org.json.JSONArray().apply {
                                val textPart = JSONObject().apply {
                                    put("text", promptText)
                                }
                                put(textPart)

                                val audioPart = JSONObject().apply {
                                    val inlineDataObj = JSONObject().apply {
                                        put("mimeType", "audio/3gpp")
                                        put("data", base64Audio)
                                    }
                                    put("inlineData", inlineDataObj)
                                }
                                put(audioPart)
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
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("PronounceCheck", "Failed to contact Gemini API", e)
                        coroutineScope.launch {
                            pronounceState = PronounceState.RESULT_ERROR
                            feedbackText = "Error hearing you, buddy! Let's try again!"
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use { res ->
                            val bodyStr = res.body?.string() ?: ""
                            if (res.isSuccessful && bodyStr.isNotEmpty()) {
                                try {
                                    val jsonRes = JSONObject(bodyStr)
                                    val candidates = jsonRes.optJSONArray("candidates")
                                    val firstCandidate = candidates?.optJSONObject(0)
                                    val content = firstCandidate?.optJSONObject("content")
                                    val parts = content?.optJSONArray("parts")
                                    val firstPart = parts?.optJSONObject(0)
                                    var text = firstPart?.optString("text") ?: ""

                                    if (text.contains("```json")) {
                                        text = text.substringAfter("```json").substringBefore("```").trim()
                                    } else if (text.contains("```")) {
                                        text = text.substringAfter("```").substringBefore("```").trim()
                                    }

                                    val parsedJson = JSONObject(text)
                                    val score = parsedJson.optInt("accuracyScore", 80)
                                    val isCorrect = score >= 70
                                    val feedback = if (!isCorrect) {
                                        getLocalizedTryAgainMessage(langCode)
                                    } else {
                                        parsedJson.optString("feedback", "Nice effort!")
                                    }

                                    coroutineScope.launch {
                                        feedbackText = feedback
                                        accuracyScore = score
                                        if (isCorrect) {
                                            pronounceState = PronounceState.RESULT_SUCCESS
                                            viewModel.playCorrectSound()
                                        } else {
                                            pronounceState = PronounceState.RESULT_ERROR
                                            viewModel.playWrongSound()
                                        }

                                        // Speak feedback
                                        viewModel.speakCustomText(feedback) {
                                            isSpeakingFeedback = false
                                        }
                                        isSpeakingFeedback = true
                                    }
                                } catch (ex: Exception) {
                                    Log.e("PronounceCheck", "Parsing error", ex)
                                    coroutineScope.launch {
                                        pronounceState = PronounceState.RESULT_ERROR
                                        feedbackText = "Hmm, Bhalu Teacher wants to listen again! Try once more! 🐻🎒"
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    pronounceState = PronounceState.RESULT_ERROR
                                    feedbackText = "My ears got clogged! Speak closer to the microphone, friend!"
                                }
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                Log.e("PronounceCheck", "File preparation error", ex)
                coroutineScope.launch {
                    pronounceState = PronounceState.RESULT_ERROR
                    feedbackText = "Audio recording failed. Let's start over!"
                }
            }
        }
    }

    // Effect to monitor when recording naturally stops
    LaunchedEffect(pronounceState) {
        if (pronounceState == PronounceState.UPLOADING) {
            checkPronunciationWithGemini()
        }
    }

    Dialog(
        onDismissRequest = {
            stopRecording(isCancelled = true)
            viewModel.stopSpeech()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color.Transparent
        ) {
            AnimalBackgroundContainer(showAnimals = true) {
                Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎤", fontSize = 28.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = if (langCode == "te") "పలికి చూడు (Speaking practice)" else "Speaking Practice",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = {
                            stopRecording(isCancelled = true)
                            viewModel.stopSpeech()
                            onDismiss()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mascot Teacher Board Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMotu) Color(0xFFFFF3E0) else Color(0xFFE8F5E9)
                    ),
                    border = BorderStroke(2.dp, if (isMotu) Color(0xFFFFB74D) else Color(0xFF81C784))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isMotu) "👨‍🦲" else "🐻",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = if (isMotu) "Motu's Microphone 🎙️" else "Bhalu Bear's Sound Board 🐾",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isMotu) Color(0xFFE65100) else Color(0xFF2E7D32)
                            )
                            Text(
                                text = if (practiceMode == PracticeMode.REALTIME_BROWSER) {
                                    if (langCode == "te") {
                                        "రియల్-టైమ్ మైక్: కింద ఉన్న మైక్రోఫోన్ నొక్కి, గట్టిగా '${targetItem.display}' అని పలుకు బంగారం! మీరు పలికేది ఇక్కడ చూపిస్తాం!"
                                    } else {
                                        "Real-time Practice: Tap the microphone and say '${targetItem.display}'. Watch your words and accuracy update live!"
                                    }
                                } else {
                                    if (langCode == "te") {
                                        "AI మూల్యాంకనం: కింద ఉన్న మైక్రోఫోన్ నొక్కి, గట్టిగా '${targetItem.display}' అని చెప్పి రికార్డ్ చేయి బంగారం! ఆపై చూద్దాం!"
                                    } else {
                                        "AI Evaluation: Press the microphone below and say '${targetItem.display}' aloud for the AI Teacher!"
                                    }
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Beautiful interactive Practice Mode selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val browserSelected = practiceMode == PracticeMode.REALTIME_BROWSER
                    val geminiSelected = practiceMode == PracticeMode.AI_EVALUATION
                    
                    // Browser Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (browserSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { 
                                practiceMode = PracticeMode.REALTIME_BROWSER
                                feedbackText = ""
                                browserTranscript = ""
                                browserAccuracyScore = 0
                                browserPlaySuccessSoundTriggered = false
                                viewModel.stopSpeech()
                                stopRecording(isCancelled = true)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌐", fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp))
                            Text(
                                text = if (langCode == "te") "లైవ్ ఉచ్చారణ (Browser)" else "Real-time Practice",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (browserSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    // Gemini Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (geminiSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { 
                                practiceMode = PracticeMode.AI_EVALUATION
                                feedbackText = ""
                                viewModel.stopSpeech()
                                recognizerHelper.stopListening()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐻", fontSize = 16.sp, modifier = Modifier.padding(end = 4.dp))
                            Text(
                                text = if (langCode == "te") "AI గురువు (Gemini)" else "AI Evaluation",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (geminiSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Large Pronounce target card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val displayFontSize = remember(targetItem.display) {
                            val len = targetItem.display.length
                            if (len <= 2) {
                                76.sp
                            } else if (len <= 5) {
                                54.sp
                            } else if (len <= 8) {
                                38.sp
                            } else if (len <= 12) {
                                28.sp
                            } else {
                                22.sp
                            }
                        }
                        Text(
                            text = targetItem.display,
                            fontSize = displayFontSize,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (langCode == "te") "సహాయం: ${targetItem.subtitle}" else "Help: ${targetItem.subtitle}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                if (practiceMode == PracticeMode.REALTIME_BROWSER) {
                    // Real-Time Browser Speech Recognition UI
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isListeningBrowser) {
                            // Pulsing green background rings
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00C853).copy(alpha = 0.25f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(pulseScale * 0.85f)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00C853).copy(alpha = 0.4f))
                            )
                        }

                        val micColor = if (isListeningBrowser) Color(0xFF00C853) else MaterialTheme.colorScheme.primary

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(micColor)
                                .clickable {
                                    if (isListeningBrowser) {
                                        recognizerHelper.stopListening()
                                    } else {
                                        browserTranscript = ""
                                        browserAccuracyScore = 0
                                        browserPlaySuccessSoundTriggered = false
                                        recognizerHelper.startListening(langCode)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isListeningBrowser) Icons.Default.MicNone else Icons.Default.Mic,
                                contentDescription = "Browser Microphone Action",
                                tint = Color.White,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isListeningBrowser) {
                            if (langCode == "te") "నేను వింటున్నాను... మాట్లాడు బంగారం! 🎙️" else "Listening... Speak now! 🎙️"
                        } else {
                            if (langCode == "te") "మాట్లాడటానికి మైక్రోఫోన్ నొక్కు 🎙️" else "Tap microphone to speak! 🎙️"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isListeningBrowser) Color(0xFF00C853) else MaterialTheme.colorScheme.onBackground
                    )

                    // Live Transcript and Accuracy feedback card
                    AnimatedVisibility(
                        visible = browserTranscript.isNotEmpty(),
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        val isExcellent = browserAccuracyScore >= 80
                        val isOk = browserAccuracyScore >= 50

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isExcellent) Color(0xFFE8F5E9) else if (isOk) Color(0xFFFFF3E0) else Color(0xFFFFEBEE)
                            ),
                            border = BorderStroke(
                                2.5.dp,
                                if (isExcellent) Color(0xFF4CAF50) else if (isOk) Color(0xFFFF9800) else Color(0xFFF44336)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isExcellent) "🥳 ⭐" else if (isOk) "👍 😊" else "🌱 ✨",
                                            fontSize = 22.sp,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = if (isExcellent) {
                                                if (langCode == "te") "అద్భుతమైన ఉచ్చారణ! 🌟" else "Excellent Speaking! 🌟"
                                            } else if (isOk) {
                                                if (langCode == "te") "మంచి ప్రయత్నం! దాదాపు వచ్చేసింది! ✨" else "Good Try! Almost there! ✨"
                                            } else {
                                                if (langCode == "te") "నెమ్మదిగా, స్పష్టంగా చెప్పి చూడు! 🌱" else "Keep practicing! Speak clearly! 🌱"
                                            },
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isExcellent) Color(0xFF2E7D32) else if (isOk) Color(0xFFE65100) else Color(0xFFC62828)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isExcellent) Color(0xFFC8E6C9) else if (isOk) Color(0xFFFFE0B2) else Color(0xFFFFCDD2))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Accuracy: $browserAccuracyScore%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isExcellent) Color(0xFF2E7D32) else if (isOk) Color(0xFFE65100) else Color(0xFFC62828)
                                        )
                                    }
                                }

                                Divider(
                                    color = (if (isExcellent) Color(0xFF4CAF50) else if (isOk) Color(0xFFFF9800) else Color(0xFFF44336)).copy(alpha = 0.15f),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                Text(
                                    text = if (langCode == "te") "మీరు పలికిన పదం:" else "You spoke:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = "\"$browserTranscript\"",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    // Hear Target word button
                                    Button(
                                        onClick = {
                                            viewModel.speakCustomText(targetItem.display) {}
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Hear Correct Pronunciation", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (langCode == "te") "సరైన ఉచ్చారణ విను" else "Listen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // AI Evaluation Practice UI (Original MediaRecorder + Gemini)
                    Box(
                        modifier = Modifier.size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (pronounceState == PronounceState.RECORDING) {
                            // Pulsing background rings
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(pulseScale)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935).copy(alpha = 0.25f))
                            )
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(pulseScale * 0.85f)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935).copy(alpha = 0.4f))
                            )
                        }

                        // Main Mic Circle
                        val micColor = when (pronounceState) {
                            PronounceState.RECORDING -> Color(0xFFE53935) // Deep Red recording
                            PronounceState.UPLOADING -> Color(0xFFFFA000) // Orange loading
                            else -> MaterialTheme.colorScheme.primary
                        }

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(micColor)
                                .clickable(enabled = pronounceState == PronounceState.IDLE || pronounceState == PronounceState.RESULT_SUCCESS || pronounceState == PronounceState.RESULT_ERROR) {
                                    startRecording()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (pronounceState == PronounceState.UPLOADING) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(40.dp))
                            } else {
                                Icon(
                                    imageVector = if (pronounceState == PronounceState.RECORDING) Icons.Default.MicNone else Icons.Default.Mic,
                                    contentDescription = "Microphone Action",
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mic Action State Subtitle
                    Text(
                        text = when (pronounceState) {
                            PronounceState.RECORDING -> {
                                if (langCode == "te") "రికార్డ్ అవుతోంది... (${recordingDuration} సెకన్లు) 🔴" else "Recording... (${recordingDuration}s) 🔴"
                            }
                            PronounceState.UPLOADING -> {
                                if (langCode == "te") "పరిశీలిస్తున్నాము... 🧠🔍" else "Evaluating pronunciation... 🧠🔍"
                            }
                            else -> {
                                if (langCode == "te") "మాట్లాడటానికి మైక్రోఫోన్ నొక్కు 🎙️" else "Tap microphone to speak! 🎙️"
                            }
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pronounceState == PronounceState.RECORDING) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onBackground
                    )

                    if (pronounceState == PronounceState.RECORDING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                stopRecording(isCancelled = false)
                                pronounceState = PronounceState.UPLOADING
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Done speaking")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (langCode == "te") "నేను చెప్పేసాను! 👍" else "Done Speaking! 👍", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // AI Feedback Result banners (Confetti-like Stars / Wrong tries)
                    AnimatedVisibility(
                        visible = (pronounceState == PronounceState.RESULT_SUCCESS || pronounceState == PronounceState.RESULT_ERROR) && feedbackText.isNotEmpty(),
                        enter = slideInVertically { it } + fadeIn(),
                        exit = slideOutVertically { it } + fadeOut()
                    ) {
                        val isGood = pronounceState == PronounceState.RESULT_SUCCESS

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 28.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isGood) Color(0xFFFFFDE7) else Color(0xFFFFEBEE) // Yellow golden or light red
                            ),
                            border = BorderStroke(
                                2.5.dp,
                                if (isGood) Color(0xFFFBC02D) else Color(0xFFC62828)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isGood) "⭐" else "❌",
                                            fontSize = 24.sp,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = if (isGood) {
                                                if (langCode == "te") "మంచి ప్రయత్నం! శభాష్! ⭐" else "Amazing Speaking! ⭐"
                                            } else {
                                                if (langCode == "te") "మళ్లీ నేర్చుకుందాం! ప్రయత్నించు! ✨" else "Let's learn and try again! ✨"
                                            },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isGood) Color(0xFFF57F17) else Color(0xFFB71C1C)
                                        )
                                    }

                                    // Accuracy score badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isGood) Color(0xFFFFF59D) else Color(0xFFFFCDD2))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Accuracy: $accuracyScore%",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isGood) Color(0xFFF57F17) else Color(0xFFB71C1C)
                                        )
                                    }
                                }

                                Divider(
                                    color = (if (isGood) Color(0xFFFBC02D) else Color(0xFFC62828)).copy(alpha = 0.15f),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )

                                // Feedback content Text
                                Text(
                                    text = feedbackText,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Read feedback speech button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    if (isSpeakingFeedback) {
                                        IconButton(
                                            onClick = {
                                                viewModel.stopSpeech()
                                                isSpeakingFeedback = false
                                            },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                                        ) {
                                            Icon(imageVector = Icons.Default.VolumeOff, contentDescription = "Stop Speech", tint = MaterialTheme.colorScheme.onErrorContainer)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                viewModel.speakCustomText(feedbackText) {
                                                    isSpeakingFeedback = false
                                                }
                                                isSpeakingFeedback = true
                                            },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                        ) {
                                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read Aloud", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
            }
        }
    }
}

private fun getLocalizedTryAgainMessage(langCode: String): String = when (langCode) {
    "te" -> "మళ్లీ ప్రయత్నించు బంగారం! 90% కంటే తక్కువ ఖచ్చితత్వం ఉంది. నువ్వు చేయగలవు! 🌱"
    "ta" -> "மீண்டும் முயலுங்கள் செல்லமே! துல்லியம் 90% க்கும் குறைவாக உள்ளது. உங்களால் முடியும்! 🌱"
    "hi" -> "फिर से कोशिश करो प्यारे बच्चे! सटीकता 90% से कम है। आप कर सकते हैं! 🌱"
    "kn" -> "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸು ಕಂದಾ! ನಿಖರತೆ 90% ಕ್ಕಿಂತ ಕಡಿಮೆಯಿದೆ. ನೀನು ಮಾಡಬಲ್ಲೆ! 🌱"
    "ml" -> "വീണ്ടും ശ്രമിക്കൂ കുട്ടീ! കൃത്യത 90%-ൽ താഴെയാണ്. നിനക്ക് സാധിക്കും! 🌱"
    "bn" -> "আবার চেষ্টা করো সোনা! নির্ভুলता 90% এর নিচে। তুমি পারবে! 🌱"
    "mr" -> "पुन्हा प्रयत्न करा बाळा! अचूकता 90% पेक्षा कमी आहे. तू करू शकतोस! 🌱"
    "gu" -> "ફરીથી પ્રયત્ن કર બેટા! ચોકસાઈ 90% થી ઓછી છે. તમે કરી શકો છો! 🌱"
    "ar" -> "حاول مرة أخرى يا بطل! الدقة أقل من 90٪. يمكنك القيام بذلك! 🌱"
    else -> "Try again, buddy! Accuracy is below 90%. You can do it! 🌱"
}
