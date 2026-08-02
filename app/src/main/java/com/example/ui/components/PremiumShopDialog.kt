package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumShopDialog(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    val diamonds by viewModel.diamonds.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Video watching simulation state
    var isWatchingVideo by remember { mutableStateOf(false) }
    var videoCountdown by remember { mutableStateOf(5) }

    // Daily Challenge state
    var showDailyChallenge by remember { mutableStateOf(false) }
    var selectedChallengeAnswer by remember { mutableStateOf<Int?>(null) }
    var showChallengeFeedback by remember { mutableStateOf<Boolean?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Header Panel
                Column(modifier = Modifier.fillMaxSize()) {
                    // Custom Kids Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF00B0FF), Color(0xFF00E5FF))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "💎 Diamond Shop",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        // Close button & Diamonds display
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "💎", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$diamonds",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Dialog",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Content
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Info text
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "💡", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "Earn Diamonds to ask your AI Teacher questions! AI Teacher queries cost 10 Diamonds or can be unlocked by watching a rewarded ad.",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            // 1. Daily Educational Challenge
                            item {
                                val isChallengeDone = viewModel.isChallengeCompletedToday()
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isChallengeDone) Color(0xFFE0F2F1) else Color(0xFFFFF8E1)
                                    ),
                                    border = BorderStroke(3.dp, if (isChallengeDone) Color(0xFF00BFA5) else Color(0xFFFFB300))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "🎯", fontSize = 28.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Daily Language Challenge",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF795548)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = if (isChallengeDone) {
                                                "Aadindhi Chalu! (Challenge Completed today! Come back tomorrow!)"
                                            } else {
                                                "Answer the kid vocabulary puzzle and win 💎 25 Diamonds instantly!"
                                            },
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (!isChallengeDone) {
                                            Button(
                                                onClick = {
                                                    selectedChallengeAnswer = null
                                                    showChallengeFeedback = null
                                                    showDailyChallenge = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9100)),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Text("Play Daily Challenge 🎯", fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Text(
                                                text = "Completed! 🎉 +25 💎 Added",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00796B),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Watch Educational Videos
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFECEFF1)
                                    ),
                                    border = BorderStroke(2.dp, Color(0xFF90A4AE))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "🎥 Watch Learn & Earn!",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF37474F)
                                            )
                                            Text(
                                                text = "Watch a cute cartoon educational video to win 💎 15 Diamonds!",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                isWatchingVideo = true
                                                videoCountdown = 5
                                                coroutineScope.launch {
                                                    while (videoCountdown > 0) {
                                                        delay(1000)
                                                        videoCountdown--
                                                    }
                                                    isWatchingVideo = false
                                                    viewModel.earnDiamonds(15)
                                                    viewModel.playSuccessSound()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                                            shape = RoundedCornerShape(12.dp),
                                            enabled = !isWatchingVideo
                                        ) {
                                            Text("Watch 📺", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Daily Vocabulary puzzle challenge pop-up
    if (showDailyChallenge) {
        AlertDialog(
            onDismissRequest = { showDailyChallenge = false },
            title = {
                Text(
                    text = "🌟 Daily Word Puzzle 🌟",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
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
                        text = "What is the English name for the bird \"నెమలి\" (Nemali) / \"மயில்\" (Mayil) / \"मोर\" (Mor)?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val options = listOf("Parrot 🦜", "Peacock 🦚", "Pigeon 🐦", "Eagle 🦅")
                    options.forEachIndexed { index, option ->
                        val isSelected = selectedChallengeAnswer == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (showChallengeFeedback == null) {
                                        selectedChallengeAnswer = index
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                2.dp,
                                if (isSelected) Color(0xFFFF9100) else Color.LightGray
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White
                            )
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(14.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black
                            )
                        }
                    }

                    showChallengeFeedback?.let { isCorrect ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isCorrect) "🎉 Shabash! Correct Answer! +25 💎" else "❌ Oops! Try Again!",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = if (isCorrect) Color(0xFF00C853) else Color(0xFFD50000)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val selectedIndex = selectedChallengeAnswer
                        if (selectedIndex != null) {
                            val isCorrect = selectedIndex == 1 // Peacock is correct
                            showChallengeFeedback = isCorrect
                            if (isCorrect) {
                                viewModel.completeDailyChallenge()
                                coroutineScope.launch {
                                    delay(1500)
                                    showDailyChallenge = false
                                }
                            } else {
                                viewModel.playWrongSound()
                            }
                        }
                    },
                    enabled = selectedChallengeAnswer != null && showChallengeFeedback == null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Submit Answer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDailyChallenge = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Video watching animation state overlay
    if (isWatchingVideo) {
        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📺🎬🍿", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cute Animated Learning Video",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Watching educational cartoon standard family-safe child appropriate category...",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(
                        progress = videoCountdown.toFloat() / 5f,
                        color = Color(0xFFE91E63),
                        strokeWidth = 6.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Earning rewards in $videoCountdown seconds...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
