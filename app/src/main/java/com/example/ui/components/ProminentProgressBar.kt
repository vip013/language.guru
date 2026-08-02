package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProminentProgressBar(
    progressPercent: Float,
    completedCount: Int,
    totalCount: Int,
    langCode: String,
    modifier: Modifier = Modifier
) {
    // Smooth animate progress changes
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progressBarAnimation"
    )

    // Pulse animation for the glowing badges and sparkles
    val infiniteTransition = rememberInfiniteTransition(label = "sparklePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Bouncing mascot animation (offset vertically)
    val mascotBounce by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotBounce"
    )

    // Language-specific custom translations
    val title = when (langCode) {
        "te" -> "✨ నీ అభ్యాస ప్రయాణం! ✨"
        "hi" -> "✨ आपकी सीखने की यात्रा! ✨"
        "ta" -> "✨ உங்கள் கற்றல் பயணம்! ✨"
        "kn" -> "✨ ನಿಮ್ಮ ಕಲಿಕೆಯ ಪ್ರಯಾಣ! ✨"
        "ml" -> "✨ നിങ്ങളുടെ പഠന യാത്ര! ✨"
        "bn" -> "✨ আপনার শেখার যাত্রা! ✨"
        "mr" -> "✨ तुमचा शिकण्याचा प्रवास! ✨"
        "gu" -> "✨ તમારી શીખવાની યાત્રા! ✨"
        "ar" -> "✨ رحلة التعلم الخاصة بك! ✨"
        else -> "✨ Your Learning Journey! ✨"
    }

    val lessonWord = when (langCode) {
        "te" -> "పాఠాలు"
        "hi" -> "पाठ"
        "ta" -> "பாடங்கள்"
        "kn" -> "ಪಾಠಗಳು"
        "ml" -> "പാഠങ്ങൾ"
        "bn" -> "পাঠ"
        "mr" -> "धडे"
        "gu" -> "પાઠ"
        "ar" -> "دروس"
        else -> "Lessons"
    }

    val completedWord = when (langCode) {
        "te" -> "పూర్తయినవి"
        "hi" -> "पूरे हुए"
        "ta" -> "முடிந்தது"
        "kn" -> "ಪೂರ್ಣಗೊಂಡಿದೆ"
        "ml" -> "പൂർത്തിയായി"
        "bn" -> "সম্পন্ন"
        "mr" -> "पूर्ण"
        "gu" -> "પૂર્ણ"
        "ar" -> "اكتملت"
        else -> "Completed"
    }

    // Dynamic encouraging message based on completion tier
    val statusMsg = when {
        progressPercent == 0f -> when (langCode) {
            "te" -> "నీ అడ్వెంచర్ ప్రారంభించు! 🚀"
            "hi" -> "अपना साहसिक कार्य शुरू करें! 🚀"
            else -> "Let's start your adventure! 🚀"
        }
        progressPercent < 0.35f -> when (langCode) {
            "te" -> "చాలా మంచి ప్రారంభం! ముందుకు సాగండి! 🎈"
            "hi" -> "बहुत बढ़िया शुरुआत! आगे बढ़ें! 🎈"
            else -> "Great start! Keep going! 🎈"
        }
        progressPercent < 0.7f -> when (langCode) {
            "te" -> "నువ్వు అద్భుతంగా చేస్తున్నావు! 🌟"
            "hi" -> "आप कमाल कर रहे हैं! 🌟"
            else -> "You are doing amazing! 🌟"
        }
        progressPercent < 1.0f -> when (langCode) {
            "te" -> "మరొక్క అడుగు... గురు అవ్వడానికి! 🏆"
            "hi" -> "बस एक कदम दूर... गुरु बनने के लिए! 🏆"
            else -> "Almost a Language Guru! 🏆"
        }
        else -> when (langCode) {
            "te" -> "అద్భుతం! నువ్వు నిజమైన లాంగ్వేజ్ గురువువి! 👑"
            "hi" -> "अद्भुत! आप एक सच्चे लैंग्वेज गुरु हैं! 👑"
            else -> "Incredible! You are a true Language Guru! 👑"
        }
    }

    // Bright playful child-friendly gradients
    val trackGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE040FB), // Glowing Purple
            Color(0xFF00E5FF), // Electric Cyan
            Color(0xFF00E676)  // Bright Green
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0xFFE040FB).copy(alpha = 0.1f)
            )
            .testTag("prominent_progress_bar_card"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4).copy(alpha = 0.95f) // Warm golden buttery color
        ),
        border = BorderStroke(1.dp, Color(0xFFFFD54F)) // Sleek playful golden frame
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.Center
        ) {
            val activeMascot = when {
                progressPercent == 0f -> "🌱"
                progressPercent < 0.35f -> "🦁"
                progressPercent < 0.7f -> "🐯"
                progressPercent < 1.0f -> "🚀"
                else -> "👑"
            }

            // Line 1: Title and completed count + Percentage with emoji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🌟",
                        fontSize = 11.sp,
                        modifier = Modifier.offset(y = mascotBounce.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "$title ($completedCount/$totalCount)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF6A1B9A),
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(progressPercent * 100).toInt()}% Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF8E24AA)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = activeMascot,
                        fontSize = 11.sp,
                        modifier = Modifier.offset(y = mascotBounce.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Line 2: Slim Progress track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(trackGradient)
                )
            }
        }
    }
}
