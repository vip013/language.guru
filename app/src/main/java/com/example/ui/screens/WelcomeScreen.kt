package com.example.ui.screens

import com.example.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LanguageConfig
import com.example.data.LanguageData
import com.example.ui.components.AnimalBackgroundContainer
import com.example.ui.components.AppFooter

@Composable
fun WelcomeScreen(
    onLanguageSelected: (String) -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    viewModel: com.example.viewmodel.LearningViewModel? = null
) {
    val languages = LanguageData.languages

    // Continuous float animation for friendly floating cartoon letters / stars
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    AnimalBackgroundContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Full-bleed Top Header Box containing the Banner and the floating top items overlaid on it!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                // Beautiful high-fidelity illustration banner modeled perfectly after the uploaded image!
                LanguageGuruBrandBanner(
                    isDarkMode = isDarkMode,
                    modifier = Modifier.fillMaxSize()
                )

                // Overlay Row of decorative top floating cute items (Theme Switcher and Play & Learn Star)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left floating decorative theme toggle balloon - extremely clear and bold
                    Box(
                        modifier = Modifier
                            .offset(y = bounceY.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDarkMode) Color(0xFF251A3C) else Color(0xFFFFF9C4))
                            .border(2.dp, if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFFFB300), RoundedCornerShape(16.dp))
                            .clickable { onToggleDarkMode() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isDarkMode) "☀️" else "🌙",
                                fontSize = 15.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = if (isDarkMode) "Light" else "Dark",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkMode) Color(0xFFFFD54F) else Color(0xFFE65100)
                            )
                        }
                    }

                    // Right floating decorative star - bold and high contrast
                    Row(
                        modifier = Modifier.offset(y = -bounceY.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD600),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Play & Read!", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Black,
                            color = Color.Black,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.White.copy(alpha = 0.8f),
                                    offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }
            }

            // Bottom section containing the Theme Switcher card, text, and Grid with padding!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                val gridState = rememberLazyGridState()

                // Language Grid Selection with Custom Scrollbar and headers
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent() // Draw children first

                            val layoutInfo = gridState.layoutInfo
                            val visibleItems = layoutInfo.visibleItemsInfo
                            val totalItems = layoutInfo.totalItemsCount

                            if (totalItems > 0 && visibleItems.isNotEmpty() && totalItems > visibleItems.size) {
                                val firstVisibleItem = visibleItems.first()
                                val firstVisibleIndex = firstVisibleItem.index
                                val firstVisibleOffset = gridState.firstVisibleItemScrollOffset.toFloat()
                                val itemSize = firstVisibleItem.size.height.toFloat()

                                val estimatedRows = (totalItems + 1) / 2
                                val firstVisibleRow = firstVisibleIndex / 2
                                val totalHeightEstimate = estimatedRows * itemSize
                                val viewportHeight = layoutInfo.viewportSize.height.toFloat()

                                if (totalHeightEstimate > viewportHeight) {
                                    val denominator = totalHeightEstimate - viewportHeight
                                    val scrollPercent = if (denominator > 0f) {
                                        ((firstVisibleRow * itemSize) + firstVisibleOffset) / denominator
                                    } else {
                                        0f
                                    }
                                    val safeScrollPercent = if (scrollPercent.isNaN() || scrollPercent.isInfinite()) 0f else scrollPercent.coerceIn(0f, 1f)

                                    val scrollbarWidth = 6.dp.toPx()
                                    val scrollbarPadding = 4.dp.toPx()
                                    val trackHeight = size.height - (scrollbarPadding * 2)
                                    val thumbHeightFraction = (viewportHeight / totalHeightEstimate).coerceIn(0.15f, 1.0f)
                                    val thumbHeight = trackHeight * thumbHeightFraction
                                    val maxTranslation = trackHeight - thumbHeight
                                    val translationY = maxTranslation * safeScrollPercent

                                    // Draw track
                                    drawRoundRect(
                                        color = Color.Black.copy(alpha = 0.05f),
                                        topLeft = Offset(size.width - scrollbarWidth - scrollbarPadding, scrollbarPadding),
                                        size = Size(scrollbarWidth, trackHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(scrollbarWidth / 2)
                                    )

                                    // Draw thumb
                                    drawRoundRect(
                                        color = Color(0xFF00897B),
                                        topLeft = Offset(size.width - scrollbarWidth - scrollbarPadding, translationY + scrollbarPadding),
                                        size = Size(scrollbarWidth, thumbHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(scrollbarWidth / 2)
                                    )
                                }
                            }
                        }
                ) {
                    // Header Item containing the Theme Switcher card and Title with no gaps!
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Spacer(modifier = Modifier.height(2.dp))

                            // 🎨 Child-friendly Magic Background Theme Switcher row!
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 0.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkMode) Color(0xFF1E1730).copy(alpha = 0.85f) else Color(0xFFFFFBEB).copy(alpha = 0.88f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 2.dp,
                                    color = if (isDarkMode) Color(0xFF80D8FF).copy(alpha = 0.4f) else Color(0xFFFFD54F).copy(alpha = 0.6f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎨 Choose Background Magic Theme!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00796B),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    val themes = listOf(
                                        Triple("jungle", "🌴 Jungle", "Always Unlocked"),
                                        Triple("galaxy", "🌌 Space", "40 💎"),
                                        Triple("candy", "🍭 Candy", "50 💎"),
                                        Triple("ocean", "🐬 Ocean", "55 💎")
                                    )

                                    val unlockedThemes by (viewModel?.unlockedThemes?.collectAsState() ?: remember { mutableStateOf(setOf("default", "jungle")) })
                                    val isPremium by (viewModel?.isPremium?.collectAsState() ?: remember { mutableStateOf(false) })

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        themes.forEach { (themeId, label, costStr) ->
                                            val isUnlocked = themeId == "jungle" || unlockedThemes.contains(themeId) || isPremium
                                            val rawActiveTheme = com.example.ui.components.BackgroundThemeState.currentTheme.value
                                            val isActive = rawActiveTheme == themeId || (themeId == "galaxy" && rawActiveTheme == "space")

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        when {
                                                            isActive -> if (isDarkMode) Color(0xFFE91E63) else Color(0xFFFF4081)
                                                            isUnlocked -> if (isDarkMode) Color(0xFF311B92).copy(alpha = 0.5f) else Color(0xFFE0F2F1)
                                                            else -> if (isDarkMode) Color.Black.copy(alpha = 0.3f) else Color(0xFFEEEEEE)
                                                        }
                                                    )
                                                    .border(
                                                        width = if (isActive) 2.5.dp else 1.dp,
                                                        color = when {
                                                            isActive -> Color.White
                                                            isUnlocked -> if (isDarkMode) Color(0xFF80D8FF) else Color(0xFF009688)
                                                            else -> Color.Gray.copy(alpha = 0.5f)
                                                        },
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        if (isUnlocked) {
                                                            val mappedTheme = if (themeId == "galaxy") "space" else themeId
                                                            viewModel?.setBackgroundTheme(mappedTheme)
                                                            com.example.ui.components.BackgroundThemeState.currentTheme.value = mappedTheme
                                                        }
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = label,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = when {
                                                            isActive -> Color.White
                                                            isUnlocked -> if (isDarkMode) Color(0xFFE0F2F1) else Color(0xFF004D40)
                                                            else -> Color.Gray
                                                        }
                                                    )
                                                    if (!isUnlocked) {
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(text = "🔒", fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp)) // Perfect, extremely neat, tight spacing!

                            Text(
                                text = "Choose Your Language!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00796B), // Bright glowing neon cyan or rich dark teal
                                textAlign = TextAlign.Center,
                                style = androidx.compose.ui.text.TextStyle(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = if (isDarkMode) Color.Black.copy(alpha = 0.6f) else Color(0xFFB2DFDB).copy(alpha = 0.5f),
                                        offset = androidx.compose.ui.geometry.Offset(1.5f, 1.5f),
                                        blurRadius = 2f
                                    )
                                ),
                                modifier = Modifier.padding(top = 0.dp, bottom = 0.dp)
                            )
                        }
                    }

                    items(languages) { lang ->
                        LanguageCard(
                            lang = lang,
                            onClick = { onLanguageSelected(lang.code) }
                        )
                    }
                }

                // Bottom mascot footer
                AppFooter(modifier = Modifier.padding(bottom = 12.dp))
            } // Closes sub-column
        } // Closes outer column
    } // Closes AnimalBackgroundContainer
} // Closes welcome screen

@Composable
fun LanguageCard(
    lang: LanguageConfig,
    onClick: () -> Unit
) {
    // Generate lovely kids custom vibrant colors for cards dynamically with deep borders for high contrast
    val cardBg = when (lang.code) {
        "te" -> Color(0xFF4C0B14) // Rich dark red
        "en" -> Color(0xFF0F1E4C) // Rich dark blue
        "ta" -> Color(0xFF4C2A0B) // Rich dark brown-orange
        "hi" -> Color(0xFF0F3B1A) // Rich dark forest green
        "ar" -> Color(0xFF380F4C) // Rich dark purple
        "kn" -> Color(0xFF0B3A36) // Rich dark teal
        "ml" -> Color(0xFF4C3E0B) // Rich dark gold/mustard
        "bn" -> Color(0xFF4C1E0F) // Rich dark sienna
        "mr" -> Color(0xFF2E1A12) // Rich dark brown
        "gu" -> Color(0xFF0F364C) // Rich dark cyan
        else -> MaterialTheme.colorScheme.surface
    }

    val cardBorderColor = when (lang.code) {
        "te" -> Color(0xFFFF1744) // Glowing Red
        "en" -> Color(0xFF2979FF) // Glowing Blue
        "ta" -> Color(0xFFFF9100) // Glowing Orange
        "hi" -> Color(0xFF00E676) // Glowing Green
        "ar" -> Color(0xFFD500F9) // Glowing Purple
        "kn" -> Color(0xFF00BFA5) // Glowing Teal
        "ml" -> Color(0xFFFFD600) // Glowing Gold
        "bn" -> Color(0xFFFF3D00) // Glowing Sienna
        "mr" -> Color(0xFF8D6E63) // Glowing Light Brown
        "gu" -> Color(0xFF00E5FF) // Glowing Cyan
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp) // Taller height for beautiful 3D icons, native name and subtitle translation
            .clickable(onClick = onClick)
            .testTag("lang_card_${lang.code}"),
        shape = RoundedCornerShape(28.dp), // Rich round kid-friendly corners
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(4.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Language Mascot Graphic / Illustration
            LanguageMascotImage(langCode = lang.code)
            Spacer(modifier = Modifier.height(10.dp))
            // Native Language Text - Made extra bold with beautiful high-contrast text shadow!
            Text(
                text = lang.nativeName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.85f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 3f
                    )
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            // Parent English translation / subtitle for best accessibility & context!
            Text(
                text = lang.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = cardBorderColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun LanguageMascotImage(langCode: String) {
    // Unique color theme and elements for each language
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0F0B18)
    
    // Custom cartoon mascot emojis and letters with lovely glowing gradients
    val (nativeChar, mascotEmoji, accentColor, bgGradientColors) = when (langCode) {
        "te" -> Quad(
            "అ", 
            "🦚", 
            Color(0xFFFF1744), 
            listOf(Color(0xFFE0115F), Color(0xFFFF85A1))
        ) // Telugu: Peacock (Warm Pink & Magenta)
        "en" -> Quad(
            "A", 
            "🧸", 
            Color(0xFF2979FF), 
            listOf(Color(0xFF1E88E5), Color(0xFF82B1FF))
        ) // English: Teddy Bear (Azure Blue & Soft Blue)
        "ta" -> Quad(
            "அ", 
            "🐘", 
            Color(0xFFFF9100), 
            listOf(Color(0xFFFF6D00), Color(0xFFFFD180))
        ) // Tamil: Elephant (Sunny Orange & Gold)
        "hi" -> Quad(
            "अ", 
            "🐯", 
            Color(0xFF00E676), 
            listOf(Color(0xFF2E7D32), Color(0xFFB9F6CA))
        ) // Hindi: Tiger Cub (Forest Green & Mint)
        "ar" -> Quad(
            "أ", 
            "🐫", 
            Color(0xFFD500F9), 
            listOf(Color(0xFF9C27B0), Color(0xFFEA80FC))
        ) // Arabic: Camel (Royal Purple & Orchid Pink)
        "kn" -> Quad(
            "ಅ", 
            "🐒", 
            Color(0xFF00BFA5), 
            listOf(Color(0xFF00796B), Color(0xFFA7FFEB))
        ) // Kannada: Monkey (Rich Teal & Turquoise)
        "ml" -> Quad(
            "അ", 
            "🐿️", 
            Color(0xFFFFD600), 
            listOf(Color(0xFFF57F17), Color(0xFFFFE57F))
        ) // Malayalam: Cute Squirrel (Amber & Pale Gold)
        "bn" -> Quad(
            "অ", 
            "🦋", 
            Color(0xFFFF3D00), 
            listOf(Color(0xFFE65100), Color(0xFFFF9E80))
        ) // Bengali: Butterfly (Deep Orange & Salmon)
        "mr" -> Quad(
            "अ", 
            "🦁", 
            Color(0xFF8D6E63), 
            listOf(Color(0xFF5D4037), Color(0xFFFFCC80))
        ) // Marathi: Lion Cub (Cocoa Brown & Warm Gold)
        "gu" -> Quad(
            "અ", 
            "Parrot", // We will use "🦜" but we can put 🦜 directly
            Color(0xFF00E5FF), 
            listOf(Color(0xFF0097A7), Color(0xFF80DEEA))
        ) // Gujarati: Parrot (Cyan & Soft Blue)
        else -> Quad(
            "❓", 
            "✨", 
            Color.LightGray, 
            listOf(Color.Gray, Color.LightGray)
        )
    }

    // Friendly floating / breathing offset animation unique to this card based on langCode duration phase
    val infiniteTransition = rememberInfiniteTransition(label = "mascot_floating_$langCode")
    val duration = when (langCode) {
        "te" -> 1600
        "en" -> 1400
        "ta" -> 1800
        "hi" -> 1500
        "ar" -> 1700
        "kn" -> 1450
        "ml" -> 1650
        "bn" -> 1550
        "mr" -> 1750
        "gu" -> 1350
        else -> 1500
    }
    
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration + 200, delayMillis = 50),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(76.dp)
            .offset(y = floatOffset.dp)
            .scale(scaleFactor),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glowing/Slick Colored Circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isDark) {
                            listOf(bgGradientColors[0].copy(alpha = 0.45f), bgGradientColors[1].copy(alpha = 0.15f))
                        } else {
                            listOf(bgGradientColors[1], bgGradientColors[0].copy(alpha = 0.7f))
                        }
                    )
                )
                .border(
                    width = 3.dp,
                    color = accentColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Main Character Mascot Emoji (Prominent & Cute)
            Text(
                text = if (mascotEmoji == "Parrot") "🦜" else mascotEmoji,
                fontSize = 42.sp,
                textAlign = TextAlign.Center
            )

            // Dynamic background sparkles / stars for extra child appeal
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "✨",
                    fontSize = 11.sp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 8.dp, y = 8.dp)
                )
                Text(
                    text = "🌟",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 6.dp, y = (-6).dp)
                )
            }
        }

        // Overlapping rounded sticker badge for the Native Alphabet Letter!
        Box(
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 1.dp, y = 1.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isDark) Color(0xFF2E1C3C) else Color.White)
                .border(
                    width = 2.dp,
                    color = accentColor,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nativeChar,
                fontSize = if (langCode == "en") 16.sp else 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isDark) accentColor else bgGradientColors[0],
                textAlign = TextAlign.Center,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.15f),
                        offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                        blurRadius = 1f
                    )
                )
            )
        }
    }
}

@Composable
fun LanguageGuruBrandBanner(isDarkMode: Boolean, modifier: Modifier = Modifier) {
    val bannerBgColor = if (isDarkMode) Color(0xFF0F1B2F) else Color(0xFFE0F7FA)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp),
        colors = CardDefaults.cardColors(
            containerColor = bannerBgColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Sky blue sky pattern (if not dark mode, light vibrant blue, else deep cosmic space)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isDarkMode) {
                                  listOf(Color(0xFF0F1B2F), Color(0xFF1F3C6D))
                            } else {
                                  listOf(Color(0xFFE0F7FA), Color(0xFFFFF9C4))
                            }
                        )
                    )
            )

            // Text colors with excellent contrast
            val subtitleColor = if (isDarkMode) Color.White.copy(alpha = 0.95f) else Color(0xFF0D47A1)
            val titleColor = if (isDarkMode) Color.White else Color(0xFF1A237E)
            val shadowColor = if (isDarkMode) Color.Black.copy(alpha = 0.5f) else Color(0xFF90CAF9).copy(alpha = 0.3f)

            // 2. Large Centered Welcome Text "Welcome to Language Guru"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(top = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🍎",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "Welcome to",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = subtitleColor,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = shadowColor,
                                offset = androidx.compose.ui.geometry.Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(2.dp))
                
                // Rainbow arched/curved beautiful text: "Language Guru" (Multi-colored letters)
                ArchedRainbowText(
                    text = "Language Guru",
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Purple pill container "LEARN • PLAY • SPEAK"
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6200EE))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LEARN • PLAY • SPEAK",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Orange circle with "अ"
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = 120.dp)
                    .background(Color(0xFFFF9800), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "अ", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }

            // Beautiful Floating Apple Fruit 🍎 below Dark Mode Toggle
            Text(
                text = "🍎",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 64.dp, y = 78.dp)
            )

            // Beautiful scattered multilingual badges placed perfectly in empty spaces
            // Telugu "అ" (Below the Dark Mode area, near the globe)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 24.dp, y = (-25).dp)
                    .background(Color(0xFFE91E63), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "అ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Tamil "அ" (Below the Play & Read star area, near the orange "अ")
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-24).dp, y = (-25).dp)
                    .background(Color(0xFFFF5722), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "அ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Kannada "ಅ" (In the upper center-left empty area)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = 68.dp, y = (-50).dp)
                    .background(Color(0xFF009688), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "ಅ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Bengali "অ" (In the upper center-right empty area)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-68).dp, y = (-50).dp)
                    .background(Color(0xFFFF9800), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "অ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Malayalam "അ" (Below and to the left of the main title column)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = (-60).dp, y = 52.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "അ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Gujarati "અ" (Below and to the right of the main title column, shifted right to prevent overlapping)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopCenter)
                    .offset(x = 110.dp, y = 52.dp)
                    .background(Color(0xFF9C27B0), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "અ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Arabic "أ" (At the bottom-center right empty space, next to the purple pill button)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomCenter)
                    .offset(x = 65.dp, y = (-12).dp)
                    .background(Color(0xFF00B0FF), CircleShape)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "أ", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }

            // Microphone icon in white circle with blue rings
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-48).dp, y = 114.dp)
                    .background(Color.White, CircleShape)
                    .border(1.5.dp, Color(0xFF1FA1FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎙️", fontSize = 14.sp)
            }

            // Globe on a stand (Left side)
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .align(Alignment.TopStart)
                    .offset(x = 16.dp, y = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌍", fontSize = 26.sp)
            }

            // Decorative floaters right at the bottom edge of the gradient banner
            // A Block at bottom left
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = 16.dp, y = (-12).dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFFFF9C4))
                    .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = Color(0xFFE53935),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Smiley Yellow Star at bottom left (slightly floating)
            Text(
                text = "⭐",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 44.dp, y = (-8).dp)
            )

            // Open Book in bottom center-left
            Text(
                text = "📖",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = (-55).dp, y = (-8).dp)
            )

            // "1 2 3" blocks at bottom right
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-44).dp, y = (-12).dp),
                horizontalArrangement = Arrangement.spacedBy(1.5.dp)
            ) {
                listOf(
                    Triple("1", Color(0xFF4CAF50), Color(0xFFE8F5E9)),
                    Triple("2", Color(0xFFE91E63), Color(0xFFFCE4EC)),
                    Triple("3", Color(0xFF2196F3), Color(0xFFE3F2FD))
                ).forEach { (num, border, bg) ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(bg)
                            .border(0.75.dp, border, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = num, color = border, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            // Cute grey elephant at bottom right
            Text(
                text = "🐘",
                fontSize = 28.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-12).dp, y = (-8).dp)
            )
        }
    }
}

@Composable
private fun SpeechBubble(
    text: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 2.dp))
            .background(bgColor)
            .border(
                1.5.dp, 
                Color.White.copy(alpha = 0.8f), 
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 2.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun ArchedRainbowText(text: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "color_shift")
    val colorOffsetFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 9f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "color_index"
    )
    val colorOffset = colorOffsetFloat.toInt()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val totalChars = text.length
        val peakOffset = 18f // peak upward arch offset in dp for perfect curvature
        val rainbowColors = listOf(
            Color(0xFFFF1744), // Red
            Color(0xFFFF5722), // Deep Orange
            Color(0xFFFF9100), // Orange
            Color(0xFFFFEA00), // Yellow
            Color(0xFF00E676), // Green
            Color(0xFF00B0FF), // Light Blue
            Color(0xFF2979FF), // Indigo Blue
            Color(0xFF651FFF), // Purple/Violet
            Color(0xFFE91E63)  // Pink
        )

        text.forEachIndexed { index, char ->
            val angle = (index.toFloat() / (totalChars - 1).coerceAtLeast(1)) * Math.PI
            val yOffset = -kotlin.math.sin(angle) * peakOffset
            val color = rainbowColors[(index + colorOffset) % rainbowColors.size]
            
            Text(
                text = char.toString(),
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = color,
                modifier = Modifier
                    .offset(y = yOffset.dp)
                    .padding(horizontal = 0.5.dp),
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.35f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}
