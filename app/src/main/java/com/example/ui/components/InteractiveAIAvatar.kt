package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun InteractiveAIAvatar(
    isFemale: Boolean,
    avatarType: String = if (isFemale) "girl" else "boy",
    expression: String, // "happy", "excited", "thinking", "celebrating", "encouraging"
    isSpeaking: Boolean,
    action: String, // "idle", "wave", "point", "clap", "thumbs_up", "jump"
    avatarSize: String, // "small", "medium", "large"
    spokenText: String,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onToggleGender: () -> Unit,
    modifier: Modifier = Modifier,
    showControls: Boolean = true
) {
    // 1. Determine dimensions based on size
    val avatarSizeDp = when (avatarSize) {
        "small" -> 110.dp
        "large" -> 180.dp
        else -> 145.dp // medium
    }

    // 2. Animate breathing (idle bounce)
    val infiniteTransition = rememberInfiniteTransition(label = "breathe")
    val breatheScaleY by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheY"
    )

    // 3. Eye Blinking (Randomized scheduler)
    var eyeClosed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 5500))
            eyeClosed = true
            delay(130)
            eyeClosed = false
        }
    }

    // 4. Lip-sync looping mouth animation (when speaking)
    val mouthSyncTransition = rememberInfiniteTransition(label = "lip_sync")
    val mouthScaleY by if (isSpeaking) {
        mouthSyncTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(Random.nextInt(110, 160), easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            ),
            label = "mouthY"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    // 5. Jump Action
    val jumpOffset by animateDpAsState(
        targetValue = if (action == "jump") (-32).dp else 0.dp,
        animationSpec = if (action == "jump") {
            infiniteRepeatable(
                animation = tween(280, easing = EaseOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        },
        label = "jump"
    )

    // 6. Wave arm rotation
    val waveRotation by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    // 7. Clapping offset (rapid clapping hands)
    val clapOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clap"
    )

    Column(
        modifier = modifier
            .testTag("interactive_ai_avatar")
            .wrapContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Speech Bubble (Cartoon dialog)
        if (spokenText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .widthIn(max = 220.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .align(Alignment.End)
            ) {
                Text(
                    text = spokenText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showControls) {
                // Left Quick-action overlay panel (gender, mute)
                Column(
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .background(Color(0x99222222), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleGender,
                        modifier = Modifier.size(28.dp).testTag("avatar_gender_toggle")
                    ) {
                        Icon(
                            imageVector = if (isFemale) Icons.Default.Female else Icons.Default.Male,
                            contentDescription = "Toggle Avatar Gender",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier.size(28.dp).testTag("avatar_mute_toggle")
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Toggle Mute Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            val avatarBgColor = when (avatarType) {
                "boy" -> Color(0xFF81C784)
                "girl" -> Color(0xFFFFF176)
                "rabbit" -> Color(0xFFFFB74D)
                "bear" -> Color(0xFF80DEEA)
                "monkey" -> Color(0xFFCE93D8)
                "panda" -> Color(0xFFE0E0E0)
                "elephant" -> Color(0xFFEF9A9A)
                else -> Color(0xFFFFF176)
            }

            // The main avatar graphic
            Box(
                modifier = Modifier
                    .size(avatarSizeDp)
                    .offset(y = jumpOffset)
                    .scale(scaleX = 1f, scaleY = breatheScaleY)
                    .clip(CircleShape)
                    .background(avatarBgColor, shape = CircleShape)
                    .border(3.dp, Color.White, CircleShape)
                    .clickable { onToggleGender() }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Define generic points for face elements so we can share eye/mouth coordinates!
                    val leftEyeCenter = Offset(w * 0.36f, h * 0.46f)
                    val rightEyeCenter = Offset(w * 0.64f, h * 0.46f)

                    // Draw Ears (Behind face)
                    when (avatarType) {
                        "rabbit" -> {
                            // Left ear
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.28f, h * 0.05f),
                                size = Size(w * 0.14f, h * 0.32f),
                                cornerRadius = CornerRadius(20f, 20f)
                            )
                            drawRoundRect(
                                color = Color(0xFFFF8A80), // Pink inner
                                topLeft = Offset(w * 0.31f, h * 0.1f),
                                size = Size(w * 0.08f, h * 0.22f),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                            // Right ear
                            drawRoundRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.58f, h * 0.05f),
                                size = Size(w * 0.14f, h * 0.32f),
                                cornerRadius = CornerRadius(20f, 20f)
                            )
                            drawRoundRect(
                                color = Color(0xFFFF8A80), // Pink inner
                                topLeft = Offset(w * 0.61f, h * 0.1f),
                                size = Size(w * 0.08f, h * 0.22f),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }
                        "bear" -> {
                            // Left ear
                            drawCircle(color = Color(0xFF8D6E63), radius = w * 0.12f, center = Offset(w * 0.26f, h * 0.24f))
                            drawCircle(color = Color(0xFFFFCC80), radius = w * 0.06f, center = Offset(w * 0.26f, h * 0.24f))
                            // Right ear
                            drawCircle(color = Color(0xFF8D6E63), radius = w * 0.12f, center = Offset(w * 0.74f, h * 0.24f))
                            drawCircle(color = Color(0xFFFFCC80), radius = w * 0.06f, center = Offset(w * 0.74f, h * 0.24f))
                        }
                        "monkey" -> {
                            // Left ear
                            drawCircle(color = Color(0xFF5D4037), radius = w * 0.14f, center = Offset(w * 0.18f, h * 0.48f))
                            drawCircle(color = Color(0xFFFFCC80), radius = w * 0.08f, center = Offset(w * 0.18f, h * 0.48f))
                            // Right ear
                            drawCircle(color = Color(0xFF5D4037), radius = w * 0.14f, center = Offset(w * 0.82f, h * 0.48f))
                            drawCircle(color = Color(0xFFFFCC80), radius = w * 0.08f, center = Offset(w * 0.82f, h * 0.48f))
                        }
                        "panda" -> {
                            // Left ear
                            drawCircle(color = Color(0xFF212121), radius = w * 0.13f, center = Offset(w * 0.24f, h * 0.22f))
                            // Right ear
                            drawCircle(color = Color(0xFF212121), radius = w * 0.13f, center = Offset(w * 0.76f, h * 0.22f))
                        }
                        "elephant" -> {
                            // Left huge ear
                            drawRoundRect(
                                color = Color(0xFF90A4AE),
                                topLeft = Offset(w * 0.05f, h * 0.25f),
                                size = Size(w * 0.26f, h * 0.45f),
                                cornerRadius = CornerRadius(40f, 40f)
                            )
                            drawRoundRect(
                                color = Color(0xFFEF9A9A),
                                topLeft = Offset(w * 0.1f, h * 0.32f),
                                size = Size(w * 0.16f, h * 0.3f),
                                cornerRadius = CornerRadius(25f, 25f)
                            )
                            // Right huge ear
                            drawRoundRect(
                                color = Color(0xFF90A4AE),
                                topLeft = Offset(w * 0.69f, h * 0.25f),
                                size = Size(w * 0.26f, h * 0.45f),
                                cornerRadius = CornerRadius(40f, 40f)
                            )
                            drawRoundRect(
                                color = Color(0xFFEF9A9A),
                                topLeft = Offset(w * 0.74f, h * 0.32f),
                                size = Size(w * 0.16f, h * 0.3f),
                                cornerRadius = CornerRadius(25f, 25f)
                            )
                        }
                    }

                    // Draw Main Head Base
                    val headColor = when (avatarType) {
                        "rabbit", "panda" -> Color.White
                        "bear" -> Color(0xFF8D6E63)
                        "monkey" -> Color(0xFF6D4C41)
                        "elephant" -> Color(0xFFB0BEC5)
                        else -> Color(0xFFFFE0B2) // Skin for boy & girl
                    }
                    drawCircle(
                        color = headColor,
                        radius = w * 0.36f,
                        center = Offset(w * 0.5f, h * 0.48f)
                    )

                    // Draw Special Head Overlays (Monkey Face Mask, Panda Eye patches)
                    when (avatarType) {
                        "monkey" -> {
                            // Heart-shaped tan face overlay
                            val maskPath = Path().apply {
                                moveTo(w * 0.5f, h * 0.32f)
                                cubicTo(w * 0.36f, h * 0.22f, w * 0.22f, h * 0.38f, w * 0.24f, h * 0.54f)
                                cubicTo(w * 0.26f, h * 0.68f, w * 0.4f, h * 0.76f, w * 0.5f, h * 0.76f)
                                cubicTo(w * 0.6f, h * 0.76f, w * 0.74f, h * 0.68f, w * 0.76f, h * 0.54f)
                                cubicTo(w * 0.78f, h * 0.38f, w * 0.64f, h * 0.22f, w * 0.5f, h * 0.32f)
                                close()
                            }
                            drawPath(maskPath, color = Color(0xFFFFCC80))
                        }
                        "panda" -> {
                            // Left dark eye patch
                            drawRoundRect(
                                color = Color(0xFF212121),
                                topLeft = Offset(w * 0.28f, h * 0.38f),
                                size = Size(w * 0.15f, h * 0.18f),
                                cornerRadius = CornerRadius(20f, 20f)
                            )
                            // Right dark eye patch
                            drawRoundRect(
                                color = Color(0xFF212121),
                                topLeft = Offset(w * 0.57f, h * 0.38f),
                                size = Size(w * 0.15f, h * 0.18f),
                                cornerRadius = CornerRadius(20f, 20f)
                            )
                        }
                        "girl" -> {
                            // Draw hair
                            drawCircle(color = Color(0xFF3E2723), radius = w * 0.12f, center = Offset(w * 0.2f, h * 0.35f))
                            drawCircle(color = Color(0xFF3E2723), radius = w * 0.12f, center = Offset(w * 0.8f, h * 0.35f))
                            // Main top hair
                            drawArc(
                                color = Color(0xFF3E2723),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = true,
                                size = Size(w * 0.72f, h * 0.45f),
                                topLeft = Offset(w * 0.14f, h * 0.18f)
                            )
                            // Fringe
                            val fringePath = Path().apply {
                                moveTo(w * 0.15f, h * 0.4f)
                                quadraticTo(w * 0.35f, h * 0.32f, w * 0.5f, h * 0.42f)
                                quadraticTo(w * 0.65f, h * 0.32f, w * 0.85f, h * 0.4f)
                                lineTo(w * 0.85f, h * 0.25f)
                                lineTo(w * 0.15f, h * 0.25f)
                                close()
                            }
                            drawPath(fringePath, color = Color(0xFF3E2723))
                        }
                        "boy" -> {
                            // Short male spikes / top hair
                            val shortHairPath = Path().apply {
                                moveTo(w * 0.18f, h * 0.4f)
                                lineTo(w * 0.2f, h * 0.22f)
                                lineTo(w * 0.3f, h * 0.15f)
                                lineTo(w * 0.4f, h * 0.22f)
                                lineTo(w * 0.5f, h * 0.12f)
                                lineTo(w * 0.6f, h * 0.22f)
                                lineTo(w * 0.7f, h * 0.15f)
                                lineTo(w * 0.8f, h * 0.22f)
                                lineTo(w * 0.82f, h * 0.4f)
                                close()
                            }
                            drawPath(shortHairPath, color = Color(0xFF4E342E))
                        }
                    }

                    // Blushing cheeks
                    drawCircle(
                        color = Color(0xFFFF8A80).copy(alpha = 0.4f),
                        radius = w * 0.06f,
                        center = Offset(w * 0.28f, h * 0.54f)
                    )
                    drawCircle(
                        color = Color(0xFFFF8A80).copy(alpha = 0.4f),
                        radius = w * 0.06f,
                        center = Offset(w * 0.72f, h * 0.54f)
                    )

                    // Draw Eyes
                    if (eyeClosed) {
                        drawLine(
                            color = Color(0xFF212121),
                            start = Offset(w * 0.32f, h * 0.46f),
                            end = Offset(w * 0.4f, h * 0.46f),
                            strokeWidth = 3.5f
                        )
                        drawLine(
                            color = Color(0xFF212121),
                            start = Offset(w * 0.6f, h * 0.46f),
                            end = Offset(w * 0.68f, h * 0.46f),
                            strokeWidth = 3.5f
                        )
                    } else {
                        val pupilOffsetX = when (expression) {
                            "thinking" -> -2f
                            "encouraging" -> 2f
                            else -> 0f
                        }
                        val pupilOffsetY = if (action == "point") -2f else 0f

                        val pupilColor = if (avatarType == "panda") Color.Black else Color(0xFF004D40)

                        // Left Eye
                        drawCircle(color = Color.White, radius = w * 0.055f, center = leftEyeCenter)
                        drawCircle(color = pupilColor, radius = w * 0.038f, center = leftEyeCenter + Offset(pupilOffsetX, pupilOffsetY))
                        drawCircle(color = Color.White, radius = w * 0.012f, center = leftEyeCenter + Offset(pupilOffsetX - 2, pupilOffsetY - 2))

                        // Right Eye
                        drawCircle(color = Color.White, radius = w * 0.055f, center = rightEyeCenter)
                        drawCircle(color = pupilColor, radius = w * 0.038f, center = rightEyeCenter + Offset(pupilOffsetX, pupilOffsetY))
                        drawCircle(color = Color.White, radius = w * 0.012f, center = rightEyeCenter + Offset(pupilOffsetX - 2, pupilOffsetY - 2))
                    }

                    // Special features (Bear muzzle/nose, Rabbit muzzle/nose, Panda muzzle, Elephant Trunk, Glasses)
                    when (avatarType) {
                        "bear", "monkey" -> {
                            // Tan Muzzle
                            drawCircle(
                                color = Color(0xFFFFCC80),
                                radius = w * 0.12f,
                                center = Offset(w * 0.5f, h * 0.59f)
                            )
                            // Dark nose
                            drawCircle(
                                color = Color(0xFF3E2723),
                                radius = w * 0.03f,
                                center = Offset(w * 0.5f, h * 0.55f)
                            )
                        }
                        "rabbit" -> {
                            // Pink nose
                            drawCircle(
                                color = Color(0xFFFF8A80),
                                radius = w * 0.025f,
                                center = Offset(w * 0.5f, h * 0.53f)
                            )
                            // Two cute rabbit front teeth
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.47f, h * 0.59f),
                                size = Size(w * 0.03f, h * 0.03f)
                            )
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(w * 0.50f, h * 0.59f),
                                size = Size(w * 0.03f, h * 0.03f)
                            )
                        }
                        "panda" -> {
                            // Small white snout
                            drawCircle(
                                color = Color(0xFFEEEEEE),
                                radius = w * 0.08f,
                                center = Offset(w * 0.5f, h * 0.58f)
                            )
                            // Black nose
                            drawCircle(
                                color = Color.Black,
                                radius = w * 0.025f,
                                center = Offset(w * 0.5f, h * 0.56f)
                            )
                        }
                        "elephant" -> {
                            // Elephant trunk
                            val trunkPath = Path().apply {
                                moveTo(w * 0.46f, h * 0.52f)
                                quadraticTo(w * 0.42f, h * 0.7f, w * 0.5f, h * 0.74f)
                                quadraticTo(w * 0.58f, h * 0.72f, w * 0.54f, h * 0.52f)
                                close()
                            }
                            drawPath(trunkPath, color = Color(0xFF90A4AE))
                            // Little trunk lines
                            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.46f, h * 0.58f), Offset(w * 0.52f, h * 0.58f), strokeWidth = 2f)
                            drawLine(Color.White.copy(alpha = 0.5f), Offset(w * 0.45f, h * 0.63f), Offset(w * 0.51f, h * 0.63f), strokeWidth = 2f)
                        }
                        "girl", "boy" -> {
                            // Elegant Glasses for human avatar teachers!
                            val glassRadius = w * 0.11f
                            val frameColor = if (avatarType == "girl") Color(0xFFE53935) else Color(0xFF1E88E5)
                            drawCircle(color = frameColor, radius = glassRadius, center = leftEyeCenter, style = Stroke(width = 4f))
                            drawCircle(color = frameColor, radius = glassRadius, center = rightEyeCenter, style = Stroke(width = 4f))
                            drawLine(color = frameColor, start = leftEyeCenter + Offset(glassRadius, 0f), end = rightEyeCenter - Offset(glassRadius, 0f), strokeWidth = 4.5f)
                        }
                    }

                    // Draw Mouth
                    when {
                        isSpeaking -> {
                            drawRoundRect(
                                color = Color(0xFFD81B60),
                                topLeft = Offset(w * 0.46f, h * 0.59f),
                                size = Size(w * 0.08f, h * 0.06f * mouthScaleY),
                                cornerRadius = CornerRadius(10f, 10f)
                            )
                        }
                        expression == "thinking" -> {
                            val path = Path().apply {
                                moveTo(w * 0.46f, h * 0.60f)
                                quadraticTo(w * 0.5f, h * 0.58f, w * 0.54f, h * 0.60f)
                            }
                            drawPath(path, color = Color(0xFF212121), style = Stroke(width = 3.5f))
                        }
                        expression == "excited" || expression == "celebrating" -> {
                            drawArc(
                                color = Color(0xFFE53935),
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = true,
                                size = Size(w * 0.14f, h * 0.11f),
                                topLeft = Offset(w * 0.43f, h * 0.56f)
                            )
                        }
                        else -> {
                            val path = Path().apply {
                                moveTo(w * 0.44f, h * 0.58f)
                                quadraticTo(w * 0.5f, h * 0.62f, w * 0.56f, h * 0.58f)
                            }
                            drawPath(path, color = Color(0xFF212121), style = Stroke(width = 3.5f))
                        }
                    }

                    // Draw Clothes / Body
                    val bodyColor = when (avatarType) {
                        "girl" -> Color(0xFFE53935)
                        "boy" -> Color(0xFFFF9100)
                        "rabbit" -> Color(0xFFFF7043)
                        "bear" -> Color(0xFF4DB6AC)
                        "monkey" -> Color(0xFFFFD54F)
                        "panda" -> Color(0xFF37474F)
                        "elephant" -> Color(0xFF5C6BC0)
                        else -> Color(0xFFE53935)
                    }
                    drawRoundRect(
                        color = bodyColor,
                        topLeft = Offset(w * 0.28f, h * 0.78f),
                        size = Size(w * 0.44f, h * 0.25f),
                        cornerRadius = CornerRadius(24f, 24f)
                    )

                    // Body accessories
                    if (avatarType == "girl") {
                        drawCircle(color = Color(0xFFFFD54F), radius = w * 0.025f, center = Offset(w * 0.5f, h * 0.81f))
                    } else if (avatarType == "boy") {
                        val tiePath = Path().apply {
                            moveTo(w * 0.48f, h * 0.8f)
                            lineTo(w * 0.52f, h * 0.8f)
                            lineTo(w * 0.53f, h * 0.9f)
                            lineTo(w * 0.5f, h * 0.94f)
                            lineTo(w * 0.47f, h * 0.9f)
                            close()
                        }
                        drawPath(tiePath, color = Color(0xFF1565C0))
                    } else if (avatarType == "rabbit") {
                        // Draw a small cute orange carrot tie on rabbit body
                        val carrotPath = Path().apply {
                            moveTo(w * 0.48f, h * 0.8f)
                            lineTo(w * 0.52f, h * 0.8f)
                            lineTo(w * 0.5f, h * 0.92f)
                            close()
                        }
                        drawPath(carrotPath, color = Color(0xFFFF9800))
                    }
                }

                // Wave overlay arm
                if (action == "wave") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(waveRotation)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Draw extended waving hand
                            drawCircle(
                                color = Color(0xFFFFE0B2),
                                radius = size.width * 0.05f,
                                center = Offset(size.width * 0.84f, size.height * 0.7f)
                            )
                        }
                    }
                }

                // Pointing overlay arm
                if (action == "point") {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Draw arm pointing leftwards towards the card
                            val armPath = Path().apply {
                                moveTo(w * 0.35f, h * 0.82f)
                                lineTo(w * 0.12f, h * 0.68f)
                                lineTo(w * 0.12f, h * 0.6f)
                                lineTo(w * 0.35f, h * 0.76f)
                                close()
                            }
                            drawPath(armPath, color = if (isFemale) Color(0xFFE53935) else Color(0xFFFF9100))
                            // Pointing hand bubble
                            drawCircle(
                                color = Color(0xFFFFE0B2),
                                radius = w * 0.05f,
                                center = Offset(w * 0.1f, h * 0.64f)
                            )
                        }
                    }
                }

                // Clapping overlay stars / hands
                if (action == "clap") {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Draw rapid clapping hands rotating with clapOffset
                            drawCircle(
                                color = Color(0xFFFFE0B2),
                                radius = w * 0.05f,
                                center = Offset(w * 0.5f + clapOffset, h * 0.74f)
                            )
                            // Draw cute celebratory sparks
                            drawCircle(color = Color(0xFFFFD54F), radius = w * 0.015f, center = Offset(w * 0.25f, h * 0.62f))
                            drawCircle(color = Color(0xFFFFD54F), radius = w * 0.015f, center = Offset(w * 0.75f, h * 0.62f))
                            drawCircle(color = Color(0xFFFFD54F), radius = w * 0.012f, center = Offset(w * 0.5f, h * 0.32f))
                        }
                    }
                }

                // Thumbs up overlay
                if (action == "thumbs_up") {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 16.dp, end = 16.dp)
                            .size(28.dp)
                            .background(Color(0xFFFFB300), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👍", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
