package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LearningItem
import com.example.data.LessonCategory
import com.example.data.LanguageData
import com.example.viewmodel.LearningViewModel
import com.example.ui.components.AnimalBackgroundContainer
import com.example.ui.components.AutoScaleText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import com.example.ui.components.MascotHomeworkSolverDialog
import com.example.ui.components.LetterTracingDialog
import com.example.ui.components.PronunciationCheckDialog
import androidx.compose.foundation.horizontalScroll
import com.example.ui.components.InteractiveAIAvatar
import com.example.ui.components.AppFooter
import com.example.ui.components.ConfettiCelebration
import com.example.ui.components.BouncyStarsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    viewModel: LearningViewModel
) {
    val activeCategory by viewModel.activeCategory.collectAsState()
    val activeIndex by viewModel.activeItemIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isCompleted by viewModel.isLessonCompleted.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val isVoiceOn by viewModel.isVoiceOn.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()
    val baseVoiceCharacter by viewModel.voiceCharacter.collectAsState()
    val voiceCharacter = if (activeCategory == LessonCategory.VEGETABLES) "motu" else baseVoiceCharacter
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val langCode = selectedLanguage?.code ?: "en"
    var showHomeworkSolver by remember { mutableStateOf(false) }

    val isAvatarEnabled by viewModel.isAvatarEnabled.collectAsState()
    val avatarSize by viewModel.avatarSize.collectAsState()
    val avatarPosition by viewModel.avatarPosition.collectAsState()
    val avatarExpression by viewModel.avatarExpression.collectAsState()
    val avatarAction by viewModel.avatarAction.collectAsState()
    val avatarIsSpeaking by viewModel.avatarIsSpeaking.collectAsState()
    val avatarText by viewModel.avatarText.collectAsState()
    val isAvatarMuted by viewModel.isAvatarMuted.collectAsState()
    val isAvatarFemale by viewModel.isAvatarFemale.collectAsState()
    val avatarType by viewModel.avatarType.collectAsState()

    var showQuickQuiz by remember { mutableStateOf(false) }
    var showLetterTracing by remember { mutableStateOf(false) }
    var showPronunciationCheck by remember { mutableStateOf(false) }
    var quizDecoy by remember { mutableStateOf<LearningItem?>(null) }

    val items = viewModel.getLessonItems()
    val currentItem = viewModel.getCurrentItem()
    val totalCount = items.size

    val progress = if (totalCount > 0) (activeIndex + 1).toFloat() / totalCount else 0f

    // Soft animated float for letter cards
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    AnimalBackgroundContainer(showAnimals = activeCategory != LessonCategory.LETTERS) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = activeCategory?.getTitle(viewModel.selectedLanguage.value?.code ?: "en") ?: "Lesson",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.navigateToDashboard() },
                            modifier = Modifier.testTag("back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Dashboard",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showHomeworkSolver = true },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = "🎒",
                                fontSize = 24.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            bottomBar = {
                AppFooter(
                    langCode = langCode,
                    viewModel = viewModel,
                    isNavigationMode = true
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
            if (!isCompleted && currentItem != null) {
                // ACTIVE LESSON CONTENT
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Small progress text and indicators
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Item ${activeIndex + 1} of $totalCount",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )

                            // Status Auto tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isPlaying) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isPlaying) "⚡ Playing" else "⏸️ Paused",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlaying) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    }

                    // CENTRAL GIANT DISPLAY CARD + INTERACTIVE MONKEY TEACHER
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val cardBgHex = activeCategory?.colorHex ?: "#FF9AA2"
                        val borderStrokeColor = when (activeCategory) {
                            LessonCategory.LETTERS -> Color(0xFFE53935)
                            LessonCategory.LETTER_WORD -> Color(0xFF3949AB)
                            LessonCategory.NUMBERS -> Color(0xFF00897B)
                            LessonCategory.CONSONANTS -> Color(0xFFF4511E)
                            LessonCategory.SPICES -> Color(0xFFD81B60)
                            LessonCategory.MONTHS -> Color(0xFF0288D1)
                            LessonCategory.POEMS -> Color(0xFFE040FB)
                            else -> Color(0xFFFF4081)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (activeCategory == LessonCategory.POEMS) 410.dp else 330.dp) // Dynamic height for longer poems
                                .offset(y = bounceY.dp)
                                .clickable { viewModel.replayCurrentItem() },
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(android.graphics.Color.parseColor(cardBgHex)).copy(alpha = 0.95f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(4.dp, borderStrokeColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            val scrollState = rememberScrollState()
                            androidx.compose.runtime.LaunchedEffect(currentItem) {
                                scrollState.scrollTo(0)
                            }
                            val isScrollable = scrollState.maxValue > 0

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                                    .padding(
                                        start = 16.dp,
                                        end = if (isScrollable) 28.dp else 16.dp,
                                        top = 16.dp,
                                        bottom = 16.dp
                                    )
                                    .drawWithContent {
                                        drawContent() // Draw children first

                                        if (isScrollable) {
                                            val viewportSize = scrollState.viewportSize.toFloat()
                                            val maxValue = scrollState.maxValue.toFloat()
                                            val totalHeight = maxValue + viewportSize
                                            val scrollPercent = if (maxValue > 0f) scrollState.value.toFloat() / maxValue else 0f
                                            val safeScrollPercent = if (scrollPercent.isNaN() || scrollPercent.isInfinite()) 0f else scrollPercent.coerceIn(0f, 1f)

                                            val scrollbarWidth = 6.dp.toPx()
                                            val scrollbarPadding = 12.dp.toPx()
                                            val trackHeight = size.height - (scrollbarPadding * 2)
                                            val thumbHeightFraction = (viewportSize / totalHeight).coerceIn(0.15f, 1.0f)
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
                                                color = borderStrokeColor,
                                                topLeft = Offset(size.width - scrollbarWidth - scrollbarPadding, translationY + scrollbarPadding),
                                                size = Size(scrollbarWidth, thumbHeight),
                                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(scrollbarWidth / 2)
                                            )
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val isPoem = activeCategory == LessonCategory.POEMS
                                val langCode = selectedLanguage?.code ?: "en"

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if (isAvatarEnabled) {
                                        InteractiveAIAvatar(
                                            isFemale = true,
                                            avatarType = "girl",
                                            expression = avatarExpression,
                                            isSpeaking = avatarIsSpeaking,
                                            action = avatarAction,
                                            avatarSize = "small",
                                            spokenText = avatarText,
                                            isMuted = isAvatarMuted,
                                            onToggleMute = { viewModel.toggleAvatarMuted() },
                                            onToggleGender = { viewModel.toggleAvatarGender() },
                                            modifier = Modifier
                                                .size(90.dp)
                                                .padding(end = 8.dp)
                                                .testTag("card_avatar"),
                                            showControls = false
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        // Large cartoon emoji if present
                                        if (currentItem.visualEmoji.isNotEmpty()) {
                                            Text(
                                                text = currentItem.visualEmoji,
                                                fontSize = if (isPoem) 100.sp else 60.sp,
                                                modifier = Modifier.padding(bottom = 2.dp)
                                            )
                                        }

                                        // Big visual word/letter
                                        val displayFontSize = remember(currentItem.display, activeCategory) {
                                            if (isPoem) {
                                                32.sp
                                            } else {
                                                val textLen = currentItem.display.length
                                                val isSingleCharCategory = activeCategory == LessonCategory.LETTERS ||
                                                        activeCategory == LessonCategory.NUMBERS ||
                                                        activeCategory == LessonCategory.CONSONANTS ||
                                                        activeCategory == LessonCategory.BASIC_MATH
                                                
                                                if (isSingleCharCategory) {
                                                    if (textLen <= 1) 115.sp else if (textLen <= 2) 88.sp else 65.sp
                                                } else {
                                                    // For words (colors, shapes, animals, vegetables, etc.)
                                                    if (textLen <= 3) 42.sp else if (textLen <= 5) 32.sp else if (textLen <= 8) 26.sp else 20.sp
                                                }
                                            }
                                        }
                                        
                                        val displayLineHeight = remember(displayFontSize) {
                                            (displayFontSize.value * 1.15f).sp
                                        }

                                        if (isPoem) {
                                            Text(
                                                text = currentItem.display,
                                                fontSize = displayFontSize,
                                                lineHeight = displayLineHeight,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                maxLines = 8,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                            )
                                        } else {
                                            AutoScaleText(
                                                text = currentItem.display,
                                                initialFontSize = displayFontSize,
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1,
                                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        // Pronunciation Spelling subtitle
                                        Text(
                                            text = if (langCode != "en") currentItem.subtitle.substringBefore("(").trim() else currentItem.subtitle,
                                            fontSize = if (isPoem) 16.sp else 15.sp,
                                            lineHeight = if (isPoem) 20.sp else androidx.compose.ui.unit.TextUnit.Unspecified,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.85f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                if (!isCompleted) {
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Quiz Button
                                        Button(
                                            onClick = {
                                                val decoys = items.filter { it.display != currentItem.display }
                                                quizDecoy = if (decoys.isNotEmpty()) decoys.random() else null
                                                showQuickQuiz = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("quiz_trigger_button"),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            AutoscaledButtonText(text = getLocalizedQuiz(langCode))
                                        }

                                        // Practice Button
                                        if (activeCategory == LessonCategory.LETTERS || activeCategory == LessonCategory.NUMBERS || activeCategory == LessonCategory.BASIC_MATH || activeCategory == LessonCategory.SHAPES) {
                                            Button(
                                                onClick = { showLetterTracing = true },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF004D40), // Dark rich forest teal
                                                    contentColor = Color(0xFFE0F2F1)    // Light vibrant teal text
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .testTag("tracing_trigger_button"),
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                AutoscaledButtonText(text = getLocalizedPractice(langCode))
                                            }
                                        }

                                        // Speak & Learn Button
                                        Button(
                                            onClick = { showPronunciationCheck = true },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF880E4F), // Dark rich pink magenta
                                                contentColor = Color(0xFFFCE4EC)    // Light pink text
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .testTag("pronounce_trigger_button"),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            AutoscaledButtonText(text = getLocalizedSpeakAndLearn(langCode))
                                        }
                                    }
                                }
                            }
                        }

                        if (!isAvatarEnabled) {
                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Bear Teacher Mascot pointing up at the board
                            BearTeacherMascot(
                                isPlaying = isPlaying,
                                onReplay = { viewModel.replayCurrentItem() },
                                voiceCharacter = voiceCharacter,
                                currentItem = currentItem,
                                activeCategory = activeCategory,
                                langCode = selectedLanguage?.code ?: "en"
                            )
                        }
                    }

                    // BOTTOM CONTROLS & SPEED PANEL
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Media navigation controls (Prev, Play, Next)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mute button
                            IconButton(
                                onClick = { viewModel.toggleVoiceOn() },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = if (isVoiceOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = "Mute Toggle",
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Prev button
                            IconButton(
                                onClick = { viewModel.goToPrevItem() },
                                enabled = activeIndex > 0,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (activeIndex > 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Previous Item",
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Large center Play / Pause button
                            IconButton(
                                onClick = { viewModel.togglePlayPause() },
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .testTag("play_pause_button"),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) {
                                        // Simple pause symbol drawn in custom vector or using play indicator
                                        Icons.Default.VolumeMute // let's replace with custom drawing or simple symbol
                                    } else {
                                        Icons.Default.PlayArrow
                                    },
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(36.dp)
                                )
                                // Render simple play pause indicator text as overlay to be fully intuitive
                                Text(
                                    text = if (isPlaying) "⏸️" else "▶️",
                                    fontSize = 28.sp
                                )
                            }

                            // Next button
                            IconButton(
                                onClick = { viewModel.goToNextItem() },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next Item",
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Replay button
                            IconButton(
                                onClick = { viewModel.replayCurrentItem() },
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Replay Voice",
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Speech rate control pill buttons
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Speed:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            listOf(0.75f, 1.0f, 1.25f).forEach { speed ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (voiceSpeed == speed) MaterialTheme.colorScheme.primary
                                            else Color.Transparent
                                        )
                                        .clickable { viewModel.setVoiceSpeed(speed) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${speed}x",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (voiceSpeed == speed) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 🎉 CONGRATULATIONS / CELEBRATION OVERLAY
                AnimatedVisibility(
                    visible = isCompleted,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)) // Dimmed background overlay
                    ) {
                        // Background particles explosion
                        ConfettiCelebration(
                            isCompleted = isCompleted,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Center Congratulatory Modal Card
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                                .widthIn(max = 420.dp)
                                .testTag("congratulations_modal"),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                            border = BorderStroke(
                                4.dp,
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        Color(0xFFFFD600), // Gold
                                        Color(0xFFFF4081)  // Vibrant Pink
                                    )
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🎉🎈🏆",
                                    fontSize = 52.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Text(
                                    text = when (langCode) {
                                        "te" -> "భలే చేసావు! 🌟"
                                        "ta" -> "அற்புதம்! 🌟"
                                        "hi" -> "बहुत बढ़िया! 🌟"
                                        "ar" -> "رائع جداً! 🌟"
                                        "kn" -> "ಅದ್ಭುತ ಕೆಲಸ! 🌟"
                                        "ml" -> "മികച്ച പ്രവർത്തനം! 🌟"
                                        else -> "Great Job! 🌟"
                                    },
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                val moduleTitle = activeCategory?.getTitle(langCode) ?: ""
                                val moduleIcon = activeCategory?.getIcon(langCode) ?: "📚"

                                Text(
                                    text = when (langCode) {
                                        "te" -> "నువ్వు $moduleIcon $moduleTitle పాఠాన్ని 100% పూర్తి చేసావు! బంగారు నక్షత్రాలు మరియు డైమండ్స్ గెలుచుకున్నావు! 🏆"
                                        "ta" -> "நீங்கள் $moduleIcon $moduleTitle பாடத்தை 100% முடித்துவிட்டீர்கள்! தங்க நட்சத்திரங்கள் மற்றும் வைரங்களை வென்றுள்ளீர்கள்! 🏆"
                                        "hi" -> "आपने $moduleIcon $moduleTitle पाठ को 100% पूरा कर लिया है! आपने सुनहरे सितारे और हीरे अर्जित किए हैं! 🏆"
                                        "ar" -> "لقد أكملت درس $moduleIcon $moduleTitle بنسبة 100%! لقد حصلت على نجوم ذهبية وجواهر! 🏆"
                                        "kn" -> "ನೀವು $moduleIcon $moduleTitle ಪಾಠವನ್ನು 100% ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ! ಚಿನ್ನದ ನಕ್ಷತ್ರಗಳು ಮತ್ತು ವಜ್ರಗಳನ್ನು ಗಳಿಸಿದ್ದೀರಿ! 🏆"
                                        "ml" -> "നിങ്ങൾ $moduleIcon $moduleTitle പാഠം 100% പൂർത്തിയാക്കി! നിങ്ങൾക്ക് സ്വർണ്ണ നക്ഷത്രങ്ങളും വജ്രങ്ങളും ലഭിച്ചു! 🏆"
                                        else -> "You completed 100% of $moduleIcon $moduleTitle! You earned golden stars & diamonds! 🏆"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Bouncy stars
                                BouncyStarsRow(visible = isCompleted)

                                Spacer(modifier = Modifier.height(20.dp))

                                // Diamonds earned pill display
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE3F2FD)) // Light blue background
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "💎",
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(
                                        text = if (isPremium) "+10 Diamonds Earned!" else "+5 Diamonds Earned!",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1565C0)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Actions Column
                                Button(
                                    onClick = { viewModel.learnAgain() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("learn_again_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Green for retry
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = when (langCode) {
                                            "te" -> "⭐ మళ్లీ నేర్చుకో"
                                            "ta" -> "⭐ மீண்டும் கற்க"
                                            "hi" -> "⭐ फिर से सीखें"
                                            "ar" -> "⭐ تعلم مجدداً"
                                            "kn" -> "⭐ ಮತ್ತೊಮ್ಮೆ కలి"
                                            "ml" -> "⭐ വീണ്ടും പഠിക്കുക"
                                            else -> "⭐ Learn Again"
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.nextLesson() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("next_lesson_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = when (langCode) {
                                            "te" -> "➡ తదుపరి పాఠం"
                                            "ta" -> "➡ அடுத்த பாடம்"
                                            "hi" -> "➡ अगला पाठ"
                                            "ar" -> "➡ الدرس التالي"
                                            "kn" -> "➡ ಮುಂದಿನ ಪಾಠ"
                                            "ml" -> "➡ അടുത്ത പാഠം"
                                            else -> "➡ Next Lesson"
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { viewModel.navigateToDashboard() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = when (langCode) {
                                            "te" -> "🏡 ప్రధాన పేజీకి వెళ్ళు"
                                            "ta" -> "🏡 முகப்புப் பக்கத்திற்குச் செல்"
                                            "hi" -> "🏡 मुख्य पृष्ठ पर जाएं"
                                            "ar" -> "🏡 العودة للرئيسية"
                                            "kn" -> "🏡 ಮುಖ್ಯ ಪುಟಕ್ಕೆ ಹೋಗಿ"
                                            "ml" -> "🏡 പ്രധാന പേജിലേക്ക് പോവുക"
                                            else -> "🏡 Back to Dashboard"
                                        },
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

    // Kids Quick Quiz Dialog
    if (showQuickQuiz && currentItem != null) {
        val decoy = quizDecoy
        val options = remember(currentItem, decoy) {
            if (decoy != null) {
                listOf(currentItem, decoy).shuffled()
            } else {
                listOf(currentItem)
            }
        }

        AlertDialog(
            onDismissRequest = { showQuickQuiz = false },
            title = {
                Text(
                    text = when (selectedLanguage?.code ?: "en") {
                        "te" -> "సమాధానం చెప్పండి! 🧠"
                        "ta" -> "பதில் கூறுங்கள்! 🧠"
                        "hi" -> "उत्तर दें! 🧠"
                        "ar" -> "أجب الآن! 🧠"
                        "kn" -> "ಉತ್ತರಿಸಿ! 🧠"
                        "ml" -> "ഉത്തരം പറയൂ! 🧠"
                        else -> "Can you find? 🧠"
                    },
                    fontWeight = FontWeight.Bold,
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
                        text = when (selectedLanguage?.code ?: "en") {
                            "te" -> "దీన్ని గుర్తించండి: ${currentItem.display}"
                            "ta" -> "இதைக் கண்டறியவும்: ${currentItem.display}"
                            "hi" -> "इसको पहचानें: ${currentItem.display}"
                            "ar" -> "تحديد: ${currentItem.display}"
                            "kn" -> "ಇದನ್ನು ಗುರುತಿಸಿ: ${currentItem.display}"
                            "ml" -> "ഇത് കണ്ടെത്തുക: ${currentItem.display}"
                            else -> "Tap on: ${currentItem.display} (${currentItem.subtitle})"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        options.forEach { option ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(115.dp)
                                    .clickable {
                                        if (option.display == currentItem.display) {
                                            viewModel.triggerCorrectAnswerAnimation {
                                                showQuickQuiz = false
                                            }
                                        } else {
                                            viewModel.triggerWrongAnswerAnimation()
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        if (option.visualEmoji.isNotEmpty()) {
                                            Text(option.visualEmoji, fontSize = 32.sp)
                                        }
                                        Text(
                                            option.display,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuickQuiz = false }) {
                    Text(
                        text = when (selectedLanguage?.code ?: "en") {
                            "te" -> "మూసివేయి"
                            "ta" -> "மூடு"
                            "hi" -> "बंद करें"
                            "ar" -> "إغلاق"
                            "kn" -> "ಮುಚ್ಚಿ"
                            "ml" -> "അടയ്ക്കുക"
                            else -> "Close"
                        }
                    )
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showHomeworkSolver) {
        MascotHomeworkSolverDialog(
            viewModel = viewModel,
            voiceCharacter = voiceCharacter,
            langCode = selectedLanguage?.code ?: "en",
            onDismiss = { showHomeworkSolver = false }
        )
    }

    if (showLetterTracing && currentItem != null) {
        LetterTracingDialog(
            viewModel = viewModel,
            targetItem = currentItem,
            langCode = selectedLanguage?.code ?: "en",
            voiceCharacter = voiceCharacter,
            onDismiss = { showLetterTracing = false }
        )
    }

    if (showPronunciationCheck && currentItem != null) {
        PronunciationCheckDialog(
            viewModel = viewModel,
            targetItem = currentItem,
            langCode = selectedLanguage?.code ?: "en",
            voiceCharacter = voiceCharacter,
            onDismiss = { showPronunciationCheck = false }
        )
    }
}

@Composable
fun BearTeacherMascot(
    isPlaying: Boolean,
    onReplay: () -> Unit,
    voiceCharacter: String,
    currentItem: LearningItem?,
    activeCategory: LessonCategory?,
    langCode: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bear_teacher")
    
    val stickRotation by infiniteTransition.animateFloat(
        initialValue = -22f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) 350 else 1300, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stick_wave"
    )

    val bearScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bear_bounce"
    )

    // Compute dynamic bubble text
    val bubbleText = remember(currentItem, activeCategory, isPlaying, langCode, voiceCharacter) {
        if (currentItem == null) {
            getMascotBubbleText(langCode, voiceCharacter, "repeat")
        } else {
            when {
                activeCategory == LessonCategory.LETTERS -> {
                    val disp = currentItem.display
                    if (isPlaying) {
                        getMascotBubbleText(langCode, voiceCharacter, "say_with_me", disp)
                    } else {
                        getMascotBubbleText(langCode, voiceCharacter, "hear_disp", disp)
                    }
                }
                activeCategory == LessonCategory.LETTER_WORD -> {
                    val disp = currentItem.display
                    val emoji = currentItem.visualEmoji
                    val sentence = if (disp.contains("-")) {
                        val letter = disp.substringBefore("-").trim()
                        val wordPart = disp.substringAfter("-").trim()
                        when (langCode) {
                            "te" -> "$letter అంటే $wordPart $emoji"
                            "hi" -> "$letter से $wordPart $emoji"
                            "ta" -> "$letter என்றால் $wordPart $emoji"
                            "kn" -> "$letter ಅಂದರೆ $wordPart $emoji"
                            "ml" -> "$letter എന്നാൽ $wordPart $emoji"
                            "ar" -> "$letter مثل $wordPart $emoji"
                            "bn" -> "$letter মানে $wordPart $emoji"
                            "mr" -> "$letter म्हणजे $wordPart $emoji"
                            "gu" -> "$letter એટલે $wordPart $emoji"
                            else -> "$letter for $wordPart $emoji"
                        }
                    } else {
                        "$disp $emoji"
                    }
                    if (isPlaying) {
                        sentence
                    } else {
                        getMascotBubbleText(langCode, voiceCharacter, "hear_sentence", sentence)
                    }
                }
                else -> {
                    val disp = currentItem.display
                    val sub = currentItem.subtitle
                    val emoji = currentItem.visualEmoji
                    val sentence = when (activeCategory) {
                        LessonCategory.STATES_CAPITALS -> {
                            if (emoji.isNotEmpty()) "${currentItem.voiceText} $emoji" else currentItem.voiceText
                        }
                        LessonCategory.NATIONAL_SYMBOLS -> {
                            if (emoji.isNotEmpty()) "${currentItem.voiceText} $emoji" else currentItem.voiceText
                        }
                        LessonCategory.RELATIONSHIPS, LessonCategory.FOODS, LessonCategory.FRUITS_FLOWERS, LessonCategory.BODY_PARTS -> {
                            if (langCode != "en") {
                                if (emoji.isNotEmpty()) "$disp $emoji" else disp
                            } else {
                                if (emoji.isNotEmpty()) "$disp ($sub) $emoji" else "$disp ($sub)"
                            }
                        }
                        else -> {
                            if (langCode != "en") {
                                if (emoji.isNotEmpty()) "$disp $emoji" else disp
                            } else {
                                if (emoji.isNotEmpty()) "$disp ($sub) $emoji" else "$disp ($sub)"
                            }
                        }
                    }
                    if (isPlaying) {
                        sentence
                    } else {
                        getMascotBubbleText(langCode, voiceCharacter, "hear_sentence", sentence)
                    }
                }
            }
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .scale(bearScale)
            .clickable { onReplay() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        if (voiceCharacter == "motu") {
            // Motu Mascot Layout (Bald head, large orange tummy, red vest, samosa in hand!)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(76.dp)
            ) {
                // Bald man face
                Text(
                    text = "👨‍🦲",
                    fontSize = 46.sp,
                    modifier = Modifier.align(Alignment.Center).offset(y = (-14).dp)
                )
                // Big round belly/orange kurta
                Text(
                    text = "🟠",
                    fontSize = 34.sp,
                    modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-6).dp)
                )
                // Red vest/scarf
                Text(
                    text = "🧣",
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.Center).offset(y = (-4).dp)
                )
                // Large Samosa in hand!
                Text(
                    text = "🥟",
                    fontSize = 26.sp,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 6.dp, y = (-6).dp)
                )
            }
        } else {
            // Bhalu Bear Mascot Layout
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.size(76.dp)
            ) {
                Text(
                    text = "🐻",
                    fontSize = 58.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                Text(
                    text = "👓", // Glasses overlay
                    fontSize = 20.sp,
                    modifier = Modifier.offset(x = (-8).dp, y = 20.dp)
                )
                Text(
                    text = "🎓",
                    fontSize = 22.sp,
                    modifier = Modifier.offset(x = (-10).dp, y = (-2).dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "📏",
            fontSize = 32.sp,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = stickRotation
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 1f)
                }
                .offset(y = (-14).dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        val bubbleBorderColor = if (voiceCharacter == "motu") Color(0xFFE64A19) else Color(0xFF8D6E63)
        val bubbleTitleColor = if (voiceCharacter == "motu") Color(0xFFBF360C) else Color(0xFF5D4037)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                .background(Color.White)
                .border(2.5.dp, bubbleBorderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = getMascotNameText(langCode, voiceCharacter),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = bubbleTitleColor
                )
                Text(
                    text = bubbleText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }
        }
    }
}

private fun getColorForText(text: String, index: Int): Color {
    val clean = text.trim().lowercase()
    if (clean.isEmpty()) return Color(0xFF2D251D)
    
    return when (clean) {
        "a", "అ", "ఆ" -> Color(0xFF1E88E5) // Blue
        "b", "ఇ", "ఈ" -> Color(0xFFE53935) // Red
        "c", "ఉ", "ఊ" -> Color(0xFFD81B60) // Pink
        "d", "ఋ", "ౠ" -> Color(0xFF43A047) // Green
        "e", "ఎ", "ఏ" -> Color(0xFFF57C00) // Orange
        "f", "ఐ", "ఒ" -> Color(0xFF8E24AA) // Purple
        "g", "ఓ", "ఔ" -> Color(0xFF00897B) // Teal
        "h", "అం", "అః" -> Color(0xFFE64A19) // Dark Orange
        else -> {
            // High contrast vibrant colors list
            val colorsList = listOf(
                Color(0xFF1E88E5), // Blue
                Color(0xFFE53935), // Red
                Color(0xFFD81B60), // Pink
                Color(0xFF43A047), // Green
                Color(0xFFF57C00), // Orange
                Color(0xFF8E24AA), // Purple
                Color(0xFF00897B), // Teal
                Color(0xFFE64A19), // Dark Orange
                Color(0xFF3F51B5), // Indigo
                Color(0xFF00ACC1), // Cyan
                Color(0xFF5E35B1)  // Violet
            )
            val pos = (index + clean.hashCode()).let { if (it < 0) -it else it }
            colorsList[pos % colorsList.size]
        }
    }
}

private fun getMascotNameText(langCode: String, voiceCharacter: String): String {
    if (voiceCharacter == "guru") {
        return when (langCode) {
            "te" -> "లాంగ్వేజ్ గురు 👩‍🏫✨:"
            "hi" -> "लैंग्वेज गुरु 👩‍🏫✨:"
            "ta" -> "லாங்குவேஜ் குரு 👩‍🏫✨:"
            "kn" -> "ಲ್ಯಾಂಗ್ವೇಜ್ ಗುರು 👩‍🏫✨:"
            "ml" -> "ലാംഗ്വേజ్ ഗുരു 👩‍🏫✨:"
            "bn" -> "ল্যাঙ্গুয়েজ গুরু 👩‍🏫✨:"
            "mr" -> "लँग्वेज गुरु 👩‍🏫✨:"
            "gu" -> "લેંગ્વેજ ગુરુ 👩‍🏫✨:"
            "ar" -> "معلم اللغة 👩‍🏫✨:"
            else -> "Language Guru 👩‍🏫✨:"
        }
    }
    return if (voiceCharacter == "motu") {
        when (langCode) {
            "te" -> "మోటు 🥟🧡:"
            "hi" -> "मोटू 🥟🧡:"
            "ta" -> "மோட்டு 🥟🧡:"
            "kn" -> "ಮೋಟು 🥟🧡:"
            "ml" -> "മോട്ടു 🥟🧡:"
            "bn" -> "মোটু 🥟🧡:"
            "mr" -> "मोटू 🥟🧡:"
            "gu" -> "મોટુ 🥟🧡:"
            "ar" -> "موتو 🥟🧡:"
            else -> "Motu 🥟🧡:"
        }
    } else {
        when (langCode) {
            "te" -> "గురువుగారు ఎలుగుబంటి 🐻:"
            "hi" -> "शिक्षक भालू 🐻:"
            "ta" -> "ஆசிரியர் கரடி 🐻:"
            "kn" -> "ಗುರುಗಳು ಕರಡಿ 🐻:"
            "ml" -> "ಗುരു കരടി 🐻:"
            "bn" -> "গুরু ভাল্লুক 🐻:"
            "mr" -> "शिक्षक अस्वल 🐻:"
            "gu" -> "શિક્ષક રીંછ 🐻:"
            "ar" -> "المعلم دب 🐻:"
            else -> "Teacher Bear 🐻:"
        }
    }
}

private fun getMascotBubbleText(
    langCode: String,
    voiceCharacter: String,
    type: String,
    value: String = ""
): String {
    val isMotu = voiceCharacter == "motu"
    return when (type) {
        "repeat" -> {
            if (isMotu) {
                when (langCode) {
                    "te" -> "మళ్ళీ వినడానికి నన్ను నొక్కండి, భయ్యా! 🥟✨"
                    "hi" -> "दोहराने के लिए मुझे दबाएं, भैया! 🥟✨"
                    "ta" -> "திரும்பக் கேட்க என்னைத் தட்டவும், பையா! 🥟✨"
                    "kn" -> "ಮತ್ತೆ ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ, ಭಯ್ಯಾ! 🥟✨"
                    "ml" -> "വീണ്ടും കേൾക്കാൻ എന്നെ അമർത്തൂ, ഭയ്യാ! 🥟✨"
                    "ar" -> "اضغط عليّ للإعادة يا أخي! 🥟✨"
                    "bn" -> "আবার শোনার জন্য আমাকে টিপুন, ভাইয়া! 🥟✨"
                    "mr" -> "पुन्हा ऐकण्यासाठी मला दाबा, भैय्या! 🥟✨"
                    "gu" -> "ફરીથી साંભળવા માટે મને દબાવો, ભાઈ! 🥟✨"
                    else -> "Tap me to repeat, bhaiya! 🥟✨"
                }
            } else {
                when (langCode) {
                    "te" -> "మళ్ళీ వినడానికి నన్ను నొక్కండి! 🐻🗣️"
                    "hi" -> "दोहराने के लिए मुझे दबाएं! 🐻🗣️"
                    "ta" -> "திரும்பக் கேட்க என்னைத் தட்டவும்! 🐻🗣️"
                    "kn" -> "ಮತ್ತೆ ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ! 🐻🗣️"
                    "ml" -> "വീണ്ടും കേൾക്കാൻ എന്നെ അമർത്തൂ! 🐻🗣️"
                    "ar" -> "اضغط عليّ للإعادة! 🐻🗣️"
                    "bn" -> "আবার শোনার জন্য আমাকে টিপুন! 🐻🗣️"
                    "mr" -> "पुन्हा ऐकण्यासाठी मला दाबा! 🐻🗣️"
                    "gu" -> "ફરીથી સાંભળવા માટે મને દબાવો! 🐻🗣️"
                    else -> "Tap me to repeat! 🐻🗣️"
                }
            }
        }
        "say_with_me" -> {
            if (isMotu) {
                when (langCode) {
                    "te" -> "నాతో పాటు '$value' అని చెప్పండి, భయ్యా! 🥟✨"
                    "hi" -> "मेरे साथ '$value' बोलो, भैया! 🥟✨"
                    "ta" -> "என்னோடு சேர்ந்து '$value' சொல்லுங்கள், பையா! 🥟✨"
                    "kn" -> "ನನ್ನ ಜೊತೆ '$value' ಹೇಳಿ, ಭಯ್ಯಾ! 🥟✨"
                    "ml" -> "എന്റെ കൂടെ '$value' പറയൂ, ഭയ്യാ! 🥟✨"
                    "ar" -> "قل معى '$value' يا أخي! 🥟✨"
                    "bn" -> "আমার সাথে বলুন '$value', ভাইয়া! 🥟✨"
                    "mr" -> "माझ्यासोबत '$value' म्हणा, भैय्या! 🥟✨"
                    "gu" -> "મારી સાથે '$value' બોલો, ભાઈ! 🥟✨"
                    else -> "Say '$value' with me, bhaiya! 🥟✨"
                }
            } else {
                when (langCode) {
                    "te" -> "'$value' వినండి! 🐻👂"
                    "hi" -> "'$value' सुनो! 🐻👂"
                    "ta" -> "'$value' கேளுங்கள்! 🐻👂"
                    "kn" -> "'$value' ಕೇಳಿ! 🐻👂"
                    "ml" -> "'$value' കേൾക്കൂ! 🐻👂"
                    "ar" -> "استمع إلى '$value'! 🐻👂"
                    "bn" -> "'$value' শুনুন! 🐻👂"
                    "mr" -> "'$value' ऐका! 🐻👂"
                    "gu" -> "'$value' સાંભળો! 🐻👂"
                    else -> "Listen to '$value'! 🐻👂"
                }
            }
        }
        "hear_disp" -> {
            if (isMotu) {
                when (langCode) {
                    "te" -> "వినడానికి నన్ను నొక్కండి, భయ్యా: '$value'! 🥟✨"
                    "hi" -> "सुनने के लिए मुझे दबाएं, भैया: '$value'! 🥟✨"
                    "ta" -> "கேட்க என்னைத் தட்டவும், பையா: '$value'! 🥟✨"
                    "kn" -> "ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ, ಭಯ್ಯಾ: '$value'! 🥟✨"
                    "ml" -> "കേൾക്കാൻ എന്നെ അമർത്തൂ, ഭയ്യാ: '$value'! 🥟✨"
                    "ar" -> "اضغط عليّ لسماع '$value' يا أخي! 🥟✨"
                    "bn" -> "শোনার জন্য আমাকে টিপুন, ভাইয়া: '$value'! 🥟✨"
                    "mr" -> "ऐकण्यासाठी मला दाबा, भैय्या: '$value'! 🥟✨"
                    "gu" -> "સાંભળવા માટે મને દબાવો, ભાઈ: '$value'! 🥟✨"
                    else -> "Tap me to hear '$value', bhaiya! 🥟✨"
                }
            } else {
                when (langCode) {
                    "te" -> "'$value' వినడానికి నన్ను నొక్కండి! 🐻🗣️"
                    "hi" -> "'$value' सुनने के लिए मुझे दबाएं! 🐻🗣️"
                    "ta" -> "'$value' கேட்க என்னைத் தட்டவும்! 🐻🗣️"
                    "kn" -> "'$value' ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ! 🐻🗣️"
                    "ml" -> "'$value' കേൾക്കാൻ എന്നെ അമർത്തൂ! 🐻🗣️"
                    "ar" -> "اضغط عليّ لسماع '$value'! 🐻🗣️"
                    "bn" -> "'$value' শোনার জন্য আমাকে টিপুন! 🐻🗣️"
                    "mr" -> "'$value' ऐकण्यासाठी मला दाबा! 🐻🗣️"
                    "gu" -> "'$value' સાંભળવા માટે મને દબાવો! 🐻🗣️"
                    else -> "Tap me to hear '$value'! 🐻🗣️"
                }
            }
        }
        "hear_sentence" -> {
            if (isMotu) {
                when (langCode) {
                    "te" -> "వినడానికి నన్ను నొక్కండి, భయ్యా: '$value'! 🥟✨"
                    "hi" -> "सुनने के लिए मुझे दबाएं, भैया: '$value'! 🥟✨"
                    "ta" -> "கேட்க என்னைத் தட்டவும், பையா: '$value'! 🥟✨"
                    "kn" -> "ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ, ಭಯ್ಯಾ: '$value'! 🥟✨"
                    "ml" -> "കേൾക്കാൻ എന്നെ അമർത്തൂ, ഭയ്യാ: '$value'! 🥟✨"
                    "ar" -> "اضغط عليّ لسماع '$value' يا أخي! 🥟✨"
                    "bn" -> "শোনার জন্য আমাকে টিপুন, ভাইয়া: '$value'! 🥟✨"
                    "mr" -> "ऐकण्यासाठी मला दाबा, भैय्या: '$value'! 🥟✨"
                    "gu" -> "સાંભળવા માટે મને દબાવો, ભાઈ: '$value'! 🥟✨"
                    else -> "Tap me to hear '$value', bhaiya! 🥟✨"
                }
            } else {
                when (langCode) {
                    "te" -> "వినడానికి నన్ను నొక్కండి: '$value'! 🐻🗣️"
                    "hi" -> "सुनने के लिए मुझे दबाएं: '$value'! 🐻🗣️"
                    "ta" -> "கேட்க என்னைத் தட்டவும்: '$value'! 🐻🗣️"
                    "kn" -> "ಕೇಳಲು ನನ್ನನ್ನು ಒತ್ತಿ: '$value'! 🐻🗣️"
                    "ml" -> "കേൾക്കാൻ എന്നെ അമർത്തൂ: '$value'! 🐻🗣️"
                    "ar" -> "اضغط عليّ لسماع '$value'! 🐻🗣️"
                    "bn" -> "শোনার জন্য আমাকে টিপুন: '$value'! 🐻🗣️"
                    "mr" -> "ऐकण्यासाठी मला दाबा: '$value'! 🐻🗣️"
                    "gu" -> "સાંભળવા માટે મને દબાવો: '$value'! 🐻🗣️"
                    else -> "Tap me to hear '$value'! 🐻🗣️"
                }
            }
        }
        else -> ""
    }
}

fun getLocalizedQuiz(langCode: String): String {
    return when (langCode) {
        "te" -> "కిడ్స్ క్విజ్! 🎮"
        "ta" -> "கிட்ஸ் குவிஸ்! 🎮"
        "hi" -> "किड्स क्विज़! 🎮"
        "kn" -> "ಕಿಡ್ಸ್ ಕ್ವಿಜ್! 🎮"
        "ml" -> "കിഡ്സ് ക്വിസ്! 🎮"
        "bn" -> "কিডস কুইজ! 🎮"
        "mr" -> "किड्स क्विझ! 🎮"
        "gu" -> "કિડ્સ ક્વિઝ! 🎮"
        "ar" -> "مسابقة أطفال! 🎮"
        else -> "Kids Quiz! 🎮"
    }
}

fun getLocalizedPractice(langCode: String): String {
    return when (langCode) {
        "te" -> "రాత అభ్యాసం ✍️"
        "ta" -> "எழுத்துப் பயிற்சி ✍️"
        "hi" -> "लिखने का अभ्यास ✍️"
        "kn" -> "ಬರವಣಿಗೆ ಅಭ್ಯಾಸ ✍️"
        "ml" -> "എഴുത്തു പരിശീലനം ✍️"
        "bn" -> "হাতের লেখা অনুশীলন ✍️"
        "mr" -> "लेखन सराव ✍️"
        "gu" -> "લેખન અભ્યાસ ✍️"
        "ar" -> "ممارسة الكتابة ✍️"
        else -> "Tracing Practice ✍️"
    }
}

fun getLocalizedSpeakAndLearn(langCode: String): String {
    return when (langCode) {
        "te" -> "మాట్లాడి నేర్చుకో 🗣️"
        "ta" -> "பேசி பழగు 🗣️"
        "hi" -> "बोलो और सीखो 🗣️"
        "kn" -> "ಮಾತನಾಡಿ ಕಲಿ 🗣️"
        "ml" -> "സംസാരിച്ചു പഠിക്കാം 🗣️"
        "bn" -> "বলে শেখো 🗣️"
        "mr" -> "बोला आणि शिका 🗣️"
        "gu" -> "બોલો અને શીખો 🗣️"
        "ar" -> "تحدث وتعلم 🗣️"
        else -> "Speak & Learn 🗣️"
    }
}

fun getLocalizedSpeakInstruction(langCode: String, display: String): String {
    return when (langCode) {
        "te" -> "\"$display\" అని పలకండి 🗣️"
        "ta" -> "\"$display\" என்று சொல்லுங்கள் 🗣️"
        "hi" -> "\"$display\" बोलिए 🗣️"
        "kn" -> "\"$display\" ಎಂದು ಹೇಳಿ 🗣️"
        "ml" -> "\"$display\" എന്ന് പറയുക 🗣️"
        "bn" -> "\"$display\" বলুন 🗣️"
        "mr" -> "\"$display\" म्हणा 🗣️"
        "gu" -> "\"$display\" બોલો 🗣️"
        "ar" -> "قل \"$display\" 🗣️"
        else -> "Say \"$display\" 🗣️"
    }
}

@Composable
fun AutoscaledButtonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    var fontSize by remember(text) { mutableStateOf(11.sp) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && fontSize.value > 7.5f) {
                fontSize = (fontSize.value - 0.5f).sp
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier.graphicsLayer {
            alpha = if (readyToDraw) 1f else 0f
        }
    )
}

