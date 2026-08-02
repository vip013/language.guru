package com.example.ui.components

import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

data class ConfettiParticle(
    val normX: Float,
    val normY: Float,
    var x: Float = -1f,
    var y: Float = -1f,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val radius: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val shapeType: Int, // 0 = Circle, 1 = Square, 2 = Star, 3 = Ribbon, 4 = Emoji
    var alpha: Float = 1f,
    val emoji: String? = null
)

data class FloatingBalloon(
    val id: Int,
    var startX: Float,
    var x: Float,
    var y: Float,
    var speed: Float,
    val size: Float,
    val emoji: String,
    var swayTime: Float = 0f,
    val swaySpeed: Float,
    var popped: Boolean = false
)

@Composable
fun ConfettiCelebration(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val rewardEmojis = remember { listOf("🏅", "🏆", "⭐", "✨", "🎉", "🍬", "🍭") }
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val balloons = remember { mutableStateListOf<FloatingBalloon>() }
    var ticker by remember { mutableStateOf(0) }

    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    val starPath = remember {
        Path().apply {
            val points = 5
            val radius = 1f
            val innerRadius = 0.4f
            val angleStep = Math.PI / points
            var currentAngle = -Math.PI / 2
            moveTo(
                (radius * cos(currentAngle)).toFloat(),
                (radius * sin(currentAngle)).toFloat()
            )
            for (i in 0 until points * 2) {
                currentAngle += angleStep
                val r = if (i % 2 == 0) innerRadius else radius
                lineTo(
                    (r * cos(currentAngle)).toFloat(),
                    (r * sin(currentAngle)).toFloat()
                )
            }
            close()
        }
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            particles.clear()
            balloons.clear()
            
            // Wait for canvas to be measured
            while (canvasWidth == 0f || canvasHeight == 0f) {
                delay(16)
            }

            val random = java.util.Random()
            
            // Initialize 6 floating balloons/candies below screen
            val balloonEmojis = listOf("🎈", "🍬", "🍭", "🌟", "🎁", "🎈")
            repeat(6) { index ->
                val startX = (0.15f + random.nextFloat() * 0.7f) * canvasWidth
                balloons.add(
                    FloatingBalloon(
                        id = index,
                        startX = startX,
                        x = startX,
                        y = canvasHeight + 100f + (index * 160f), // Staggered spawn heights below screen
                        speed = 1.8f + random.nextFloat() * 2.2f,
                        size = 80f + random.nextFloat() * 30f, // 80 to 110px size
                        emoji = balloonEmojis[index % balloonEmojis.size],
                        swaySpeed = 0.02f + random.nextFloat() * 0.03f
                    )
                )
            }

            // Popper 1: Bottom Left shooting up-right
            repeat(45) {
                val angleDeg = -25f - random.nextFloat() * 45f // -25 to -70 deg
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val speed = 12f + random.nextFloat() * 26f
                val sType = random.nextInt(5)
                val emojiChar = if (sType == 4) rewardEmojis.random() else null
                particles.add(
                    ConfettiParticle(
                        normX = 0.05f,
                        normY = 0.95f,
                        vx = (cos(angleRad) * speed).toFloat(),
                        vy = (sin(angleRad) * speed).toFloat(),
                        color = listOf(
                            Color(0xFFFF4081), // Candy Pink
                            Color(0xFF00E676), // Bright Green
                            Color(0xFF00B0FF), // Ocean Blue
                            Color(0xFFFFD600), // Golden Yellow
                            Color(0xFFFF3D00), // Sunset Orange
                            Color(0xFFD500F9), // Purple Candy
                            Color(0xFF1DE9B6)  // Vibrant Teal
                        ).random(),
                        radius = 6f + random.nextFloat() * 12f,
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = -6f + random.nextFloat() * 12f,
                        shapeType = sType,
                        emoji = emojiChar
                    )
                )
            }

            // Popper 2: Bottom Right shooting up-left
            repeat(45) {
                val angleDeg = -110f - random.nextFloat() * 45f // -110 to -155 deg
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val speed = 12f + random.nextFloat() * 26f
                val sType = random.nextInt(5)
                val emojiChar = if (sType == 4) rewardEmojis.random() else null
                particles.add(
                    ConfettiParticle(
                        normX = 0.95f,
                        normY = 0.95f,
                        vx = (cos(angleRad) * speed).toFloat(),
                        vy = (sin(angleRad) * speed).toFloat(),
                        color = listOf(
                            Color(0xFFFF4081),
                            Color(0xFF00E676),
                            Color(0xFF00B0FF),
                            Color(0xFFFFD600),
                            Color(0xFFFF3D00),
                            Color(0xFFD500F9),
                            Color(0xFF1DE9B6)
                        ).random(),
                        radius = 6f + random.nextFloat() * 12f,
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = -6f + random.nextFloat() * 12f,
                        shapeType = sType,
                        emoji = emojiChar
                    )
                )
            }

            // Central fountain popper
            repeat(30) {
                val angleDeg = -75f - random.nextFloat() * 30f // -75 to -105 deg (mostly straight up)
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val speed = 10f + random.nextFloat() * 22f
                val sType = if (random.nextFloat() < 0.35f) 4 else 2 // 35% chance of medal/trophy emoji, 65% star
                val emojiChar = if (sType == 4) listOf("🏅", "🏆", "⭐").random() else null
                particles.add(
                    ConfettiParticle(
                        normX = 0.5f,
                        normY = 0.8f,
                        vx = (cos(angleRad) * speed).toFloat(),
                        vy = (sin(angleRad) * speed).toFloat(),
                        color = listOf(
                            Color(0xFFFFD600), // Lots of gold stars in center!
                            Color(0xFFFF4081),
                            Color(0xFF00E676),
                            Color(0xFF00B0FF)
                        ).random(),
                        radius = 8f + random.nextFloat() * 10f,
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = -5f + random.nextFloat() * 10f,
                        shapeType = sType,
                        emoji = emojiChar
                    )
                )
            }

            // --- ADD GORGEOUS SEQUENTIAL STAR-BURST EXPLOSIONS ---
            // Instant Star-burst left
            repeat(20) {
                val angleRad = random.nextFloat() * 2 * Math.PI
                val speed = 4f + random.nextFloat() * 12f
                particles.add(
                    ConfettiParticle(
                        normX = 0.25f,
                        normY = 0.4f,
                        vx = (cos(angleRad) * speed).toFloat(),
                        vy = (sin(angleRad) * speed).toFloat(),
                        color = Color(0xFFFFD600), // Sparkling Gold Star Burst
                        radius = 10f + random.nextFloat() * 10f,
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = -6f + random.nextFloat() * 12f,
                        shapeType = 2 // Star shape
                    )
                )
            }

            // Instant Star-burst right
            repeat(20) {
                val angleRad = random.nextFloat() * 2 * Math.PI
                val speed = 4f + random.nextFloat() * 12f
                particles.add(
                    ConfettiParticle(
                        normX = 0.75f,
                        normY = 0.4f,
                        vx = (cos(angleRad) * speed).toFloat(),
                        vy = (sin(angleRad) * speed).toFloat(),
                        color = Color(0xFFFF4081), // Vivid Pink Star Burst
                        radius = 10f + random.nextFloat() * 10f,
                        rotation = random.nextFloat() * 360f,
                        rotationSpeed = -6f + random.nextFloat() * 12f,
                        shapeType = 2 // Star shape
                    )
                )
            }

            // Delayed Star-burst 1: At 450ms, in upper middle
            launch {
                delay(450)
                repeat(25) {
                    val angleRad = random.nextFloat() * 2 * Math.PI
                    val speed = 4f + random.nextFloat() * 14f
                    particles.add(
                        ConfettiParticle(
                            normX = 0.5f,
                            normY = 0.3f,
                            vx = (cos(angleRad) * speed).toFloat(),
                            vy = (sin(angleRad) * speed).toFloat(),
                            color = Color(0xFF00E676), // Bright Green
                            radius = 9f + random.nextFloat() * 11f,
                            rotation = random.nextFloat() * 360f,
                            rotationSpeed = -7f + random.nextFloat() * 14f,
                            shapeType = 2 // Star shape
                        )
                    )
                }
            }

            // Delayed Star-burst 2: At 900ms, in lower left
            launch {
                delay(900)
                repeat(20) {
                    val angleRad = random.nextFloat() * 2 * Math.PI
                    val speed = 3f + random.nextFloat() * 10f
                    particles.add(
                        ConfettiParticle(
                            normX = 0.3f,
                            normY = 0.6f,
                            vx = (cos(angleRad) * speed).toFloat(),
                            vy = (sin(angleRad) * speed).toFloat(),
                            color = Color(0xFF00B0FF), // Ocean Blue
                            radius = 8f + random.nextFloat() * 9f,
                            rotation = random.nextFloat() * 360f,
                            rotationSpeed = -5f + random.nextFloat() * 10f,
                            shapeType = 2 // Star shape
                        )
                    )
                }
            }

            // Delayed Star-burst 3: At 1350ms, in lower right
            launch {
                delay(1350)
                repeat(20) {
                    val angleRad = random.nextFloat() * 2 * Math.PI
                    val speed = 3f + random.nextFloat() * 10f
                    particles.add(
                        ConfettiParticle(
                            normX = 0.7f,
                            normY = 0.6f,
                            vx = (cos(angleRad) * speed).toFloat(),
                            vy = (sin(angleRad) * speed).toFloat(),
                            color = Color(0xFFD500F9), // Purple Star
                            radius = 8f + random.nextFloat() * 9f,
                            rotation = random.nextFloat() * 360f,
                            rotationSpeed = -5f + random.nextFloat() * 10f,
                            shapeType = 2 // Star shape
                        )
                    )
                }
            }

            // Physics loop
            while (true) {
                withFrameMillis {
                    particles.forEach { p ->
                        p.vy += 0.32f // Gravity
                        p.vx *= 0.975f // Air resistance
                        p.vy *= 0.975f
                        p.x += p.vx
                        p.y += p.vy
                        p.rotation += p.rotationSpeed
                        
                        // Slowly fade out
                        p.alpha = (p.alpha - 0.005f).coerceAtLeast(0f)
                    }

                    // Update balloons
                    balloons.forEach { b ->
                        if (!b.popped) {
                            b.y -= b.speed // Float upwards
                            b.swayTime += b.swaySpeed
                            b.x = b.startX + sin(b.swayTime.toDouble()).toFloat() * 35f // Sway left and right
                            
                            // If a balloon goes off the top, recycle it
                            if (b.y < -120f) {
                                b.y = canvasHeight + 100f
                                b.startX = (0.15f + random.nextFloat() * 0.7f) * canvasWidth
                                b.x = b.startX
                                b.speed = 1.5f + random.nextFloat() * 2.5f
                            }
                        }
                    }
                    ticker++
                }
            }
        } else {
            particles.clear()
            balloons.clear()
        }
    }

    if (isCompleted && (particles.isNotEmpty() || balloons.isNotEmpty())) {
        Canvas(
            modifier = modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    canvasWidth = size.width.toFloat()
                    canvasHeight = size.height.toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val random = java.util.Random()
                        var balloonPopped = false
                        
                        // Check if we clicked any balloon
                        balloons.forEach { b ->
                            if (!b.popped) {
                                // Add offset so text coordinate matches visually
                                val dx = offset.x - b.x
                                val dy = offset.y - (b.y - b.size / 3f) // Adjustment because drawText baseline is at bottom
                                val distance = Math.hypot(dx.toDouble(), dy.toDouble()).toFloat()
                                if (distance < b.size * 0.8f) {
                                    b.popped = true
                                    balloonPopped = true
                                    
                                    // Play pop sound!
                                    try {
                                        android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 100)
                                            .startTone(android.media.ToneGenerator.TONE_PROP_ACK, 80)
                                    } catch (e: Exception) {
                                        Log.e("Confetti", "Error playing balloon pop sound", e)
                                    }
                                    
                                    // Burst 25 colorful star and candy particles from the popped balloon!
                                    repeat(25) {
                                        val angleRad = random.nextFloat() * 2 * Math.PI
                                        val speed = 3f + random.nextFloat() * 15f
                                        val sType = random.nextInt(5)
                                        val emojiChar = if (sType == 4) rewardEmojis.random() else null
                                        particles.add(
                                            ConfettiParticle(
                                                normX = -1f,
                                                normY = -1f,
                                                x = b.x,
                                                y = b.y - b.size / 3f,
                                                vx = (cos(angleRad) * speed).toFloat(),
                                                vy = (sin(angleRad) * speed).toFloat(),
                                                color = listOf(
                                                    Color(0xFFFF4081),
                                                    Color(0xFF00E676),
                                                    Color(0xFF00B0FF),
                                                    Color(0xFFFFD600),
                                                    Color(0xFFFF3D00),
                                                    Color(0xFFD500F9),
                                                    Color(0xFF1DE9B6)
                                                ).random(),
                                                radius = 8f + random.nextFloat() * 12f,
                                                rotation = random.nextFloat() * 360f,
                                                rotationSpeed = -8f + random.nextFloat() * 16f,
                                                shapeType = sType,
                                                emoji = emojiChar
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        
                        // If no balloon popped, spawn a beautiful mini touch popper
                        if (!balloonPopped) {
                            try {
                                android.media.ToneGenerator(android.media.AudioManager.STREAM_MUSIC, 60)
                                    .startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 50)
                            } catch (e: Exception) {}
                            
                            repeat(15) {
                                val angleRad = random.nextFloat() * 2 * Math.PI
                                val speed = 4f + random.nextFloat() * 12f
                                val sType = random.nextInt(5)
                                val emojiChar = if (sType == 4) rewardEmojis.random() else null
                                particles.add(
                                    ConfettiParticle(
                                        normX = -1f,
                                        normY = -1f,
                                        x = offset.x,
                                        y = offset.y,
                                        vx = (cos(angleRad) * speed).toFloat(),
                                        vy = (sin(angleRad) * speed).toFloat(),
                                        color = listOf(
                                            Color(0xFFFFD600),
                                            Color(0xFFFF4081),
                                            Color(0xFF00E676),
                                            Color(0xFF00B0FF),
                                            Color(0xFF1DE9B6)
                                        ).random(),
                                        radius = 6f + random.nextFloat() * 10f,
                                        rotation = random.nextFloat() * 360f,
                                        rotationSpeed = -6f + random.nextFloat() * 12f,
                                        shapeType = sType,
                                        emoji = emojiChar
                                    )
                                )
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            particles.forEach { p ->
                if (p.x == -1f) {
                    p.x = p.normX * width
                    p.y = p.normY * height
                }

                if (p.alpha > 0f) {
                    when (p.shapeType) {
                        0 -> { // Circle
                            drawCircle(
                                color = p.color.copy(alpha = p.alpha),
                                radius = p.radius,
                                center = Offset(p.x, p.y)
                            )
                        }
                        1 -> { // Square
                            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                                drawRect(
                                    color = p.color.copy(alpha = p.alpha),
                                    topLeft = Offset(p.x - p.radius, p.y - p.radius),
                                    size = Size(p.radius * 2, p.radius * 2)
                                )
                            }
                        }
                        2 -> { // Star
                            translate(left = p.x, top = p.y) {
                                rotate(degrees = p.rotation, pivot = Offset.Zero) {
                                    scale(scale = p.radius * 1.5f, pivot = Offset.Zero) {
                                        drawPath(
                                            path = starPath,
                                            color = p.color.copy(alpha = p.alpha)
                                        )
                                    }
                                }
                            }
                        }
                        3 -> { // Ribbon
                            rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                                drawRect(
                                    color = p.color.copy(alpha = p.alpha),
                                    topLeft = Offset(p.x - p.radius, p.y - p.radius / 3f),
                                    size = Size(p.radius * 2, p.radius * 0.6f)
                                )
                            }
                        }
                        4 -> { // Emoji
                            if (p.emoji != null) {
                                rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
                                    val paint = android.graphics.Paint().apply {
                                        textSize = p.radius * 2.5f
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        isAntiAlias = true
                                        alpha = (p.alpha * 255).toInt().coerceIn(0, 255)
                                    }
                                    drawContext.canvas.nativeCanvas.drawText(
                                        p.emoji,
                                        p.x,
                                        p.y,
                                        paint
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Draw floating balloons/candies
            balloons.forEach { b ->
                if (!b.popped) {
                    val paint = android.graphics.Paint().apply {
                        textSize = b.size
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        b.emoji,
                        b.x,
                        b.y,
                        paint
                    )
                }
            }
        }
    }
}

@Composable
fun BouncyStarsRow(visible: Boolean) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        var start1 by remember { mutableStateOf(false) }
        var start2 by remember { mutableStateOf(false) }
        var start3 by remember { mutableStateOf(false) }

        LaunchedEffect(visible) {
            if (visible) {
                delay(300)
                start1 = true
                delay(300)
                start2 = true
                delay(300)
                start3 = true
            } else {
                start1 = false
                start2 = false
                start3 = false
            }
        }

        AnimatedStarBadge(show = start1, scaleFactor = 0.9f, badgeText = "⭐")
        Spacer(modifier = Modifier.width(12.dp))
        AnimatedStarBadge(show = start2, scaleFactor = 1.3f, badgeText = "🌟") // Center star is large
        Spacer(modifier = Modifier.width(12.dp))
        AnimatedStarBadge(show = start3, scaleFactor = 0.9f, badgeText = "⭐")
    }
}

@Composable
fun AnimatedStarBadge(show: Boolean, scaleFactor: Float, badgeText: String) {
    val scale by animateFloatAsState(
        targetValue = if (show) scaleFactor else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "starScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "star_sway")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Text(
        text = badgeText,
        fontSize = 54.sp,
        modifier = Modifier
            .scale(scale)
            .graphicsLayer {
                rotationZ = rotation
            }
            .padding(horizontal = 4.dp)
    )
}
