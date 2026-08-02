package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LearningItem
import com.example.viewmodel.LearningViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.BreakIterator
import kotlin.random.Random

fun getGraphemes(text: String): List<String> {
    if (text.isEmpty()) return emptyList()
    val result = mutableListOf<String>()
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(text)
    var start = iterator.first()
    var end = iterator.next()
    while (end != BreakIterator.DONE) {
        val g = text.substring(start, end).trim()
        if (g.isNotEmpty()) {
            result.add(g)
        }
        start = end
        end = iterator.next()
    }
    return result
}

enum class GameType {
    BALLOON_POP,
    MATCH_ITEMS,
    MISSING_LETTER,
    LISTENING_MODE,
    MEMORY_MODE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiniGamesPlayground(
    viewModel: LearningViewModel,
    onDismiss: () -> Unit
) {
    var activeGame by remember { mutableStateOf(GameType.BALLOON_POP) }
    val totalStars by viewModel.totalStars.collectAsState()
    val selectedLang by viewModel.selectedLanguage.collectAsState()
    val langCode = selectedLang?.code ?: "en"

    // Fetch pool of words from current language configuration
    val allItems = remember(selectedLang) {
        val list = mutableListOf<LearningItem>()
        selectedLang?.lessons?.values?.forEach { list.addAll(it) }
        // Fallback if empty
        if (list.isEmpty()) {
            list.add(LearningItem("A", "Apple", "Apple", "🍎"))
            list.add(LearningItem("B", "Banana", "Banana", "🍌"))
            list.add(LearningItem("C", "Cat", "Cat", "🐱"))
        }
        list.distinctBy { it.display }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = Color.Transparent
        ) {
            AnimalBackgroundContainer(showAnimals = true) {
                Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Playground")
                    }

                    // Scoreboard badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFFFF9C4))
                            .border(2.dp, Color(0xFFFBC02D), RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⭐", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalStars",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF57F17)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Beautiful Title
                Text(
                    text = GameLocalizer.translate("playground_title", langCode),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = GameLocalizer.translate("playground_subtitle", langCode),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Custom Game Selector Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GameSelectorPill(
                        title = GameLocalizer.translate("game_balloon", langCode),
                        selected = activeGame == GameType.BALLOON_POP,
                        onClick = {
                            viewModel.playClickSound()
                            activeGame = GameType.BALLOON_POP
                        }
                    )
                    GameSelectorPill(
                        title = GameLocalizer.translate("game_match", langCode),
                        selected = activeGame == GameType.MATCH_ITEMS,
                        onClick = {
                            viewModel.playClickSound()
                            activeGame = GameType.MATCH_ITEMS
                        }
                    )
                    GameSelectorPill(
                        title = GameLocalizer.translate("game_missing", langCode),
                        selected = activeGame == GameType.MISSING_LETTER,
                        onClick = {
                            viewModel.playClickSound()
                            activeGame = GameType.MISSING_LETTER
                        }
                    )
                    GameSelectorPill(
                        title = GameLocalizer.translate("game_listening", langCode),
                        selected = activeGame == GameType.LISTENING_MODE,
                        onClick = {
                            viewModel.playClickSound()
                            activeGame = GameType.LISTENING_MODE
                        }
                    )
                    GameSelectorPill(
                        title = GameLocalizer.translate("game_memory", langCode),
                        selected = activeGame == GameType.MEMORY_MODE,
                        onClick = {
                            viewModel.playClickSound()
                            activeGame = GameType.MEMORY_MODE
                        }
                    )
                }

                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Loaded Game Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    when (activeGame) {
                        GameType.BALLOON_POP -> BalloonPopGame(
                            viewModel = viewModel,
                            allItems = allItems,
                            langCode = langCode
                        )
                        GameType.MATCH_ITEMS -> MatchItemsGame(
                            viewModel = viewModel,
                            allItems = allItems,
                            langCode = langCode
                        )
                        GameType.MISSING_LETTER -> MissingLetterGame(
                            viewModel = viewModel,
                            allItems = allItems,
                            langCode = langCode
                        )
                        GameType.LISTENING_MODE -> ListeningModeGame(
                            viewModel = viewModel,
                            allItems = allItems,
                            langCode = langCode
                        )
                        GameType.MEMORY_MODE -> MemoryModeGame(
                            viewModel = viewModel,
                            allItems = allItems,
                            langCode = langCode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            }
        }
    }
}

@Composable
fun GameSelectorPill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderStroke = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .border(borderStroke, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = contentColor
        )
    }
}

// ---------------- GAME 1: BALLOON POP ----------------

data class BalloonState(
    val id: Int,
    val text: String,
    val xPercent: Float, // 5 to 85%
    var yPercent: Float, // floats from 110f (bottom) to -20f (top)
    val color: Color,
    val speed: Float,
    val size: Int = 75
)

@Composable
fun BalloonPopGame(
    viewModel: LearningViewModel,
    allItems: List<LearningItem>,
    langCode: String
) {
    val coroutineScope = rememberCoroutineScope()
    var targetItem by remember { mutableStateOf<LearningItem?>(null) }
    var balloons by remember { mutableStateOf<List<BalloonState>>(emptyList()) }
    var burstBalloonId by remember { mutableStateOf<Int?>(null) }
    var gameStarsEarned by remember { mutableStateOf(0) }

    // Colors list for balloons
    val balloonColors = listOf(
        Color(0xFFFF5252), Color(0xFF448AFF), Color(0xFF4CAF50),
        Color(0xFFFFEB3B), Color(0xFFFF9800), Color(0xFFE040FB), Color(0xFF00BCD4)
    )

    // Select or rotate target
    fun selectNewTarget() {
        if (allItems.isNotEmpty()) {
            val valid = allItems.filter { it.display.length == 1 || it.display.length <= 4 }
            targetItem = if (valid.isNotEmpty()) valid.random() else allItems.random()
        }
    }

    // Spawn / reset a single balloon
    fun generateBalloon(id: Int, targetText: String): BalloonState {
        val randomText = if (Random.nextFloat() < 0.35f) {
            targetText
        } else {
            val randomItem = allItems.randomOrNull()
            randomItem?.display ?: targetText
        }

        return BalloonState(
            id = id,
            text = randomText,
            xPercent = Random.nextInt(10, 80).toFloat(),
            yPercent = 110f, // starts below screen
            color = balloonColors.random(),
            speed = Random.nextFloat() * 1.5f + 1.2f,
            size = Random.nextInt(75, 95)
        )
    }

    // Initialize Game
    LaunchedEffect(allItems) {
        selectNewTarget()
    }

    LaunchedEffect(targetItem) {
        val target = targetItem?.display ?: return@LaunchedEffect
        // Create 5 active floating balloons
        balloons = (1..5).map { id ->
            generateBalloon(id, target).apply {
                // stagger initial starting heights
                yPercent = Random.nextInt(50, 110).toFloat()
            }
        }
    }

    // Main float loop
    LaunchedEffect(balloons, targetItem) {
        val target = targetItem?.display ?: return@LaunchedEffect
        while (true) {
            delay(40) // ~25 FPS
            balloons = balloons.map { balloon ->
                val nextY = balloon.yPercent - balloon.speed
                if (nextY < -15f) {
                    // Respawn at bottom
                    generateBalloon(balloon.id, target)
                } else {
                    balloon.copy(yPercent = nextY)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                )
            )
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Target Board Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.8f))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = GameLocalizer.translate("balloon_instruction", langCode),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = targetItem?.display ?: "",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (!targetItem?.visualEmoji.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = targetItem?.visualEmoji ?: "", fontSize = 28.sp)
                    }
                }
                Text(
                    text = "(${targetItem?.subtitle})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Sky Box with floating balloons
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
        ) {
            val width = maxWidth
            val height = maxHeight

            // Render Balloons
            balloons.forEach { balloon ->
                val xPos = (balloon.xPercent / 100f) * width.value
                val yPos = (balloon.yPercent / 100f) * height.value

                Box(
                    modifier = Modifier
                        .offset(x = xPos.dp, y = yPos.dp)
                        .size(balloon.size.dp)
                        .clickable {
                            val isCorrect = balloon.text == targetItem?.display
                            if (isCorrect) {
                                viewModel.playCorrectSound()
                                viewModel.earnMiniGameStar()
                                gameStarsEarned++
                                burstBalloonId = balloon.id

                                coroutineScope.launch {
                                    delay(400) // explosion duration
                                    burstBalloonId = null
                                    selectNewTarget()
                                }
                            } else {
                                viewModel.playWrongSound()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (burstBalloonId == balloon.id) {
                        // Blast/burst animation
                        Text("💥 POP!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    } else {
                        // Drawing realistic cute rounded balloon with string
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size((balloon.size * 0.85f).dp)
                                    .clip(CircleShape)
                                    .background(balloon.color)
                                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = balloon.text,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (balloon.color == Color(0xFFFFEB3B)) Color.Black else Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                            // Balloon tail string
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(14.dp)
                                    .background(Color.DarkGray.copy(alpha = 0.4f))
                            )
                        }
                    }
                }
            }

            // Earned stars indicator inside sky
            if (gameStarsEarned > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Score: +$gameStarsEarned ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                    )
                }
            }
        }
    }
}

// ---------------- GAME 2: MATCH ITEMS ----------------

@Composable
fun MatchItemsGame(
    viewModel: LearningViewModel,
    allItems: List<LearningItem>,
    langCode: String
) {
    // We select 4 unique items that contain valid emojis/drawings
    val matchPool = remember(allItems) {
        val withEmojis = allItems.filter { it.visualEmoji.isNotEmpty() }
        if (withEmojis.size >= 4) withEmojis.shuffled().take(4) else allItems.shuffled().take(4)
    }

    var selectedLeft by remember { mutableStateOf<String?>(null) }
    var selectedRight by remember { mutableStateOf<String?>(null) }
    var matchedLeft by remember { mutableStateOf<Set<String>>(emptySet()) }
    var matchedRight by remember { mutableStateOf<Set<String>>(emptySet()) }
    var successRound by remember { mutableStateOf(false) }

    // Shuffle left display and right emoji lists once
    val leftItems = remember(matchPool) { matchPool.map { it.display }.shuffled() }
    val rightItems = remember(matchPool) { matchPool.map { it.visualEmoji.ifEmpty { it.subtitle } }.shuffled() }

    // Track layout coordinates for drawing lines between items dynamically
    var leftCoords by remember { mutableStateOf(emptyMap<String, LayoutCoordinates>()) }
    var rightCoords by remember { mutableStateOf(emptyMap<String, LayoutCoordinates>()) }
    var parentCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }

    // Drag-to-match state
    var dragStartItem by remember { mutableStateOf<String?>(null) }
    var dragCurrentPosition by remember { mutableStateOf<Offset?>(null) }

    fun checkMatch(left: String, right: String) {
        val originalItem = matchPool.firstOrNull { it.display == left }
        val associatedRight = originalItem?.visualEmoji?.ifEmpty { originalItem.subtitle } ?: ""

        if (associatedRight == right) {
            // Correct Pair!
            viewModel.playCorrectSound()
            viewModel.earnMiniGameStar()
            matchedLeft = matchedLeft + left
            matchedRight = matchedRight + right
            selectedLeft = null
            selectedRight = null

            if (matchedLeft.size == matchPool.size) {
                successRound = true
            }
        } else {
            // Wrong try
            viewModel.playWrongSound()
            selectedLeft = null
            selectedRight = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = GameLocalizer.translate("match_instruction", langCode),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (successRound) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⭐🎉🏆🎉⭐", fontSize = 36.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = GameLocalizer.translate("match_success", langCode),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        successRound = false
                        matchedLeft = emptySet()
                        matchedRight = emptySet()
                        selectedLeft = null
                        selectedRight = null
                        leftCoords = emptyMap()
                        rightCoords = emptyMap()
                        viewModel.playClickSound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = GameLocalizer.translate("play_next_round", langCode), fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Box container coordinates tracking
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        parentCoords = coords
                    }
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary

                // Background Canvas for drawing dynamic connections
                Canvas(modifier = Modifier.matchParentSize()) {
                    val pCoords = parentCoords ?: return@Canvas

                    // 1. Draw completed matches
                    matchedLeft.forEach { leftVal ->
                        val originalItem = matchPool.firstOrNull { it.display == leftVal }
                        val rightVal = originalItem?.visualEmoji?.ifEmpty { originalItem.subtitle } ?: ""

                        val lCoords = leftCoords[leftVal]
                        val rCoords = rightCoords[rightVal]

                        if (lCoords != null && rCoords != null && lCoords.isAttached && rCoords.isAttached && pCoords.isAttached) {
                            val start = pCoords.localPositionOf(lCoords, Offset(lCoords.size.width.toFloat(), lCoords.size.height.toFloat() / 2f))
                            val end = pCoords.localPositionOf(rCoords, Offset(0f, rCoords.size.height.toFloat() / 2f))

                            drawLine(
                                color = Color(0xFF4CAF50),
                                start = start,
                                end = end,
                                strokeWidth = 6.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            )
                            drawCircle(color = Color(0xFF2E7D32), radius = 6.dp.toPx(), center = start)
                            drawCircle(color = Color(0xFF2E7D32), radius = 6.dp.toPx(), center = end)
                        }
                    }

                    // 2. Draw active dragging line
                    val activeLeft = dragStartItem ?: selectedLeft
                    val lCoords = activeLeft?.let { leftCoords[it] }
                    val currentDragPos = dragCurrentPosition ?: selectedRight?.let { rightVal ->
                        val rCoords = rightCoords[rightVal]
                        if (rCoords != null && rCoords.isAttached && pCoords.isAttached) {
                            pCoords.localPositionOf(rCoords, Offset(0f, rCoords.size.height.toFloat() / 2f))
                        } else null
                    }

                    if (activeLeft != null && lCoords != null && lCoords.isAttached && pCoords.isAttached && currentDragPos != null) {
                        val start = pCoords.localPositionOf(lCoords, Offset(lCoords.size.width.toFloat(), lCoords.size.height.toFloat() / 2f))
                        drawLine(
                            color = primaryColor,
                            start = start,
                            end = currentDragPos,
                            strokeWidth = 4.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                        drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = start)
                        drawCircle(color = primaryColor, radius = 5.dp.toPx(), center = currentDragPos)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column (Displays/Letters)
                    Column(
                        modifier = Modifier.weight(1.0f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        leftItems.forEach { itemText ->
                            val isMatched = matchedLeft.contains(itemText)
                            val isSelected = selectedLeft == itemText

                            val cardBg = when {
                                isMatched -> Color(0xFFC8E6C9) // Green match
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val strokeColor = when {
                                isMatched -> Color(0xFF4CAF50)
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .onGloballyPositioned { coords ->
                                        leftCoords = leftCoords + (itemText to coords)
                                    }
                                    .pointerInput(itemText, isMatched) {
                                        if (isMatched) return@pointerInput
                                        detectDragGestures(
                                            onDragStart = { offset ->
                                                dragStartItem = itemText
                                                selectedLeft = itemText
                                                viewModel.playClickSound()
                                                val lC = leftCoords[itemText]
                                                if (lC != null && parentCoords != null) {
                                                    dragCurrentPosition = parentCoords!!.localPositionOf(lC, offset)
                                                }
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                val lC = leftCoords[itemText]
                                                if (lC != null && parentCoords != null) {
                                                    dragCurrentPosition = parentCoords!!.localPositionOf(lC, change.position)

                                                    // Detect hover over any right item card
                                                    var hoverRightVal: String? = null
                                                    for ((rightVal, rCoords) in rightCoords) {
                                                        if (rCoords.isAttached && parentCoords!!.isAttached) {
                                                            val rCenter = parentCoords!!.localPositionOf(
                                                                rCoords,
                                                                Offset(rCoords.size.width / 2f, rCoords.size.height / 2f)
                                                            )
                                                            val distance = (dragCurrentPosition!! - rCenter).getDistance()
                                                            if (distance < 70.dp.toPx()) {
                                                                hoverRightVal = rightVal
                                                                break
                                                            }
                                                        }
                                                    }
                                                    selectedRight = hoverRightVal
                                                }
                                            },
                                            onDragEnd = {
                                                if (selectedRight != null) {
                                                    checkMatch(itemText, selectedRight!!)
                                                } else {
                                                    selectedLeft = null
                                                    selectedRight = null
                                                }
                                                dragStartItem = null
                                                dragCurrentPosition = null
                                            },
                                            onDragCancel = {
                                                dragStartItem = null
                                                dragCurrentPosition = null
                                                selectedLeft = null
                                                selectedRight = null
                                            }
                                        )
                                    }
                                    .clickable(enabled = !isMatched) {
                                        viewModel.playClickSound()
                                        selectedLeft = itemText
                                        if (selectedRight != null) {
                                            checkMatch(itemText, selectedRight!!)
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(2.dp, strokeColor),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = itemText,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isMatched) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    // Right Column (Pictures/Emojis)
                    Column(
                        modifier = Modifier.weight(1.0f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rightItems.forEach { pictureText ->
                            val isMatched = matchedRight.contains(pictureText)
                            val isSelected = selectedRight == pictureText

                            val cardBg = when {
                                isMatched -> Color(0xFFC8E6C9)
                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }

                            val strokeColor = when {
                                isMatched -> Color(0xFF4CAF50)
                                isSelected -> MaterialTheme.colorScheme.primary
                                else -> Color.Transparent
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(65.dp)
                                    .onGloballyPositioned { coords ->
                                        rightCoords = rightCoords + (pictureText to coords)
                                    }
                                    .clickable(enabled = !isMatched) {
                                        viewModel.playClickSound()
                                        selectedRight = pictureText
                                        if (selectedLeft != null) {
                                            checkMatch(selectedLeft!!, pictureText)
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = cardBg),
                                border = BorderStroke(2.dp, strokeColor),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = pictureText,
                                        fontSize = if (pictureText.length == 1) 32.sp else 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isMatched) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
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

// ---------------- GAME 3: MISSING LETTER ----------------

@Composable
fun MissingLetterGame(
    viewModel: LearningViewModel,
    allItems: List<LearningItem>,
    langCode: String
) {
    var quizItem by remember { mutableStateOf<LearningItem?>(null) }
    var displayWordWithBlank by remember { mutableStateOf("") }
    var correctChar by remember { mutableStateOf("") }
    var choices by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedChoice by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

    fun setupNewQuestion() {
        if (allItems.isEmpty()) return

        // Filter items whose native display has at least 2 grapheme clusters
        val pool = allItems.filter { getGraphemes(it.display).size >= 2 }
        val target = if (pool.isNotEmpty()) pool.random() else allItems.random()
        quizItem = target

        val graphemes = getGraphemes(target.display)
        if (graphemes.isEmpty()) return

        val blankIndex = if (graphemes.size == 1) 0 else Random.nextInt(0, graphemes.size)
        val charToBlank = graphemes[blankIndex]
        correctChar = charToBlank

        // Build blank word representation using native script
        val sb = StringBuilder()
        for (i in graphemes.indices) {
            if (i == blankIndex) sb.append(" _ ") else sb.append(graphemes[i])
        }
        displayWordWithBlank = sb.toString()

        // Generate choices in native script
        val allNativeGraphemes = allItems
            .flatMap { getGraphemes(it.display) }
            .filter { it != correctChar && it.isNotBlank() }
            .distinct()

        val decoys = if (allNativeGraphemes.size >= 2) {
            allNativeGraphemes.shuffled().take(2)
        } else {
            listOf("అ", "ఆ", "ఇ").filter { it != correctChar }
        }

        choices = (decoys + correctChar).shuffled()
        selectedChoice = null
        isAnswerCorrect = null
    }

    LaunchedEffect(allItems, langCode) {
        setupNewQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = GameLocalizer.translate("missing_instruction", langCode),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large picture indicator
                Text(
                    text = quizItem?.visualEmoji?.ifEmpty { "📚" } ?: "📚",
                    fontSize = 56.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Transliteration / English subtitle hint
                if (!quizItem?.subtitle.isNullOrEmpty()) {
                    Text(
                        text = quizItem?.subtitle ?: "",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Native Word display with blank space
                Text(
                    text = displayWordWithBlank,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selection Choices Buttons in native script
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            choices.forEach { choice ->
                val isSelected = selectedChoice == choice
                val buttonColor = when {
                    isSelected && isAnswerCorrect == true -> Color(0xFFC8E6C9) // Green success
                    isSelected && isAnswerCorrect == false -> Color(0xFFFFCDD2) // Red wrong
                    else -> MaterialTheme.colorScheme.primaryContainer
                }

                val contentColor = when {
                    isSelected && isAnswerCorrect == true -> Color(0xFF2E7D32)
                    isSelected && isAnswerCorrect == false -> Color(0xFFC62828)
                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                }

                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(buttonColor)
                        .clickable(enabled = isAnswerCorrect != true) {
                            selectedChoice = choice
                            val correct = choice == correctChar
                            if (correct) {
                                isAnswerCorrect = true
                                viewModel.playCorrectSound()
                                viewModel.earnMiniGameStar()
                                // fill the blank dynamically with full native word
                                displayWordWithBlank = quizItem?.display ?: ""
                            } else {
                                isAnswerCorrect = false
                                viewModel.playWrongSound()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = choice,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Feedback / Next Round trigger
        if (isAnswerCorrect == true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = GameLocalizer.translate("perfect_star", langCode),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Button(
                    onClick = { setupNewQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = GameLocalizer.translate("next_word", langCode), fontWeight = FontWeight.Bold)
                }
            }
        } else if (isAnswerCorrect == false) {
            Text(
                text = GameLocalizer.translate("try_again", langCode),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
        }
    }
}

@Composable
fun ListeningModeGame(
    viewModel: LearningViewModel,
    allItems: List<LearningItem>,
    langCode: String
) {
    var targetItem by remember { mutableStateOf<LearningItem?>(null) }
    var choices by remember { mutableStateOf<List<LearningItem>>(emptyList()) }
    var selectedItem by remember { mutableStateOf<LearningItem?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

    fun setupNewQuestion() {
        if (allItems.isEmpty()) return
        val filtered = allItems.filter { it.visualEmoji.isNotEmpty() }
        val target = if (filtered.isNotEmpty()) filtered.random() else allItems.random()
        targetItem = target
        selectedItem = null
        isAnswerCorrect = null

        val decoys = allItems.filter { it.display != target.display }.shuffled().take(3)
        choices = (decoys + target).shuffled()

        viewModel.speakCustomText(target.voiceText)
    }

    LaunchedEffect(allItems) {
        setupNewQuestion()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = GameLocalizer.translate("listening_title", langCode),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                targetItem?.let { viewModel.speakCustomText(it.voiceText) }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.size(140.dp, 60.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🔊", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = GameLocalizer.translate("replay_sound", langCode),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = GameLocalizer.translate("tap_heard_card", langCode),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val chunked = choices.chunked(2)
            chunked.forEach { rowChoices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowChoices.forEach { choice ->
                        val isSelected = selectedItem == choice
                        val isCorrect = choice == targetItem
                        val cardBg = when {
                            isSelected && isCorrect -> Color(0xFFE8F5E9)
                            isSelected && !isCorrect -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                        val borderColor = when {
                            isSelected && isCorrect -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> Color(0xFFE53935)
                            else -> Color.Transparent
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .clickable(enabled = isAnswerCorrect != true) {
                                    selectedItem = choice
                                    if (isCorrect) {
                                        isAnswerCorrect = true
                                        viewModel.earnMiniGameStar()
                                    } else {
                                        isAnswerCorrect = false
                                        viewModel.recordMistake(targetItem?.display ?: choice.display)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = BorderStroke(2.dp, borderColor)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (isAnswerCorrect == true || isSelected) {
                                    Text(choice.visualEmoji, fontSize = 28.sp)
                                    Text(
                                        choice.display,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(choice.visualEmoji, fontSize = 28.sp)
                                    Text(
                                        "???",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isAnswerCorrect == true) {
            Text(
                text = GameLocalizer.translate("correct_text", langCode),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { setupNewQuestion() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = GameLocalizer.translate("next_sound", langCode),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else if (isAnswerCorrect == false) {
            Text(
                text = GameLocalizer.translate("try_again", langCode),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
        }
    }
}

@Composable
fun MemoryModeGame(
    viewModel: LearningViewModel,
    allItems: List<LearningItem>,
    langCode: String
) {
    var cards by remember { mutableStateOf<List<LearningItem>>(emptyList()) }
    var targetCard by remember { mutableStateOf<LearningItem?>(null) }
    var countdown by remember { mutableStateOf(5) }
    var isFlipped by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isCorrectAnswer by remember { mutableStateOf<Boolean?>(null) }
    var triggerRestart by remember { mutableStateOf(0) }

    fun setupNewGame() {
        if (allItems.size < 4) return
        val chosen = allItems.shuffled().distinctBy { it.display }.take(4)
        cards = chosen
        targetCard = chosen.random()
        countdown = 5
        isFlipped = false
        selectedIndex = null
        isCorrectAnswer = null
    }

    LaunchedEffect(allItems, triggerRestart) {
        setupNewGame()
    }

    LaunchedEffect(countdown, isFlipped, cards) {
        if (cards.isNotEmpty() && !isFlipped && countdown > 0) {
            delay(1000)
            countdown--
            if (countdown == 0) {
                isFlipped = true
                viewModel.speakCustomText(
                    GameLocalizer.translate("cards_hidden_speak", langCode, target = targetCard?.display ?: "")
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = GameLocalizer.translate("memory_title", langCode),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (!isFlipped) {
            Text(
                text = GameLocalizer.translate("memorize_cards", langCode, countdown = countdown),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF57F17)
            )
            LinearProgressIndicator(
                progress = countdown.toFloat() / 5f,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 6.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = Color(0xFFF57F17)
            )
        } else {
            Text(
                text = GameLocalizer.translate("find_card", langCode, target = targetCard?.display ?: ""),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val chunked = cards.chunked(2)
            chunked.forEachIndexed { rowIndex, rowCards ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowCards.forEachIndexed { colIndex, card ->
                        val index = rowIndex * 2 + colIndex
                        val isSelected = selectedIndex == index
                        val isCorrect = card == targetCard
                        
                        val bg = when {
                            isSelected && isCorrect -> Color(0xFFE8F5E9)
                            isSelected && !isCorrect -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clickable(enabled = isFlipped && isCorrectAnswer != true) {
                                    selectedIndex = index
                                    if (isCorrect) {
                                        isCorrectAnswer = true
                                        viewModel.earnMiniGameStar()
                                    } else {
                                        isCorrectAnswer = false
                                        viewModel.recordMistake(targetCard?.display ?: card.display)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = bg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!isFlipped || isCorrectAnswer == true || isSelected) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(card.visualEmoji, fontSize = 36.sp)
                                        Text(
                                            card.display,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    Text("❓", fontSize = 42.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isCorrectAnswer == true) {
            Text(
                text = GameLocalizer.translate("perfect_memory", langCode),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { triggerRestart++ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = GameLocalizer.translate("play_next_round", langCode),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else if (isCorrectAnswer == false) {
            Text(
                text = GameLocalizer.translate("try_again", langCode),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
        }
    }
}

object GameLocalizer {
    fun translate(key: String, langCode: String, target: String = "", countdown: Int = 5): String {
        return when (langCode) {
            "te" -> when (key) {
                "playground_title" -> "🧩 ఆటల తోట (Playground)"
                "playground_subtitle" -> "ఆడుతూ పాడుతూ నేర్చుకుందాం! 🥳"
                "game_balloon" -> "🎈 బెలూన్ పాప్"
                "game_match" -> "🤝 జతపరుచు"
                "game_missing" -> "🔍 ఖాళీలు పూరించు"
                "game_listening" -> "👂 శబ్ద గ్రహణ"
                "game_memory" -> "🧠 జ్ఞాపకశక్తి"
                "balloon_instruction" -> "కింది వాటిలో దీనిని పగలగొట్టు: 👇"
                "match_instruction" -> "అక్షరాన్ని/పదాన్ని సరైన బొమ్మతో జతపరుచు! 🤝"
                "match_success" -> "అద్భుతం! అన్నింటినీ జత చేసావు! 🥳"
                "play_next_round" -> "మరోసారి ఆడు 🔁"
                "missing_instruction" -> "సరైన అక్షరంతో పదాన్ని పూర్తి చేయి! 🔍"
                "perfect_star" -> "శాభాష్! +1 ⭐"
                "next_word" -> "తదుపరి పదం ➡️"
                "try_again" -> "అయ్యో! మళ్లీ ప్రయత్నించు బంగారం! 💪"
                "listening_title" -> "👂 శబ్ద గ్రహణ ఆట"
                "replay_sound" -> "మళ్లీ విను"
                "tap_heard_card" -> "nuvvu విన్న పదాన్ని ఎంచుకో:"
                "next_sound" -> "తదుపరి శబ్దం ➡️"
                "correct_text" -> "శాభాష్! కరెక్ట్! 🌟"
                "memory_title" -> "🧠 జ్ఞాపకశక్తి ఆట"
                "memorize_cards" -> "కార్డులను గుర్తుపెట్టుకో! సమయం: $countdown సెకన్లు"
                "find_card" -> "$target ఎక్కడ ఉందో కనుక్కో!"
                "cards_hidden_speak" -> "కార్డులు దాచబడ్డాయి! ఇప్పుడు చెప్పు: $target ఎక్కడ ఉంది?"
                "perfect_memory" -> "శాభాష్! నువ్వు సరిగ్గా గుర్తుపట్టావు! 🎉"
                else -> ""
            }
            "ta" -> when (key) {
                "playground_title" -> "🧩 விளையாட்டு மைதானம்"
                "playground_subtitle" -> "விளையாடி நட்சத்திரங்களை வெல்லுங்கள்! 🥳"
                "game_balloon" -> "🎈 பலூன் பாப்"
                "game_match" -> "🤝 பொருத்துக"
                "game_missing" -> "🔍 விடுபட்ட எழுத்து"
                "game_listening" -> "👂 கேட்டு அறிதல்"
                "game_memory" -> "🧠 நினைவாற்றல்"
                "balloon_instruction" -> "இதைக் கொண்ட பலூனை உடைக்கவும்: 👇"
                "match_instruction" -> "எழுத்து/வார்த்தையை சரியான படத்துடன் பொருத்தவும்! 🤝"
                "match_success" -> "அற்புதமான பொருத்தம்! புதிரை முடித்துவிட்டீர்கள்! 🥳"
                "play_next_round" -> "அடுத்த சுற்று 🔁"
                "missing_instruction" -> "விடுபட்ட எழுத்தைக் கண்டுபிடித்து வார்த்தையை முடிக்கவும்! 🔍"
                "perfect_star" -> "அற்புதம்! +1 ⭐"
                "next_word" -> "அடுத்த வார்த்தை ➡️"
                "try_again" -> "மன்னிக்கவும்! மீண்டும் முயற்சிக்கவும்! 💪"
                "listening_title" -> "👂 கேட்டு அறிதல் ஆட்டம்"
                "replay_sound" -> "மீண்டும் கேள்"
                "tap_heard_card" -> "நீங்கள் கேட்ட கார்டைத் தட்டவும்:"
                "next_sound" -> "அடுத்த ஒலி ➡️"
                "correct_text" -> "அற்புதம்! நீங்கள் சரியாக கண்டுபிடித்தீர்கள்! 🌟"
                "memory_title" -> "🧠 நினைவாற்றல் விளையாட்டு"
                "memorize_cards" -> "கார்டுகளை நினைவில் கொள்ளுங்கள்! மீதமுள்ள நேரம்: $countdown வினாடிகள்"
                "find_card" -> "$target ஐக் கண்டறியவும்!"
                "cards_hidden_speak" -> "கார்டுகள் மறைக்கப்பட்டுள்ளன! இப்போது சொல்லுங்கள், $target எங்கே உள்ளது?"
                "perfect_memory" -> "அற்புதமான நினைவாற்றல்! சரியாக கண்டுபிடித்தீர்கள்! 🎉"
                else -> ""
            }
            "hi" -> when (key) {
                "playground_title" -> "🧩 मिनी गेम्स प्लेग्राउंड!"
                "playground_subtitle" -> "मज़ेदार खेल खेलें और सुनहरे सितारे जीतें! 🥳"
                "game_balloon" -> "🎈 गुब्बारा फोड़ें"
                "game_match" -> "🤝 मिलान करें"
                "game_missing" -> "🔍 गायब अक्षर"
                "game_listening" -> "👂 सुनकर पहचानें"
                "game_memory" -> "🧠 याददाश्त खेल"
                "balloon_instruction" -> "इस गुब्बारे को फोड़ें: 👇"
                "match_instruction" -> "अक्षर/शब्द का सही चित्र से मिलान करें! 🤝"
                "match_success" -> "अद्भुत मिलान! आपने पहेली पूरी कर ली! 🥳"
                "play_next_round" -> "अगला राउंड खेलें 🔁"
                "missing_instruction" -> "गायब अक्षर ढूंढकर शब्द पूरा करें! 🔍"
                "perfect_star" -> "बढ़िया! +1 ⭐"
                "next_word" -> "अगला शब्द ➡️"
                "try_again" -> "ओह! फिर से कोशिश करें! 💪"
                "listening_title" -> "👂 सुनने का खेल"
                "replay_sound" -> "फिर से सुनें"
                "tap_heard_card" -> "सुने गए शब्द पर टैप करें:"
                "next_sound" -> "अगली आवाज़ ➡️"
                "correct_text" -> "शानदार! आपने सही चुना! 🌟"
                "memory_title" -> "🧠 याददाश्त का खेल"
                "memorize_cards" -> "कार्ड याद रखें! शेष समय: $countdown सेकंड"
                "find_card" -> "$target ढूंढें!"
                "cards_hidden_speak" -> "कार्ड छिप गए हैं! अब मुझे बताओ, $target कहाँ है?"
                "perfect_memory" -> "शानदार याददाश्त! आपने सही ढूंढ लिया! 🎉"
                else -> ""
            }
            "ar" -> when (key) {
                "playground_title" -> "🧩 ساحة الألعاب المصغرة!"
                "playground_subtitle" -> "العب ألعابًا ممتعة واكسب نجومًا ذهبية! 🥳"
                "game_balloon" -> "🎈 فرقعة البالونات"
                "game_match" -> "🤝 مطابقة العناصر"
                "game_missing" -> "🔍 الحرف المفقود"
                "game_listening" -> "👂 وضع الاستماع"
                "game_memory" -> "🧠 وضع الذاكرة"
                "balloon_instruction" -> "فرقع البالون الذي يحتوي على: 👇"
                "match_instruction" -> "طابق الكلمة/الحرف بالصورة الصحيحة! 🤝"
                "match_success" -> "مطابقة رائعة! لقد أكملت اللغز! 🥳"
                "play_next_round" -> "العب الجولة التالية 🔁"
                "missing_instruction" -> "أكمل الكلمة بإيجاد الحرف المفقود! 🔍"
                "perfect_star" -> "ممتاز! +1 ⭐"
                "next_word" -> "الكلمة التالية ➡️"
                "try_again" -> "عذرًا! حاول مرة أخرى! 💪"
                "listening_title" -> "👂 لعبة الاستماع"
                "replay_sound" -> "إعادة التشغيل"
                "tap_heard_card" -> "اضغط على البطاقة التي سمعتها للتو:"
                "next_sound" -> "الصوت التالي ➡️"
                "correct_text" -> "ممتاز! إجابة صحيحة! 🌟"
                "memory_title" -> "🧠 لعبة الذاكرة"
                "memorize_cards" -> "تذكر البطاقات! الوقت المتبقي: $countdown ثوانٍ"
                "find_card" -> "ابحث عن: $target!"
                "cards_hidden_speak" -> "البطاقات مخفية! الآن أخبرني، أين هي $target؟"
                "perfect_memory" -> "ذاكرة رائعة! لقد وجدتها! 🎉"
                else -> ""
            }
            "kn" -> when (key) {
                "playground_title" -> "🧩 ಮಿನಿ ಗೇಮ್ಸ್ ಮೈದಾನ!"
                "playground_subtitle" -> "ಮೋಜಿನ ಆಟಗಳನ್ನು ಆಡಿ ಮತ್ತು ಚಿನ್ನದ ನಕ್ಷತ್ರಗಳನ್ನು ಗೆಲ್ಲಿರಿ! 🥳"
                "game_balloon" -> "🎈 ಬಲೂನ್ ಪಾಪ್"
                "game_match" -> "🤝 ಜೋಡಿಸು"
                "game_missing" -> "🔍 ಬಿಟ್ಟ ಸ್ಥಳ ತುಂಬು"
                "game_listening" -> "👂 ಆಲಿಸುವ ಮೋಡ್"
                "game_memory" -> "🧠 ನೆನಪಿನ ಶಕ್ತಿ ಆಟ"
                "balloon_instruction" -> "ಇದನ್ನು ಹೊಂದಿರುವ ಬಲೂನ್ ಅನ್ನು ಒಡೆಯಿರಿ: 👇"
                "match_instruction" -> "ಅಕ್ಷರ/ಪದವನ್ನು ಸರಿಯಾದ ಚಿತ್ರಕ್ಕೆ ಜೋಡಿಸಿ! 🤝"
                "match_success" -> "ಅದ್ಭುತ ಜೋಡಣೆ! ನೀವು ಒಗಟನ್ನು ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ! 🥳"
                "play_next_round" -> "ಮುಂದಿನ ಸುತ್ತು ಆಡಿ 🔁"
                "missing_instruction" -> "ಬಿಟ್ಟ ಅಕ್ಷರವನ್ನು ಪತ್ತೆಹಚ್ಚಿ ಪದವನ್ನು ಪೂರ್ಣಗೊಳಿಸಿ! 🔍"
                "perfect_star" -> "ಶಬಾಶ್! +1 ⭐"
                "next_word" -> "ಮುಂದಿನ ಪದ ➡️"
                "try_again" -> "ಅಯ್ಯೋ! ಮತ್ತೊಮ್ಮೆ ಪ್ರಯತ್ನಿಸಿ! 💪"
                "listening_title" -> "👂 ಆಲಿಸುವ ಆಟ"
                "replay_sound" -> "ಮತ್ತೊಮ್ಮೆ ಕೇಳಿ"
                "tap_heard_card" -> "ನೀವು ಕೇಳಿದ ಕಾರ್ಡ್ ಅನ್ನು ಸ್ಪರ್ಶಿಸಿ:"
                "next_sound" -> "ಮುಂದಿನ ಧ್ವನಿ ➡️"
                "correct_text" -> "ಅದ್ಭುತ! ಸರಿಯಾಗಿದೆ! 🌟"
                "memory_title" -> "🧠 ನೆನಪಿನ ಶಕ್ತಿಯ ಆಟ"
                "memorize_cards" -> "ಕಾರ್ಡ್‌ಗಳನ್ನು ನೆನಪಿಟ್ಟುಕೊಳ್ಳಿ! ಉಳಿದಿರುವ ಸಮಯ: $countdown ಸೆಕೆಂಡುಗಳು"
                "find_card" -> "$target ಅನ್ನು ಹುಡುಕಿ!"
                "cards_hidden_speak" -> "ಕಾರ್ಡ್‌ಗಳು ಮರೆಯಾಗಿವೆ! ಈಗ ಹೇಳಿ, $target ಎಲ್ಲಿದೆ?"
                "perfect_memory" -> "ಅದ್ಭುತ ನೆನಪಿನ ಶಕ್ತಿ! ಸರಿಯಾಗಿ ಪತ್ತೆಹಚ್ಚಿದ್ದೀರಿ! 🎉"
                else -> ""
            }
            "ml" -> when (key) {
                "playground_title" -> "🧩 മിനി ഗെയിംസ് കളിസ്ഥലം!"
                "playground_subtitle" -> "രസകരമായ ഗെയിമുകൾ കളിച്ച് സ്വർണ്ണ നക്ഷത്രങ്ങൾ നേടൂ! 🥳"
                "game_balloon" -> "🎈 ബലൂൺ പോപ്പ്"
                "game_match" -> "🤝 ചേരുംപടി ചേർക്കുക"
                "game_missing" -> "🔍 വിട്ടുപോയ അക്ഷരം"
                "game_listening" -> "👂 കേൾവി മോഡ്"
                "game_memory" -> "🧠 മെമ്മറി മോഡ്"
                "balloon_instruction" -> "ഈ ബലൂൺ പൊട്ടിക്കുക: 👇"
                "match_instruction" -> "അക്ഷരത്തെ/വാക്കിനെ ശരിയായ ചിത്രവുമായി പൊരുത്തപ്പെടുത്തുക! 🤝"
                "match_success" -> "മികച്ച പൊരുത്തപ്പെടുത്തൽ! നിങ്ങൾ പസിൽ പൂർത്തിയാക്കി! 🥳"
                "play_next_round" -> "അടുത്ത റൗണ്ട് കളിക്കുക 🔁"
                "missing_instruction" -> "വിട്ടുപോയ അക്ഷരം കണ്ടെത്തി വാക്ക് പൂർത്തിയാക്കുക! 🔍"
                "perfect_star" -> "ശരിയാണ്! +1 ⭐"
                "next_word" -> "അടുത്ത വാക്ക് ➡️"
                "try_again" -> "ഹാജരാകൂ! വീണ്ടും ശ്രമിക്കൂ! 💪"
                "listening_title" -> "👂 കേൾവി കളി"
                "replay_sound" -> "വീണ്ടും കേൾക്കുക"
                "tap_heard_card" -> "നിങ്ങൾ ഇപ്പോൾ കേട്ട കാർഡിൽ തൊടുക:"
                "next_sound" -> "അടുത്ത ശബ്ദം ➡️"
                "correct_text" -> "ശരിയാണ്! മികച്ച കണ്ടെത്തൽ! 🌟"
                "memory_title" -> "🧠 മെമ്മറി ഗെയിം"
                "memorize_cards" -> "കാർഡുകൾ ഓർമ്മിക്കുക! ബാക്കിയുള്ള സമയം: $countdown സെക്കൻഡ്"
                "find_card" -> "$target കണ്ടെത്തുക!"
                "cards_hidden_speak" -> "കാർഡുകൾ മറച്ചിരിക്കുന്നു! ഇപ്പോൾ പറയൂ, $target എവിടെയാണ്?"
                "perfect_memory" -> "മികച്ച ഓർമ്മശക്തി! ശരിയായി കണ്ടെത്തി! 🎉"
                else -> ""
            }
            "bn" -> when (key) {
                "playground_title" -> "🧩 মিনি গেম খেলার মাঠ!"
                "playground_subtitle" -> "মজার গেম খেলুন এবং সোনার তারা অর্জন করুন! 🥳"
                "game_balloon" -> "🎈 বেলুন পপ"
                "game_match" -> "🤝 মিল করুন"
                "game_missing" -> "🔍 হারিয়ে যাওয়া অক্ষর"
                "game_listening" -> "👂 শোনার মোড"
                "game_memory" -> "🧠 স্মৃতিশক্তির খেলা"
                "balloon_instruction" -> "এই বেলুনটি ফাটান: 👇"
                "match_instruction" -> "অক্ষর/শব্দটি সঠিক ছবির সাথে মিলান! 🤝"
                "match_success" -> "অসাধারণ মিল! আপনি ধাঁধাটি সম্পূর্ণ করেছেন! 🥳"
                "play_next_round" -> "পরের রাউন্ড খেলুন 🔁"
                "missing_instruction" -> "হারিয়ে যাওয়া অক্ষরটি খুঁজে শব্দটি সম্পূর্ণ করুন! 🔍"
                "perfect_star" -> "চমৎকার! +1 ⭐"
                "next_word" -> "পরের শব্দ ➡️"
                "try_again" -> "ওহ! আবার চেষ্টা করুন! 💪"
                "listening_title" -> "👂 শোনার খেলা"
                "replay_sound" -> "আবার শুনুন"
                "tap_heard_card" -> "আপনি এইমাত্র যে কার্ডটি শুনেছেন তাতে আলতো চাপুন:"
                "next_sound" -> "পরের উচ্চারণ ➡️"
                "correct_text" -> "সঠিক হয়েছে! চমৎকার! 🌟"
                "memory_title" -> "🧠 স্মৃতিশক্তির খেলা"
                "memorize_cards" -> "কার্ড মনে রাখুন! অবশিষ্ট সময়: $countdown সেকেন্ড"
                "find_card" -> "$target খুঁজুন!"
                "cards_hidden_speak" -> "কার্ডগুলি লুকানো আছে! এবার বলো, $target কোথায়?"
                "perfect_memory" -> "চমৎকার স্মৃতিশক্তি! সঠিক কার্ডটি পেয়েছ! 🎉"
                else -> ""
            }
            "mr" -> when (key) {
                "playground_title" -> "🧩 मिनी गेम्स प्लेग्राउंड!"
                "playground_subtitle" -> "मजेदार खेळ खेळा आणि सुवर्ण तारे जिंका! 🥳"
                "game_balloon" -> "🎈 फुगा फोडा"
                "game_match" -> "🤝 जुळवाजुळव"
                "game_missing" -> "🔍 गहाळ अक्षर"
                "game_listening" -> "👂 ऐकण्याचा मोड"
                "game_memory" -> "🧠 स्मरणशक्ती खेळ"
                "balloon_instruction" -> "या अक्षराचा फुगा फोडा: 👇"
                "match_instruction" -> "अक्षर/शब्दाला योग्य चित्राशी जुळवा! 🤝"
                "match_success" -> "अद्भुत जुळवणी! तुम्ही कोडे पूर्ण केले! 🥳"
                "play_next_round" -> "पुढील फेरी खेळा 🔁"
                "missing_instruction" -> "गहाळ अक्षर शोधून शब्द पूर्ण करा! 🔍"
                "perfect_star" -> "उत्कृष्ट! +1 ⭐"
                "next_word" -> "पुढील शब्द ➡️"
                "try_again" -> "अरेरे! पुन्हा प्रयत्न करा! 💪"
                "listening_title" -> "👂 ऐकण्याचा खेळ"
                "replay_sound" -> "पुन्हा ऐका"
                "tap_heard_card" -> "तुम्ही आता ऐकलेल्या कार्डवर टॅप करा:"
                "next_sound" -> "पुढील आवाज ➡️"
                "correct_text" -> "बरोबर! खूप छान! 🌟"
                "memory_title" -> "🧠 स्मरणशक्तीचा खेळ"
                "memorize_cards" -> "कार्ड लक्षात ठेवा! शिल्लक वेळ: $countdown सेकंद"
                "find_card" -> "$target शोधा!"
                "cards_hidden_speak" -> "कार्ड लपवले आहेत! आता मला सांगा, $target कुठे आहे?"
                "perfect_memory" -> "उत्कृष्ट स्मरणशक्ती! तुम्ही शोधून काढले! 🎉"
                else -> ""
            }
            "gu" -> when (key) {
                "playground_title" -> "🧩 મિની ગેમ્સ પ્લેગ્રાઉન્ડ!"
                "playground_subtitle" -> "મનોરંજક રમતો રમો અને સોનાના તારા મેળવો! 🥳"
                "game_balloon" -> "🎈 ફુગ્ગો પોપ"
                "game_match" -> "🤝 જોડો"
                "game_missing" -> "🔍 ખૂટતો અક્ષર"
                "game_listening" -> "👂 સાંભળવાનો મોડ"
                "game_memory" -> "🧠 યાદશક્તિ રમત"
                "balloon_instruction" -> "આ ફુગ્ગો ફોડો: 👇"
                "match_instruction" -> "અક્ષર/શબ્દને સાચા ચિત્ર સાથે જોડો! 🤝"
                "match_success" -> "અદ્ભુત જોડાણ! તમે કોયડો પૂરો કર્યો! 🥳"
                "play_next_round" -> "આગળનો રાઉન્ડ રમો 🔁"
                "missing_instruction" -> "ખૂટતો અક્ષર શોધીને શબ્દ પૂરો કરો! 🔍"
                "perfect_star" -> "સરસ! +1 ⭐"
                "next_word" -> "આગળનો શબ્દ ➡️"
                "try_again" -> "અરેરે! ફરીથી પ્રયત્ન કરો! 💪"
                "listening_title" -> "👂 સાંભળવાની રમત"
                "replay_sound" -> "ફરીથી સાંભળો"
                "tap_heard_card" -> "તમે હમણાં સાંભળેલા કાર્ડ પર ટેપ કરો:"
                "next_sound" -> "આગળનો અવાજ ➡️"
                "correct_text" -> "સાચું! ખૂબ સરસ! 🌟"
                "memory_title" -> "🧠 યાદશક્તિની રમત"
                "memorize_cards" -> "કાર્ડ યાદ રાખો! બાકી સમય: $countdown સેકન્ડ"
                "find_card" -> "$target શોધો!"
                "cards_hidden_speak" -> "કાર્ડ છુપાયેલા છે! હવે મને કહો, $target ક્યાં છે?"
                "perfect_memory" -> "અદ્ભुત યાદશક્તિ! સાચું ઓળખી લીધું! 🎉"
                else -> ""
            }
            else -> when (key) {
                "playground_title" -> "🧩 Mini Games Playground!"
                "playground_subtitle" -> "Play fun games and earn golden stars!"
                "game_balloon" -> "🎈 Balloon Pop"
                "game_match" -> "🤝 Match Items"
                "game_missing" -> "🔍 Missing Letter"
                "game_listening" -> "👂 Listening Mode"
                "game_memory" -> "🧠 Memory Mode"
                "balloon_instruction" -> "POP THE BALLOON WITH: 👇"
                "match_instruction" -> "Match the Word/Letter to the Correct Picture! 🤝"
                "match_success" -> "Amazing matching! You completed the puzzle!"
                "play_next_round" -> "Play Next Round 🔁"
                "missing_instruction" -> "Complete the word by finding the missing letter! 🔍"
                "perfect_star" -> "Perfect! +1 ⭐"
                "next_word" -> "Next Word ➡️"
                "try_again" -> "Oops! Try another letter, buddy! 💪"
                "listening_title" -> "👂 Listening Game"
                "replay_sound" -> "Replay"
                "tap_heard_card" -> "Tap the card you just heard:"
                "next_sound" -> "Next Sound ➡️"
                "correct_text" -> "Excellent! You got it! 🌟"
                "memory_title" -> "🧠 Memory Game"
                "memorize_cards" -> "Memorize the cards! Time left: $countdown seconds"
                "find_card" -> "Find: $target!"
                "cards_hidden_speak" -> "Cards hidden! Now tell me, where is $target?"
                "perfect_memory" -> "Perfect memory! You got it! 🎉"
                else -> ""
            }
        }
    }
}

