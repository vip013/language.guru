package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.LearningViewModel
import com.example.viewmodel.Screen

@Composable
fun AppFooter(
    modifier: Modifier = Modifier,
    langCode: String = "en",
    viewModel: LearningViewModel? = null,
    isNavigationMode: Boolean = false
) {
    if (!isNavigationMode || viewModel == null) {
        // Fallback: Original Branding Footer
        val localizedLearn = when (langCode) {
            "te" -> "నేర్చుకుందాం!"
            "ta" -> "கற்றுக்கொள்வோம்!"
            "hi" -> "आइए सीखें!"
            "ar" -> "فلنتعلم!"
            "kn" -> "ಕಲಿಯೋಣ!"
            "ml" -> "പഠിക്കാം!"
            "bn" -> "চল শিখি!"
            "mr" -> "चला शिकूया!"
            "gu" -> "ચાલો શીખીએ!"
            else -> "Let's Learn!"
        }

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🐻 Teacher Bhalu • $localizedLearn ❤️",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF6D4C41), // Rich warm Bhalu brown
                textAlign = TextAlign.Center
            )
        }
    } else {
        // WhatsApp-style Bottom Navigation Bar Mode
        val currentScreen by viewModel.currentScreen.collectAsState()
        val showVoiceChat by viewModel.showVoiceChatGlobal.collectAsState()
        val showMiniGames by viewModel.showMiniGamesGlobal.collectAsState()
        val showBadges by viewModel.showBadgesGlobal.collectAsState()

        // Localization helpers
        val homeLabel = when (langCode) {
            "te" -> "ఇల్లు"
            "ta" -> "முகப்பு"
            "hi" -> "होम"
            "ar" -> "الرئيسية"
            "kn" -> "ಮನೆ"
            "ml" -> "ഹോം"
            else -> "Home"
        }

        val aiGuruLabel = when (langCode) {
            "te" -> "గురువు"
            "ta" -> "குரு"
            "hi" -> "गुरु"
            "ar" -> "المعلم"
            "kn" -> "ಗುರು"
            "ml" -> "ഗുരു"
            else -> "AI Guru"
        }

        val gamesLabel = when (langCode) {
            "te" -> "ఆటలు"
            "ta" -> "விளையாட்டுகள்"
            "hi" -> "खेल"
            "ar" -> "الألعاب"
            "kn" -> "ಆಟಗಳು"
            "ml" -> "കളികൾ"
            else -> "Games"
        }

        val badgesLabel = when (langCode) {
            "te" -> "బహుమతులు"
            "ta" -> "பதக்கங்கள்"
            "hi" -> "बैज"
            "ar" -> "الأوسمة"
            "kn" -> "ಪದಕಗಳು"
            "ml" -> "ബാഡ്ജുകൾ"
            else -> "Badges"
        }

        val langLabel = when (langCode) {
            "te" -> "భాషలు"
            "ta" -> "மொழிகள்"
            "hi" -> "भाषाएं"
            "ar" -> "اللغات"
            "kn" -> "ಭಾಷೆಗಳು"
            "ml" -> "ഭാഷകൾ"
            else -> "Languages"
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .background(
                    color = Color(0xFFFFFDF6), // Beautiful warm buttery cream background
                    shape = RoundedCornerShape(24.dp)
                )
                .border(
                    BorderStroke(2.dp, Color(0xFFEFEBE9)), // Soft warm brown border
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                val isHomeActive = currentScreen is Screen.Dashboard || currentScreen is Screen.Learning
                NavigationTabItem(
                    icon = Icons.Default.Home,
                    label = homeLabel,
                    isActive = isHomeActive && !showVoiceChat && !showMiniGames && !showBadges,
                    testTag = "tab_home",
                    onClick = {
                        viewModel.setShowVoiceChat(false)
                        viewModel.setShowMiniGames(false)
                        viewModel.setShowBadges(false)
                        viewModel.navigateToDashboard()
                    }
                )

                // Tab 2: AI Guru
                NavigationTabItem(
                    icon = Icons.Default.Face,
                    label = aiGuruLabel,
                    isActive = showVoiceChat,
                    testTag = "tab_ai_guru",
                    onClick = {
                        viewModel.setShowMiniGames(false)
                        viewModel.setShowBadges(false)
                        viewModel.setShowVoiceChat(true)
                    }
                )

                // Tab 3: Games
                NavigationTabItem(
                    icon = Icons.Default.PlayArrow,
                    label = gamesLabel,
                    isActive = showMiniGames,
                    testTag = "tab_games",
                    onClick = {
                        viewModel.setShowVoiceChat(false)
                        viewModel.setShowBadges(false)
                        viewModel.setShowMiniGames(true)
                    }
                )

                // Tab 4: Badges
                NavigationTabItem(
                    icon = Icons.Default.Star,
                    label = badgesLabel,
                    isActive = showBadges,
                    testTag = "tab_badges",
                    onClick = {
                        viewModel.setShowVoiceChat(false)
                        viewModel.setShowMiniGames(false)
                        viewModel.setShowBadges(true)
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationTabItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF6D4C41) // Teacher Bhalu brown
    val inactiveColor = Color(0xFF9E9E9E) // Soft gray

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 32.dp)
                .background(
                    color = if (isActive) Color(0xFFFFECB3) else Color.Transparent, // Amber highlight
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else inactiveColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
