package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButtonDefaults

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.zIndex
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.BiasAlignment
import com.example.ui.components.MascotHomeworkSolverDialog
import com.example.ui.components.VoiceChatDialog
import com.example.ui.components.CameraArDialog
import com.example.data.Badge
import com.example.data.LanguageConfig
import com.example.data.LessonCategory
import com.example.data.actualTitle
import com.example.data.DifficultyLevel
import com.example.data.getDifficultyLevel
import com.example.viewmodel.LearningViewModel
import com.example.ui.components.AnimalBackgroundContainer
import com.example.ui.components.AutoScaleText
import com.example.ui.components.AppFooter
import com.example.ui.components.MiniGamesPlayground
import com.example.ui.components.PremiumShopDialog
import com.example.ui.components.CourseCertificateDialog
import com.example.ui.components.ProminentProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: LearningViewModel
) {
    val selectedLang by viewModel.selectedLanguage.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val lessonsCompletedToday by viewModel.lessonsCompletedToday.collectAsState()
    val dailyGoalTarget by viewModel.dailyGoalTarget.collectAsState()
    val badges by viewModel.badges.collectAsState()
    val activeCategory by viewModel.activeCategory.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showHomeworkSolver by remember { mutableStateOf(false) }
    var showMiniGamesPlayground by remember { mutableStateOf(false) }
    var showVoiceChat by remember { mutableStateOf(false) }
    var showCameraAr by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }
    var showCertificateDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    val diamonds by viewModel.diamonds.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val weakWords by viewModel.weakWords.collectAsState()
    val voiceCharacter by viewModel.voiceCharacter.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    val langConfig = selectedLang ?: return

    // Calculate progress percentage
    val categories = LessonCategory.values()
    val completedCount = categories.count { cat ->
        // Direct VM or helper read (since VM owns lifecycle)
        viewModel.isLessonCompleted(langConfig.code, cat)
    }
    val progressPercent = if (categories.isNotEmpty()) completedCount.toFloat() / categories.size else 0f

    AnimalBackgroundContainer {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = langConfig.actualTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.changeLanguage() },
                            modifier = Modifier.testTag("home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Change Language / Home",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { showVoiceChat = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFFE040FB)
                            )
                        ) {
                            Text(
                                text = "🤖 AI Teacher",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        }
                        // Beautiful Profile Button with Image / Fallback Placeholder
                        val context = LocalContext.current
                        val profileBitmapState = remember(profileImageUri) {
                            mutableStateOf<Bitmap?>(null)
                        }
                        LaunchedEffect(profileImageUri) {
                            if (profileImageUri.isNotEmpty()) {
                                try {
                                    val uri = Uri.parse(profileImageUri)
                                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                                        profileBitmapState.value = BitmapFactory.decodeStream(inputStream)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    profileBitmapState.value = null
                                }
                            } else {
                                profileBitmapState.value = null
                            }
                        }

                        IconButton(
                            onClick = { showProfileDialog = true },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .testTag("profile_button")
                        ) {
                            val bitmap = profileBitmapState.value
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "User Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                // Default profile placeholder icon
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Profile Placeholder",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
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
                Column(modifier = Modifier.fillMaxWidth()) {
                    com.example.data.AdMobBanner()
                    AppFooter(
                        langCode = langConfig.code,
                        viewModel = viewModel,
                        isNavigationMode = true
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Prominent Visual Progress Bar Component
                ProminentProgressBar(
                    progressPercent = progressPercent,
                    completedCount = completedCount,
                    totalCount = categories.size,
                    langCode = langConfig.code,
                    modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
                )

                // Stats Panel (Stars, Progress, and Badges toggle) - 1️⃣ Compact Learning Progress Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stars metric
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⭐", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$totalStars",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Streak metric (clickable)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.playClickSound()
                                    showStreakDialog = true
                                }
                                .background(Color(0xFFFBE9E7))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$dailyStreak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD84315)
                            )
                        }

                        // Daily Goal Target metric (clickable)
                        val lessonsLeft = (dailyGoalTarget - lessonsCompletedToday).coerceAtLeast(0)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.playClickSound()
                                    showGoalDialog = true
                                }
                                .background(if (lessonsLeft == 0) Color(0xFFE8F5E9) else Color(0xFFE1F5FE))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎯", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (lessonsLeft > 0) "$lessonsLeft left" else "Done! 🎉",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (lessonsLeft == 0) Color(0xFF2E7D32) else Color(0xFF0277BD)
                            )
                        }

                        // Badges (clickable)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showBadgesDialog = true }
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏅", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${badges.count { it.unlocked }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Progress metric
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📈", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${(progressPercent * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        // Diamonds metric (clickable to open shop)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showShopDialog = true }
                                .background(Color(0xFFE0F7FA))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💎", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$diamonds",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00ACC1)
                            )
                        }

                        // Certificate metric (clickable to open certificates)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showCertificateDialog = true }
                                .background(Color(0xFFFFF3E0))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📜", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Cert",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF6C00)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    LinearProgressIndicator(
                        progress = progressPercent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    )
                }
            }

            // 2️⃣ Quick Action Cards in a single row (3 Columns) - Now ultra-compact fixed height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val actions = listOf(
                    QuickActionItem(
                        icon = "📚",
                        title = getHomeworkTitle(langConfig.code),
                        containerColor = Color(0xFFE65100), // Vibrant Deep Orange
                        borderColor = Color(0xFFFFB300),     // Glowing golden orange
                        textColor = Color.White,             // White text
                        onClick = {
                            viewModel.playClickSound()
                            showHomeworkSolver = true
                        }
                    ),
                    QuickActionItem(
                        icon = "🎮",
                        title = getGamesTitle(langConfig.code),
                        containerColor = Color(0xFF7E57C2), // Vibrant Indigo Purple
                        borderColor = Color(0xFFBB86FC),     // Glowing radiant purple
                        textColor = Color.White,             // White text
                        onClick = {
                            viewModel.playClickSound()
                            showMiniGamesPlayground = true
                        }
                    ),
                    QuickActionItem(
                        icon = "🤖",
                        title = getAiTeacherTitle(langConfig.code),
                        containerColor = Color(0xFF0288D1), // Vibrant Sky Blue
                        borderColor = Color(0xFF03A9F4),     // Glowing neon blue
                        textColor = Color.White,             // White text
                        onClick = {
                            viewModel.playClickSound()
                            showVoiceChat = true
                        }
                    )
                )

                actions.forEach { action ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = action.containerColor),
                        border = BorderStroke(1.dp, action.borderColor),
                        onClick = action.onClick
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = action.icon, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            AutoScaleText(
                                text = action.title,
                                initialFontSize = 9.sp,
                                color = action.textColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                minFontSize = 7.sp
                            )
                        }
                    }
                }
            }

            // AI Smart Teacher Adaptive Feedback / Weak Words (maintained in original behavior)
            if (weakWords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("adaptive_review_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1C0A)), // Rich dark chocolate/amber
                    border = BorderStroke(1.5.dp, Color(0xFFFF9800)) // Glowing orange border
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("💡", fontSize = 20.sp)
                            Text(
                                text = if (langConfig.code == "te") "AI టీచర్: మళ్లీ ప్రాక్టీస్ చేయి!" else "AI Teacher: Let's Re-practice!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFFB74D) // Glowing light orange
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = if (langConfig.code == "te") 
                                "మనం తప్పులు చేసిన పదాలను మళ్లీ నేర్చుకుందాం:" 
                                else "The items you missed are shown here for re-practice:",
                            fontSize = 11.sp,
                            color = Color(0xFFFFB74D).copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(weakWords.toList()) { word ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4E2A0B)), // Dark gold/brown
                                    border = BorderStroke(1.dp, Color(0xFFFFB300)), // Glowing gold border
                                    onClick = {
                                        viewModel.playClickSound()
                                        viewModel.speakCustomText(word)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(word, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFE0B2)) // Glowing text
                                        Text("🔊", fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        TextButton(
                            onClick = {
                                viewModel.playClickSound()
                                viewModel.clearWeakWords()
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = if (langConfig.code == "te") "అన్నీ పూర్తి చేసాను! ✅" else "Mark All Done! ✅",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFB300) // Glowing amber/gold
                            )
                        }
                    }
                }
            }

            var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.BEGINNER) }
            var isPathView by remember { mutableStateOf(true) }

            LaunchedEffect(activeCategory) {
                activeCategory?.getDifficultyLevel()?.let {
                    selectedDifficulty = it
                }
            }

            val beginnerLessons = categories.filter { it.getDifficultyLevel() == DifficultyLevel.BEGINNER }
            val intermediateLessons = categories.filter { it.getDifficultyLevel() == DifficultyLevel.INTERMEDIATE }
            val advancedLessons = categories.filter { it.getDifficultyLevel() == DifficultyLevel.ADVANCED }

            val beginnerCompleted = beginnerLessons.count { viewModel.isLessonCompleted(langConfig.code, it) }
            val intermediateCompleted = intermediateLessons.count { viewModel.isLessonCompleted(langConfig.code, it) }
            val advancedCompleted = advancedLessons.count { viewModel.isLessonCompleted(langConfig.code, it) }

            val filteredCategories = categories.filter { it.getDifficultyLevel() == selectedDifficulty }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getLocalizedSelectLesson(langConfig.code),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isPathView) "Path 🗺️" else "Grid 📱",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(
                        onClick = {
                            viewModel.playClickSound()
                            isPathView = !isPathView
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Text(
                            text = if (isPathView) "🗺️" else "📱",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Difficulty levels selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DifficultyLevel.values().forEach { level ->
                    val isSelected = selectedDifficulty == level
                    val levelColor = when (level) {
                        DifficultyLevel.BEGINNER -> Color(0xFF4CAF50)
                        DifficultyLevel.INTERMEDIATE -> Color(0xFFFF9800)
                        DifficultyLevel.ADVANCED -> Color(0xFF9C27B0)
                    }
                    val completed = when (level) {
                        DifficultyLevel.BEGINNER -> beginnerCompleted
                        DifficultyLevel.INTERMEDIATE -> intermediateCompleted
                        DifficultyLevel.ADVANCED -> advancedCompleted
                    }
                    val total = when (level) {
                        DifficultyLevel.BEGINNER -> beginnerLessons.size
                        DifficultyLevel.INTERMEDIATE -> intermediateLessons.size
                        DifficultyLevel.ADVANCED -> advancedLessons.size
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.playClickSound()
                                selectedDifficulty = level
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) levelColor else levelColor.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 1.2.dp else 0.8.dp,
                            color = if (isSelected) Color.White else levelColor.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 1.dp else 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (level) {
                                    DifficultyLevel.BEGINNER -> "🌱"
                                    DifficultyLevel.INTERMEDIATE -> "🚀"
                                    DifficultyLevel.ADVANCED -> "🏆"
                                },
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = getDifficultyLevelLabel(level, langConfig.code).substringBefore(" "),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$completed/$total",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (isPathView) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    WindingLessonPath(
                        lessons = filteredCategories,
                        langCode = langConfig.code,
                        viewModel = viewModel,
                        onLessonClick = { cat -> viewModel.startLesson(cat) }
                    )
                }
            } else {
                val lazyGridState = rememberLazyGridState()

                val scrollbarColor = MaterialTheme.colorScheme.primary

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent() // Draw children first

                            val layoutInfo = lazyGridState.layoutInfo
                            val visibleItems = layoutInfo.visibleItemsInfo
                            val totalItems = layoutInfo.totalItemsCount

                            if (totalItems > 0 && visibleItems.isNotEmpty()) {
                                val firstVisibleItem = visibleItems.first()
                                val firstVisibleIndex = firstVisibleItem.index
                                val firstVisibleOffset = lazyGridState.firstVisibleItemScrollOffset.toFloat()
                                val itemSize = firstVisibleItem.size.height.toFloat()

                                val rowCount = (totalItems + 1) / 2
                                val totalHeightEstimate = rowCount * itemSize
                                val viewportHeight = layoutInfo.viewportSize.height.toFloat()

                                if (totalHeightEstimate > viewportHeight) {
                                    val denominator = totalHeightEstimate - viewportHeight
                                    val scrollPercent = if (denominator > 0f) {
                                        (((firstVisibleIndex / 2) * itemSize) + firstVisibleOffset) / denominator
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
                                        color = scrollbarColor,
                                        topLeft = Offset(size.width - scrollbarWidth - scrollbarPadding, translationY + scrollbarPadding),
                                        size = Size(scrollbarWidth, thumbHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(scrollbarWidth / 2)
                                    )
                                }
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp, end = 4.dp)
                ) {
                    items(filteredCategories) { category ->
                        val isCompleted = viewModel.isLessonCompleted(langConfig.code, category)
                        val stars = viewModel.getLessonStars(langConfig.code, category)

                        DashboardCard(
                            category = category,
                            langCode = langConfig.code,
                            isCompleted = isCompleted,
                            stars = stars,
                            onClick = { viewModel.startLesson(category) }
                        )
                    }
                }
            }
        }
    }
}

    // Daily Learning Goal Dialog
    if (showGoalDialog) {
        val langCode = langConfig.code
        val lessonsLeft = (dailyGoalTarget - lessonsCompletedToday).coerceAtLeast(0)
        
        val titleText = when (langCode) {
            "te" -> "🎯 దినసరి లక్ష్యం"
            "ta" -> "🎯 தினசரி இலக்கு"
            "hi" -> "🎯 दैनिक लक्ष्य"
            "kn" -> "🎯 ದೈನಂದಿನ ಗುರಿ"
            "ml" -> "🎯 ദിനചര്യ ലക്ഷ്യം"
            else -> "🎯 Daily Learning Goal"
        }

        val progressValue = if (dailyGoalTarget > 0) {
            (lessonsCompletedToday.toFloat() / dailyGoalTarget).coerceAtMost(1.0f)
        } else {
            0.0f
        }

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.playClickSound()
                        showGoalDialog = false
                    }
                ) {
                    Text(
                        text = when (langCode) {
                            "te" -> "సరే 👍"
                            "ta" -> "சரி 👍"
                            "hi" -> "ठीक है 👍"
                            "kn" -> "ಸರಿ 👍"
                            "ml" -> "ശരി 👍"
                            else -> "Awesome! 👍"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            title = {
                Text(
                    text = titleText,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = progressValue,
                            modifier = Modifier.fillMaxSize(),
                            color = if (lessonsLeft == 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            strokeWidth = 8.dp,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$lessonsCompletedToday/$dailyGoalTarget",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "lessons",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (lessonsLeft == 0) {
                            when (langCode) {
                                "te" -> "అద్భుతం! ఈరోజు మీ దినసరి లక్ష్యాన్ని చేరుకున్నారు! 🎉🏆"
                                "ta" -> "அற்புதம்! இன்று உங்கள் தினசரி இலக்கை அடைந்துவிட்டீர்கள்! 🎉🏆"
                                "hi" -> "शानदार! आपने आज का दैनिक लक्ष्य पूरा कर लिया है! 🎉🏆"
                                "kn" -> "ಅದ್ಭುತ! ನೀವು ಇಂದಿನ ದೈನಂದಿನ ಗುರಿಯನ್ನು ತಲುಪಿದ್ದೀರಿ! 🎉🏆"
                                "ml" -> "അതിശയകരം! നിങ്ങൾ ഇന്നത്തെ ലക്ഷ്യം പൂർത്തിയാക്കി! 🎉🏆"
                                else -> "Amazing work! You've successfully smashed your daily goal today! 🎉🏆"
                            }
                        } else {
                            when (langCode) {
                                "te" -> "ఈరోజు లక్ష్యాన్ని చేరుకోవడానికి ఇంకా $lessonsLeft పాఠం(లు) పూర్తి చేయాలి! 🚀"
                                "ta" -> "இன்றைய இலக்கை அடைய இன்னும் $lessonsLeft பாடம்(கள்) தேவை! 🚀"
                                "hi" -> "आज का लक्ष्य पूरा करने के लिए $lessonsLeft और पाठ पूरा करें! 🚀"
                                "kn" -> "ಇಂದಿನ ಗುರಿಯನ್ನು ತಲುಪಲು ಇನ್ನೂ $lessonsLeft ಪಾಠ(ಗಳು) ಬೇಕು! 🚀"
                                "ml" -> "ഇന്നത്തെ ലക്ഷ്യം പൂർത്തിയാക്കാൻ ഇനി $lessonsLeft പാഠം(കൾ) കൂടി വേണം! 🚀"
                                else -> "Just $lessonsLeft more lesson(s) left to hit your daily goal for today! Keep shining! 🚀"
                            }
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when (langCode) {
                            "te" -> "లక్ష్యాన్ని మార్చండి:"
                            "ta" -> "இலக்கை மாற்று:"
                            "hi" -> "लक्ष्य बदलें:"
                            "kn" -> "ಗುರಿ ಬದಲಾಯಿಸಿ:"
                            "ml" -> "ലക്ഷ്യം മാറ്റുക:"
                            else -> "Adjust Daily Target:"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(1, 2, 3, 5).forEach { targetValue ->
                            val isSelected = dailyGoalTarget == targetValue
                            val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 3.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(containerColor)
                                    .clickable {
                                        viewModel.playClickSound()
                                        viewModel.setDailyGoalTarget(targetValue)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$targetValue",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Daily Streak Celebration Dialog
    if (showStreakDialog) {
        val langCode = langConfig.code
        val titleText = getLocalizedStreakTitle(langCode)
        val messageText = getLocalizedStreakMessage(dailyStreak, langCode)
        val okButtonText = when (langCode) {
            "te" -> "అద్భుతం! 🌟"
            "ta" -> "அற்புதம்! 🌟"
            "hi" -> "बहुत बढ़िया! 🌟"
            "kn" -> "ಅದ್ಭುತ! 🌟"
            "ml" -> "അതിശയകരം! 🌟"
            else -> "Awesome! 🌟"
        }

        AlertDialog(
            onDismissRequest = { showStreakDialog = false },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.playClickSound()
                        showStreakDialog = false 
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = okButtonText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = titleText, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFBE9E7))
                            .border(2.dp, Color(0xFFFF5722), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔥", fontSize = 38.sp)
                            Text(
                                text = "$dailyStreak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD84315)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = messageText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // User Profile Edit Dialog
    if (showProfileDialog) {
        ProfileEditDialog(
            viewModel = viewModel,
            onDismiss = { showProfileDialog = false }
        )
    }

    // Mascot Homework Solver Dialog
    if (showHomeworkSolver) {
        MascotHomeworkSolverDialog(
            viewModel = viewModel,
            voiceCharacter = voiceCharacter,
            langCode = langConfig.code,
            onDismiss = { showHomeworkSolver = false }
        )
    }

    // Badges Dialog
    if (showBadgesDialog) {
        BadgesDialog(
            badges = badges,
            onDismiss = { showBadgesDialog = false }
        )
    }

    // Mini Games Playground Dialog
    if (showMiniGamesPlayground) {
        MiniGamesPlayground(
            viewModel = viewModel,
            onDismiss = { showMiniGamesPlayground = false }
        )
    }

    // AI Voice Chat Dialog
    if (showVoiceChat) {
        VoiceChatDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceChat = false }
        )
    }

    // AI Camera Learning & AR Dialog
    if (showCameraAr) {
        CameraArDialog(
            viewModel = viewModel,
            onDismiss = { showCameraAr = false }
        )
    }

    // Premium Shop & Diamond Shop Dialog
    if (showShopDialog) {
        PremiumShopDialog(
            viewModel = viewModel,
            onDismiss = { showShopDialog = false }
        )
    }

    // Course Certificate Dialog
    if (showCertificateDialog) {
        CourseCertificateDialog(
            viewModel = viewModel,
            langCode = langConfig.code,
            onDismiss = { showCertificateDialog = false }
        )
    }
}

@Composable
fun DashboardCard(
    category: LessonCategory,
    langCode: String,
    isCompleted: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val cardBgColor = Color(android.graphics.Color.parseColor(category.colorHex))

    val borderStrokeColor = when (category) {
        LessonCategory.LETTERS -> Color(0xFFE53935)     // Deep Red/Pink
        LessonCategory.LETTER_WORD -> Color(0xFF3949AB)  // Deep Indigo
        LessonCategory.NUMBERS -> Color(0xFF00897B)      // Deep Teal
        LessonCategory.CONSONANTS -> Color(0xFFF4511E)   // Deep Orange
        LessonCategory.SPICES -> Color(0xFFD81B60)       // Deep Magenta/Colors
        LessonCategory.MONTHS -> Color(0xFF0288D1)       // Deep Light Blue
        LessonCategory.POEMS -> Color(0xFFE040FB)        // Deep Violet/Purple
        LessonCategory.VEGETABLES -> Color(0xFF2E7D32)   // Deep Green
        LessonCategory.STATES_CAPITALS -> Color(0xFF3E2723)  // Deep Brown
        LessonCategory.NATIONAL_SYMBOLS -> Color(0xFF0D47A1) // Deep Royal Blue
        LessonCategory.RELATIONSHIPS -> Color(0xFFFF6F00)    // Deep Amber
        LessonCategory.FOODS -> Color(0xFF1B5E20)            // Deep Forest Green
        LessonCategory.FRUITS_FLOWERS -> Color(0xFFB71C1C)   // Deep Crimson Red
        LessonCategory.BODY_PARTS -> Color(0xFF7B1FA2)        // Deep Purple
        LessonCategory.SHAPES -> Color(0xFFFBC02D)          // Deep Yellow/Gold
        LessonCategory.BASIC_MATH -> Color(0xFF1976D2)      // Deep Blue
        LessonCategory.DIRECTIONS -> Color(0xFFC2185B)      // Deep Pink
        LessonCategory.SCHOOL_OBJECTS -> Color(0xFF388E3C)  // Deep Green
        LessonCategory.WEATHER -> Color(0xFF0097A7)         // Deep Cyan
    }

    // Hover, Press & Completed Achievement Animations
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isTargeted = isHovered || isPressed

    val scale by animateFloatAsState(
        targetValue = if (isTargeted) 1.06f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isTargeted) 3f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardRotation"
    )

    val elevation by animateDpAsState(
        targetValue = if (isTargeted) 12.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardElevation"
    )

    val borderAnimWidth by animateDpAsState(
        targetValue = if (isTargeted) 3.5.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderWidth"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "completedShine")
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .drawBehind {
                if (isCompleted) {
                    val width = size.width
                    val height = size.height
                    val progressX = width * shineProgress
                    
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.0f),
                            Color.White.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.0f)
                        ),
                        start = Offset(progressX - 80.dp.toPx(), 0f),
                        end = Offset(progressX + 80.dp.toPx(), height)
                    )
                    drawRect(brush = brush)
                }
            }
            .testTag("dashboard_card_${category.name}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(borderAnimWidth, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.getIcon(langCode), fontSize = 24.sp)
            }

            // Titles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                AutoScaleText(
                    text = category.getTitle(langCode),
                    initialFontSize = 13.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minFontSize = 8.sp
                )
                AutoScaleText(
                    text = getCategorySubTitle(category, langCode),
                    initialFontSize = 9.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minFontSize = 7.sp
                )
            }

            // Status / Stars
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🏅 ", fontSize = 11.sp)
                        repeat(stars) {
                            Text(text = "⭐", fontSize = 11.sp)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.40f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = getLocalizedPlay(langCode),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

fun getCategorySubTitle(category: LessonCategory, langCode: String): String {
    return when (langCode) {
        "te" -> when (category) {
            LessonCategory.LETTERS -> "అక్షరాలు నేర్చుకో"
            LessonCategory.LETTER_WORD -> "అక్షరాలు మరియు పదాలు"
            LessonCategory.NUMBERS -> "అంకెలు 1-100"
            LessonCategory.CONSONANTS -> "100 సాధారణ పదాలు"
            LessonCategory.SPICES -> "అందమైన రంగులు"
            LessonCategory.MONTHS -> "సంవత్సరపు నెలలు"
            LessonCategory.POEMS -> "జంతువులు & పక్షులు"
            LessonCategory.VEGETABLES -> "కూరగాయల పేర్లు"
            LessonCategory.STATES_CAPITALS -> "రాష్ట్రాలు & రాజధానులు"
            LessonCategory.NATIONAL_SYMBOLS -> "జాతీయ చిహ్నాలు"
            LessonCategory.RELATIONSHIPS -> "కుటుంబ బంధుత్వాలు"
            LessonCategory.FOODS -> "రుచికరమైన ఆహారాలు"
            LessonCategory.FRUITS_FLOWERS -> "పండ్లు & పువ్వులు"
            LessonCategory.BODY_PARTS -> "శరీర భాగాలు"
            LessonCategory.SHAPES -> "వివిధ ఆకారాలు"
            LessonCategory.BASIC_MATH -> "సులభమైన గణితం"
            LessonCategory.DIRECTIONS -> "నాలుగు దిక్కులు"
            LessonCategory.SCHOOL_OBJECTS -> "పాఠశాల వస్తువులు"
            LessonCategory.WEATHER -> "రోజువారీ వాతావరణం"
        }
        "ta" -> when (category) {
            LessonCategory.LETTERS -> "எழுத்துக்களைக் கற்றுக்கொள்ளுங்கள்"
            LessonCategory.LETTER_WORD -> "எழுத்துக்கள் மற்றும் வார்த்தைகள்"
            LessonCategory.NUMBERS -> "எண்கள் 1-100"
            LessonCategory.CONSONANTS -> "100 அடிப்படை சொற்கள்"
            LessonCategory.SPICES -> "அழகான வண்ணங்கள்"
            LessonCategory.MONTHS -> "வருடத்தின் மாதங்கள்"
            LessonCategory.POEMS -> "விலங்குகள் & பறவைகள்"
            LessonCategory.VEGETABLES -> "காயகறி பெயர்கள்"
            LessonCategory.STATES_CAPITALS -> "மாநிலங்கள் & தலைநகரங்கள்"
            LessonCategory.NATIONAL_SYMBOLS -> "தேசிய சின்னங்கள்"
            LessonCategory.RELATIONSHIPS -> "குடும்ப உறவுகள்"
            LessonCategory.FOODS -> "சுவையான உணவுகள்"
            LessonCategory.FRUITS_FLOWERS -> "பழங்கள் & மலர்கள்"
            LessonCategory.BODY_PARTS -> "உடல் உறுப்புகள்"
            LessonCategory.SHAPES -> "பல்வேறு வடிவங்கள்"
            LessonCategory.BASIC_MATH -> "எளிய கணிதம்"
            LessonCategory.DIRECTIONS -> "நான்கு திசைகள்"
            LessonCategory.SCHOOL_OBJECTS -> "பள்ளி பொருட்கள்"
            LessonCategory.WEATHER -> "தினசரி வானிலை"
        }
        "hi" -> when (category) {
            LessonCategory.LETTERS -> "वर्णमाला सीखें"
            LessonCategory.LETTER_WORD -> "अक्षर और शब्द"
            LessonCategory.NUMBERS -> "गिनती 1-100"
            LessonCategory.CONSONANTS -> "100 बुनियादी शब्द"
            LessonCategory.SPICES -> "सुंदर रंग"
            LessonCategory.MONTHS -> "वर्ष के महीने"
            LessonCategory.POEMS -> "जानवर और पक्षी"
            LessonCategory.VEGETABLES -> "सब्जियों के नाम"
            LessonCategory.STATES_CAPITALS -> "राज्य और राजधानियाँ"
            LessonCategory.NATIONAL_SYMBOLS -> "राष्ट्रीय प्रतीक"
            LessonCategory.RELATIONSHIPS -> "पारिवारिक रिश्ते"
            LessonCategory.FOODS -> "स्वादिष्ट भोजन"
            LessonCategory.FRUITS_FLOWERS -> "फल और फूल"
            LessonCategory.BODY_PARTS -> "शरीर के अंग"
            LessonCategory.SHAPES -> "विभिन्न आकृतियाँ"
            LessonCategory.BASIC_MATH -> "आसान गणित"
            LessonCategory.DIRECTIONS -> "चार दिशाएँ"
            LessonCategory.SCHOOL_OBJECTS -> "स्कूल की वस्तुएँ"
            LessonCategory.WEATHER -> "दैनिक मौसम"
        }
        else -> when (category) {
            LessonCategory.LETTERS -> "Letters of Alphabet"
            LessonCategory.LETTER_WORD -> "Alphabet & Picture Words"
            LessonCategory.NUMBERS -> "Learn Numbers 1-100"
            LessonCategory.CONSONANTS -> "100 Common Words"
            LessonCategory.SPICES -> "Learn Colors"
            LessonCategory.MONTHS -> "Learn Months in Order"
            LessonCategory.POEMS -> "Animals & Birds"
            LessonCategory.VEGETABLES -> "Learn Vegetable Names"
            LessonCategory.STATES_CAPITALS -> "Indian States & Capitals"
            LessonCategory.NATIONAL_SYMBOLS -> "India's National Symbols"
            LessonCategory.RELATIONSHIPS -> "Family & Relationships"
            LessonCategory.FOODS -> "Traditional Food Items"
            LessonCategory.FRUITS_FLOWERS -> "Fruits & Flowers Names"
            LessonCategory.BODY_PARTS -> "Human Body Parts"
            LessonCategory.SHAPES -> "Explore Common Shapes"
            LessonCategory.BASIC_MATH -> "Fun Mathematics"
            LessonCategory.DIRECTIONS -> "Learn Directions"
            LessonCategory.SCHOOL_OBJECTS -> "Common School Items"
            LessonCategory.WEATHER -> "Learn Daily Weather"
        }
    }
}

@Composable
fun SettingsDialog(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isVoiceOn by viewModel.isVoiceOn.collectAsState()
    val useWebSpeechApi by viewModel.useWebSpeechApi.collectAsState()
    val voiceSpeed by viewModel.voiceSpeed.collectAsState()
    val isBgmOn by viewModel.isBgmOn.collectAsState()
    val voiceCharacter by viewModel.voiceCharacter.collectAsState()
    val avatarType by viewModel.avatarType.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val langCode = selectedLanguage?.code ?: "en"
    val unlockedThemes by viewModel.unlockedThemes.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()

    var showResetConfirm by remember { mutableStateOf(false) }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = getSettingsText("title", langCode),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                if (!showResetConfirm) {
                    // Dark Mode toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getSettingsText("dark_mode", langCode),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.toggleDarkMode() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // Voice On/Off
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getSettingsText("voice_guide", langCode),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isVoiceOn,
                            onCheckedChange = { viewModel.toggleVoiceOn() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        )
                    }

                    // Web Speech API toggle
                    if (isVoiceOn) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = getSettingsText("web_speech", langCode),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = getSettingsText("web_speech_desc", langCode),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Switch(
                                checked = useWebSpeechApi,
                                onCheckedChange = { viewModel.toggleWebSpeechApi() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }

                    // Voice Character Selector (Bhalu Bear vs Motu)
                    if (isVoiceOn) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = getSettingsText("voice_char", langCode),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.setVoiceCharacter("guru") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (voiceCharacter == "guru") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        contentColor = if (voiceCharacter == "guru") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1.1f),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(getSettingsText("char_guru", langCode), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { viewModel.setVoiceCharacter("bear") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (voiceCharacter == "bear") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        contentColor = if (voiceCharacter == "bear") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(getSettingsText("char_bear", langCode), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = { viewModel.setVoiceCharacter("motu") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (voiceCharacter == "motu") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        contentColor = if (voiceCharacter == "motu") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(0.9f),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Text(getSettingsText("char_motu", langCode), fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                }
                            }
                        }
                    }

                    // Voice Speed Selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = getSettingsText("voice_speed", langCode),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = when (voiceSpeed) {
                                    0.75f -> "0.75x (${getSettingsText("speed_slow", langCode)})"
                                    1.25f -> "1.25x (${getSettingsText("speed_fast", langCode)})"
                                    else -> "1x (${getSettingsText("speed_normal", langCode)})"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.setVoiceSpeed(0.75f) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (voiceSpeed == 0.75f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    contentColor = if (voiceSpeed == 0.75f) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("0.75x", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.setVoiceSpeed(1.0f) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (voiceSpeed == 1.0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    contentColor = if (voiceSpeed == 1.0f) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("1x", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.setVoiceSpeed(1.25f) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (voiceSpeed == 1.25f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    contentColor = if (voiceSpeed == 1.25f) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("1.25x", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Background Music On/Off
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getSettingsText("bg_music", langCode),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = isBgmOn,
                            onCheckedChange = { viewModel.toggleBackgroundMusic() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose Avatar Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = getSettingsText("choose_avatar", langCode),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val avatarList = when (langCode) {
                            "te" -> listOf(
                                "girl" to "👩 మేడం",
                                "boy" to "👨 సార్",
                                "panda" to "🐼 పాండా",
                                "monkey" to "🐵 కోతి",
                                "rabbit" to "🐰 కుందేలు",
                                "bear" to "🐻 ఎలుగుబంటి",
                                "elephant" to "🐘 ఏనుగు"
                            )
                            "ta" -> listOf(
                                "girl" to "👩 மேடம்",
                                "boy" to "👨 சார்",
                                "panda" to "🐼 பாண்டா",
                                "monkey" to "🐵 குரங்கு",
                                "rabbit" to "🐰 முயல்",
                                "bear" to "🐻 கரடி",
                                "elephant" to "🐘 யானை"
                            )
                            "hi" -> listOf(
                                "girl" to "👩 मैडम",
                                "boy" to "👨 सर",
                                "panda" to "🐼 पांडा",
                                "monkey" to "🐵 बंदर",
                                "rabbit" to "🐰 खरगोश",
                                "bear" to "🐻 भालू",
                                "elephant" to "🐘 हाथी"
                            )
                            "ar" -> listOf(
                                "girl" to "👩 معلمة",
                                "boy" to "👨 معلم",
                                "panda" to "🐼 باندا",
                                "monkey" to "🐵 قرد",
                                "rabbit" to "🐰 أرنب",
                                "bear" to "🐻 دب",
                                "elephant" to "🐘 فيل"
                            )
                            "kn" -> listOf(
                                "girl" to "👩 ಮೇಡಂ",
                                "boy" to "👨 ಸರ್",
                                "panda" to "🐼 ಪಾಂಡಾ",
                                "monkey" to "🐵 ಕೋತಿ",
                                "rabbit" to "🐰 ಮೊಲ",
                                "bear" to "🐻 ಕರಡಿ",
                                "elephant" to "🐘 ಆನೆ"
                            )
                            "ml" -> listOf(
                                "girl" to "👩 മാഡം",
                                "boy" to "👨 സാർ",
                                "panda" to "🐼 പാണ്ട",
                                "monkey" to "🐵 കുരങ്ങ്",
                                "rabbit" to "🐰 മുയൽ",
                                "bear" to "🐻 കരടി",
                                "elephant" to "🐘 ആന"
                            )
                            else -> listOf(
                                "girl" to "👩 Madam",
                                "boy" to "👨 Sir",
                                "panda" to "🐼 Panda",
                                "monkey" to "🐵 Monkey",
                                "rabbit" to "🐰 Rabbit",
                                "bear" to "🐻 Bear",
                                "elephant" to "🐘 Elephant"
                            )
                        }
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(avatarList) { (type, label) ->
                                val isSelected = avatarType == type
                                Card(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clickable { viewModel.setAvatarType(type) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                                    ) {
                                        Text(
                                            text = label.split(" ")[0], // Emoji
                                            fontSize = 28.sp,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = label.split(" ")[1], // Name
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Choose Background Theme Section (Matching Avatar row beautifully!)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "🎨 Background Magic Theme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        val themeList = listOf(
                            Triple("jungle", "🌴 Jungle", "Always Unlocked"),
                            Triple("galaxy", "🌌 Space", "40 💎"),
                            Triple("candy", "🍭 Candy", "50 💎"),
                            Triple("ocean", "🐬 Ocean", "55 💎")
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            items(themeList) { (themeId, label, costStr) ->
                                val isUnlocked = themeId == "jungle" || unlockedThemes.contains(themeId) || isPremium
                                val activeThemeRaw = com.example.ui.components.BackgroundThemeState.currentTheme.value
                                val isActive = activeThemeRaw == themeId || (themeId == "galaxy" && activeThemeRaw == "space")
                                
                                Card(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clickable {
                                            if (isUnlocked) {
                                                val mappedTheme = if (themeId == "galaxy") "space" else themeId
                                                viewModel.setBackgroundTheme(mappedTheme)
                                            } else {
                                                // If locked, trigger opening shop or let them know
                                                onDismiss()
                                                viewModel.earnDiamonds(0) // Play diamond sfx
                                            }
                                        },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = BorderStroke(
                                        width = if (isActive) 3.dp else 1.dp,
                                        color = if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = label.split(" ")[0], // Emoji
                                                fontSize = 24.sp,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            if (!isUnlocked) {
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(text = "🔒", fontSize = 10.sp)
                                            }
                                        }
                                        Text(
                                            text = label.split(" ")[1], // Name
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Button(
                        onClick = { showPrivacyPolicy = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer, contentColor = MaterialTheme.colorScheme.onTertiaryContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(getSettingsText("privacy_policy", langCode), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.changeLanguage() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(getSettingsText("change_lang", langCode), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(getSettingsText("reset_progress", langCode), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Confirm reset
                    Text(
                        text = getSettingsText("confirm_reset_1", langCode),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    Text(
                        text = getSettingsText("confirm_reset_2", langCode),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showResetConfirm = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(getSettingsText("keep_progress", langCode), color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                viewModel.resetProgress()
                                showResetConfirm = false
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("reset_confirm_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(getSettingsText("yes_reset", langCode), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(getSettingsText("close", langCode), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showPrivacyPolicy) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyPolicy = false })
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔒", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                Text("Privacy Policy & Data Safety", fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Language Guru is committed to protecting the privacy of children and all users.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "1. Data Collection & Usage\n• We do NOT sell, rent, or trade any personal data.\n• User progress, earned stars, and avatar choices are saved strictly on your local device.\n• Camera and Microphone permissions are requested solely for interactive features (AR Object Scanner, Speech Pronunciation Practice, AI Voice Chat). Audio and camera data are processed in real-time and never permanently stored.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "2. Google Play Families & Ads Policy\n• Ads delivered via Google AdMob are configured with child-directed treatment (TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE) and G-rated content bounds.\n• No personalized ads or interest-based targeting are used for child users.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "3. Third-Party Services\n• Gemini AI APIs are used for real-time educational evaluation and conversational practice under standard API data processing terms.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Got It! 👍", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

fun getSettingsText(key: String, langCode: String): String {
    return when (key) {
        "privacy_policy" -> when (langCode) {
            "te" -> "🔒 ప్రైవసీ పాలసీ & డేటా సేఫ్టీ"
            "ta" -> "🔒 தனியுரிமைக் கொள்கை & தரவு பாதுகாப்பு"
            "hi" -> "🔒 गोपनीयता नीति एवं डेटा सुरक्षा"
            "ar" -> "🔒 سياسة الخصوصية وأمان البيانات"
            "kn" -> "🔒 ಗೌಪ್ಯತಾ ನೀತಿ ಮತ್ತು ಡೇಟಾ ಸುರಕ್ಷತೆ"
            "ml" -> "🔒 സ്വകാര്യതാ നയവും ഡാറ്റാ സുരക്ഷയും"
            else -> "🔒 Privacy Policy & Data Safety"
        }
        "title" -> when (langCode) {
            "te" -> "⚙️ పిల్లల సెట్టింగ్స్"
            "ta" -> "⚙️ குழந்தைகள் அமைப்புகள்"
            "hi" -> "⚙️ बच्चों की सेटिंग्स"
            "ar" -> "⚙️ إعدادات الأطفال"
            "kn" -> "⚙️ ಮಕ್ಕಳ ಸೆಟ್ಟಿಂಗ್‌ಗಳು"
            "ml" -> "⚙️ കുട്ടികളുടെ ക്രമീകരണങ്ങൾ"
            else -> "⚙️ Kids Settings"
        }
        "dark_mode" -> when (langCode) {
            "te" -> "🌙 డార్క్ మోడ్"
            "ta" -> "🌙 இருண்ட பயன்முறை"
            "hi" -> "🌙 डार्क मोड"
            "ar" -> "🌙 الوضع الداكن"
            "kn" -> "🌙 ಡಾರ್ಕ್ ಮೋಡ್"
            "ml" -> "🌙 ഡാർക്ക് മോഡ്"
            else -> "🌙 Dark Mode"
        }
        "voice_guide" -> when (langCode) {
            "te" -> "🔊 వాయిస్ గైడ్ ఆన్"
            "ta" -> "🔊 குரல் வழிகாட்டி ஆன்"
            "hi" -> "🔊 वॉयस गाइड ऑन"
            "ar" -> "🔊 تشغيل الدليل الصوتي"
            "kn" -> "🔊 ಧ್ವನಿ ಮಾರ್ಗದರ್ಶಿ ಆನ್"
            "ml" -> "🔊 ശബ്ദ സഹായി ഓൺ"
            else -> "🔊 Voice Guide On"
        }
        "web_speech" -> when (langCode) {
            "te" -> "🌐 వెబ్ స్పీచ్ API"
            "ta" -> "🌐 வலை பேச்சு API"
            "hi" -> "🌐 वेब स्पीच API"
            "ar" -> "🌐 واجهة برمجة تطبيقات ويب"
            "kn" -> "🌐 ವೆಬ್ ಸ್ಪೀಚ್ API"
            "ml" -> "🌐 വെബ് സപീച്ച് API"
            else -> "🌐 Web Speech API"
        }
        "web_speech_desc" -> when (langCode) {
            "te" -> "వెబ్‌వ్యూ ద్వారా బ్రౌజర్ TTS ఇంజిన్‌ని ఉపయోగించండి"
            "ta" -> "வெப்வியூ வழியாக உலாவி டிடிஎஸ் இயந்திரத்தைப் பயன்படுத்தவும்"
            "hi" -> "वेबव्यू के माध्यम से ब्राउज़र टीटीएस इंजन का उपयोग करें"
            "ar" -> "استخدم محرك نطق النصوص للمتصفح عبر WebView"
            "kn" -> "ವೆಬ್‌ವ್ಯೂ ಮೂಲಕ ಬ್ರೌಸರ್ ಟಿಟಿಎಸ್ ಎಂಜಿನ್ ಬಳಸಿ"
            "ml" -> "വെബ്‌വ్యూ വഴി ബ്രൗസർ ടിടിഎസ് എഞ്ചിൻ ഉപയോഗിക്കുക"
            else -> "Use browser TTS engine via WebView"
        }
        "voice_char" -> when (langCode) {
            "te" -> "🎭 వాయిస్ పాత్ర"
            "ta" -> "🎭 குரல் பாத்திரம்"
            "hi" -> "🎭 आवाज का पात्र"
            "ar" -> "🎭 شخصية الصوت"
            "kn" -> "🎭 ಧ್ವನಿ ಪಾತ್ರ"
            "ml" -> "🎭 ശബ്ദ കഥാപാത്രം"
            else -> "🎭 Voice Character"
        }
        "char_guru" -> when (langCode) {
            "te" -> "👩‍🏫 లాంగ్వేజ్ గురు"
            "ta" -> "👩‍🏫 லாங்குவேஜ் குரு"
            "hi" -> "👩‍🏫 लैंग्वेज गुरु"
            "ar" -> "👩‍🏫 معلم اللغة"
            "kn" -> "👩‍🏫 ಲ್ಯಾಂಗ್ವೇಜ್ ಗುರು"
            "ml" -> "👩‍🏫 ലാംഗ്വേజ్ ഗുരു"
            else -> "👩‍🏫 Language Guru"
        }
        "char_bear" -> when (langCode) {
            "te" -> "🐻 భాలు ఎలుగుబంటి"
            "ta" -> "🐻 பாலு கரடி"
            "hi" -> "🐻 भालू बियर"
            "ar" -> "🐻 الدب بهالو"
            "kn" -> "🐻 ಭಾಲು ಕರಡಿ"
            "ml" -> "🐻 ഭാലു കരടി"
            else -> "🐻 Bhalu Bear"
        }
        "char_motu" -> when (langCode) {
            "te" -> "🥟 మోటు"
            "ta" -> "🥟 மோட்டு"
            "hi" -> "🥟 मोटू"
            "ar" -> "🥟 موتو"
            "kn" -> "🥟 ಮೋಟು"
            "ml" -> "🥟 മോട്ടു"
            else -> "🥟 Motu"
        }
        "voice_speed" -> when (langCode) {
            "te" -> "🗣️ వాయిస్ వేగం"
            "ta" -> "🗣️ குரல் వేகம்"
            "hi" -> "🗣️ आवाज की गति"
            "ar" -> "🗣️ سرعة الصوت"
            "kn" -> "🗣️ ಧ್ವನಿ ವೇಗ"
            "ml" -> "🗣️ ശബ്ദ വേഗത"
            else -> "🗣️ Voice Speed"
        }
        "speed_slow" -> when (langCode) {
            "te" -> "నెమ్మదిగా"
            "ta" -> "மெதுவாக"
            "hi" -> "धीमा"
            "ar" -> "بطيء"
            "kn" -> "ನಿಧಾನ"
            "ml" -> "പതുക്കെ"
            else -> "Slow"
        }
        "speed_fast" -> when (langCode) {
            "te" -> "వేగంగా"
            "ta" -> "வேகமாக"
            "hi" -> "तेज़"
            "ar" -> "سريع"
            "kn" -> "ವೇಗ"
            "ml" -> "വേഗത്തിൽ"
            else -> "Fast"
        }
        "speed_normal" -> when (langCode) {
            "te" -> "సాధారణం"
            "ta" -> "சாதாரண"
            "hi" -> "सामान्य"
            "ar" -> "عادي"
            "kn" -> "ಸಾಮಾನ್ಯ"
            "ml" -> "സാധാരണ"
            else -> "Normal"
        }
        "bg_music" -> when (langCode) {
            "te" -> "🎶 బ్యాక్‌గ్రౌండ్ సంగీతం"
            "ta" -> "🎶 பின்னணி இசை"
            "hi" -> "🎶 पृष्ठभूमि संगीत"
            "ar" -> "🎶 موسيقى الخلفية"
            "kn" -> "🎶 ಹಿನ್ನೆಲೆ ಸಂಗೀತ"
            "ml" -> "🎶 പശ്ചാത്തല സംഗീതം"
            else -> "🎶 Background Music"
        }
        "choose_avatar" -> when (langCode) {
            "te" -> "👧 అవతార్ ఎంచుకోండి"
            "ta" -> "👧 அவதாரத்தைத் தேர்வுசெய்"
            "hi" -> "👧 अवतार चुनें"
            "ar" -> "👧 اختر صورتك الرمزية"
            "kn" -> "👧 ಅವತಾರವನ್ನು ಆರಿಸಿ"
            "ml" -> "👧 അവതാർ തിരഞ്ഞെടുക്കുക"
            else -> "👧 Choose Avatar"
        }
        "change_lang" -> when (langCode) {
            "te" -> "🌍 భాష మార్చండి"
            "ta" -> "🌍 மொழியை மாற்று"
            "hi" -> "🌍 भाषा बदलें"
            "ar" -> "🌍 تغيير اللغة"
            "kn" -> "🌍 ಭಾಷೆ ಬದಲಾಯಿಸಿ"
            "ml" -> "🌍 ഭാഷ മാറ്റുക"
            else -> "🌍 Change Language"
        }
        "reset_progress" -> when (langCode) {
            "te" -> "🔄 మొత్తం పురోగతిని రీసెట్ చేయండి"
            "ta" -> "🔄 அனைத்து முன்னேற்றங்களையும் மீட்டமை"
            "hi" -> "🔄 सभी प्रगति रीसेट करें"
            "ar" -> "🔄 إعادة تعيين كل التقدم"
            "kn" -> "🔄 ಎಲ್ಲಾ ಪ್ರಗತಿಯನ್ನು ಮರುಹೊಂದಿಸಿ"
            "ml" -> "🔄 എല്ലാ പുരോഗതിയും റീസെറ്റ് ചെയ്യുക"
            else -> "🔄 Reset All Progress"
        }
        "confirm_reset_1" -> when (langCode) {
            "te" -> "మీ నక్షత్రాలు మరియు పురోగతిని నిజంగా రీసెట్ చేయాలనుకుంటున్నారా?"
            "ta" -> "உங்கள் நட்சத்திரங்கள் மற்றும் முன்னேற்றத்தை மீட்டமைக்க வேண்டுமா?"
            "hi" -> "क्या आप वाकई अपने सभी सितारों और प्रगति को रीसेट करना चाहते हैं?"
            "ar" -> "هل أنت متأكد أنك تريد إعادة تعيين كل النجوم والتقدم الخاص بك؟"
            "kn" -> "ನಿಮ್ಮ ಎಲ್ಲಾ ನಕ್ಷತ್ರಗಳು ಮತ್ತು ಪ್ರಗತಿಯನ್ನು ಮರುಹೊಂದಿಸಲು ನೀವು ಖಚಿತವಾಗಿ ಬಯಸುವಿರಾ?"
            "ml" -> "നിങ്ങളുടെ എല്ലാ നക്ഷത്രങ്ങളും പുരോഗതിയും റീസെറ്റ് ചെയ്യണമെന്ന് ഉറപ്പാണോ?"
            else -> "Are you sure you want to reset all your stars and progress?"
        }
        "confirm_reset_2" -> when (langCode) {
            "te" -> "ఇది అన్ని బ్యాడ్జ్‌లు మరియు నక్షత్రాలను తొలగిస్తుంది!"
            "ta" -> "ఇది அனைத்து பேட்ஜ்கள் மற்றும் நட்சத்திரங்களை அழித்துவிடும்!"
            "hi" -> "इससे सभी बैज और सितारे मिट जाएंगे!"
            "ar" -> "سيؤدي هذا إلى مسح جميع الشارات والنجوم!"
            "kn" -> "ಇದು ಎಲ್ಲಾ ಬ್ಯಾಡ್ಜ್‌ಗಳು ಮತ್ತು ನಕ್ಷತ್ರಗಳನ್ನು అಳಿಸಿಹಾಕುತ್ತದೆ!"
            "ml" -> "ഇത് എല്ലാ ബാഡ്ജുകളും നക്ഷത്രങ്ങളും ഇല്ലാതാക്കും!"
            else -> "This will erase all badges and stars!"
        }
        "keep_progress" -> when (langCode) {
            "te" -> "వద్దు, అలాగే ఉంచు!"
            "ta" -> "இல்லை, அப்படியே இருக்கட்டும்!"
            "hi" -> "नहीं, इसे रखें!"
            "ar" -> "لا، احتفظ به!"
            "kn" -> "ಬೇಡ, ಹಾಗೆಯೇ ಇರಲಿ!"
            "ml" -> "വേണ്ട, അങ്ങനെ തന്നെ ഇരിക്കട്ടെ!"
            else -> "No, Keep It!"
        }
        "yes_reset" -> when (langCode) {
            "te" -> "అవును, రీసెట్ చేయి!"
            "ta" -> "ஆம், மீட்டமை!"
            "hi" -> "हाँ, रीसेट करें!"
            "ar" -> "نعم، أعد التعيين!"
            "kn" -> "ಹೌದು, ಮರುಹೊಂದಿಸಿ!"
            "ml" -> "അതെ, റീസെറ്റ് ചെയ്യുക!"
            else -> "Yes, Reset!"
        }
        "close" -> when (langCode) {
            "te" -> "మూసివేయి"
            "ta" -> "மூடு"
            "hi" -> "बंद करें"
            "ar" -> "إغلاق"
            "kn" -> "ಮುಚ್ಚಿ"
            "ml" -> "അടയ്ക്കുക"
            else -> "Close"
        }
        else -> ""
    }
}

@Composable
fun BadgesDialog(
    badges: List<Badge>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆 Unlocked Badges",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(badges) { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (badge.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (badge.unlocked) Color.White
                                        else Color.LightGray.copy(alpha = 0.5f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (badge.unlocked) badge.icon else "🔒",
                                    fontSize = 24.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (badge.unlocked) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = badge.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Super!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LanguageMascotBadge(langCode: String, modifier: Modifier = Modifier) {
    val (nativeChar, emoji, charColor) = when (langCode) {
        "te" -> Triple("అ", "🦚", Color(0xFFD81B60)) // Telugu: Peacock / Warm Pink
        "en" -> Triple("A", "🎒", Color(0xFF1E88E5))  // English: Backpack / Blue
        "ta" -> Triple("அ", "🪕", Color(0xFFF4511E)) // Tamil: Lute / Orange
        "hi" -> Triple("अ", "🪷", Color(0xFF43A047))  // Hindi: Lotus / Green
        "ar" -> Triple("أ", "🐪", Color(0xFF8E24AA))  // Arabic: Camel / Purple
        "kn" -> Triple("ಅ", "🍃", Color(0xFF00897B)) // Kannada: Leaf / Teal
        "ml" -> Triple("అ", "🌴", Color(0xFFFFB300)) // Malayalam: Palm / Gold
        "bn" -> Triple("অ", "🐯", Color(0xFFE65100)) // Bengali: Tiger / Orange
        "mr" -> Triple("अ", "🦁", Color(0xFF8D6E63)) // Marathi: Lion / Brown
        "gu" -> Triple("અ", "🪔", Color(0xFF00ACC1)) // Gujarati: Diya / Cyan
        else -> Triple("❓", "✨", Color.DarkGray)
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(charColor.copy(alpha = 0.15f))
            .border(1.5.dp, charColor, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nativeChar,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = charColor
        )
    }
}

private data class QuickActionItem(
    val icon: String,
    val title: String,
    val containerColor: Color,
    val borderColor: Color,
    val textColor: Color,
    val onClick: () -> Unit
)

fun getLocalizedStreakTitle(langCode: String): String = when (langCode) {
    "te" -> "రోజువారీ సాధన స్ట్రీక్! 🔥"
    "ta" -> "தினசரி பயிற்சி தொடர்ச்சி! 🔥"
    "hi" -> "दैनिक अभ्यास सिलसिला! 🔥"
    "kn" -> "ದೈನಂದಿನ ಅಭ್ಯಾಸದ ಸ್ಟ್ರೀಕ್! 🔥"
    "ml" -> "ദിനചര്യ പരിശീലനം! 🔥"
    else -> "Daily Practice Streak! 🔥"
}

fun getLocalizedStreakMessage(dailyStreak: Int, langCode: String): String = when (langCode) {
    "te" -> "నువ్వు వరుసగా $dailyStreak రోజులు సాధన చేశావు బంగారం! ఇలాగే ప్రతిరోజూ కొత్త విషయాలు నేర్చుకో! 🚀✨"
    "ta" -> "நீங்கள் தொடர்ந்து $dailyStreak நாட்கள் பயிற்சி செய்துள்ளீர்கள்! தினமும் புதிய விஷயங்களைக் கற்றுக்கொள்ளுங்கள்! 🚀✨"
    "hi" -> "आपने लगातार $dailyStreak दिनों तक अभ्यास किया है! हर दिन नई चीजें सीखते रहें! 🚀✨"
    "kn" -> "ನೀವು ಸತತವಾಗಿ $dailyStreak ದಿನಗಳ ಕಾಲ ಅಭ್ಯಾಸ ಮಾಡಿದ್ದೀರಿ! ಪ್ರತಿದิน ಹೊಸ ವಿಷಯಗಳನ್ನು ಕಲಿಯಿರಿ! 🚀✨"
    "ml" -> "നിങ്ങൾ തുടർച്ചയായി $dailyStreak ദിവസങ്ങൾ പരിശീലിച്ചു! ദിവസവും പുതിയ കാര്യങ്ങൾ പഠിക്കുക! 🚀✨"
    else -> "You have practiced for $dailyStreak days in a row! Keep going every day to build a magical learning habit! 🚀✨"
}

fun getLocalizedSelectLesson(langCode: String): String = when (langCode) {
    "te" -> "పాఠాన్ని ఎంచుకోండి! 🎓"
    "ta" -> "பாடத்தைத் தேர்வு செய்க! 🎓"
    "hi" -> "पाठ चुनें! 🎓"
    "ar" -> "اختر درساً! 🎓"
    "kn" -> "ಪಾಠವನ್ನು ಆರಿಸಿ! 🎓"
    "ml" -> "പാಠം തിരഞ്ഞെടുക്കുക! 🎓"
    "bn" -> "একটি পাঠ নির্বাচন করুন! 🎓"
    "mr" -> "पाठ निवडा! 🎓"
    "gu" -> "પાઠ પસંદ કરો! 🎓"
    else -> "Select a Lesson! 🎓"
}

fun getLocalizedPlay(langCode: String): String = when (langCode) {
    "te" -> "నేర్చుకో 🌟"
    "ta" -> "விளையாடு 🌟"
    "hi" -> "खेलो 🌟"
    "ar" -> "ابدأ 🌟"
    "kn" -> "ಕಲಿ 🌟"
    "ml" -> "പഠിക്കുക 🌟"
    "bn" -> "খেলো 🌟"
    "mr" -> "शिका 🌟"
    "gu" -> "રમો 🌟"
    else -> "Play 🌟"
}

fun getHomeworkTitle(langCode: String): String = when (langCode) {
    "te" -> "హోంవర్క్ 📚"
    "ta" -> "வீட்டுப்பாடம் 📚"
    "hi" -> "गृहकार्य 📚"
    "ar" -> "الواجب المدرسي 📚"
    "kn" -> "ಮನೆಕೆಲಸ 📚"
    "ml" -> "ഹോംവർക്ക് 📚"
    "bn" -> "বাড়ির কাজ 📚"
    "mr" -> "गृहपाठ 📚"
    "gu" -> "ગૃહકાર્ય 📚"
    else -> "Homework 📚"
}

fun getGamesTitle(langCode: String): String = when (langCode) {
    "te" -> "ఆటలు 🎮"
    "ta" -> "விளையாட்டுகள் 🎮"
    "hi" -> "खेल 🎮"
    "ar" -> "ألعاب 🎮"
    "kn" -> "ಆಟಗಳು 🎮"
    "ml" -> "കളികൾ 🎮"
    "bn" -> "খেলাধুলা 🎮"
    "mr" -> "खेळ 🎮"
    "gu" -> "રમતો 🎮"
    else -> "Games 🎮"
}

fun getAiTeacherTitle(langCode: String): String = when (langCode) {
    "te" -> "AI గురువు 🤖"
    "ta" -> "AI ஆசிரியர் 🤖"
    "hi" -> "AI शिक्षक 🤖"
    "ar" -> "معلم الذكاء الاصطناعي 🤖"
    "kn" -> "AI ಶಿಕ್ಷಕ 🤖"
    "ml" -> "AI ടീച്ചർ 🤖"
    "bn" -> "এআই শিক্ষক 🤖"
    "mr" -> "एआय शिक्षक 🤖"
    "gu" -> "AI શિક્ષક 🤖"
    else -> "AI Teacher 🤖"
}

fun getCameraTitle(langCode: String): String = when (langCode) {
    "te" -> "కెమెరా లెర్నింగ్ 📷"
    "ta" -> "கேமரா கற்றல் 📷"
    "hi" -> "कैमरा लर्निंग 📷"
    "ar" -> "التعلم بالكاميرا 📷"
    "kn" -> "ಕ್ಯಾಮೆರಾ ಕಲಿಕೆ 📷"
    "ml" -> "ക്യാമറ ലேണിംഗ് 📷"
    "bn" -> "ক্যামেরা লার্নিং 📷"
    "mr" -> "कॅмера लर्निंग 📷"
    "gu" -> "કેમેરા લર્નિંગ 📷"
    else -> "Camera Learning 📷"
}



fun getDifficultyLevelLabel(level: DifficultyLevel, langCode: String): String = when (level) {
    DifficultyLevel.BEGINNER -> when (langCode) {
        "te" -> "బిగినర్ 🌱"
        "ta" -> "தொடக்க நிலை 🌱"
        "hi" -> "शुरुआती 🌱"
        "ar" -> "مبتدئ 🌱"
        "kn" -> "ಆರಂಭಿಕ 🌱"
        "ml" -> "തുടക്കക്കാരൻ 🌱"
        "bn" -> "শিক্ষানবিস 🌱"
        "mr" -> "नवशिक्या 🌱"
        "gu" -> "પ્રારંભિક 🌱"
        else -> "Beginner 🌱"
    }
    DifficultyLevel.INTERMEDIATE -> when (langCode) {
        "te" -> "ఇంటర్మీడియట్ 🚀"
        "ta" -> "இடைநிலை 🚀"
        "hi" -> "मध्यम 🚀"
        "ar" -> "متوسط 🚀"
        "kn" -> "ಮಧ್ಯಂತರ 🚀"
        "ml" -> "ഇടത്തരം 🚀"
        "bn" -> "मध्यवर्ती 🚀"
        "mr" -> "मध्यम 🚀"
        "gu" -> "મધ્યવર્તી 🚀"
        else -> "Intermediate 🚀"
    }
    DifficultyLevel.ADVANCED -> when (langCode) {
        "te" -> "అడ్వాన్స్డ్ 🏆"
        "ta" -> "மேம்பட்ட நிலை 🏆"
        "hi" -> "उन्नत 🏆"
        "ar" -> "متقدم 🏆"
        "kn" -> "ಸುಧಾರಿತ 🏆"
        "ml" -> "ഉയർന്ന നില 🏆"
        "bn" -> "উন্নত 🏆"
        "mr" -> "प्रगत 🏆"
        "gu" -> "અદ્યતન 🏆"
        else -> "Advanced 🏆"
    }
}

@Composable
fun WindingLessonPath(
    lessons: List<LessonCategory>,
    langCode: String,
    viewModel: LearningViewModel,
    onLessonClick: (LessonCategory) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .drawBehind {
                    val pathWidth = size.width
                    val pathHeight = size.height
                    val itemCount = lessons.size
                    if (itemCount > 1) {
                        // Apply vertical offsets so that path starts/ends at the center of the first/last card
                        val topPaddingPx = 55.dp.toPx()
                        val bottomPaddingPx = 55.dp.toPx()
                        val drawableHeight = pathHeight - topPaddingPx - bottomPaddingPx
                        
                        if (drawableHeight > 0) {
                            val stepY = drawableHeight / (itemCount - 1)
                            val path = Path()
                            
                            for (i in 0 until itemCount) {
                                val bias = when (i % 4) {
                                    0 -> -0.5f // Left
                                    1 -> 0f    // Center
                                    2 -> 0.5f  // Right
                                    else -> 0f // Center (3)
                                }
                                val x = (pathWidth / 2f) + (bias * (pathWidth / 2f))
                                val y = topPaddingPx + i * stepY
                                
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    val prevBias = when ((i - 1) % 4) {
                                        0 -> -0.5f
                                        1 -> 0f
                                        2 -> 0.5f
                                        else -> 0f
                                    }
                                    val prevX = (pathWidth / 2f) + (prevBias * (pathWidth / 2f))
                                    val prevY = topPaddingPx + (i - 1) * stepY
                                    
                                    val controlY1 = prevY + (stepY / 2f)
                                    val controlY2 = prevY + (stepY / 2f)
                                    
                                    path.cubicTo(
                                        prevX, controlY1,
                                        x, controlY2,
                                        x, y
                                    )
                                }
                            }
                            
                            // Draw thick road base (brown)
                            drawPath(
                                path = path,
                                color = Color(0xFFD7CCC8),
                                style = Stroke(
                                    width = 12f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    join = androidx.compose.ui.graphics.StrokeJoin.Round
                                )
                            )
                            
                            // Draw dashed center line (white)
                            drawPath(
                                path = path,
                                color = Color(0xFFFFFFFF),
                                style = Stroke(
                                    width = 4f,
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(15f, 15f),
                                        phase = 0f
                                    )
                                )
                            )
                        }
                    }
                }
                .padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            lessons.forEachIndexed { index, category ->
                val isCompleted = viewModel.isLessonCompleted(langCode, category)
                val stars = viewModel.getLessonStars(langCode, category)
                
                val bias = when (index % 4) {
                    0 -> -0.5f // Left
                    1 -> 0f    // Center
                    2 -> 0.5f  // Right
                    else -> 0f // Center
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = BiasAlignment(bias, 0f)
                ) {
                    MilestoneNodeCard(
                        stepNumber = index + 1,
                        category = category,
                        langCode = langCode,
                        isCompleted = isCompleted,
                        stars = stars,
                        onClick = { onLessonClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneNodeCard(
    stepNumber: Int,
    category: LessonCategory,
    langCode: String,
    isCompleted: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val cardBgColor = Color(android.graphics.Color.parseColor(category.colorHex))
    
    val borderStrokeColor = when (category) {
        LessonCategory.LETTERS -> Color(0xFFE53935)
        LessonCategory.LETTER_WORD -> Color(0xFF3949AB)
        LessonCategory.NUMBERS -> Color(0xFF00897B)
        LessonCategory.CONSONANTS -> Color(0xFFF4511E)
        LessonCategory.SPICES -> Color(0xFFD81B60)
        LessonCategory.MONTHS -> Color(0xFF0288D1)
        LessonCategory.POEMS -> Color(0xFFE040FB)
        LessonCategory.VEGETABLES -> Color(0xFF2E7D32)
        LessonCategory.STATES_CAPITALS -> Color(0xFF3E2723)
        LessonCategory.NATIONAL_SYMBOLS -> Color(0xFF0D47A1)
        LessonCategory.RELATIONSHIPS -> Color(0xFFFF6F00)
        LessonCategory.FOODS -> Color(0xFF1B5E20)
        LessonCategory.FRUITS_FLOWERS -> Color(0xFFB71C1C)
        LessonCategory.BODY_PARTS -> Color(0xFF7B1FA2)
        LessonCategory.SHAPES -> Color(0xFFFBC02D)
        LessonCategory.BASIC_MATH -> Color(0xFF1976D2)
        LessonCategory.DIRECTIONS -> Color(0xFFC2185B)
        LessonCategory.SCHOOL_OBJECTS -> Color(0xFF388E3C)
        LessonCategory.WEATHER -> Color(0xFF0097A7)
    }

    // Hover, Press & Completed Achievement Animations
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isTargeted = isHovered || isPressed

    val scale by animateFloatAsState(
        targetValue = if (isTargeted) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "milestoneScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isTargeted) -2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "milestoneRotation"
    )

    val elevation by animateDpAsState(
        targetValue = if (isTargeted) 8.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "milestoneElevation"
    )

    val borderAnimWidth by animateDpAsState(
        targetValue = if (isTargeted) 3.5.dp else 2.5.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "milestoneBorderWidth"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "milestoneCompletedShine")
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "milestoneShineProgress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(105.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
            .testTag("dashboard_card_${category.name}")
    ) {
        // Step bubble
        Box(
            modifier = Modifier
                .offset(y = 8.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(borderStrokeColor)
                .padding(horizontal = 6.dp, vertical = 1.5.dp)
                .zIndex(2f)
        ) {
            Text(
                text = "STEP $stepNumber",
                color = Color.White,
                fontSize = 7.sp,
                fontWeight = FontWeight.Black
            )
        }

        // Main bubble
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            border = BorderStroke(borderAnimWidth, borderStrokeColor),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotation
                }
                .drawBehind {
                    if (isCompleted) {
                        val width = size.width
                        val height = size.height
                        val progressX = width * shineProgress
                        
                        val brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.0f),
                                Color.White.copy(alpha = 0.4f),
                                Color.White.copy(alpha = 0.0f)
                            ),
                            start = Offset(progressX - 60.dp.toPx(), 0f),
                            end = Offset(progressX + 60.dp.toPx(), height)
                        )
                        drawRect(brush = brush)
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large visual circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = category.getIcon(langCode), fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                AutoScaleText(
                    text = category.getTitle(langCode),
                    initialFontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minFontSize = 8.sp
                )

                Spacer(modifier = Modifier.height(1.dp))

                AutoScaleText(
                    text = getCategorySubTitle(category, langCode),
                    initialFontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minFontSize = 6.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stars or Play indicator - wrapContentHeight and slightly reduced initial font size + AutoScaleText for full visibility
                Box(
                    modifier = Modifier.wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏅 ", fontSize = 7.5.sp)
                            repeat(stars) {
                                Text(text = "⭐", fontSize = 7.5.sp)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.40f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            AutoScaleText(
                                text = getLocalizedPlay(langCode),
                                initialFontSize = 6.5.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                minFontSize = 5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditDialog(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val profileName by viewModel.profileName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val langCode = selectedLanguage?.code ?: "en"

    var tempName by remember { mutableStateOf(profileName) }
    val bitmapState = remember(profileImageUri) { mutableStateOf<Bitmap?>(null) }

    // Load bitmap from uri
    LaunchedEffect(profileImageUri) {
        if (profileImageUri.isNotEmpty()) {
            try {
                val uri = Uri.parse(profileImageUri)
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    bitmapState.value = BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                bitmapState.value = null
            }
        } else {
            bitmapState.value = null
        }
    }

    // Picker for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                viewModel.updateProfileImageUri(uri.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Camera picture preview launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val filename = "profile_pic_${System.currentTimeMillis()}.jpg"
                context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
                val file = context.getFileStreamPath(filename)
                viewModel.updateProfileImageUri(Uri.fromFile(file).toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val titleText = when (langCode) {
        "te" -> "👤 ప్రొఫైల్ సవరించండి"
        "hi" -> "👤 प्रोफ़ाइल संपादित करें"
        else -> "👤 Edit Profile"
    }

    val nameLabelText = when (langCode) {
        "te" -> "నీ ముద్దు పేరు:"
        "hi" -> "आपका प्यारा नाम:"
        else -> "Your Nickname:"
    }

    val photoLabelText = when (langCode) {
        "te" -> "ప్రొఫైల్ ఫోటో:"
        "hi" -> "प्रोफ़ाइल फ़ोटो:"
        else -> "Profile Photo:"
    }

    val saveText = when (langCode) {
        "te" -> "సేవ్ చేయి"
        "hi" -> "सहेजें"
        else -> "Save"
    }

    val cancelText = when (langCode) {
        "te" -> "రద్దు చేయి"
        "hi" -> "रद्द करें"
        else -> "Cancel"
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titleText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Large Profile Image Preview
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val currentBitmap = bitmapState.value
                    if (currentBitmap != null) {
                        Image(
                            bitmap = currentBitmap.asImageBitmap(),
                            contentDescription = "Profile Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Default Profile",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Camera and Gallery buttons row
                Text(
                    text = photoLabelText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Take photo button
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Choose from gallery button
                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Reset / Delete Photo option if exists
                    if (profileImageUri.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.updateProfileImageUri("") },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Photo", modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nickname input field
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text(text = nameLabelText) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save & Cancel Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = cancelText, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                viewModel.updateProfileName(tempName)
                            }
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = saveText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

