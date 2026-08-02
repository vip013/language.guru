package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Singleton background state to let children dynamically change background theme from any screen!
object BackgroundThemeState {
    val currentTheme = androidx.compose.runtime.mutableStateOf("jungle") // "jungle", "space", "candy", "ocean"
    var onThemeChanged: ((String) -> Unit)? = null
}

@Composable
fun AnimalBackgroundContainer(
    modifier: Modifier = Modifier,
    showAnimals: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        val infiniteTransition = rememberInfiniteTransition(label = "background_animals")

        val monkeyY by infiniteTransition.animateFloat(
            initialValue = -12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200),
                repeatMode = RepeatMode.Reverse
            ),
            label = "monkey"
        )

        val bearY by infiniteTransition.animateFloat(
            initialValue = -15f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bear"
        )

        val koalaY by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(1900),
                repeatMode = RepeatMode.Reverse
            ),
            label = "koala"
        )

        val elephantY by infiniteTransition.animateFloat(
            initialValue = -18f,
            targetValue = 18f,
            animationSpec = infiniteRepeatable(
                animation = tween(3300),
                repeatMode = RepeatMode.Reverse
            ),
            label = "elephant"
        )

        val lionY by infiniteTransition.animateFloat(
            initialValue = -14f,
            targetValue = 14f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "lion"
        )

        val tigerY by infiniteTransition.animateFloat(
            initialValue = -16f,
            targetValue = 16f,
            animationSpec = infiniteRepeatable(
                animation = tween(2600),
                repeatMode = RepeatMode.Reverse
            ),
            label = "tiger"
        )

        val pandaY by infiniteTransition.animateFloat(
            initialValue = -11f,
            targetValue = 11f,
            animationSpec = infiniteRepeatable(
                animation = tween(2100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "panda"
        )

        // Check if dark mode is currently active
        val isDark = MaterialTheme.colorScheme.background == Color(0xFF0F0B18)
        val activeTheme = BackgroundThemeState.currentTheme.value

        // Multi-color beautiful vibrant sky gradient background for kids
        val kidsGradient = when (activeTheme) {
            "space" -> {
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF06030F), // Absolute deep space
                            Color(0xFF0F0B29), // Mystical nebula purple
                            Color(0xFF1D0E47), // Deep purple-indigo
                            Color(0xFF030C22)  // Bottom space shadow
                        )
                    } else {
                        listOf(
                            Color(0xFF1A237E), // Space Blue (Top)
                            Color(0xFF3F51B5), // Indigo
                            Color(0xFF9FA8DA), // Soft Periwinkle
                            Color(0xFFE8EAF6)  // Cosmic Starlight (Bottom)
                        )
                    }
                )
            }
            "candy" -> {
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF2D0B2E), // Plummy dark candy sky
                            Color(0xFF4A148C), // Violet
                            Color(0xFF880E4F), // Magenta grape
                            Color(0xFF3E2723)  // Milk chocolate (Bottom)
                        )
                    } else {
                        listOf(
                            Color(0xFFFCE4EC), // Creamy Pastel Pink (Top)
                            Color(0xFFFFF1F2), // Cotton Candy
                            Color(0xFFFFF9C4), // Golden Honey
                            Color(0xFFFFE0B2)  // Sweet Cream Peach (Bottom)
                        )
                    }
                )
            }
            "ocean" -> {
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF020E25), // Midnight dark ocean surface
                            Color(0xFF003057), // Deep blue water
                            Color(0xFF004D40), // Dark seaweed green
                            Color(0xFF01120B)  // Ocean floor shadow (Bottom)
                        )
                    } else {
                        listOf(
                            Color(0xFF00E5FF), // Bright Aquatic Cyan (Top)
                            Color(0xFF00B0FF), // Clear Coral Sea Blue
                            Color(0xFF006064), // Emerald green water
                            Color(0xFF004D40)  // Deep Coral sea floor (Bottom)
                        )
                    }
                )
            }
            else -> { // "jungle"
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF0C0720), // Deep space violet (Top)
                            Color(0xFF1B0D3A), // Dark indigo-purple aurora
                            Color(0xFF0B2E26), // Glowing neon dark teal
                            Color(0xFF240415)  // Deep neon dark magenta (Bottom)
                        )
                    } else {
                        listOf(
                            Color(0xFF80D8FF), // Vibrant Sky Blue (Top)
                            Color(0xFFE1F5FE), // Soft Blue
                            Color(0xFFFFF9C4), // Golden Sun Glow
                            Color(0xFFE8F5E9)  // Fresh light pasture green (Bottom)
                        )
                    }
                )
            }
        }

        // Draw the beautiful, colorful landscape in a Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(kidsGradient)
        ) {
            val width = size.width
            val height = size.height

            when (activeTheme) {
                "space" -> {
                    // Draw Cosmic Space Landscape!
                    // 1. Draw a glowing Moon
                    val moonCenter = Offset(width * 0.85f, height * 0.12f)
                    val moonRadius = 40.dp.toPx()
                    drawCircle(
                        color = Color(0xFFFFF59D).copy(alpha = 0.25f),
                        radius = moonRadius * 1.3f,
                        center = moonCenter
                    )
                    drawCircle(
                        color = Color(0xFFFFF9C4),
                        radius = moonRadius,
                        center = moonCenter
                    )
                    // Moon craters
                    drawCircle(color = Color(0xFFF0F4C3).copy(alpha = 0.8f), radius = moonRadius * 0.2f, center = Offset(moonCenter.x - moonRadius*0.3f, moonCenter.y - moonRadius*0.2f))
                    drawCircle(color = Color(0xFFF0F4C3).copy(alpha = 0.8f), radius = moonRadius * 0.15f, center = Offset(moonCenter.x + moonRadius*0.2f, moonCenter.y + moonRadius*0.3f))
                    drawCircle(color = Color(0xFFF0F4C3).copy(alpha = 0.8f), radius = moonRadius * 0.1f, center = Offset(moonCenter.x - moonRadius*0.1f, moonCenter.y + moonRadius*0.4f))

                    // 2. Draw Twinkling Stars & Constellations
                    val starPositions = listOf(
                        Offset(width * 0.1f, height * 0.08f),
                        Offset(width * 0.3f, height * 0.22f),
                        Offset(width * 0.7f, height * 0.05f),
                        Offset(width * 0.5f, height * 0.15f),
                        Offset(width * 0.25f, height * 0.45f),
                        Offset(width * 0.8f, height * 0.52f),
                        Offset(width * 0.9f, height * 0.35f),
                        Offset(width * 0.15f, height * 0.65f),
                        Offset(width * 0.6f, height * 0.72f)
                    )
                    starPositions.forEachIndexed { i, pos ->
                        val sizeStar = if (i % 2 == 0) 4.dp.toPx() else 6.dp.toPx()
                        drawCircle(
                            color = Color.White.copy(alpha = if (i % 3 == 0) 0.9f else 0.5f),
                            radius = sizeStar,
                            center = pos
                        )
                    }

                    // 3. Draw a Saturn Planet
                    val saturnCenter = Offset(width * 0.25f, height * 0.32f)
                    val saturnRadius = 18.dp.toPx()
                    drawCircle(
                        color = Color(0xFFFFCC80),
                        radius = saturnRadius,
                        center = saturnCenter
                    )
                    // Saturn Rings
                    drawArc(
                        color = Color(0xFFFFE082).copy(alpha = 0.8f),
                        startAngle = -30f,
                        sweepAngle = 240f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx()),
                        topLeft = Offset(saturnCenter.x - saturnRadius * 1.8f, saturnCenter.y - saturnRadius * 0.4f),
                        size = Size(saturnRadius * 3.6f, saturnRadius * 0.8f)
                    )

                    // 4. Draw Cosmic Nebulae clouds
                    drawCosmicCloud(width * 0.45f, height * 0.45f, 1.2f)
                    drawCosmicCloud(width * 0.85f, height * 0.75f, 0.8f)
                }
                "candy" -> {
                    // Draw Sweet Candy Land Landscape!
                    // 1. Draw Cotton Candy Clouds
                    drawCandyCloud(width * 0.20f, height * 0.15f, 1.1f)
                    drawCandyCloud(width * 0.75f, height * 0.28f, 0.9f)
                    drawCandyCloud(width * 0.12f, height * 0.55f, 0.8f)

                    // 2. Draw a big Rainbow
                    val rainbowCenter = Offset(width * 0.5f, height * 0.65f)
                    val baseRadius = 130.dp.toPx()
                    val bandWidth = 7.dp.toPx()
                    val candyRainbowColors = listOf(
                        Color(0xFFFF8A80), // Soft Pink
                        Color(0xFFFFD180), // Soft Peach
                        Color(0xFFFFFF8D), // Pastel Yellow
                        Color(0xFFA7FFEB), // Soft Mint
                        Color(0xFF80D8FF), // Pastel Blue
                        Color(0xFFF8BBD0)  // Cotton Candy
                    )
                    candyRainbowColors.forEachIndexed { index, color ->
                        val radius = baseRadius - (index * bandWidth)
                        drawArc(
                            color = color.copy(alpha = 0.8f),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = bandWidth),
                            topLeft = Offset(rainbowCenter.x - radius, rainbowCenter.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    // 3. Draw giant lollipops in the background hills
                    drawGiantLollipop(width * 0.22f, height * 0.88f, Color(0xFFFF4081))
                    drawGiantLollipop(width * 0.78f, height * 0.92f, Color(0xFF00E5FF))

                    // 4. Draw rolling strawberry chocolate hills
                    val hillPath1 = Path().apply {
                        moveTo(0f, height)
                        quadraticTo(width * 0.35f, height * 0.88f, width * 0.75f, height * 0.94f)
                        quadraticTo(width * 0.9f, height * 0.96f, width, height * 0.92f)
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    val hillPath2 = Path().apply {
                        moveTo(width * 0.25f, height)
                        quadraticTo(width * 0.65f, height * 0.84f, width, height * 0.89f)
                        lineTo(width, height)
                        lineTo(width * 0.25f, height)
                    }
                    drawPath(path = hillPath2, color = Color(0xFFF48FB1).copy(alpha = 0.7f)) // Strawberry Pink
                    drawPath(path = hillPath1, color = Color(0xFFCE93D8).copy(alpha = 0.8f)) // Lavender Grape
                }
                "ocean" -> {
                    // Draw Underwater Ocean Paradise Landscape!
                    // 1. Draw glowing translucent water bubbles rising
                    val bubblePositions = listOf(
                        Offset(width * 0.15f, height * 0.85f),
                        Offset(width * 0.18f, height * 0.65f),
                        Offset(width * 0.45f, height * 0.75f),
                        Offset(width * 0.42f, height * 0.45f),
                        Offset(width * 0.78f, height * 0.90f),
                        Offset(width * 0.82f, height * 0.60f),
                        Offset(width * 0.80f, height * 0.30f),
                        Offset(width * 0.52f, height * 0.20f),
                        Offset(width * 0.25f, height * 0.40f)
                    )
                    bubblePositions.forEachIndexed { i, pos ->
                        val radius = if (i % 2 == 0) 8.dp.toPx() else 14.dp.toPx()
                        // Outer bubble border
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f),
                            radius = radius,
                            center = pos,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        // Inner bubble highlight
                        drawCircle(
                            color = Color.White.copy(alpha = 0.15f),
                            radius = radius * 0.7f,
                            center = pos
                        )
                    }

                    // 2. Draw animated seaweed kelp at the seabed
                    drawSeaweed(width * 0.12f, height, 80.dp.toPx(), Color(0xFF00E676))
                    drawSeaweed(width * 0.28f, height, 110.dp.toPx(), Color(0xFF00C853))
                    drawSeaweed(width * 0.72f, height, 95.dp.toPx(), Color(0xFF2E7D32))
                    drawSeaweed(width * 0.88f, height, 75.dp.toPx(), Color(0xFF00E676))

                    // 3. Draw a starfish resting on the seabed
                    val starfishCenter = Offset(width * 0.35f, height * 0.95f)
                    drawCircle(color = Color(0xFFFF7043), radius = 10.dp.toPx(), center = starfishCenter)

                    // 4. Draw Sandy Seabed
                    val bedPath = Path().apply {
                        moveTo(0f, height)
                        quadraticTo(width * 0.5f, height * 0.92f, width, height * 0.96f)
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    drawPath(path = bedPath, color = Color(0xFFFFE082).copy(alpha = 0.85f)) // Beautiful golden sand
                }
                else -> {
                    // Default: Sunny Safari theme (Jungle)
                    // 1. Draw a beautiful smiling Sun with rays
                    val sunCenter = Offset(width * 0.85f, height * 0.12f)
                    val sunRadius = 40.dp.toPx()
                    // Sun Glow
                    drawCircle(
                        color = Color(0xFFFFD54F).copy(alpha = 0.4f),
                        radius = sunRadius * 1.4f,
                        center = sunCenter
                    )
                    // Sun Body
                    drawCircle(
                        color = Color(0xFFFFB300),
                        radius = sunRadius,
                        center = sunCenter
                    )
                    // Sun Rays (8 rays)
                    val rayLength = 20.dp.toPx()
                    val rayThickness = 4.dp.toPx()
                    for (i in 0 until 8) {
                        val angle = Math.toRadians(i * 45.0)
                        val startX = sunCenter.x + (sunRadius + 5.dp.toPx()) * Math.cos(angle).toFloat()
                        val startY = sunCenter.y + (sunRadius + 5.dp.toPx()) * Math.sin(angle).toFloat()
                        val endX = startX + rayLength * Math.cos(angle).toFloat()
                        val endY = startY + rayLength * Math.sin(angle).toFloat()
                        drawLine(
                            color = Color(0xFFFF8F00),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = rayThickness
                        )
                    }

                    // 2. Draw a complete magnificent Rainbow
                    val rainbowCenter = Offset(width * 0.5f, height * 0.65f)
                    val baseRadius = 140.dp.toPx()
                    val bandWidth = 8.dp.toPx()
                    val rainbowColors = listOf(
                        Color(0xFFFF1744), // Red
                        Color(0xFFFF9100), // Orange
                        Color(0xFFFFEA00), // Yellow
                        Color(0xFF00E676), // Green
                        Color(0xFF2979FF), // Blue
                        Color(0xFFD500F9)  // Violet
                    )
                    rainbowColors.forEachIndexed { index, color ->
                        val radius = baseRadius - (index * bandWidth)
                        drawArc(
                            color = color.copy(alpha = 0.65f), // Glowing vibrant rainbow
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = bandWidth),
                            topLeft = Offset(rainbowCenter.x - radius, rainbowCenter.y - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    // 3. Draw fluffy cartoon Clouds
                    drawCloud(width * 0.20f, height * 0.15f, 1.1f, isDark)
                    drawCloud(width * 0.75f, height * 0.28f, 0.9f, isDark)
                    drawCloud(width * 0.12f, height * 0.55f, 0.8f, isDark)

                    // 4. Draw rolling beautiful Green Hills at the bottom
                    val hillPath1 = Path().apply {
                        moveTo(0f, height)
                        quadraticTo(width * 0.35f, height * 0.88f, width * 0.75f, height * 0.94f)
                        quadraticTo(width * 0.9f, height * 0.96f, width, height * 0.92f)
                        lineTo(width, height)
                        lineTo(0f, height)
                    }
                    val hillPath2 = Path().apply {
                        moveTo(width * 0.25f, height)
                        quadraticTo(width * 0.65f, height * 0.84f, width, height * 0.89f)
                        lineTo(width, height)
                        lineTo(width * 0.25f, height)
                    }
                    // Dark glowing hills vs. Soft green hills
                    if (isDark) {
                        drawPath(path = hillPath2, color = Color(0xFF0F3A24).copy(alpha = 0.8f)) // Deep forest emerald
                        drawPath(path = hillPath1, color = Color(0xFF1C0D30).copy(alpha = 0.9f)) // Deep midnight purple-blue
                    } else {
                        drawPath(path = hillPath2, color = Color(0xFFA5D6A7).copy(alpha = 0.8f)) // Light Lime Green
                        drawPath(path = hillPath1, color = Color(0xFF81C784).copy(alpha = 0.9f)) // Fresh Grass Green
                    }

                    // 5. Draw colorful floating balloons
                    drawBalloon(width * 0.15f, height * 0.32f, if (isDark) Color(0xFFFF2A7A) else Color(0xFFFF4081)) // Pink balloon
                    drawBalloon(width * 0.85f, height * 0.48f, if (isDark) Color(0xFF9035FF) else Color(0xFF7C4DFF)) // Purple balloon
                }
            }
        }

        if (showAnimals) {
            // Layer beautiful kids animal/object stickers on top of the drawing depending on the theme
            
            // Sticker 1 - Top Left
            val (sticker1, color1Light, color1Dark) = when (activeTheme) {
                "space" -> Triple("🚀", Color(0xFFBBDEFB), Color(0xFF0D47A1))
                "candy" -> Triple("🍭", Color(0xFFF8BBD0), Color(0xFF880E4F))
                "ocean" -> Triple("🐬", Color(0xFFB2EBF2), Color(0xFF006064))
                else -> Triple("🐒", Color(0xFFE0A96D), Color(0xFF6D3B1F)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 15.dp, y = (80 + monkeyY).dp)
                    .alpha(0.8f)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color1Dark else color1Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker1, fontSize = 52.sp)
            }

            // Sticker 2 - Top Right
            val (sticker2, color2Light, color2Dark) = when (activeTheme) {
                "space" -> Triple("🛸", Color(0xFFE1BEE7), Color(0xFF4A148C))
                "candy" -> Triple("🍩", Color(0xFFFFE082), Color(0xFFE65100))
                "ocean" -> Triple("🦈", Color(0xFFCFD8DC), Color(0xFF37474F))
                else -> Triple("🐻", Color(0xFFBCAAA4), Color(0xFF4E3629)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-15).dp, y = (190 + bearY).dp)
                    .alpha(0.8f)
                    .size(95.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color2Dark else color2Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker2, fontSize = 54.sp)
            }

            // Sticker 3 - Middle Left
            val (sticker3, color3Light, color3Dark) = when (activeTheme) {
                "space" -> Triple("👽", Color(0xFFC8E6C9), Color(0xFF1B5E20))
                "candy" -> Triple("🍬", Color(0xFFD1C4E9), Color(0xFF311B92))
                "ocean" -> Triple("🐙", Color(0xFFFFCDD2), Color(0xFFB71C1C))
                else -> Triple("🐨", Color(0xFFB39DDB), Color(0xFF3B2E5C)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 10.dp, y = (-70 + koalaY).dp)
                    .alpha(0.8f)
                    .size(85.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color3Dark else color3Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker3, fontSize = 48.sp)
            }

            // Sticker 4 - Middle Right
            val (sticker4, color4Light, color4Dark) = when (activeTheme) {
                "space" -> Triple("🧑‍🚀", Color(0xFFFFECB3), Color(0xFFFF6F00))
                "candy" -> Triple("🦄", Color(0xFFF8BBD0), Color(0xFF4A148C))
                "ocean" -> Triple("🐳", Color(0xFFE0F7FA), Color(0xFF006064))
                else -> Triple("🐘", Color(0xFF64B5F6), Color(0xFF1D3B5C)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-15).dp, y = (90 + elephantY).dp)
                    .alpha(0.8f)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color4Dark else color4Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker4, fontSize = 58.sp)
            }

            // Sticker 5 - Bottom Left
            val (sticker5, color5Light, color5Dark) = when (activeTheme) {
                "space" -> Triple("🪐", Color(0xFFFFCC80), Color(0xFFE65100))
                "candy" -> Triple("🧁", Color(0xFFFCE4EC), Color(0xFFC2185B))
                "ocean" -> Triple("🐠", Color(0xFFFFF9C4), Color(0xFFF57F17))
                else -> Triple("🦁", Color(0xFFFFD54F), Color(0xFF6B5115)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 15.dp, y = (-120 + lionY).dp)
                    .alpha(0.8f)
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color5Dark else color5Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker5, fontSize = 52.sp)
            }

            // Sticker 6 - Bottom Right
            val (sticker6, color6Light, color6Dark) = when (activeTheme) {
                "space" -> Triple("🌕", Color(0xFFFFF9C4), Color(0xFFF57F17))
                "candy" -> Triple("🍦", Color(0xFFFFE082), Color(0xFF4E3629))
                "ocean" -> Triple("🐡", Color(0xFFFFE082), Color(0xFFE65100))
                else -> Triple("🐯", Color(0xFFFF8A65), Color(0xFF6B2B15)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-15).dp, y = (-70 + tigerY).dp)
                    .alpha(0.8f)
                    .size(95.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color6Dark else color6Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker6, fontSize = 54.sp)
            }

            // Sticker 7 - Center Background
            val (sticker7, color7Light, color7Dark) = when (activeTheme) {
                "space" -> Triple("🌠", Color(0xFFE0F2F1), Color(0xFF004D40))
                "candy" -> Triple("🍪", Color(0xFFD7CCC8), Color(0xFF3E2723))
                "ocean" -> Triple("🪼", Color(0xFFE1BEE7), Color(0xFF4A148C))
                else -> Triple("🐼", Color(0xFF81C784), Color(0xFF1C3A1D)) // jungle
            }
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-150 + pandaY).dp)
                    .alpha(0.8f)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(if (isDark) color7Dark else color7Light),
                contentAlignment = Alignment.Center
            ) {
                Text(text = sticker7, fontSize = 62.sp)
            }
        }

        // A beautiful soft semi-transparent glazed overlay that ensures letters and buttons 
        // stand out with amazing contrast in both Light and Dark modes without blending with the busy background!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) {
                        Color(0xFF0F0B18).copy(alpha = 0.45f) // Ultra-deep indigo glaze
                    } else {
                        Color(0xFFFFFDF6).copy(alpha = 0.35f) // Cream buttery glaze
                    }
                )
        )

        content()
    }
}

// Helper function to draw a fluffy cloud with circles
private fun DrawScope.drawCloud(x: Float, y: Float, scale: Float, isDark: Boolean = true) {
    val r = 24.dp.toPx() * scale
    val color = if (isDark) Color(0xFFD3BFF5).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.88f)
    drawCircle(color = color, radius = r, center = Offset(x, y))
    drawCircle(color = color, radius = r * 1.3f, center = Offset(x + r * 0.8f, y - r * 0.2f))
    drawCircle(color = color, radius = r * 0.9f, center = Offset(x + r * 1.6f, y))
    drawCircle(color = color, radius = r * 1.1f, center = Offset(x + r * 0.8f, y + r * 0.2f))
}

// Helper function to draw a cute balloon with a thread
private fun DrawScope.drawBalloon(x: Float, y: Float, color: Color) {
    val balloonRadius = 14.dp.toPx()
    // Balloon body
    drawCircle(color = color.copy(alpha = 0.65f), radius = balloonRadius, center = Offset(x, y))
    // Tiny triangle base of balloon
    val trianglePath = Path().apply {
        moveTo(x, y + balloonRadius)
        lineTo(x - 3.dp.toPx(), y + balloonRadius + 5.dp.toPx())
        lineTo(x + 3.dp.toPx(), y + balloonRadius + 5.dp.toPx())
        close()
    }
    drawPath(path = trianglePath, color = color.copy(alpha = 0.65f))
    // Wavy balloon string/thread
    val stringPath = Path().apply {
        moveTo(x, y + balloonRadius + 5.dp.toPx())
        cubicTo(
            x - 5.dp.toPx(), y + balloonRadius + 15.dp.toPx(),
            x + 5.dp.toPx(), y + balloonRadius + 25.dp.toPx(),
            x, y + balloonRadius + 35.dp.toPx()
        )
    }
    drawPath(path = stringPath, color = Color.Gray.copy(alpha = 0.4f), style = Stroke(width = 1.5.dp.toPx()))
}

// Custom Space drawing helpers
private fun DrawScope.drawCosmicCloud(x: Float, y: Float, scale: Float) {
    val r = 28.dp.toPx() * scale
    val color = Color(0xFF7E57C2).copy(alpha = 0.25f) // Glowing soft purple aura
    drawCircle(color = color, radius = r, center = Offset(x, y))
    drawCircle(color = color, radius = r * 1.4f, center = Offset(x + r * 0.8f, y))
    drawCircle(color = color, radius = r * 1.1f, center = Offset(x + r * 1.5f, y + r * 0.2f))
}

// Custom Candy drawing helpers
private fun DrawScope.drawCandyCloud(x: Float, y: Float, scale: Float) {
    val r = 24.dp.toPx() * scale
    val color = Color(0xFFF8BBD0).copy(alpha = 0.75f) // Cotton candy pink
    val highlightColor = Color(0xFFFCE4EC).copy(alpha = 0.8f) // Softer cream highlight
    drawCircle(color = color, radius = r, center = Offset(x, y))
    drawCircle(color = color, radius = r * 1.3f, center = Offset(x + r * 0.7f, y - r * 0.1f))
    drawCircle(color = color, radius = r * 0.9f, center = Offset(x + r * 1.5f, y))
    drawCircle(color = highlightColor, radius = r * 0.5f, center = Offset(x + r * 0.3f, y - r * 0.4f))
}

private fun DrawScope.drawGiantLollipop(x: Float, y: Float, color: Color) {
    val r = 22.dp.toPx()
    // Stick
    drawLine(
        color = Color(0xFFD7CCC8),
        start = Offset(x, y),
        end = Offset(x, y + 45.dp.toPx()),
        strokeWidth = 3.dp.toPx()
    )
    // Lollipop Head
    drawCircle(color = color, radius = r, center = Offset(x, y))
    // Lollipop swirl
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = r * 0.6f,
        center = Offset(x, y),
        style = Stroke(width = 2.5.dp.toPx())
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = r * 0.25f,
        center = Offset(x, y),
        style = Stroke(width = 2.dp.toPx())
    )
}

// Custom Ocean drawing helpers
private fun DrawScope.drawSeaweed(x: Float, seabedY: Float, kelpHeight: Float, color: Color) {
    val path = Path().apply {
        moveTo(x, seabedY)
        cubicTo(
            x - 12.dp.toPx(), seabedY - kelpHeight * 0.3f,
            x + 12.dp.toPx(), seabedY - kelpHeight * 0.7f,
            x, seabedY - kelpHeight
        )
        // Thickness
        lineTo(x + 4.dp.toPx(), seabedY - kelpHeight)
        cubicTo(
            x + 16.dp.toPx(), seabedY - kelpHeight * 0.7f,
            x - 8.dp.toPx(), seabedY - kelpHeight * 0.3f,
            x + 6.dp.toPx(), seabedY
        )
        close()
    }
    drawPath(path = path, color = color.copy(alpha = 0.75f))
}

