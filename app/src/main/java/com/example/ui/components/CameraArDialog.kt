package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.viewmodel.LearningViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraArDialog(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    val activeLang by viewModel.selectedLanguage.collectAsState()
    val langCode = activeLang?.code ?: "en"

    var isArMode by remember { mutableStateOf(false) } // false = Object Scanner, true = AR Playground

    Dialog(
        onDismissRequest = {
            viewModel.stopSpeech()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = if (langCode == "te") "📷 స్మార్ట్ కెమెరా & AR" else "📷 Smart Camera & AR",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                viewModel.stopSpeech()
                                onDismiss()
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Selectors (Object Scanner vs AR 3D Elephant)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.playClickSound()
                                isArMode = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isArMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_object_mode_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (langCode == "te") "🔍 వస్తువుల గుర్తింపు" else "🔍 Object Scanner",
                                fontWeight = FontWeight.Bold,
                                color = if (!isArMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.playClickSound()
                                isArMode = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isArMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_ar_mode_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (langCode == "te") "🕶️ AR 3D ఏనుగు" else "🕶️ AR 3D Elephant",
                                fontWeight = FontWeight.Bold,
                                color = if (isArMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (!isArMode) {
                        ObjectScannerView(viewModel = viewModel, langCode = langCode)
                    } else {
                        ArPlaygroundView(viewModel = viewModel, langCode = langCode)
                    }
                }
            }
        }
    }
}

// --- Mode 1: Object Scanner & Multilingual Translator ---
@Composable
fun ObjectScannerView(
    viewModel: LearningViewModel,
    langCode: String
) {
    // List of daily scan targets
    val scanTargets = listOf(
        ScanObject("Apple", "🍎", "ఆపిల్ (Apple)", "सेब (Seb)", "ஆப்பிள் (Aappil)"),
        ScanObject("Pen", "🖊️", "పెన్ను (Pennu)", "पेन (Pen)", "பேனா (Penaa)"),
        ScanObject("Book", "📖", "పుస్తకం (Pusthakam)", "किताब (Kitaab)", "புத்தகம் (Puthagam)"),
        ScanObject("Chair", "🪑", "కుర్చీ (Kurchi)", "कुर्सी (Kursi)", "நாற்காலி (Naarkaali)"),
        ScanObject("Dog", "🐶", "కుక్క (Kukka)", "कुत्ता (Kutta)", "நாய் (Naai)"),
        ScanObject("Cat", "🐱", "పిల్లి (Pilli)", "बिल्ली (Billi)", "பூனை (Poonai)"),
        ScanObject("Flower", "🌸", "పువ్వు (Puvvu)", "फूल (Phool)", "பூ (Poo)")
    )

    var activeScanItem by remember { mutableStateOf(scanTargets[0]) }
    var isScanning by remember { mutableStateOf(false) }
    var showScanResult by remember { mutableStateOf(false) }

    // Laser bar sliding animation
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 240f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    LaunchedEffect(isScanning) {
        if (isScanning) {
            viewModel.speakCustomText(
                if (langCode == "te") "కెమెరా స్కాన్ చేస్తోంది... ఒక క్షణం ఆగండి!" else "Scanning object in front of camera... please wait!"
            )
            kotlinx.coroutines.delay(2000)
            isScanning = false
            showScanResult = true
            
            // Speak standard explanation
            val desc = if (langCode == "te") {
                "అద్భుతం! ఇది ఒక ${activeScanItem.name}! తెలుగులో దీనిని ${activeScanItem.telugu} అంటారు."
            } else {
                "Great scan! This is a ${activeScanItem.name}. In Telugu, we call it ${activeScanItem.telugu}."
            }
            viewModel.speakCustomText(desc)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (langCode == "te") "వస్తువును కెమెరా ముందు ఉంచి, స్కాన్ చేయి!" else "Place an object in front of camera and tap Scan!",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Horizontal Row of Domestic objects to select
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(scanTargets) { item ->
                val selected = activeScanItem == item && !isScanning
                Card(
                    modifier = Modifier
                        .clickable {
                            viewModel.playClickSound()
                            activeScanItem = item
                            showScanResult = false
                        }
                        .testTag("scan_select_${item.name}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.emoji, fontSize = 24.sp)
                        Text(item.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Simulated Camera Viewfinder
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.Black)
                .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Simulated live camera frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(Color(0xFF212121), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Outer framing guides
                Text(
                    text = activeScanItem.emoji,
                    fontSize = 110.sp,
                    modifier = Modifier.scale(if (isScanning) 1.1f else 1.0f)
                )

                // Laser scan line overlay
                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .offset(y = (laserOffset - 120).dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Green, Color.Transparent)
                                )
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Big colorful Scan Trigger Button
        Button(
            onClick = {
                viewModel.playClickSound()
                isScanning = true
                showScanResult = false
            },
            enabled = !isScanning,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
            modifier = Modifier
                .size(180.dp, 56.dp)
                .testTag("scan_trigger_button"),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (isScanning) {
                    if (langCode == "te") "స్కాన్ అవుతోంది..." else "Scanning..."
                } else {
                    if (langCode == "te") "📸 స్కాన్ చేయి" else "📸 Scan Object"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Multilingual results card
        if (showScanResult) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ బహుభాషా అనువాదం (Translations) ✨",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    TranslationRow("English (ఇంగ్లీష్)", activeScanItem.name, viewModel, "en")
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    TranslationRow("Telugu (తెలుగు)", activeScanItem.telugu, viewModel, "te")
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    TranslationRow("Hindi (హిందీ)", activeScanItem.hindi, viewModel, "hi")
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    TranslationRow("Tamil (తమిళం)", activeScanItem.tamil, viewModel, "ta")
                }
            }
        }
    }
}

@Composable
fun TranslationRow(
    language: String,
    word: String,
    viewModel: LearningViewModel,
    lang: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = language, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = word, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
        
        IconButton(
            onClick = {
                viewModel.playClickSound()
                viewModel.speakCustomText(word)
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Speak translation",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// --- Mode 2: AR 3D Elephant Playground ---
@Composable
fun ArPlaygroundView(
    viewModel: LearningViewModel,
    langCode: String
) {
    var elephantScale by remember { mutableStateOf(1.2f) }
    var elephantRotation by remember { mutableStateOf(0f) }
    var elephantOffset by remember { mutableStateOf(IntOffset(0, 0)) }
    var isRotatingAnimation by remember { mutableStateOf(false) }

    // Spin animation
    val animatedRotation by animateFloatAsState(
        targetValue = if (isRotatingAnimation) elephantRotation + 360f else elephantRotation,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        finishedListener = {
            isRotatingAnimation = false
            elephantRotation = (elephantRotation + 360f) % 360f
        },
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        viewModel.speakCustomText(
            if (langCode == "te") {
                "స్వాగతం! ఇది AR ఏనుగు ఆట! ఏనుగుని తాకి కదిలించు, తిప్పు, లేదా సైజ్ మార్చు!"
            } else {
                "Welcome to AR Elephant Playground! Drag to move, rotate, or scale the elephant in your room!"
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (langCode == "te") {
                "👉 ఏనుగుని లాగి కదిలించు! పిన్చ్ చేయి లేదా జరుపు!"
            } else {
                "👉 Drag the elephant around! Tap controls to rotate or scale!"
            },
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Main camera viewfinder container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.Black)
                .border(3.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Simulated real-room camera backdrop
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF37474F), Color(0xFF263238))
                        )
                    )
            ) {
                // Tracking grid overlay
                Text(
                    text = "AR TRACKING ACTIVE: PLACE ON SURFACE",
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
                )

                // Drag-and-drop cartoon 3D elephant
                Box(
                    modifier = Modifier
                        .offset { elephantOffset }
                        .scale(elephantScale)
                        .rotate(animatedRotation)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                elephantOffset = IntOffset(
                                    x = elephantOffset.x + dragAmount.x.roundToInt(),
                                    y = elephantOffset.y + dragAmount.y.roundToInt()
                                )
                            }
                        }
                        .size(140.dp)
                        .align(Alignment.Center)
                        .testTag("ar_3d_elephant"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🐘", fontSize = 80.sp)
                        Text(
                            text = if (langCode == "te") "ఏనుగు (3D AR)" else "Elephant (3D)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // AI Mascot in corner
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Text("💡", fontSize = 24.sp, modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AR Controls Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Size Scale Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (langCode == "te") "సైజు (Scale):" else "Scale:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = elephantScale,
                        onValueChange = { elephantScale = it },
                        valueRange = 0.5f..2.5f,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("elephant_scale_slider")
                    )
                    Text(
                        text = "${(elephantScale * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons (Rotate, Reset, Speak trunk trump)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            isRotatingAnimation = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ar_rotate_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Rotate")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (langCode == "te") "360° తిప్పు" else "Rotate 360°", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            viewModel.speakCustomText(
                                if (langCode == "te") {
                                    "ఏనుగు అరుస్తోంది: భీం భీం! ఏనుగు చాలా పెద్ద జంతువు!"
                                } else {
                                    "Elephant trumpets: Bheem Bheem! Elephants are very majestic animals!"
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ar_trumpet_button")
                    ) {
                        Text(text = if (langCode == "te") "🔊 గీంకరించు" else "🔊 Trumpet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.playClickSound()
                            elephantScale = 1.2f
                            elephantRotation = 0f
                            elephantOffset = IntOffset(0, 0)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.8f)
                            .testTag("ar_reset_button")
                    ) {
                        Text(text = if (langCode == "te") "రీసెట్" else "Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Support class
data class ScanObject(
    val name: String,
    val emoji: String,
    val telugu: String,
    val hindi: String,
    val tamil: String
)
