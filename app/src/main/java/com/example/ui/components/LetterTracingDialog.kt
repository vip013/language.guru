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
    val subtitleLower = item.subtitle.lowercase()
    if (subtitleLower.contains("circle")) {
        return "◯" // White Large Circle for tracing
    }
    if (subtitleLower.contains("triangle")) {
        return "△" // White Up-Pointing Triangle for tracing
    }
    if (subtitleLower.contains("square")) {
        return "□" // White Square for tracing
    }
    if (subtitleLower.contains("rectangle")) {
        return "▭" // White Rectangle for tracing
    }
    if (subtitleLower.contains("star")) {
        return "☆" // White Star for tracing
    }

    val display = item.display
    if (display.contains("(") && display.contains(")")) {
        val extracted = display.substringAfter("(").substringBefore(")").trim()
        if (extracted.isNotEmpty()) {
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

    // Direct Gemini API Call for Tracing Validation
    fun checkTracingWithGemini() {
        val width = canvasSize.width
        val height = canvasSize.height
        if (width <= 0 || height <= 0 || strokes.isEmpty()) {
            return
        }
        
        tracingState = TracingState.CHECKING
        feedbackText = ""
        
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Generate offscreen bitmap representing what they traced
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bitmap)
                
                // Chalkboard dark slate bg
                val bgPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#1B2631") // Solid chalkboard blue-gray
                    style = android.graphics.Paint.Style.FILL
                }
                canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
                
                // White background guideline text
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#34495E") // Light slate gridline/watermark
                    val letterRatio = when (targetText.length) {
                        1 -> 0.45f
                        2 -> 0.32f
                        3 -> 0.24f
                        4 -> 0.18f
                        else -> 0.14f
                    }
                    textSize = (width * letterRatio).coerceIn(80f, 400f)
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                val xPos = width / 2f
                val textBounds = android.graphics.Rect()
                textPaint.getTextBounds(targetText, 0, targetText.length, textBounds)
                val yPos = (height / 2f) + (textBounds.height() / 2f) - textBounds.bottom
                canvas.drawText(targetText, xPos, yPos, textPaint)
                
                // Traced strokes
                strokes.forEach { stroke ->
                    if (stroke.points.size > 1) {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.rgb(
                                (stroke.color.red * 255).toInt(),
                                (stroke.color.green * 255).toInt(),
                                (stroke.color.blue * 255).toInt()
                            )
                            strokeWidth = 28f
                            style = android.graphics.Paint.Style.STROKE
                            strokeCap = android.graphics.Paint.Cap.ROUND
                            strokeJoin = android.graphics.Paint.Join.ROUND
                            isAntiAlias = true
                        }
                        val path = android.graphics.Path().apply {
                            val first = stroke.points.first()
                            moveTo(first.x, first.y)
                            for (i in 1 until stroke.points.size) {
                                val p = stroke.points[i]
                                lineTo(p.x, p.y)
                            }
                        }
                        canvas.drawPath(path, paint)
                    }
                }
                
                // Base64 compression
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                
                // Prompt instructing Gemini 
                val characterName = if (isMotu) "Motu (the kid-friendly buddy)" else "Bhalu Bear (the warm school teacher)"
                val promptText = """
                    You are acting as $characterName, a friendly kid's helper teacher.
                    A child has tried to trace or write the character/symbol "$targetText".
                    In the image:
                    - The thin, dark blue-gray background outline is the template watermark they were supposed to trace.
                    - The bright, thick colored lines are the actual finger strokes drawn by the child.
                    
                    Evaluate their drawing:
                    - Be encouraging but realistic and precise. Rate their drawing/tracing on an honest scale of 0 to 100 for "accuracyScore", based on how closely their strokes match and align with the path of the target template "$targetText".
                    - If the "accuracyScore" is 50 or higher, set "isCorrect" to true.
                    - If the "accuracyScore" is less than 50, you MUST set "isCorrect" to false.
                    - IMPORTANT LENIENCY OVERRIDE: If the character "$targetText" is Tamil "ஈ" (EE) or has complex structures with multiple disconnected strokes and dots, you MUST be extremely generous and lenient. Since children drawing with fingers struggle to place dots inside and outside the rectangular frame of "ஈ" perfectly, any attempt that roughly matches its vertical-horizontal structure and outer lines should be scored 60-80 and marked as Correct (isCorrect = true). Do not fail them for missing dots or minor alignment issues.
                    
                    You MUST respond with a valid JSON object containing exactly these keys:
                    {
                      "isCorrect": true or false,
                      "feedback": "A short, cheerful, encouraging sentence of feedback with cute emojis. The selected kid's language is '$langCode'. You MUST write the feedback entirely in '$langCode' (e.g. if 'te', write in sweet Telugu; if 'ta', write in Tamil; if 'hi', write in Hindi; if 'kn', write in Kannada; if 'ml', write in Malayalam; if 'ar', write in Arabic; if 'bn', write in Bengali; if 'mr', write in Marathi; if 'gu', write in Gujarati; if 'en', write in English). If they scored below 50, gently ask them to try again entirely in '$langCode' to reach at least 50%. Do NOT use any markdown symbols like double asterisks (**) or single asterisks (*) anywhere in the feedback string.",
                      "accuracyScore": an integer from 0 to 100 based on stroke completeness and template alignment
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
                        Log.e("TracingGemini", "Failed network tracing request", e)
                        coroutineScope.launch {
                            tracingState = TracingState.RESULT_ERROR
                            feedbackText = "Error evaluating. Try again, buddy!"
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
                                    
                                    // Extract JSON if model wrapped in markdown
                                    if (text.contains("```json")) {
                                        text = text.substringAfter("```json").substringBefore("```").trim()
                                    } else if (text.contains("```")) {
                                        text = text.substringAfter("```").substringBefore("```").trim()
                                    }
                                    
                                    val parsedJson = JSONObject(text)
                                    var score = parsedJson.optInt("accuracyScore", 80)
                                    
                                    // Robust Override for Tamil "ஈ" (EE) to ensure children are never wrongly marked incorrect
                                    if (targetText == "ஈ" || targetText.contains("ஈ")) {
                                        if (score < 65) {
                                            score = 75
                                        }
                                    }
                                    
                                    val isCorrect = score >= 50
                                    val feedback = if (!isCorrect) {
                                        getLocalizedTryAgainMessage(langCode)
                                    } else {
                                        if (targetText == "ஈ" || targetText.contains("ஈ")) {
                                            // Provide highly positive Tamil/English encouraging feedback
                                            if (langCode == "ta") "அற்புதம்! ஈ எழுத்தை மிக அழகாக எழுதினீர்கள்! 🌟"
                                            else parsedJson.optString("feedback", "Excellent job tracing the letter ஈ! 🌟")
                                        } else {
                                            parsedJson.optString("feedback", "Good try!")
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
                                            if (attemptCount >= 30 && !isPremium) {
                                                showInterventionDialog = true
                                            }
                                        }
                                        
                                        // Speak feedback
                                        viewModel.speakCustomText(feedback) {
                                            isSpeakingFeedback = false
                                        }
                                        isSpeakingFeedback = true
                                    }
                                } catch (ex: Exception) {
                                    Log.e("TracingGemini", "JSON parsing tracing failure: $bodyStr", ex)
                                    coroutineScope.launch {
                                        tracingState = TracingState.RESULT_ERROR
                                        feedbackText = "Almost there! Let's clean the board and try once more!"
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    tracingState = TracingState.RESULT_ERROR
                                    feedbackText = "Oh! My marker is dry. Let's trace again!"
                                }
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                Log.e("TracingGemini", "Error preparing bitmap", ex)
                coroutineScope.launch {
                    tracingState = TracingState.RESULT_ERROR
                    feedbackText = "Oops! Something went wrong. Let's try drawing again!"
                }
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
                    IconButton(
                        onClick = {
                            viewModel.stopSpeech()
                            onDismiss()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
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
                                text = "Drawing letters takes practice! You've tried 30 times. Would you like to spend 2 Stars or 1 Diamond to get tracing help and retry?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Option 1: Spend 2 Stars
                            Button(
                                onClick = {
                                    if (viewModel.deductStars(2)) {
                                        attemptCount = 0
                                        showInterventionDialog = false
                                        // Auto-draw demo for them as help!
                                        showDemoStroke = true
                                        feedbackText = "Stars used! Retrying with tracing guidelines!"
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

                            // Option 2: Spend 1 Diamond
                            Button(
                                onClick = {
                                    if (viewModel.deductDiamonds(1)) {
                                        attemptCount = 0
                                        showInterventionDialog = false
                                        showDemoStroke = true
                                        feedbackText = "Diamond spent! Guidance activated!"
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

                            // Option 3: Join VIP Club / Go Premium
                            Button(
                                onClick = {
                                    // Simulated VIP activation
                                    viewModel.buyPremiumSubscription("VIP Quick Start")
                                    attemptCount = 0
                                    showInterventionDialog = false
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                            ) {
                                Text("👑 Join VIP Club (Free Unlimited Retry!)", fontWeight = FontWeight.ExtraBold)
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

fun getTracingInstruction(targetText: String, langCode: String): String {
    return when (langCode) {
        "te" -> "పలక మీద చూపిస్తున్న అక్షరం/గుర్తు '$targetText' ని నీ వేలితో దిద్దు బంగారం! ఆపై 'సరిచూడు' నొక్కు!"
        "ta" -> "பலகையில் காட்டப்பட்டுள்ள எழுத்து/குறியீடு '$targetText' ஐ உங்கள் விரலால் வரைந்து, பின்னர் 'சரிபார்' என்பதை அழுத்தவும்!"
        "hi" -> "स्लेट पर दिखाए गए अक्षर/प्रतीक '$targetText' को अपनी उंगली से ट्रेस करें और फिर 'जांचें' दबाएं!"
        "ar" -> "تتبع الحرف/الرمز '$targetText' على اللوح بإصبعك ثم اضغط على 'تحقق'!"
        "kn" -> "ಹಲಗೆಯ ಮೇಲೆ ತೋರಿಸಿರುವ ಅಕ್ಷರ/ಚಿಹ್ನೆ '$targetText' ಅನ್ನು ನಿಮ್ಮ ಬೆರಳಿನಿಂದ ಬರೆಯಿರಿ ಮತ್ತು ನಂತರ 'ಸರಿನೋಡು' ಒತ್ತಿ!"
        "ml" -> "സ്ലേറ്റിൽ കാണിച്ചിരിക്കുന്ന അക്ഷരം/ചിഹ്നം '$targetText' നിങ്ങളുടെ വിരൽ കൊണ്ട് വരയ്ക്കുക, തുടർന്ന് 'പരിശോധിക്കുക' അമർത്തുക!"
        else -> "Trace the letter/symbol '$targetText' on the slate with your finger and press 'Check Trace'!"
    }
}

private fun getLocalizedTryAgainMessage(langCode: String): String = when (langCode) {
    "te" -> "మళ్లీ ప్రయత్నించు బంగారం! 50% కంటే తక్కువ ఖచ్చితత్వం ఉంది. నువ్వు చేయగలవు! 🌱"
    "ta" -> "மீண்டும் முயலுங்கள் செல்லமே! துல்லியம் 50% க்கும் குறைவாக உள்ளது. உங்களால் முடியும்! 🌱"
    "hi" -> "फिर से कोशिश करो प्यारे बच्चे! सटीकता 50% से कम है। आप कर सकते हैं! 🌱"
    "kn" -> "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸು ಕಂದಾ! ನಿಖರತೆ 50% ಕ್ಕಿಂತ ಕಡಿಮೆಯಿದೆ. ನೀನು ಮಾಡಬಲ್ಲೆ! 🌱"
    "ml" -> "വീണ്ടും ശ്രമിക്കൂ കുട്ടീ! കൃത്യത 50%-ൽ താഴെയാണ്. നിനക്ക് സാധിക്കും! 🌱"
    "bn" -> "আবার চেষ্টা করো সোনা! নির্ভুলता 50% এর নিচে। তুমি পারবে! 🌱"
    "mr" -> "पुन्हा प्रयत्न करा बाळा! अचूकता 50% पेक्षा कमी आहे. तू करू शकतोस! 🌱"
    "gu" -> "ફરીથી પ્રયત્ન કર બેટા! ચોકસાઈ 50% થી ઓછી છે. તમે કરી શકો છો! 🌱"
    "ar" -> "حاول مرة أخرى يا بطل! الدقة أقل من 50٪. يمكنك القيام بذلك! 🌱"
    else -> "Try again, buddy! Accuracy is below 50%. You can do it! 🌱"
}

