package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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
fun CourseCertificateDialog(
    viewModel: LearningViewModel,
    langCode: String,
    onDismiss: () -> Unit
) {
    val diamonds by viewModel.diamonds.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val boughtCertificates by viewModel.boughtCertificates.collectAsState()

    var childName by remember { mutableStateOf("") }
    var selectedAnimalTemplate by remember { mutableStateOf("lion") } // lion, tiger, panda, unicorn
    var currentLanguageName = remember(langCode) {
        when (langCode) {
            "te" -> "Telugu"
            "ta" -> "Tamil"
            "hi" -> "Hindi"
            "kn" -> "Kannada"
            "ml" -> "Malayalam"
            "ar" -> "Arabic"
            else -> "English"
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var isUnlocked by remember { mutableStateOf(boughtCertificates.contains(langCode)) }
    var isGeneratingSimulated by remember { mutableStateOf(false) }
    var showSuccessOverlay by remember { mutableStateOf(false) }

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
                Column(modifier = Modifier.fillMaxSize()) {
                    // Kids Style Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFB300), Color(0xFFFF5252))
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📜 Kid's Golden Certificate",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Interactive Preview Card (Certificate template design!)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.4f)
                                .border(
                                    BorderStroke(
                                        6.dp,
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                                        )
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDF2)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Background decoration
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                        .border(2.dp, Color(0xFFFFD54F).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                )

                                // Main text layout
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "🏅", fontSize = 28.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "CERTIFICATE OF ACHIEVEMENT",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFB71C1C),
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "🏅", fontSize = 28.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "This golden credential is proudly awarded to",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = childName.ifEmpty { "Your Child's Name" },
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0D47A1),
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(180.dp)
                                                .height(2.dp)
                                                .background(Color(0xFFFFD54F))
                                        )
                                    }

                                    Text(
                                        text = "for successfully finishing all lessons & challenges in\nLanguage Guru's $currentLanguageName Course! 🚀🎉",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )

                                    // Mascot and Signatures
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        // Left Animal Mascot
                                        Text(
                                            text = when (selectedAnimalTemplate) {
                                                "lion" -> "🦁 Lion Club Member"
                                                "tiger" -> "🐯 Tiger Club Member"
                                                "panda" -> "🐼 Panda Club Member"
                                                "unicorn" -> "🦄 Magic Club Member"
                                                else -> "🦉 Wise Owl Member"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF424242)
                                        )

                                        // Right Signature Seal
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "Language Guru Team",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray
                                            )
                                            Text(
                                                text = "🌟 Verified Seal 🌟",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFFFFA000)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Customizations: Name Input
                        OutlinedTextField(
                            value = childName,
                            onValueChange = { childName = it },
                            label = { Text("Enter Child's Name ✍️") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFFB300),
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        // Choose Mascot template
                        Text(
                            text = "Choose Your Favorite Animal Template:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val animals = listOf(
                                "lion" to "🦁",
                                "tiger" to "🐯",
                                "panda" to "🐼",
                                "unicorn" to "🦄"
                            )
                            animals.forEach { (id, emoji) ->
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (selectedAnimalTemplate == id) Color(0xFFFFE082)
                                            else Color.LightGray.copy(alpha = 0.2f)
                                        )
                                        .border(
                                            2.dp,
                                            if (selectedAnimalTemplate == id) Color(0xFFFFB300) else Color.Transparent,
                                            RoundedCornerShape(14.dp)
                                        )
                                        .clickable { selectedAnimalTemplate = id },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 28.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Unlock/Buy Button
                        if (isUnlocked) {
                            Button(
                                onClick = {
                                    isGeneratingSimulated = true
                                    coroutineScope.launch {
                                        delay(2000)
                                        isGeneratingSimulated = false
                                        showSuccessOverlay = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                shape = RoundedCornerShape(16.dp),
                                enabled = childName.isNotBlank() && !isGeneratingSimulated
                            ) {
                                if (isGeneratingSimulated) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Download & Print Certificate 🏆", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        } else {
                            // Needs to unlock
                            val cost = 100
                            val canAfford = diamonds >= cost || isPremium
                            Button(
                                onClick = {
                                    if (isPremium) {
                                        viewModel.unlockCertificateFree(langCode)
                                        isUnlocked = true
                                        viewModel.playSuccessSound()
                                    } else if (diamonds >= cost) {
                                        if (viewModel.purchaseCertificate(langCode, cost)) {
                                            isUnlocked = true
                                            viewModel.playSuccessSound()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPremium) Color(0xFFFF4081) else if (canAfford) Color(0xFF00C853) else Color.Gray
                                ),
                                shape = RoundedCornerShape(16.dp),
                                enabled = (canAfford || isPremium) && childName.isNotBlank()
                            ) {
                                if (isPremium) {
                                    Text("Claim FREE VIP Certificate! 👑", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                } else {
                                    Text("Unlock Certificate for 💎 100", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                // SUCCESS OVERLAY (Confetti, Print preview, share)
                if (showSuccessOverlay) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(16.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎉🥳📜", fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Certificate Generated!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD84315),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Excellent job, ${childName}! You are officially a language champion! 🏆",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        // Simulator share
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("WhatsApp with Parents 📱", fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        // Simulator print
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Print / Save to Gallery 🖨️", fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                TextButton(
                                    onClick = {
                                        showSuccessOverlay = false
                                        onDismiss()
                                    }
                                ) {
                                    Text("Go Back 🚪", fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
