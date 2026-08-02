package com.example.ui.components

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.BuildConfig
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

enum class SolverState {
    IDLE,
    DECODING,
    SOLVING,
    SUCCESS,
    ERROR
}

@Composable
fun MascotHomeworkSolverDialog(
    viewModel: LearningViewModel,
    voiceCharacter: String,
    langCode: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val isPremium by viewModel.isPremium.collectAsState()
    val diamonds by viewModel.diamonds.collectAsState()
    val aiQuestionsCountToday by viewModel.aiQuestionsCountToday.collectAsState()
    var showLimitReachedDialog by remember { mutableStateOf(false) }
    var bypassLimitCheck by remember { mutableStateOf(false) }

    var isWatchingAd by remember { mutableStateOf(false) }
    var adCountdown by remember { mutableStateOf(5) }

    var solverState by remember { mutableStateOf(SolverState.IDLE) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var solutionText by remember { mutableStateOf("") }
    var isSpeakingSolution by remember { mutableStateOf(false) }
    
    val isMotu = voiceCharacter == "motu"
    
    // Decodes selected Uri to Bitmap off main thread
    LaunchedEffect(selectedImageUri) {
        selectedImageUri?.let { uri ->
            solverState = SolverState.DECODING
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val contentResolver = context.contentResolver
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(contentResolver, uri)
                        ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                            decoder.isMutableRequired = true
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                } catch (e: Exception) {
                    Log.e("MascotSolver", "Error decoding uri", e)
                    null
                }
            }
            if (bitmap != null) {
                previewBitmap = bitmap
                solverState = SolverState.IDLE
            } else {
                solverState = SolverState.ERROR
            }
        }
    }

    // Launchers for Gallery and Camera Snapshot
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            previewBitmap = bitmap
            selectedImageUri = null // Clear Uri as we have Bitmap directly
            solverState = SolverState.IDLE
        }
    }

    // Direct Gemini API Call Function
    fun solveHomeworkWithGemini() {
        val bitmap = previewBitmap ?: return
        
        if (!bypassLimitCheck) {
            val currentCount = viewModel.getAiQuestionsCountToday()
            if (currentCount >= 5) {
                showLimitReachedDialog = true
                return
            }
        }

        if (bypassLimitCheck) {
            bypassLimitCheck = false
        }

        viewModel.incrementAiQuestionsCountToday()

        solverState = SolverState.SOLVING
        solutionText = ""
        
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Compress & Base64 encode
                val maxDimension = 850
                val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                    val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                    val newWidth = if (bitmap.width > bitmap.height) maxDimension else (maxDimension * aspectRatio).toInt()
                    val newHeight = if (bitmap.height > bitmap.width) maxDimension else (maxDimension / aspectRatio).toInt()
                    Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
                } else {
                    bitmap
                }
                
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
                val base64Image = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                
                // Formulate prompt
                val characterName = if (isMotu) "Motu (with his samosas 🥟)" else "Bhalu Bear (the wise teddy 🐻)"
                val promptText = """
                    You are acting as ${'$'}characterName, a friendly, ultra-supportive study partner mascot for kids.
                    Analyze this homework image. Solve and explain it clearly, step-by-step, so a 5 to 10 year old kid can easily understand it!
                    
                    Guidelines:
                    1. Start with a joyful greeting! (e.g. "Hey buddy! Let me take a look... 🧐" or "Aha! Motu here, let's crack this together! 🥟✨")
                    2. Explain the solution using friendly bullet points, simple analogies, and lots of fun emojis. Keep descriptions clear and easy to follow.
                    3. Do NOT use any markdown symbols like double asterisks (**) or single asterisks (*) to bold text, just write standard clean text.
                    4. If the user's language is Telugu ('te'), you MUST write your explanation strictly and entirely in Telugu script (తెలుగు లిపి). If Hindi ('hi'), write in Devanagari Hindi script (हिन्दी). If Tamil ('ta'), write in Tamil script (தமிழ்). If Kannada ('kn'), write in Kannada script (ಕನ್ನಡ). If Malayalam ('ml'), write in Malayalam script (മലയാളം). Otherwise, explain in cheerful English.
                    5. Give a warm encouraging closing to motivate them!
                """.trimIndent()

                // Construct Request JSON
                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObj = JSONObject().apply {
                            val partsArray = JSONArray().apply {
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
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("MascotSolver", "API network failure", e)
                        coroutineScope.launch { solverState = SolverState.ERROR }
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
                                    val text = firstPart?.optString("text") ?: ""
                                    
                                    coroutineScope.launch {
                                        if (text.isNotEmpty()) {
                                            solutionText = text
                                            solverState = SolverState.SUCCESS
                                            // Automatically trigger success sfx and speak!
                                            viewModel.speakCustomText(text) {
                                                isSpeakingSolution = false
                                            }
                                            isSpeakingSolution = true
                                        } else {
                                            solverState = SolverState.ERROR
                                        }
                                    }
                                } catch (ex: Exception) {
                                    Log.e("MascotSolver", "JSON parsing failure: $bodyStr", ex)
                                    coroutineScope.launch { solverState = SolverState.ERROR }
                                }
                            } else {
                                Log.e("MascotSolver", "API Error code: ${res.code} response: $bodyStr")
                                coroutineScope.launch { solverState = SolverState.ERROR }
                            }
                        }
                    }
                })

            } catch (ex: Exception) {
                Log.e("MascotSolver", "Image prep error", ex)
                coroutineScope.launch { solverState = SolverState.ERROR }
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
                        Text(
                            text = if (isMotu) "🥟" else "🎓",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = getMascotSolverText("title", langCode, isMotu),
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
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mascot Stage with CSS-inspired Compose Animations!
                MascotStage(
                    state = solverState,
                    isMotu = isMotu,
                    isSpeaking = isSpeakingSolution
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive Speech Bubble
                SpeechBubble(
                    state = solverState,
                    isMotu = isMotu,
                    langCode = langCode
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Photo Selection Box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    onClick = {
                        if (solverState == SolverState.IDLE || solverState == SolverState.ERROR || solverState == SolverState.SUCCESS) {
                            galleryLauncher.launch("image/*")
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (previewBitmap != null) {
                            Image(
                                bitmap = previewBitmap!!.asImageBitmap(),
                                contentDescription = "Homework Image Preview",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            // Small overlay button to change
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(18.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    .clickable { previewBitmap = null; selectedImageUri = null; solverState = SolverState.IDLE; viewModel.stopSpeech(); solutionText = "" }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove Image",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Camera",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = getMascotSolverText("tap_to_choose", langCode),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = getMascotSolverText("supports", langCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions panel
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Snapshot Camera Button
                    OutlinedButton(
                        onClick = {
                            viewModel.playClickSound()
                            cameraLauncher.launch(null)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(getMascotSolverText("snap_photo", langCode), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    // Solve Button (Enabled only if image is loaded and not currently solving)
                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            solveHomeworkWithGemini()
                        },
                        enabled = previewBitmap != null && solverState != SolverState.SOLVING && solverState != SolverState.DECODING,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // Friendly Green
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (solverState == SolverState.SOLVING) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Solve")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(getMascotSolverText("solve_with_mascot", langCode), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                // Solution results area
                if (solverState == SolverState.SUCCESS && solutionText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // TTS speaker controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getMascotSolverText("explanation", langCode),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (isSpeakingSolution) {
                                        Button(
                                            onClick = {
                                                viewModel.stopSpeech()
                                                isSpeakingSolution = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                        ) {
                                            Icon(imageVector = Icons.Default.VolumeOff, contentDescription = "Stop")
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(getMascotSolverText("stop", langCode), fontSize = 12.sp)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                viewModel.speakCustomText(solutionText) {
                                                    isSpeakingSolution = false
                                                }
                                                isSpeakingSolution = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                                        ) {
                                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "Read Aloud")
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(getMascotSolverText("read_aloud", langCode), fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            Text(
                                text = solutionText,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
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
                                    solveHomeworkWithGemini()
                                } else {
                                    viewModel.playWrongSound()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                            enabled = diamonds >= 10,
                            modifier = Modifier.fillMaxWidth()
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
                                                solveHomeworkWithGemini()
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
                                        solveHomeworkWithGemini()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29B6F6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("🎥 Option 2: Watch One Rewarded Ad", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        TextButton(
                            onClick = { showLimitReachedDialog = false },
                            modifier = Modifier.fillMaxWidth()
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

@Composable
fun MascotStage(
    state: SolverState,
    isMotu: Boolean,
    isSpeaking: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_stage")

    // Slow breathing CSS-like float offset
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_float"
    )

    // Quick wiggle for speaking or solving
    val wiggleRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 250 else 750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_wiggle"
    )

    // Pulsing size animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == SolverState.SOLVING) 400 else 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_pulse"
    )

    Box(
        modifier = Modifier
            .size(160.dp)
            .graphicsLayer {
                translationY = floatOffset
                rotationZ = if (state == SolverState.SOLVING || isSpeaking) wiggleRotation else 0f
            }
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Decorative glowing background circle
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.05f,
            targetValue = 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(1400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_glow"
        )
        
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    if (state == SolverState.SOLVING) Color(0xFFFFEB3B).copy(alpha = pulseAlpha)
                    else if (state == SolverState.SUCCESS) Color(0xFF4CAF50).copy(alpha = pulseAlpha)
                    else MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
                )
        )

        // Render Active Mascot using custom emojis and shapes
        if (isMotu) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Bald head
                Text(
                    text = when (state) {
                        SolverState.SOLVING -> "🧐"
                        SolverState.SUCCESS -> "🤩"
                        SolverState.ERROR -> "🥺"
                        else -> "👨‍🦲"
                    },
                    fontSize = 72.sp,
                    modifier = Modifier.align(Alignment.Center).offset(y = (-20).dp)
                )
                // Round orange tummy
                Text(
                    text = "🟠",
                    fontSize = 58.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-10).dp)
                )
                // Red vest/scarf
                Text(
                    text = "🧣",
                    fontSize = 38.sp,
                    modifier = Modifier.align(Alignment.Center).offset(y = (-5).dp)
                )
                // Large Samosa!
                Text(
                    text = "🥟",
                    fontSize = 36.sp,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 10.dp, y = (-10).dp)
                )
            }
        } else {
            // Bhalu Bear
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp)
            ) {
                // Bear face
                Text(
                    text = when (state) {
                        SolverState.SOLVING -> "🤔"
                        SolverState.SUCCESS -> "🥳"
                        SolverState.ERROR -> "😢"
                        else -> "🐻"
                    },
                    fontSize = 84.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                
                if (state != SolverState.SUCCESS && state != SolverState.SOLVING) {
                    // Glasses overlay
                    Text(
                        text = "👓",
                        fontSize = 28.sp,
                        modifier = Modifier.align(Alignment.Center).offset(y = 5.dp)
                    )
                }

                // Graduate Hat
                Text(
                    text = "🎓",
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-15).dp)
                )
            }
        }

        // Floating CSS elements depending on the state
        if (state == SolverState.SOLVING) {
            FloatingThinkingElements()
        } else if (state == SolverState.SUCCESS) {
            FloatingSuccessElements()
        }
    }
}

@Composable
fun SpeechBubble(
    state: SolverState,
    isMotu: Boolean,
    langCode: String
) {
    val speechText = when (state) {
        SolverState.DECODING -> getMascotSolverText("decoding", langCode)
        SolverState.SOLVING -> if (isMotu) getMascotSolverText("solving_motu", langCode) else getMascotSolverText("solving_bhalu", langCode)
        SolverState.SUCCESS -> if (isMotu) getMascotSolverText("success_motu", langCode) else getMascotSolverText("success_bhalu", langCode)
        SolverState.ERROR -> getMascotSolverText("error", langCode)
        else -> if (isMotu) getMascotSolverText("idle_motu", langCode) else getMascotSolverText("idle_bhalu", langCode)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                color = if (isMotu) Color(0xFFFFF3E0) else Color(0xFFEFEBE9),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                2.dp,
                if (isMotu) Color(0xFFFFB74D) else Color(0xFFBCAAA4),
                RoundedCornerShape(20.dp)
            )
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = speechText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMotu) Color(0xFFE65100) else Color(0xFF4E342E),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun FloatingThinkingElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking_elements")
    
    val rotateAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate_icons"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationZ = rotateAnimation },
        contentAlignment = Alignment.Center
    ) {
        // Orbiting lightbulbs and question marks
        Text(
            text = "💡",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp)
        )
        Text(
            text = "❓",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 25.dp)
        )
        Text(
            text = "🔎",
            fontSize = 22.sp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-25).dp)
        )
        Text(
            text = "✏️",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 25.dp)
        )
    }
}

@Composable
fun FloatingSuccessElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "success_elements")
    
    val scaleAnimation by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_success"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✨",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-15).dp, y = (-15).dp)
                .scale(scaleAnimation)
        )
        Text(
            text = "🎉",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 15.dp, y = (-15).dp)
                .scale(scaleAnimation)
        )
        Text(
            text = "⭐",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-15).dp, y = 15.dp)
                .scale(scaleAnimation)
        )
        Text(
            text = "🎈",
            fontSize = 26.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 15.dp, y = 15.dp)
                .scale(scaleAnimation)
        )
    }
}

fun getMascotSolverText(key: String, langCode: String, isMotu: Boolean = false): String {
    return when (key) {
        "title" -> when (langCode) {
            "te" -> if (isMotu) "మోటు హోంవర్క్ సహాయకుడు" else "భాలు స్టడీ ల్యాబ్"
            "ta" -> if (isMotu) "மோட்டுவின் வீட்டுப்பாடம் உதவியாளர்" else "பாலுவின் படிப்பு கூடம்"
            "hi" -> if (isMotu) "मोटू का होमवर्क सहायक" else "भालू की स्टडी लैब"
            "ar" -> if (isMotu) "مساعد واجبات موتو" else "مختبر دراسة بالو"
            "kn" -> if (isMotu) "ಮೋಟು ಹೋಮ್ವರ್ಕ್ ಸಹಾಯಕ" else "ಭಾಲೂ ಸ್ಟಡಿ ಲ್ಯಾಬ್"
            "ml" -> if (isMotu) "മോട്ടു ഹോംവർക്ക് സഹായി" else "ഭാലു സ്റ്റഡി ലാബ്"
            else -> if (isMotu) "Motu's Homework Helper" else "Bhalu's Study Lab"
        }
        "tap_to_choose" -> when (langCode) {
            "te" -> "హోంవర్క్ ఫొటోను ఎంచుకోవడానికి ఇక్కడ నొక్కండి!"
            "ta" -> "வீட்டுப்பாடம் புகைப்படத்தைத் தேர்ந்தெடுக்க இங்கே தட்டவும்!"
            "hi" -> "होमवर्क फोटो चुनने के लिए यहाँ टैप करें!"
            "ar" -> "اضغط هنا لاختيار صورة الواجب المنزلي!"
            "kn" -> "ಹೋಮ್ವರ್ಕ್ ಫೋಟೋ ಆಯ್ಕೆ ಮಾಡಲು ಇಲ್ಲಿ ಟ್ಯಾಪ್ ಮಾಡಿ!"
            "ml" -> "ಹോംവർക്ക് ഫോട്ടോ തിരഞ്ഞെടുക്കാൻ ഇവിടെ ടാപ്പ് ചെയ്യുക!"
            else -> "Tap here to choose a homework photo!"
        }
        "supports" -> when (langCode) {
            "te" -> "గణితం, ఇంగ్లీష్, రాయడం మరియు గీయడం సపోర్ట్ చేస్తుంది!"
            "ta" -> "கணிதம், ஆங்கிலம், எழுதுதல் மற்றும் வரைதல் ஆதரிக்கிறது!"
            "hi" -> "गणित, अंग्रेजी, लिखना और चित्र बनाना समर्थित है!"
            "ar" -> "يدعم الرياضيات، اللغة الإنجليزية، الكتابة والرسم!"
            "kn" -> "ಗಣಿತ, ಇಂಗ್ಲಿಷ್, ಬರವಣಿಗೆ ಮತ್ತು ರೇಖಾಚಿತ್ರವನ್ನು ಬೆಂಬಲಿಸುತ್ತದೆ!"
            "ml" -> "ഗണിതം, ഇംഗ്ലീഷ്, എഴുത്ത്, വരയ്ക്കൽ എന്നിവ പിന്തുണയ്ക്കുന്നു!"
            else -> "Supports Math, English, writing & drawing!"
        }
        "snap_photo" -> when (langCode) {
            "te" -> "ఫొటో తీయి 📸"
            "ta" -> "படம் எடு 📸"
            "hi" -> "फोटो खींचें 📸"
            "ar" -> "التقط صورة 📸"
            "kn" -> "ಫೋಟೋ ತೆಗೆಯಿರಿ 📸"
            "ml" -> "ഫോട്ടോ എടുക്കുക 📸"
            else -> "Snap Photo"
        }
        "solve_with_mascot" -> when (langCode) {
            "te" -> "మాస్కాట్‌తో సాల్వ్ చేయి! 🚀"
            "ta" -> "மஸ்காட் மூலம் தீர்க்கவும்! 🚀"
            "hi" -> "मैस्कॉट के साथ हल करें! 🚀"
            "ar" -> "حل مع جالب الحظ! 🚀"
            "kn" -> "ಮ್ಯಾಸ್ಕಾಟ್ನೊಂದಿಗೆ ಪರಿಹರಿಸಿ! 🚀"
            "ml" -> "മാസ്കറ്റിനൊപ്പം പരിഹരിക്കുക! 🚀"
            else -> "Solve with Mascot!"
        }
        "explanation" -> when (langCode) {
            "te" -> "💡 వివరణ"
            "ta" -> "💡 விளக்கம்"
            "hi" -> "💡 स्पष्टीकरण"
            "ar" -> "💡 التوضيح"
            "kn" -> "💡 ವಿವರಣೆ"
            "ml" -> "💡 വിശദീകരണം"
            else -> "💡 Explanation"
        }
        "stop" -> when (langCode) {
            "te" -> "ఆపు 🛑"
            "ta" -> "நிறுத்து 🛑"
            "hi" -> "रोकें 🛑"
            "ar" -> "إيقاف 🛑"
            "kn" -> "ನಿಲ್ಲಿಸು 🛑"
            "ml" -> "നിർത്തുക 🛑"
            else -> "Stop"
        }
        "read_aloud" -> when (langCode) {
            "te" -> "చదివి వినిపించు 🔊"
            "ta" -> "சத்தமாக வாசி 🔊"
            "hi" -> "जोर से पढ़ें 🔊"
            "ar" -> "اقرأ بصوت عالٍ 🔊"
            "kn" -> "ಗಟ್ಟಿಯಾಗಿ ಓದು 🔊"
            "ml" -> "ഉറക്കെ വായിക്കുക 🔊"
            else -> "Read Aloud"
        }
        "decoding" -> when (langCode) {
            "te" -> "ఫొటోని లోడ్ చేస్తున్నాను... ఒక్క నిమిషం దయచేసి! 🖼️"
            "ta" -> "புகைப்படம் தயார் செய்யப்படுகிறது... ஒரு நிமிடம் காத்திருங்கள்! 🖼️"
            "hi" -> "फोटो लोड हो रहा है... कृपया एक सेकंड रुकें! 🖼️"
            "ar" -> "جاري تحضير الصورة... دقيقة واحدة من فضلك! 🖼️"
            "kn" -> "ಕಡತ ಲೋಡ್ ಆಗುತ್ತಿದೆ... ಸ್ವಲ್ಪ ತಾಳಿ! 🖼️"
            "ml" -> "ഫോട്ടോ ലോഡ് ചെയ്യുന്നു... ദയവായി ഒരു നിമിഷം! 🖼️"
            else -> "Preparing your awesome photo... Just a second! 🖼️"
        }
        "solving_motu" -> when (langCode) {
            "te" -> "ఓహో! నా సమోసా పక్కన పెట్టి మరి ఆలోచిస్తున్నాను... సమోసా పవర్! 🥟💡"
            "ta" -> "ஓஹோ! எனது சமோசாவைத் தள்ளி வைத்து யோசிக்கிறேன்... சமோசா பவர்! 🥟💡"
            "hi" -> "ओहो! मैं अपना समोसा साइड में रखकर सोच रहा हूँ... समोसा पावर! 🥟💡"
            "ar" -> "أوه! سأضع السمبوسة جانباً لأفكر... قوة السمبوسة! 🥟💡"
            "kn" -> "ಓಹೋ! ನನ್ನ ಸಮೋಸಾವನ್ನು ಪಕ್ಕಕ್ಕೆ ಇಟ್ಟು ಯೋಚಿಸುತ್ತಿದ್ದೇನೆ... ಸಮೋಸಾ ಪವರ್! 🥟💡"
            "ml" -> "ഓഹോ! എന്റെ സമോസ മാറ്റിവെച്ചു ഞാൻ ആലോചിക്കുന്നു... സമോസ പവർ! 🥟💡"
            else -> "Whoa! Putting my samosa aside to solve this... Samosa power activate! 🥟💡"
        }
        "solving_bhalu" -> when (langCode) {
            "te" -> "నా చదువుల టోపీ వేసుకుని ఆలోచిస్తున్నాను... ఇది చాలా ఈజీ! 🐻👓"
            "ta" -> "எனது படிப்பு தொப்பியை அணிந்து யோசிக்கிறேன்... இது மிகவும் எளிது! 🐻👓"
            "hi" -> "मैं अपनी स्टडी कैप पहनकर सोच रहा हूँ... यह बहुत आसान है! 🐻👓"
            "ar" -> "سأرتدي قبعة الدراسة لأفكر... هذا سهل للغاية! 🐻👓"
            "kn" -> "ನನ್ನ ಸ್ಟಡಿ ಕ್ಯಾಪ್ ಧರಿಸಿ ಯೋಚಿಸುತ್ತಿದ್ದೇನೆ... ಇದು ತುಂಬಾ ಸುಲಭ! 🐻👓"
            "ml" -> "ഞാൻ എന്റെ സ്റ്റഡി ക്യാപ് ധരിച്ചു ആലോചിക്കുന്നു... ഇത് വളരെ എളുപ്പമാണ്! 🐻👓"
            else -> "Let me put on my wise glasses and solve this! This will be super fun! 🐻👓"
        }
        "success_motu" -> when (langCode) {
            "te" -> "అరెరె! నేను చక్కగా సాల్వ్ చేశాను! కింద ఉన్న జవాబు చూడు! 🥳"
            "ta" -> "அடடா! நான் வெற்றிகரமாக தீர்த்துவிட்டேன்! கீழே உள்ள பதிலைப் பார்! 🥳"
            "hi" -> "अरे वाह! मैंने इसे हल कर दिया है! नीचे दिया गया उत्तर देखें! 🥳"
            "ar" -> "يا سلام! لقد قمت بحلها بنجاح! انظر الإجابة بالأسفل! 🥳"
            "kn" -> "ಅರೆರೆ! ನಾನು ಯಶಸ್ವಿಯಾಗಿ ಪರಿಹರಿಸಿದ್ದೇನೆ! ಕೆಳಗಿನ ಉತ್ತರ ನೋಡಿ! 🥳"
            "ml" -> "അയ്യോ! ഞാൻ വിജയകരമായി പരിഹരിച്ചു! താഴെയുള്ള ഉത്തരം കാണൂ! 🥳"
            else -> "Look at that! I successfully solved it! Read the explanation below! 🥳"
        }
        "success_bhalu" -> when (langCode) {
            "te" -> "భలే! నేను సాల్వ్ చేసాను! ఎంత బాగుందో చూడు! 🌟"
            "ta" -> "அருமை! நான் தீர்த்துவிட்டேன்! எவ்வளவு அழகாக இருக்கிறது என்று பார்! 🌟"
            "hi" -> "शाबाश! मैंने हल निकाल लिया है! देखो यह कितना अच्छा है! 🌟"
            "ar" -> "رائع! لقد قمت بحلها! انظر كم هي جميلة! 🌟"
            "kn" -> "ಭಲೇ! ನಾನು ಪರಿಹರಿಸಿದ್ದೇನೆ! ಎಷ್ಟು ಚೆನ್ನಾಗಿದೆ ನೋಡಿ! 🌟"
            "ml" -> "കൊള്ളാം! ഞാൻ പരിഹരിച്ചു! എത്ര മനോഹരമാണെന്ന് കാണൂ! 🌟"
            else -> "Hooray! I have cracked the answer! Let's read the solution together! 🌟"
        }
        "error" -> when (langCode) {
            "te" -> "అయ్యో! ఫొటో స్పష్టంగా లేదు. దయచేసి ఇంకోసారి ఫొటో తీయి! 🥺"
            "ta" -> "அய்யோ! புகைப்படம் தெளிவாக இல்லை. தயவுசெய்து மீண்டும் படம் எடுக்கவும்! 🥺"
            "hi" -> "ओह! फोटो साफ नहीं है। कृपया एक बार फिर से फोटो खींचें! 🥺"
            "ar" -> "عذراً! الصورة ليست واضحة. يرجى التقاط الصورة مرة أخرى! 🥺"
            "kn" -> "ಅಯ್ಯೋ! ಫೋಟೋ ಸ್ಪಷ್ಟವಾಗಿಲ್ಲ. ದಯವಿಟ್ಟು ಮತ್ತೊಮ್ಮೆ ಫೋಟೋ ತೆಗೆದುಕೊಳ್ಳಿ! 🥺"
            "ml" -> "അയ്യോ! ഫോട്ടോ വ്യക്തമല്ല. ദയവായി വീണ്ടും ഫോട്ടോ എടുക്കൂ! 🥺"
            else -> "Oops! I couldn't read the photo properly. Let's snap a clearer picture! 🥺"
        }
        "idle_motu" -> when (langCode) {
            "te" -> "హలో దోస్త్! హోంవర్క్ ఫొటో అప్లోడ్ చెయ్, మోటు సాల్వ్ చేస్తాడు! 🥟✏️"
            "ta" -> "ஹலோ நண்பா! வீட்டுப்பாடம் புகைப்படத்தைப் பதிவேற்று, மோட்டு தீர்த்து வைப்பான்! 🥟✏️"
            "hi" -> "नमस्ते दोस्त! होमवर्क फोटो अपलोड करो, मोटू हल कर देगा! 🥟✏️"
            "ar" -> "مرحباً صديقي! حمّل صورة الواجب وسيقوم موتو بحلها! 🥟✏️"
            "kn" -> "ಹಲೋ ದೋಸ್ತ್! ಹೋಮ್ವರ್ಕ್ ಫೋಟೋ ಅಪ್ಲೋಡ್ ಮಾಡಿ, ಮೋಟು ಪರಿಹರಿಸುತ್ತಾನೆ! 🥟✏️"
            "ml" -> "ഹലോ കൂട്ടുകാരാ! ഹോംവർക്ക് ഫോട്ടോ അപ്‌ലോഡ് ചെയ്യൂ, മോട്ടു പരിഹരിച്ചു തരും! 🥟✏️"
            else -> "Hey buddy! Upload any homework photo and Motu will solve it in seconds! 🥟✏️"
        }
        "idle_bhalu" -> when (langCode) {
            "te" -> "నమస్కారం! హోంవర్క్ ఫొటో చూపించు, కలిసి చదువుకుందాం! 🐻🎒"
            "ta" -> "வணக்கம்! வீட்டுப்பாடம் புகைப்படத்தைக் காட்டு, சேர்ந்து படிப்போம்! 🐻🎒"
            "hi" -> "नमस्ते! होमवर्क फोटो दिखाओ, मिलकर पढ़ाई करते हैं! 🐻🎒"
            "ar" -> "مرحباً! أرني صورة الواجب ودعنا ندرس معاً! 🐻🎒"
            "kn" -> "ನಮಸ್ಕಾರ! ಹೋಮ್ವರ್ಕ್ ಫೋಟೋ ತೋರಿಸಿ, ಒಟ್ಟಿಗೆ ಓದೋಣ! 🐻🎒"
            "ml" -> "നമസ്കാരം! ഹോംവർക്ക് ഫോട്ടോ കാണിക്കൂ, നമുക്ക് ഒരുമിച്ച് പഠിക്കാം! 🐻🎒"
            else -> "Hello friend! Show me any homework photo and let's solve it together! 🐻🎒"
        }
        else -> ""
    }
}
