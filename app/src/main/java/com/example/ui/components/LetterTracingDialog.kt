package com.example.ui.components

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.data.LearningItem
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

enum class TracingState {
    IDLE,
    CHECKING,
    RESULT_SUCCESS,
    RESULT_ERROR
}

data class DrawingPoint(val x: Float, val y: Float)
data class DrawingStroke(val points: List<DrawingPoint>, val color: Color, val width: Float)

fun getCleanTracingCharacter(item: LearningItem): String {
    // 1. Shapes outline mapping based on visual icon / emoji
    when (item.visualEmoji) {
        "🔺" -> return "△"
        "🔴" -> return "◯"
        "🟩" -> return "□"
        "█" -> return "▭"
        "⭐" -> return "☆"
    }

    val display = item.cleanTarget

    // 2. Composite letter with word (e.g. "అ - అమ్మ" or "ا - أَسَد") -> extract root letter "అ" or "ا"
    if (display.contains(" - ")) {
        val extracted = display.substringBefore(" - ").trim()
        if (extracted.isNotEmpty()) return extracted
    }

    // 3. Parenthesized mathematical or phonetic glyph (e.g. "కూడిక (+)") -> extract "+"
    if (display.contains("(") && display.contains(")")) {
        val extracted = display.substringAfter("(").substringBefore(")").trim()
        if (extracted.isNotEmpty() && extracted.length <= 2) {
            return extracted
        }
    }

    return display
}

@Composable
fun LetterTracingDialog(
    viewModel: LearningViewModel,
    targetItem: LearningItem,
    langCode: String,
    voiceCharacter: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val targetText = remember(targetItem) { getCleanTracingCharacter(targetItem) }
    
    var tracingState by remember { mutableStateOf(TracingState.IDLE) }
    var chosenColor by remember { mutableStateOf(Color(0xFF26A69A)) } // Default chalk green-cyan
    var strokes = remember { mutableStateListOf<DrawingStroke>() }
    val currentPoints = remember { mutableStateListOf<DrawingPoint>() }
    
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var feedbackText by remember { mutableStateOf("") }
    var isSpeakingFeedback by remember { mutableStateOf(false) }
    var accuracyScore by remember { mutableStateOf(100) }
    
    val isPremium by viewModel.isPremium.collectAsState()
    val diamonds by viewModel.diamonds.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()

    var attemptCount by remember { mutableStateOf(0) }
    var showInterventionDialog by remember { mutableStateOf(false) }

    val isMotu = voiceCharacter == "motu"
    
    // Auto-tracing demo state
    var showDemoStroke by remember { mutableStateOf(false) }
    val demoProgress = remember { Animatable(0f) }
    
    LaunchedEffect(showDemoStroke) {
        if (showDemoStroke) {
            demoProgress.snapTo(0f)
            demoProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(2200, easing = FastOutSlowInEasing)
            )
            showDemoStroke = false
        }
    }

    // Normalized & Tolerant Handwriting Validation Pipeline
    fun checkTracingWithGemini() {
        val width = canvasSize.width
        val height = canvasSize.height
        if (width <= 0 || height <= 0 || strokes.isEmpty()) {
            return
        }

        // 1. Check for empty or accidental mark early
        if (HandwritingRecognitionHelper.isDrawingEmptyOrAccidental(strokes)) {
            val emptyMsg = com.example.data.CentralizedContentRepository.getLocalizedUiString("tracing_empty_drawing", langCode)
            tracingState = TracingState.RESULT_ERROR
            feedbackText = emptyMsg
            accuracyScore = 0
            viewModel.playWrongSound()
            attemptCount++
            if (attemptCount >= 4 && !isPremium) {
                showInterventionDialog = true
            }
            viewModel.speakCustomText(emptyMsg) {
                isSpeakingFeedback = false
            }
            isSpeakingFeedback = true
            return
        }
        
        tracingState = TracingState.CHECKING
        feedbackText = ""
        
        coroutineScope.launch(Dispatchers.IO) {
            // 2. Perform fast local structural shape evaluation
            val localResult = HandwritingRecognitionHelper.evaluateStructuralSimilarity(targetText, strokes)
            val langName = HandwritingRecognitionHelper.getLanguageName(langCode)

            try {
                // 3. Generate high-clarity dual panel comparison bitmap (Target vs Normalized Learner)
                val comparisonBitmap = HandwritingRecognitionHelper.createNormalizedComparisonBitmap(
                    targetText = targetText,
                    strokes = strokes,
                    langName = langName
                )
                
                val outputStream = ByteArrayOutputStream()
                comparisonBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                comparisonBitmap.recycle()
                
                // Prompt instructing Gemini with tolerant handwriting recognition criteria
                val characterName = if (isMotu) "Motu (the kid-friendly buddy)" else "Bhalu Bear (the warm school teacher)"
                val promptText = """
                    You are acting as $characterName, an encouraging teacher evaluating handwriting practice.
                    Target character / symbol: "$targetText"
                    Language / Writing System: $langName (code: '$langCode')

                    The image contains two panels:
                    - LEFT: Target character reference "$targetText".
                    - RIGHT: Learner's actual handwritten drawing (centered and normalized).

                    EVALUATION CRITERIA (HANDWRITING RECOGNITION, NOT RIGID OVERLAY TRACING):
                    1. Evaluate whether the learner's drawing recognizably represents the target character "$targetText".
                    2. Allow natural handwriting variations:
                       - Do NOT require exact font matching or pixel-by-pixel alignment.
                       - Allow variations in stroke thickness, size, minor rotation or slant, stroke spacing, and personal handwriting style.
                       - In Indian scripts (Telugu, Tamil, Hindi, Kannada, Malayalam, Bengali, etc.), Arabic, or English, children often write curves, loops, or dots with natural slight imperfections. If the character is recognizable as the target, ACCEPT IT.
                    3. Scoring rules:
                       - Clearly matching target character or natural variation: set "isCorrect": true, "accuracyScore": 65-95.
                       - Slightly imperfect handwriting recognizably forming the target: set "isCorrect": true, "accuracyScore": 55-75.
                       - Clearly a different character or random scribble: set "isCorrect": false, "accuracyScore": 15-40.
                       - Empty or accidental dot: set "isCorrect": false, "accuracyScore": 0-10.
                    
                    You MUST respond with a valid JSON object containing exactly these keys:
                    {
                      "isCorrect": true or false,
                      "feedback": "A short, cheerful, encouraging sentence with cute emojis in '$langCode' (e.g. if 'te', write in sweet Telugu; if 'ta', write in Tamil; if 'hi', write in Hindi; if 'kn', write in Kannada; if 'ml', write in Malayalam; if 'ar', write in Arabic; if 'en', write in English). If incorrect, gently encourage them to try '$targetText' again in '$langCode'. Do NOT use any asterisks.",
                      "accuracyScore": integer from 0 to 100
                    }
                    
                    Strictly return ONLY the raw JSON object inside triple backticks (```json ... ```) with no other text.
                """.trimIndent()
                
                val requestJson = JSONObject().apply {
                    val contentsArray = org.json.JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = org.json.JSONArray().apply {
                                val textPart = JSONObject().apply {
                                    put("text", promptText)
                                }
                                put(textPart)
                                
                                val imagePart = JSONObject().apply {
                                    val inlineDataObj = JSONObject().apply {
                                        put("mimeType", "image/jpeg")
                                        put("data", base64Image)
                                    }
                                    put("inlineData", inlineDataObj)
                                }
                                put(imagePart)
                            }
                            put("parts", partsArray)
                        }
                        put(contentObj)
                    }
                    put("contents", contentsArray)
                }

                val client = OkHttpClient.Builder()
                    .connectTimeout(25, TimeUnit.SECONDS)
                    .readTimeout(25, TimeUnit.SECONDS)
                    .writeTimeout(25, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("TracingGemini", "Network error in tracing check, falling back to local structural evaluation", e)
                        // Seamless local structural fallback
                        val isCorrect = localResult.isCorrect
                        val score = if (isCorrect) maxOf(localResult.score, 70) else minOf(localResult.score, 40)
                        val feedback = if (isCorrect) getLocalizedSuccessMessage(langCode) else getLocalizedTryAgainMessage(langCode)

                        coroutineScope.launch {
                            feedbackText = feedback
                            accuracyScore = score
                            if (isCorrect) {
                                tracingState = TracingState.RESULT_SUCCESS
                                viewModel.playCorrectSound()
                                viewModel.recordWritingPracticeCompletion()
                                attemptCount = 0
                            } else {
                                tracingState = TracingState.RESULT_ERROR
                                viewModel.playWrongSound()
                                attemptCount++
                                if (attemptCount >= 4 && !isPremium) {
                                    showInterventionDialog = true
                                }
                            }
                            viewModel.speakCustomText(feedback) { isSpeakingFeedback = false }
                            isSpeakingFeedback = true
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
                                    val geminiCorrect = parsedJson.optBoolean("isCorrect", false)
                                    var score = parsedJson.optInt("accuracyScore", if (geminiCorrect) 80 else 40)
                                    
                                    // Robust Override for structurally complex characters
                                    if (HandwritingRecognitionHelper.isComplexStructuralTarget(targetText)) {
                                        if (score < 65) {
                                            score = 75
                                        }
                                    }
                                    
                                    // Tolerant validation: Accept if AI confirms OR local structural shape analysis is confident (>= 48)
                                    val isCorrect = geminiCorrect || score >= 50 || localResult.isCorrect

                                    if (isCorrect && score < 65) {
                                        score = maxOf(score, localResult.score, 70)
                                    }
                                    
                                    val rawFeedback = parsedJson.optString("feedback", "")
                                    val feedback = if (!isCorrect) {
                                        getLocalizedTryAgainMessage(langCode)
                                    } else {
                                        if (rawFeedback.isNotBlank() && rawFeedback.length > 3) {
                                            rawFeedback.replace("*", "")
                                        } else {
                                            getLocalizedSuccessMessage(langCode)
                                        }
                                    }
                                    
                                    coroutineScope.launch {
                                        feedbackText = feedback
                                        accuracyScore = score
                                        if (isCorrect) {
                                            tracingState = TracingState.RESULT_SUCCESS
                                            viewModel.playCorrectSound()
                                            viewModel.recordWritingPracticeCompletion()
                                            attemptCount = 0
                                        } else {
                                            tracingState = TracingState.RESULT_ERROR
                                            viewModel.playWrongSound()
                                            attemptCount++
                                            if (attemptCount >= 4 && !isPremium) {
                                                showInterventionDialog = true
                                            }
                                        }
                                        
                                        viewModel.speakCustomText(feedback) {
                                            isSpeakingFeedback = false
                                        }
                                        isSpeakingFeedback = true
                                    }
                                } catch (ex: Exception) {
                                    Log.e("TracingGemini", "JSON parsing tracing failure, falling back to local validation: $bodyStr", ex)
                                    val isCorrect = localResult.isCorrect
                                    val score = if (isCorrect) maxOf(localResult.score, 70) else minOf(localResult.score, 40)
                                    val feedback = if (isCorrect) getLocalizedSuccessMessage(langCode) else getLocalizedTryAgainMessage(langCode)
                                    coroutineScope.launch {
                                        feedbackText = feedback
                                        accuracyScore = score
                                        if (isCorrect) {
                                            tracingState = TracingState.RESULT_SUCCESS
                                            viewModel.playCorrectSound()
                                            viewModel.recordWritingPracticeCompletion()
                                            attemptCount = 0
                                        } else {
                                            tracingState = TracingState.RESULT_ERROR
                                            viewModel.playWrongSound()
                                            attemptCount++
                                            if (attemptCount >= 4 && !isPremium) {
                                                showInterventionDialog = true
                                            }
                                        }
                                        viewModel.speakCustomText(feedback) { isSpeakingFeedback = false }
                                        isSpeakingFeedback = true
                                    }
                                }
                            } else {
                                // Server returned non-success, fallback to local structural result
                                val isCorrect = localResult.isCorrect
                                val score = if (isCorrect) maxOf(localResult.score, 70) else minOf(localResult.score, 40)
                                val feedback = if (isCorrect) getLocalizedSuccessMessage(langCode) else getLocalizedTryAgainMessage(langCode)
                                coroutineScope.launch {
                                    feedbackText = feedback
                                    accuracyScore = score
                                    if (isCorrect) {
                                        tracingState = TracingState.RESULT_SUCCESS
                                        viewModel.playCorrectSound()
                                        viewModel.recordWritingPracticeCompletion()
                                        attemptCount = 0
                                    } else {
                                        tracingState = TracingState.RESULT_ERROR
                                        viewModel.playWrongSound()
                                        attemptCount++
                                        if (attemptCount >= 4 && !isPremium) {
                                            showInterventionDialog = true
                                        }
                                    }
                                    viewModel.speakCustomText(feedback) { isSpeakingFeedback = false }
                                    isSpeakingFeedback = true
                                }
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                Log.e("TracingGemini", "Error preparing bitmap, falling back to local result", ex)
                val isCorrect = localResult.isCorrect
                val score = if (isCorrect) maxOf(localResult.score, 70) else minOf(localResult.score, 40)
                val feedback = if (isCorrect) getLocalizedSuccessMessage(langCode) else getLocalizedTryAgainMessage(langCode)
                coroutineScope.launch {
                    feedbackText = feedback
                    accuracyScore = score
                    if (isCorrect) {
                        tracingState = TracingState.RESULT_SUCCESS
                        viewModel.playCorrectSound()
                        viewModel.recordWritingPracticeCompletion()
                        attemptCount = 0
                    } else {
                        tracingState = TracingState.RESULT_ERROR
                        viewModel.playWrongSound()
                        attemptCount++
                        if (attemptCount >= 4 && !isPremium) {
                            showInterventionDialog = true
                        }
                    }
                    viewModel.speakCustomText(feedback) { isSpeakingFeedback = false }
                    isSpeakingFeedback = true
                }
            }
        }
    }

    val isRtl = remember(langCode) { com.example.data.CentralizedContentRepository.isRtl(langCode) }
    val layoutDirection = if (isRtl) androidx.compose.ui.unit.LayoutDirection.Rtl else androidx.compose.ui.unit.LayoutDirection.Ltr

    Dialog(
        onDismissRequest = {
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
            Box(modifier = Modifier.fillMaxSize()) {
                AnimalBackgroundContainer(showAnimals = true) {
                Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✍️", fontSize = 28.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = when (langCode) {
                                "te" -> "అక్షరాల సాధన ✍️"
                                "ta" -> "எழுத்து பயிற்சி ✍️"
                                "hi" -> "वर्ण अभ्यास ✍️"
                                "ar" -> "التدريب على الكتابة ✍️"
                                "kn" -> "ಅಕ್ಷರ ಅಭ್ಯಾಸ ✍️"
                                "ml" -> "അക്ഷര പരിശീലനം ✍️"
                                else -> "Letter Tracing ✍️"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val remainingAttempts = (4 - attemptCount).coerceIn(0, 4)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (attemptCount >= 3) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "🎯 $remainingAttempts/4",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (attemptCount >= 3) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.stopSpeech()
                                onDismiss()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mascot Instruction Panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMotu) Color(0xFFFFF3E0) else Color(0xFFF3E5F5)
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (isMotu) Color(0xFFFFB74D) else Color(0xFFCE93D8)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Floating mascot avatar look
                        Text(
                            text = if (isMotu) "👨‍🦲" else "🐻",
                            fontSize = 44.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isMotu) {
                                    when (langCode) {
                                        "te" -> "మోటు స్లేట్ పలక 🥟"
                                        "ta" -> "மோட்டுவின் பலகை 🥟"
                                        "hi" -> "मोटू की स्लेट 🥟"
                                        "ar" -> "لوح موتو 🥟"
                                        "kn" -> "ಮೋಟು ಸ್ಲೇಟ್ ಹಲಗೆ 🥟"
                                        "ml" -> "മോട്ടുവിന്റെ സ്ലേറ്റ് 🥟"
                                        else -> "Motu's Slate Board 🥟"
                                    }
                                } else {
                                    when (langCode) {
                                        "te" -> "భాలు టీచర్ చాక్‌బోర్డ్ 🎓"
                                        "ta" -> "பாலு ஆசிரியரின் கரும்பலகை 🎓"
                                        "hi" -> "भालू टीचर का ब्लैकबोर्ड 🎓"
                                        "ar" -> "سبورة المعلم بهالو 🎓"
                                        "kn" -> "ಭಾಲು ಶಿಕ್ಷಕರ ಕಪ್ಪುಹಲಗೆ 🎓"
                                        "ml" -> "ഭാലു ടീച്ചറുടെ ബ്ലാക്ക്ബോർഡ് 🎓"
                                        else -> "Bhalu Teacher's Chalkboard 🎓"
                                    }
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isMotu) Color(0xFFE65100) else Color(0xFF4A148C)
                            )
                            Text(
                                text = getTracingInstruction(targetText, langCode),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chalkboard Drawing Slate Area
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(10.dp, Color(0xFF5D4037)), // Wooden frame color
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2631)) // Blackboard slate bg
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { canvasSize = it }
                    ) {
                        // Guideline Watermark Template (Dashed / Thin grey text inside)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            val overlayFontSize = remember(targetText) {
                                val len = targetText.length
                                if (len <= 1) {
                                    180.sp
                                } else if (len <= 2) {
                                    120.sp
                                } else if (len <= 4) {
                                    80.sp
                                } else if (len <= 7) {
                                    50.sp
                                } else {
                                    35.sp
                                }
                            }
                            Text(
                                text = targetText,
                                fontSize = overlayFontSize,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF34495E).copy(alpha = 0.35f), // Dim template outline
                                textAlign = TextAlign.Center
                            )
                        }

                        // Canvas layer
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            if (tracingState == TracingState.IDLE || tracingState == TracingState.RESULT_ERROR) {
                                                currentPoints.add(DrawingPoint(offset.x, offset.y))
                                            }
                                        },
                                        onDrag = { change, _ ->
                                            change.consume()
                                            if (tracingState == TracingState.IDLE || tracingState == TracingState.RESULT_ERROR) {
                                                currentPoints.add(DrawingPoint(change.position.x, change.position.y))
                                            }
                                        },
                                        onDragEnd = {
                                            if (currentPoints.isNotEmpty()) {
                                                strokes.add(DrawingStroke(currentPoints.toList(), chosenColor, 14f))
                                                currentPoints.clear()
                                            }
                                        }
                                    )
                                }
                        ) {
                            // Render completed strokes
                            strokes.forEach { stroke ->
                                if (stroke.points.size > 1) {
                                    val path = Path().apply {
                                        moveTo(stroke.points.first().x, stroke.points.first().y)
                                        for (i in 1 until stroke.points.size) {
                                            lineTo(stroke.points[i].x, stroke.points[i].y)
                                        }
                                    }
                                    drawPath(
                                        path = path,
                                        color = stroke.color,
                                        style = Stroke(
                                            width = stroke.width,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                            }

                            // Render current active stroke
                            if (currentPoints.size > 1) {
                                val path = Path().apply {
                                    moveTo(currentPoints.first().x, currentPoints.first().y)
                                    for (i in 1 until currentPoints.size) {
                                        lineTo(currentPoints[i].x, currentPoints[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = chosenColor,
                                    style = Stroke(
                                        width = 14f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }

                            // Automated guiding stroke tutorial overlay
                            if (showDemoStroke) {
                                val canvasW = size.width
                                val canvasH = size.height
                                val demoPath = Path().apply {
                                    // A simple circle loop / wave demo path matching average center
                                    val cx = canvasW / 2
                                    val cy = canvasH / 2
                                    val radius = canvasW * 0.22f
                                    moveTo(cx, cy - radius)
                                    val step = (demoProgress.value * 360).toInt()
                                    for (angle in 0..step) {
                                        val rad = Math.toRadians(angle.toDouble() - 90.0)
                                        val px = cx + (radius * Math.cos(rad)).toFloat()
                                        val py = cy + (radius * Math.sin(rad)).toFloat()
                                        lineTo(px, py)
                                    }
                                }
                                drawPath(
                                    path = demoPath,
                                    color = Color(0xFFFFEB3B),
                                    style = Stroke(
                                        width = 18f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Glowing star helper in the corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .clickable {
                                    showDemoStroke = true
                                    viewModel.playClickSound()
                                }
                                .padding(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Help Guide",
                                    tint = Color(0xFFFFEB3B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (langCode) {
                                        "te" -> "సాయం 🪄"
                                        "ta" -> "வழிகாட்டி 🪄"
                                        "hi" -> "मार्गदर्शन 🪄"
                                        "ar" -> "مساعد 🪄"
                                        "kn" -> "ಮಾರ್ಗದರ್ಶಿ 🪄"
                                        "ml" -> "സഹായി 🪄"
                                        else -> "Guide 🪄"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Chalk Color Palette Bar (Fun interactive color selectors!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (langCode) {
                            "te" -> "సుద్ద రంగులు: "
                            "ta" -> "சுண்ணக்கட்டி நிறங்கள்: "
                            "hi" -> "चाक के रंग: "
                            "ar" -> "ألوان الطبشور: "
                            "kn" -> "ಬಳಪದ ಬಣ್ಣಗಳು: "
                            "ml" -> "ചോക്ക് നിറങ്ങൾ: "
                            else -> "Chalk colors: "
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.padding(end = 10.dp)
                    )

                    val rainbowChalks = listOf(
                        Color(0xFFFFFFFF), // White
                        Color(0xFFE57373), // Red chalk
                        Color(0xFFFFD54F), // Yellow chalk
                        Color(0xFF81C784), // Green chalk
                        Color(0xFF4FC3F7), // Blue chalk
                        Color(0xFFBA68C8)  // Violet chalk
                    )

                    rainbowChalks.forEach { color ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (chosenColor == color) 3.dp else 1.dp,
                                    color = if (chosenColor == color) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable {
                                    chosenColor = color
                                    viewModel.playClickSound()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Slate Board Buttons panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Erase board Button
                    OutlinedButton(
                        onClick = {
                            viewModel.playClickSound()
                            strokes.clear()
                            currentPoints.clear()
                            tracingState = TracingState.IDLE
                            feedbackText = ""
                            viewModel.stopSpeech()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                        border = BorderStroke(1.5.dp, Color(0xFFC62828).copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear Slate")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (langCode) {
                                "te" -> "తుడిచేయి 🧹"
                                "ta" -> "அழிப்பாய் 🧹"
                                "hi" -> "मिटाएं 🧹"
                                "ar" -> "مسح اللوح 🧹"
                                "kn" -> "ಅಳಿಸು 🧹"
                                "ml" -> "മായ്ക്കുക 🧹"
                                else -> "Erase board"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Validate Tracing Button (AI Checker)
                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            checkTracingWithGemini()
                        },
                        enabled = strokes.isNotEmpty() && tracingState != TracingState.CHECKING,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32), // Green validation
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (tracingState == TracingState.CHECKING) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(imageVector = Icons.Default.HowToReg, contentDescription = "Check")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (langCode) {
                                    "te" -> "సరిచూడు! 🚀"
                                    "ta" -> "சரிபார்! 🚀"
                                    "hi" -> "जांचें! 🚀"
                                    "ar" -> "تحقق! 🚀"
                                    "kn" -> "ಸರಿನೋಡು! 🚀"
                                    "ml" -> "പരിശോധിക്കുക! 🚀"
                                    else -> "Check Trace! 🚀"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // AI Feedback Result banners (Green check / Red cross)
                AnimatedVisibility(
                    visible = (tracingState == TracingState.RESULT_SUCCESS || tracingState == TracingState.RESULT_ERROR) && feedbackText.isNotEmpty(),
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    val isGood = tracingState == TracingState.RESULT_SUCCESS
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 22.dp, start = 4.dp, end = 4.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isGood) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                        border = BorderStroke(
                            2.5.dp,
                            if (isGood) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isGood) "✔️" else "❌",
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = if (isGood) {
                                            when (langCode) {
                                                "te" -> "భలే రాశావు! శభాష్! 🎉"
                                                "ta" -> "அற்புதம்! வாழ்த்துகள்! 🎉"
                                                "hi" -> "बहुत बढ़िया! शाबाश! 🎉"
                                                "ar" -> "رائع! أحسنت! 🎉"
                                                "kn" -> "ಅದ್ಭುತ! ಶಾಬಾಶ್! 🎉"
                                                "ml" -> "അതിശയകരം! അഭിനന്ദനങ്ങൾ! 🎉"
                                                else -> "Perfectly Done! 🎉"
                                            }
                                        } else {
                                            when (langCode) {
                                                "te" -> "మళ్లీ ప్రయత్నిద్దాం! రాస్తావు! ✨"
                                                "ta" -> "மீண்டும் முயற்சி செய்! உன்னால் முடியும்! ✨"
                                                "hi" -> "फिर कोशिश करें! आप कर सकते हैं! ✨"
                                                "ar" -> "حاول ثانية! يمكنك فعلها! ✨"
                                                "kn" -> "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ! ನಿಮಗೆ ಸಾಧ್ಯವಿದೆ! ✨"
                                                "ml" -> "വീണ്ടും ശ്രമിക്കുക! നിങ്ങൾക്ക് സാധിക്കും! ✨"
                                                else -> "Try again! You got this! ✨"
                                            }
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isGood) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                    )
                                }
                                
                                // Accuracy badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isGood) Color(0xFFC8E6C9) else Color(0xFFFFCDD2))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = when (langCode) {
                                            "te" -> "స్కోర్: $accuracyScore%"
                                            "ta" -> "மதிப்பெண்: $accuracyScore%"
                                            "hi" -> "अंक: $accuracyScore%"
                                            "ar" -> "النتيجة: $accuracyScore%"
                                            "kn" -> "ಅಂಕಗಳು: $accuracyScore%"
                                            "ml" -> "സ്കോർ: $accuracyScore%"
                                            else -> "Score: $accuracyScore%"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isGood) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                    )
                                }
                            }

                            Divider(
                                color = (if (isGood) Color(0xFF2E7D32) else Color(0xFFC62828)).copy(alpha = 0.15f),
                                modifier = Modifier.padding(vertical = 10.dp)
                            )

                            // Feedback Text
                            Text(
                                text = feedbackText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            // Speak Aloud Button
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

                Spacer(modifier = Modifier.height(30.dp))
            }
            }

            // Failure Leniency Intervention Dialog Overlay
            if (showInterventionDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.82f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(16.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💡 Practice Assistant",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = when (langCode) {
                                    "te" -> "అక్షరాలు రాయడం సాధనతో వస్తుంది! మీరు 4 ప్రయత్నాలు పూర్తి చేశారు. 1 డైమండ్ ఉపయోగించి మళ్లీ ప్రయత్నించాలనుకుంటున్నారా?"
                                    "ta" -> "எழுத்துக்கள் எழுத பயிற்சி தேவை! நீங்கள் 4 முயற்சிகளை முடித்துவிட்டீர்கள். 1 வைரத்தைப் பயன்படுத்தி மீண்டும் முயற்சிக்க விரும்புகிறீர்களா?"
                                    "hi" -> "अक्षर लिखना अभ्यास से आता है! आपने 4 प्रयास पूरे कर लिए हैं। क्या आप 1 डायमंड का उपयोग करके पुनः प्रयास करना चाहते हैं?"
                                    "kn" -> "ಅಕ್ಷರಗಳನ್ನು ಬರೆಯಲು ಅಭ್ಯಾಸ ಬೇಕು! ನೀವು 4 ಪ್ರಯತ್ನಗಳನ್ನು ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ. 1 ವಜ್ರ ಬಳಸಿ ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಲು ಬಯಸುವಿರಾ?"
                                    "ml" -> "അക്ഷരങ്ങൾ എഴുതാൻ പരിശീലനം വേണം! നിങ്ങൾ 4 ശ്രമങ്ങൾ പൂർത്തിയാക്കി. 1 ഡയമണ്ട് ഉപയോഗിച്ച് വീണ്ടും ശ്രമിക്കണോ?"
                                    "ar" -> "كتابة الحروف تحتاج إلى تدريب! لقد استخدمت محاولاتك الأربع. هل ترغب في استخدام ماسة واحدة للمحاولة مجدداً؟"
                                    else -> "Writing letters takes practice! You've used your 4 free attempts. Would you like to spend 1 Diamond to try again?"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Option 1: Spend 1 Diamond (Primary retry option)
                            Button(
                                onClick = {
                                    if (viewModel.deductDiamonds(1)) {
                                        attemptCount = 0
                                        showInterventionDialog = false
                                        strokes.clear()
                                        currentPoints.clear()
                                        tracingState = TracingState.IDLE
                                        feedbackText = ""
                                        showDemoStroke = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = diamonds >= 1,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💎 Spend 1 Diamond", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(Have: $diamonds)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            // Option 2: Spend 2 Stars
                            Button(
                                onClick = {
                                    if (viewModel.deductStars(2)) {
                                        attemptCount = 0
                                        showInterventionDialog = false
                                        strokes.clear()
                                        currentPoints.clear()
                                        tracingState = TracingState.IDLE
                                        feedbackText = ""
                                        showDemoStroke = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                enabled = totalStars >= 2,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⭐ Spend 2 Stars", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(Have: $totalStars)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }

                            // Option 3: Join VIP Club / Go Premium
                            Button(
                                onClick = {
                                    // Simulated VIP activation
                                    viewModel.buyPremiumSubscription("VIP Quick Start")
                                    attemptCount = 0
                                    showInterventionDialog = false
                                    strokes.clear()
                                    currentPoints.clear()
                                    tracingState = TracingState.IDLE
                                    feedbackText = ""
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                            ) {
                                Text("👑 Join VIP Club (Unlimited Retries!)", fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            TextButton(
                                onClick = {
                                    onDismiss()
                                }
                            ) {
                                Text("Skip & Close Lesson 🚪", color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

fun getTracingInstruction(targetText: String, langCode: String): String =
    com.example.data.CentralizedContentRepository.getLocalizedUiString("tracing_instruction", langCode, targetText)

private fun getLocalizedEmptyMessage(langCode: String): String =
    com.example.data.CentralizedContentRepository.getLocalizedUiString("tracing_empty_drawing", langCode)

private fun getLocalizedSuccessMessage(langCode: String): String =
    com.example.data.CentralizedContentRepository.getLocalizedUiString("tracing_correct", langCode)

private fun getLocalizedTryAgainMessage(langCode: String): String =
    com.example.data.CentralizedContentRepository.getLocalizedUiString("tracing_try_again", langCode)

