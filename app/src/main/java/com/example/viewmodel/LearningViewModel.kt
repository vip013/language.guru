package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Badge
import com.example.data.LanguageConfig
import com.example.data.LanguageData
import com.example.data.LearningItem
import com.example.data.LessonCategory
import com.example.data.DifficultyLevel
import com.example.data.getDifficultyLevel
import com.example.data.teluguStatesAndCapitals
import com.example.data.englishStatesAndCapitals
import com.example.data.teluguNationalSymbols
import com.example.data.englishNationalSymbols
import com.example.data.teluguRelationships
import com.example.data.englishRelationships
import com.example.data.teluguFoods
import com.example.data.englishFoods
import com.example.data.teluguFruitsAndFlowers
import com.example.data.englishFruitsAndFlowers
import com.example.data.teluguBodyParts
import com.example.data.englishBodyParts
import com.example.data.ProgressManager
import com.example.data.actualTitle
import com.example.data.localizeItem
import com.example.ui.components.SoundEffectsHelper
import com.example.ui.components.TextToSpeechHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    object Welcome : Screen()
    object Dashboard : Screen()
    object Learning : Screen()
    object Badges : Screen()
}

class LearningViewModel(application: Application) : AndroidViewModel(application) {

    private val progressManager = ProgressManager(application)
    private var ttsHelper: TextToSpeechHelper? = null
    private var sfxHelper: SoundEffectsHelper? = null

    // TTS Ready State
    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    // UI Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Selected Language State
    private val _selectedLanguage = MutableStateFlow<LanguageConfig?>(
        if (progressManager.selectedLanguage.isNotEmpty()) {
            LanguageData.getByCode(progressManager.selectedLanguage)
        } else null
    )
    val selectedLanguage: StateFlow<LanguageConfig?> = _selectedLanguage.asStateFlow()

    // Active Lesson State
    private val _activeCategory = MutableStateFlow<LessonCategory?>(
        if (progressManager.activeCategory.isNotEmpty()) {
            try {
                LessonCategory.valueOf(progressManager.activeCategory)
            } catch (e: Exception) {
                null
            }
        } else null
    )
    val activeCategory: StateFlow<LessonCategory?> = _activeCategory.asStateFlow()

    private val _activeItemIndex = MutableStateFlow(progressManager.activeItemIndex)
    val activeItemIndex: StateFlow<Int> = _activeItemIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLessonCompleted = MutableStateFlow(false)
    val isLessonCompleted: StateFlow<Boolean> = _isLessonCompleted.asStateFlow()

    // Audio & Voice Preferences (backed by persistent storage)
    private val _isVoiceOn = MutableStateFlow(progressManager.isVoiceOn)
    val isVoiceOn: StateFlow<Boolean> = _isVoiceOn.asStateFlow()

    private val _useWebSpeechApi = MutableStateFlow(progressManager.useWebSpeechApi)
    val useWebSpeechApi: StateFlow<Boolean> = _useWebSpeechApi.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(progressManager.voiceSpeed)
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    private val _isBgmOn = MutableStateFlow(progressManager.isBgmOn)
    val isBgmOn: StateFlow<Boolean> = _isBgmOn.asStateFlow()

    private val _voiceCharacter = MutableStateFlow(progressManager.voiceCharacter)
    val voiceCharacter: StateFlow<String> = _voiceCharacter.asStateFlow()

    private val _isDarkMode = MutableStateFlow(progressManager.isDarkMode ?: false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Avatar Preferences
    private val _isAvatarEnabled = MutableStateFlow(progressManager.isAvatarEnabled)
    val isAvatarEnabled: StateFlow<Boolean> = _isAvatarEnabled.asStateFlow()

    private val _avatarSize = MutableStateFlow(progressManager.avatarSize)
    val avatarSize: StateFlow<String> = _avatarSize.asStateFlow()

    private val _avatarPosition = MutableStateFlow(progressManager.avatarPosition)
    val avatarPosition: StateFlow<String> = _avatarPosition.asStateFlow()

    private val _isAvatarMuted = MutableStateFlow(progressManager.isAvatarMuted)
    val isAvatarMuted: StateFlow<Boolean> = _isAvatarMuted.asStateFlow()

    private val _isAvatarFemale = MutableStateFlow(progressManager.isAvatarFemale)
    val isAvatarFemale: StateFlow<Boolean> = _isAvatarFemale.asStateFlow()

    private val _avatarType = MutableStateFlow(progressManager.avatarType)
    val avatarType: StateFlow<String> = _avatarType.asStateFlow()

    // Transient Animation States for Avatar
    private val _avatarExpression = MutableStateFlow("happy")
    val avatarExpression: StateFlow<String> = _avatarExpression.asStateFlow()

    private val _avatarIsSpeaking = MutableStateFlow(false)
    val avatarIsSpeaking: StateFlow<Boolean> = _avatarIsSpeaking.asStateFlow()

    private val _avatarAction = MutableStateFlow("idle")
    val avatarAction: StateFlow<String> = _avatarAction.asStateFlow()

    private val _avatarText = MutableStateFlow("")
    val avatarText: StateFlow<String> = _avatarText.asStateFlow()

    private var learnedItemsCountSinceLastEncouragement = 0
    private var nextEncouragementThreshold = (5..10).random()

    // Stats & Achievements (reactive)
    private val _totalStars = MutableStateFlow(progressManager.totalStars)
    val totalStars: StateFlow<Int> = _totalStars.asStateFlow()

    private val _dailyStreak = MutableStateFlow(progressManager.dailyStreak)
    val dailyStreak: StateFlow<Int> = _dailyStreak.asStateFlow()

    private val _lessonsCompletedToday = MutableStateFlow(progressManager.lessonsCompletedToday)
    val lessonsCompletedToday: StateFlow<Int> = _lessonsCompletedToday.asStateFlow()

    private val _dailyGoalTarget = MutableStateFlow(progressManager.dailyGoalTarget)
    val dailyGoalTarget: StateFlow<Int> = _dailyGoalTarget.asStateFlow()

    private val _badges = MutableStateFlow<List<Badge>>(emptyList())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    // --- Premium & Diamond System States ---
    private val _diamonds = MutableStateFlow(progressManager.diamonds)
    val diamonds: StateFlow<Int> = _diamonds.asStateFlow()

    private val _isPremium = MutableStateFlow(progressManager.isPremium)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _isPremiumPurchased = MutableStateFlow(progressManager.isPremiumPurchased)
    val isPremiumPurchased: StateFlow<Boolean> = _isPremiumPurchased.asStateFlow()

    private val _trialTimeLeftMillis = MutableStateFlow(progressManager.getTrialTimeLeftMillis())
    val trialTimeLeftMillis: StateFlow<Long> = _trialTimeLeftMillis.asStateFlow()

    private val _unlockedAvatars = MutableStateFlow(progressManager.unlockedAvatars)
    val unlockedAvatars: StateFlow<Set<String>> = _unlockedAvatars.asStateFlow()

    private val _unlockedThemes = MutableStateFlow(progressManager.unlockedThemes)
    val unlockedThemes: StateFlow<Set<String>> = _unlockedThemes.asStateFlow()

    private val _boughtCertificates = MutableStateFlow(progressManager.boughtCertificates)
    val boughtCertificates: StateFlow<Set<String>> = _boughtCertificates.asStateFlow()

    private val _lastChallengeDate = MutableStateFlow(progressManager.lastChallengeDate)
    val lastChallengeDate: StateFlow<Long> = _lastChallengeDate.asStateFlow()

    private val _showDailyLoginReward = MutableStateFlow(false)
    val showDailyLoginReward: StateFlow<Boolean> = _showDailyLoginReward.asStateFlow()

    private val _dailyLoginAmount = MutableStateFlow(0)
    val dailyLoginAmount: StateFlow<Int> = _dailyLoginAmount.asStateFlow()

    private val _aiQuestionsCountToday = MutableStateFlow(progressManager.aiQuestionsCountToday)
    val aiQuestionsCountToday: StateFlow<Int> = _aiQuestionsCountToday.asStateFlow()

    // Global Dialog States for the Bottom Navigation Bar
    private val _showVoiceChatGlobal = MutableStateFlow(false)
    val showVoiceChatGlobal: StateFlow<Boolean> = _showVoiceChatGlobal.asStateFlow()

    private val _showMiniGamesGlobal = MutableStateFlow(false)
    val showMiniGamesGlobal: StateFlow<Boolean> = _showMiniGamesGlobal.asStateFlow()

    private val _showBadgesGlobal = MutableStateFlow(false)
    val showBadgesGlobal: StateFlow<Boolean> = _showBadgesGlobal.asStateFlow()

    private val _showSettingsGlobal = MutableStateFlow(false)
    val showSettingsGlobal: StateFlow<Boolean> = _showSettingsGlobal.asStateFlow()

    // Timer Job for the 2-second progression
    private var progressJob: Job? = null

    init {
        // Sync persistent theme to global theme state
        com.example.ui.components.BackgroundThemeState.currentTheme.value = progressManager.currentTheme
        com.example.ui.components.BackgroundThemeState.onThemeChanged = { newTheme ->
            progressManager.currentTheme = newTheme
        }

        // Init TTS & SFX helpers
        ttsHelper = TextToSpeechHelper(application) { success ->
            _isTtsReady.value = true
            if (success) {
                viewModelScope.launch {
                    delay(1200) // Small delay so the app is fully visible
                    if (_currentScreen.value == Screen.Learning) {
                        _isPlaying.value = true
                        triggerItemSpeechAndProgression()
                    } else {
                        speakWelcomeGreeting()
                    }
                }
            }
        }
        sfxHelper = SoundEffectsHelper()

        // Sync background music
        if (_isBgmOn.value) {
            sfxHelper?.startBackgroundMusic()
        }

        // Load achievements and verify daily login
        updateAchievements()
        checkDailyLoginReward()
        refreshTrialTime()
    }

    private fun updateAchievements() {
        _totalStars.value = progressManager.totalStars
        _badges.value = progressManager.getEarnedBadges()
        _diamonds.value = progressManager.diamonds
        _isPremium.value = progressManager.isPremium
        _isPremiumPurchased.value = progressManager.isPremiumPurchased
        _trialTimeLeftMillis.value = progressManager.getTrialTimeLeftMillis()
        _unlockedAvatars.value = progressManager.unlockedAvatars
        _unlockedThemes.value = progressManager.unlockedThemes
        _boughtCertificates.value = progressManager.boughtCertificates
        _lessonsCompletedToday.value = progressManager.lessonsCompletedToday
        _dailyGoalTarget.value = progressManager.dailyGoalTarget

        // Dynamic progress calculation
        updateProgressPercentage()
        checkAndUnlockCompletedLevels()
    }

    fun updateProgressPercentage() {
        val lang = _selectedLanguage.value ?: return
        val categories = LessonCategory.values()
        val completedCount = categories.count { progressManager.isLessonCompleted(lang.code, it) }
        val percent = if (categories.isNotEmpty()) completedCount.toFloat() / categories.size else 0f
        progressManager.progressPercentage = percent
    }

    fun checkAndUnlockCompletedLevels() {
        val lang = _selectedLanguage.value ?: return
        DifficultyLevel.values().forEach { level ->
            val levelCats = LessonCategory.values().filter { it.getDifficultyLevel() == level }
            if (levelCats.isNotEmpty() && levelCats.all { progressManager.isLessonCompleted(lang.code, it) }) {
                progressManager.completeLevel(level.name)
            }
        }
    }

    fun recordQuizCompletion() {
        progressManager.quizProgress += 1
        val item = getCurrentItem()
        val lang = _selectedLanguage.value?.code ?: ""
        if (item != null && lang.isNotEmpty()) {
            val completedQuizId = "${lang}_quiz_${item.display}"
            val currentQuizzes = progressManager.completedQuizzes.toMutableSet()
            currentQuizzes.add(completedQuizId)
            progressManager.completedQuizzes = currentQuizzes
        }
    }

    fun recordWritingPracticeCompletion() {
        progressManager.writingPracticeProgress += 1
        val item = getCurrentItem()
        val lang = _selectedLanguage.value?.code ?: ""
        if (item != null && lang.isNotEmpty()) {
            val completedTracingId = "${lang}_tracing_${item.display}"
            val currentTracings = progressManager.completedTracings.toMutableSet()
            currentTracings.add(completedTracingId)
            progressManager.completedTracings = currentTracings
        }
    }

    fun refreshTrialTime() {
        _trialTimeLeftMillis.value = progressManager.getTrialTimeLeftMillis()
        _isPremium.value = progressManager.isPremium
    }

    fun setDailyGoalTarget(target: Int) {
        progressManager.dailyGoalTarget = target
        updateAchievements()
    }

    // --- Diamond Shop & Premium Action Methods ---
    fun checkDailyLoginReward() {
        val lastLogin = progressManager.lastLoginDate
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60 * 60 * 1000
        
        if (lastLogin == 0L || (now - lastLogin) >= oneDayMillis) {
            val isConsecutive = lastLogin != 0L && (now - lastLogin) < (2 * oneDayMillis)
            if (isConsecutive) {
                progressManager.dailyStreak += 1
            } else {
                progressManager.dailyStreak = 1
            }
            _dailyStreak.value = progressManager.dailyStreak

            val amount = if (progressManager.isPremium) 30 else 15
            progressManager.lastLoginDate = now
            earnDiamonds(amount)
            _dailyLoginAmount.value = amount
            _showDailyLoginReward.value = true
        } else {
            _dailyStreak.value = progressManager.dailyStreak
        }
    }

    fun dismissDailyLoginReward() {
        _showDailyLoginReward.value = false
    }

    fun isChallengeCompletedToday(): Boolean {
        val lastDate = progressManager.lastChallengeDate
        if (lastDate == 0L) return false
        val now = System.currentTimeMillis()
        val oneDayMillis = 24L * 60 * 60 * 1000
        return (now - lastDate) < oneDayMillis
    }

    fun completeDailyChallenge() {
        val now = System.currentTimeMillis()
        progressManager.lastChallengeDate = now
        _lastChallengeDate.value = now
        earnDiamonds(25)
        playSuccessSound()
    }

    fun earnDiamonds(amount: Int) {
        progressManager.earnDiamonds(amount)
        _diamonds.value = progressManager.diamonds
    }

    fun spendDiamonds(amount: Int): Boolean {
        val success = progressManager.spendDiamonds(amount)
        if (success) {
            _diamonds.value = progressManager.diamonds
        }
        return success
    }

    fun deductStars(amount: Int): Boolean {
        val success = progressManager.deductStars(amount)
        if (success) {
            _totalStars.value = progressManager.totalStars
        }
        return success
    }

    fun deductDiamonds(amount: Int): Boolean {
        return spendDiamonds(amount)
    }

    fun buyStarsWithDiamonds(starCount: Int, diamondCost: Int): Boolean {
        val success = progressManager.buyStarsWithDiamonds(starCount, diamondCost)
        if (success) {
            _diamonds.value = progressManager.diamonds
            _totalStars.value = progressManager.totalStars
            playSuccessSound()
        }
        return success
    }

    fun buyPremiumSubscription(planName: String) {
        // Simulates a successful Google Play Billing flow
        progressManager.isPremiumPurchased = true
        _isPremiumPurchased.value = true
        _isPremium.value = true
        _diamonds.value = progressManager.diamonds
        playSuccessSound()
    }

    fun restorePurchases() {
        // Simulates Google Play Billing restore purchases check
        // If they had previously simulated premium or if they click it, we restore/refresh status
        _isPremiumPurchased.value = progressManager.isPremiumPurchased
        _isPremium.value = progressManager.isPremium
        playSuccessSound()
    }

    fun getAiQuestionsCountToday(): Int {
        val lastDate = progressManager.lastAiQuestionDate
        val now = System.currentTimeMillis()
        if (isNewDay(lastDate, now)) {
            progressManager.lastAiQuestionDate = now
            progressManager.aiQuestionsCountToday = 0
            _aiQuestionsCountToday.value = 0
        } else {
            _aiQuestionsCountToday.value = progressManager.aiQuestionsCountToday
        }
        return _aiQuestionsCountToday.value
    }

    fun incrementAiQuestionsCountToday() {
        val current = getAiQuestionsCountToday()
        val newVal = current + 1
        progressManager.aiQuestionsCountToday = newVal
        _aiQuestionsCountToday.value = newVal
    }

    private fun isNewDay(lastTime: Long, nowTime: Long): Boolean {
        if (lastTime == 0L) return true
        val lastCal = java.util.Calendar.getInstance().apply { timeInMillis = lastTime }
        val nowCal = java.util.Calendar.getInstance().apply { timeInMillis = nowTime }
        return lastCal.get(java.util.Calendar.YEAR) != nowCal.get(java.util.Calendar.YEAR) ||
               lastCal.get(java.util.Calendar.DAY_OF_YEAR) != nowCal.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun unlockPremiumAvatar(avatarId: String, diamondCost: Int): Boolean {
        if (spendDiamonds(diamondCost)) {
            progressManager.unlockAvatar(avatarId)
            _unlockedAvatars.value = progressManager.unlockedAvatars
            playSuccessSound()
            return true
        }
        return false
    }

    fun unlockPremiumTheme(themeId: String, diamondCost: Int): Boolean {
        if (spendDiamonds(diamondCost)) {
            progressManager.unlockTheme(themeId)
            _unlockedThemes.value = progressManager.unlockedThemes
            // Automatically select and apply the newly unlocked theme!
            progressManager.currentTheme = themeId
            com.example.ui.components.BackgroundThemeState.currentTheme.value = themeId
            playSuccessSound()
            return true
        }
        return false
    }

    fun setBackgroundTheme(themeId: String) {
        if (progressManager.unlockedThemes.contains(themeId) || progressManager.isPremium) {
            progressManager.currentTheme = themeId
            com.example.ui.components.BackgroundThemeState.currentTheme.value = themeId
            com.example.ui.components.BackgroundThemeState.onThemeChanged?.invoke(themeId)
        }
    }

    fun purchaseCertificate(courseId: String, diamondCost: Int): Boolean {
        if (spendDiamonds(diamondCost)) {
            progressManager.unlockCertificate(courseId)
            _boughtCertificates.value = progressManager.boughtCertificates
            playSuccessSound()
            return true
        }
        return false
    }

    fun unlockCertificateFree(courseId: String) {
        progressManager.unlockCertificate(courseId)
        _boughtCertificates.value = progressManager.boughtCertificates
        playSuccessSound()
    }

    fun playSuccessSound() {
        sfxHelper?.playSuccess()
    }

    // --- Weak Words / Adaptive Review ---
    private val _weakWords = MutableStateFlow<Set<String>>(progressManager.getWeakWords())
    val weakWords: StateFlow<Set<String>> = _weakWords.asStateFlow()

    private val _profileName = MutableStateFlow(progressManager.profileName)
    val profileName: StateFlow<String> = _profileName.asStateFlow()

    private val _profileImageUri = MutableStateFlow(progressManager.profileImageUri)
    val profileImageUri: StateFlow<String> = _profileImageUri.asStateFlow()

    fun updateProfileName(name: String) {
        progressManager.profileName = name
        _profileName.value = name
    }

    fun updateProfileImageUri(uriString: String) {
        progressManager.profileImageUri = uriString
        _profileImageUri.value = uriString
    }

    fun recordMistake(word: String) {
        progressManager.addWeakWord(word)
        _weakWords.value = progressManager.getWeakWords()
        
        // Update avatar to encourage child
        _avatarExpression.value = "thinking"
        _avatarAction.value = "thinking"
        
        val code = _selectedLanguage.value?.code ?: "en"
        val text = if (code == "te") {
            "చింతించకు! మళ్లీ ప్రయత్నిద్దాం. '$word' ని ఇంకోసారి ప్రాక్టీస్ చేయి!"
        } else {
            "Don't worry! Let's try '$word' again together!"
        }
        speakCustomText(text)
    }

    fun recordSuccess(word: String) {
        progressManager.removeWeakWord(word)
        _weakWords.value = progressManager.getWeakWords()
        
        // Show celebration
        _avatarExpression.value = "happy"
        _avatarAction.value = "happy"
        
        val code = _selectedLanguage.value?.code ?: "en"
        val text = if (code == "te") {
            "చాలా బాగుంది! నువ్వు '$word' ని సరిగ్గా చెప్పావు!"
        } else {
            "Awesome! You got '$word' perfectly right!"
        }
        speakCustomText(text)
    }

    fun clearWeakWords() {
        progressManager.clearWeakWords()
        _weakWords.value = emptySet()
    }

    fun earnMiniGameStar() {
        progressManager.earnMiniGameStar()
        val isPrem = progressManager.isPremium
        val dEarned = if (isPrem) 6 else 3
        earnDiamonds(dEarned)
        recordQuizCompletion()
        updateAchievements()
    }

    // --- Actions ---

    fun playClickSound() {
        sfxHelper?.playClick()
    }

    fun selectLanguage(langCode: String) {
        progressManager.selectedLanguage = langCode
        val config = LanguageData.getByCode(langCode)
        _selectedLanguage.value = config
        updateProgressPercentage()
        checkAndUnlockCompletedLevels()
        setScreen(Screen.Dashboard)
        playClickSound()
        updateAchievements()
    }

    fun navigateToWelcome() {
        setScreen(Screen.Welcome)
        playClickSound()
    }

    fun isLessonCompleted(language: String, category: LessonCategory): Boolean {
        return progressManager.isLessonCompleted(language, category)
    }

    fun getLessonStars(language: String, category: LessonCategory): Int {
        return progressManager.getLessonStars(language, category)
    }

    fun navigateToDashboard() {
        stopLessonTimer()
        setScreen(Screen.Dashboard)
        playClickSound()
        updateAchievements()
    }

    fun setShowVoiceChat(show: Boolean) {
        _showVoiceChatGlobal.value = show
        playClickSound()
    }

    fun setShowMiniGames(show: Boolean) {
        _showMiniGamesGlobal.value = show
        playClickSound()
    }

    fun setShowBadges(show: Boolean) {
        _showBadgesGlobal.value = show
        playClickSound()
    }

    fun setShowSettings(show: Boolean) {
        _showSettingsGlobal.value = show
        playClickSound()
    }

    fun navigateToBadges() {
        setScreen(Screen.Badges)
        playClickSound()
    }

    fun navigateToPremium() {
        setScreen(Screen.Dashboard)
        playClickSound()
    }

    fun startLesson(category: LessonCategory) {
        val config = _selectedLanguage.value ?: return
        setActiveCategory(category)
        setActiveItemIndex(0)
        _isLessonCompleted.value = false
        setScreen(Screen.Learning)

        // Start auto progression immediately
        _isPlaying.value = true
        triggerItemSpeechAndProgression()
    }

    private fun triggerItemSpeechAndProgression() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            val item = getCurrentItem() ?: return@launch
            val lang = _selectedLanguage.value ?: return@launch

            // Wait for TTS initialization to complete before playing speech
            if (_isVoiceOn.value) {
                var retries = 0
                while (ttsHelper != null && ttsHelper?.isReady() != true && retries < 30) {
                    delay(100)
                    retries++
                }
            }

            // Set Avatar state based on current lesson type
            val isLetters = _activeCategory.value == LessonCategory.LETTERS
            if (isLetters) {
                _avatarAction.value = "point"
            } else {
                _avatarAction.value = "idle"
            }
            _avatarExpression.value = "happy"

            // 1. Play speech with a Completion callback
            val deferred = CompletableDeferred<Unit>()
            speakCurrentItem {
                deferred.complete(Unit)
            }

            // 2. Wait for completion
            try {
                deferred.await()
            } catch (e: Exception) {
                // Job was cancelled (e.g. user manually navigated or paused)
                return@launch
            }

            _avatarAction.value = "idle" // Reset pointing action

            // 3. Post-speech delay & transition
            if (_isPlaying.value && !_isLessonCompleted.value) {
                val isPoem = _activeCategory.value == LessonCategory.POEMS && lang.code != "te"
                val postSpeechDelay = if (isLetters) {
                    4000L // Wait 4 seconds for letters
                } else if (isPoem) {
                    2500L // 2.5s gap for poems
                } else {
                    1500L // 1.5s for normal items
                }
                delay(postSpeechDelay)

                // Advance to next item
                if (_isPlaying.value && !_isLessonCompleted.value) {
                    val items = getLessonItems()
                    if (_activeItemIndex.value < items.lastIndex) {
                        setActiveItemIndex(_activeItemIndex.value + 1)
                        triggerItemSpeechAndProgression() // Recurse/trigger for next item
                    } else {
                        finishLesson()
                    }
                }
            }
        }
    }

    private fun startLessonTimer() {
        triggerItemSpeechAndProgression()
    }

    private fun stopLessonTimer() {
        progressJob?.cancel()
        progressJob = null
        _isPlaying.value = false
        ttsHelper?.stop()
    }

    fun getLessonItems(): List<LearningItem> {
        val config = _selectedLanguage.value ?: return emptyList()
        val category = _activeCategory.value ?: return emptyList()
        val items = config.lessons[category]
        if (items != null) return items

        // Fallback for languages that do not have custom lists for these new categories
        val isTe = config.code == "te"
        val baseList = when (category) {
            LessonCategory.STATES_CAPITALS -> if (isTe) teluguStatesAndCapitals else englishStatesAndCapitals
            LessonCategory.NATIONAL_SYMBOLS -> if (isTe) teluguNationalSymbols else englishNationalSymbols
            LessonCategory.RELATIONSHIPS -> if (isTe) teluguRelationships else englishRelationships
            LessonCategory.FOODS -> if (isTe) teluguFoods else englishFoods
            LessonCategory.FRUITS_FLOWERS -> if (isTe) teluguFruitsAndFlowers else englishFruitsAndFlowers
            LessonCategory.BODY_PARTS -> if (isTe) teluguBodyParts else englishBodyParts
            else -> emptyList()
        }

        if (config.code != "te" && config.code != "en") {
            return baseList.map { item -> localizeItem(item, category, config.code) }
        }
        return baseList
    }

    fun getCurrentItem(): LearningItem? {
        val items = getLessonItems()
        val index = _activeItemIndex.value
        if (index in items.indices) {
            return items[index]
        }
        return null
    }

    fun speakCurrentItem(onDone: () -> Unit = {}) {
        val item = getCurrentItem() ?: return
        val lang = _selectedLanguage.value ?: return
        val isPoem = _activeCategory.value == LessonCategory.POEMS && lang.code != "te"

        val textToSpeak = item.voiceText

        val activeChar = if (_activeCategory.value == LessonCategory.VEGETABLES) "motu" else _voiceCharacter.value

        _avatarIsSpeaking.value = true
        _avatarText.value = textToSpeak
        // Speak slowly and clearly via TTS immediately
        ttsHelper?.speak(
            text = textToSpeak,
            subtitle = item.subtitle,
            langCode = lang.code,
            speed = _voiceSpeed.value,
            isVoiceOn = _isVoiceOn.value,
            isPoem = isPoem,
            voiceCharacter = activeChar,
            onDone = {
                _avatarIsSpeaking.value = false
                _avatarText.value = ""
                onDone()
            }
        )
    }

    fun speakCustomText(text: String, onDone: () -> Unit = {}) {
        val isMuted = _isAvatarMuted.value
        _avatarIsSpeaking.value = true
        val cleanText = text.replace("**", "")
            .replace("*", "")
            .replace("__", "")
            .replace("_", "")
            .replace("`", "")
            .replace("#", "")
            .replace("~", "")
        _avatarText.value = cleanText
        if (isMuted) {
            viewModelScope.launch {
                val delayMs = (cleanText.length * 85L).coerceIn(1200L, 4000L)
                delay(delayMs)
                _avatarIsSpeaking.value = false
                _avatarText.value = ""
                onDone()
            }
        } else {
            val lang = _selectedLanguage.value ?: return
            val activeChar = _voiceCharacter.value
            ttsHelper?.speak(
                text = text,
                subtitle = "",
                langCode = lang.code,
                speed = _voiceSpeed.value,
                isVoiceOn = _isVoiceOn.value,
                isPoem = false,
                voiceCharacter = activeChar,
                onDone = {
                    _avatarIsSpeaking.value = false
                    _avatarText.value = ""
                    onDone()
                }
            )
        }
    }

    fun stopSpeech() {
        ttsHelper?.stop()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        playClickSound()
        if (_isPlaying.value) {
            triggerItemSpeechAndProgression()
        } else {
            progressJob?.cancel()
            ttsHelper?.stop()
        }
    }

    fun goToNextItem() {
        val items = getLessonItems()
        if (_activeItemIndex.value < items.lastIndex) {
            setActiveItemIndex(_activeItemIndex.value + 1)
            triggerItemSpeechAndProgression()
        } else {
            // Completed!
            finishLesson()
        }
    }

    fun goToPrevItem() {
        if (_activeItemIndex.value > 0) {
            setActiveItemIndex(_activeItemIndex.value - 1)
            playClickSound()
            triggerItemSpeechAndProgression()
        }
    }

    fun replayCurrentItem() {
        triggerItemSpeechAndProgression()
    }

    private fun finishLesson() {
        _isLessonCompleted.value = true
        _isPlaying.value = false
        stopLessonTimer()

        val lang = _selectedLanguage.value ?: return
        val cat = _activeCategory.value ?: return

        // Award stars (e.g. 3 stars)
        progressManager.completeLesson(lang.code, cat, starsAwarded = 3)

        // Award diamonds on lesson completion
        val isPrem = progressManager.isPremium
        val dEarned = if (isPrem) 10 else 5
        earnDiamonds(dEarned)

        // Play celebration sound effects
        sfxHelper?.playSuccess()
        updateAchievements()

        // Avatar celebration sequence
        viewModelScope.launch {
            _avatarExpression.value = "celebrating"
            _avatarAction.value = "clap"
            delay(1200)
            val congratsMsg = when (lang.code) {
                "te" -> "అద్భుతం! మీరు పాఠాన్ని పూర్తి చేసారు! మీకు బంగారు నక్షత్రాలు లభించాయి!"
                "ta" -> "அற்புதம்! நீங்கள் பாடத்தை முடித்துவிட்டீர்கள்! உங்களுக்கு தங்க நட்சத்திரங்கள் கிடைத்துள்ளன!"
                "hi" -> "अद्भुत! आपने पाठ पूरा कर लिया है! आपको सोने के सितारे मिले हैं!"
                "ar" -> "مدهش! لقد أكملت الدرس! لقد حصلت على نجوم ذهبية!"
                "kn" -> "ಅದ್ಭುತ! ನೀವು ಪಾಠವನ್ನು ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ! ನಿಮಗೆ ಚಿನ್ನದ ನಕ್ಷತ್ರಗಳು ದೊರೆತಿವೆ!"
                "ml" -> "അത്ഭുതം! നിങ്ങൾ പാഠം പൂർത്തിയാക്കി! നിങ്ങൾക്ക് സ്വർണ്ണ നക്ഷത്രങ്ങൾ ലഭിച്ചു!"
                "bn" -> "চমৎকার! আপনি পাঠটি সম্পন্ন করেছেন! আপনি সোনার তারা পেয়েছেন!"
                "mr" -> "अद्भुत! तुम्ही पाठ पूर्ण केला आहे! तुम्हाला सोन्याचे तारे मिळाले आहेत!"
                "gu" -> "અદ્ભુત! તમે પાઠ પૂર્ણ કર્યો છે! તમને સોનાના તારા મળ્યા છે!"
                else -> "Amazing! You completed the lesson! You earned golden stars!"
            }
            speakCustomText(congratsMsg) {
                _avatarExpression.value = "happy"
                _avatarAction.value = "thumbs_up"
            }
        }
    }

    fun learnAgain() {
        setActiveItemIndex(0)
        _isLessonCompleted.value = false
        _isPlaying.value = true
        playClickSound()
        triggerItemSpeechAndProgression()
    }

    fun nextLesson() {
        val activeCat = _activeCategory.value ?: return
        val currentLevel = activeCat.getDifficultyLevel()
        val levelCategories = LessonCategory.values().filter { it.getDifficultyLevel() == currentLevel }
        val currentIndex = levelCategories.indexOf(activeCat)
        
        val nextCat = if (currentIndex != -1 && currentIndex < levelCategories.size - 1) {
            levelCategories[currentIndex + 1]
        } else {
            // Last category in current level, move to first category of next level
            val nextLevel = when (currentLevel) {
                DifficultyLevel.BEGINNER -> DifficultyLevel.INTERMEDIATE
                DifficultyLevel.INTERMEDIATE -> DifficultyLevel.ADVANCED
                DifficultyLevel.ADVANCED -> DifficultyLevel.BEGINNER
            }
            val nextLevelCategories = LessonCategory.values().filter { it.getDifficultyLevel() == nextLevel }
            nextLevelCategories.firstOrNull() ?: LessonCategory.LETTERS
        }

        startLesson(nextCat)
    }

    // --- Settings Settings ---

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        progressManager.isDarkMode = newValue
        playClickSound()
    }

    fun toggleVoiceOn() {
        val newValue = !_isVoiceOn.value
        _isVoiceOn.value = newValue
        progressManager.isVoiceOn = newValue
        playClickSound()
        if (!newValue) {
            ttsHelper?.stop()
        }
    }

    fun setVoiceCharacter(character: String) {
        _voiceCharacter.value = character
        progressManager.voiceCharacter = character
        playClickSound()
        // Replay/Trigger speak if in learning screen so they can hear the new voice immediately
        if (_currentScreen.value == Screen.Learning) {
            triggerItemSpeechAndProgression()
        }
    }

    fun setVoiceSpeed(speed: Float) {
        _voiceSpeed.value = speed
        progressManager.voiceSpeed = speed
        playClickSound()
    }

    fun toggleWebSpeechApi() {
        val newValue = !_useWebSpeechApi.value
        progressManager.useWebSpeechApi = newValue
        _useWebSpeechApi.value = newValue
        playClickSound()
    }

    fun toggleBackgroundMusic() {
        val newValue = !_isBgmOn.value
        _isBgmOn.value = newValue
        progressManager.isBgmOn = newValue
        playClickSound()

        if (newValue) {
            sfxHelper?.startBackgroundMusic()
        } else {
            sfxHelper?.stopBackgroundMusic()
        }
    }

    fun changeLanguage() {
        stopLessonTimer()
        _currentScreen.value = Screen.Welcome
        playClickSound()
    }

    // --- Avatar Controls & Settings ---

    fun toggleAvatarEnabled() {
        val newValue = !_isAvatarEnabled.value
        _isAvatarEnabled.value = newValue
        progressManager.isAvatarEnabled = newValue
        playClickSound()
    }

    fun setAvatarSize(size: String) {
        _avatarSize.value = size
        progressManager.avatarSize = size
        playClickSound()
    }

    fun setAvatarPosition(position: String) {
        _avatarPosition.value = position
        progressManager.avatarPosition = position
        playClickSound()
    }

    fun toggleAvatarMuted() {
        val newValue = !_isAvatarMuted.value
        _isAvatarMuted.value = newValue
        progressManager.isAvatarMuted = newValue
        playClickSound()
    }

    fun toggleAvatarGender() {
        val newValue = !_isAvatarFemale.value
        _isAvatarFemale.value = newValue
        progressManager.isAvatarFemale = newValue
        playClickSound()
    }

    fun setAvatarType(type: String) {
        _avatarType.value = type
        progressManager.avatarType = type
        // Also update standard gender flag for compatibility
        val isFemaleType = type == "girl" || type == "rabbit" || type == "panda"
        _isAvatarFemale.value = isFemaleType
        progressManager.isAvatarFemale = isFemaleType
        playClickSound()
    }

    fun speakWelcomeGreeting() {
        if (!_isAvatarEnabled.value) return
        _avatarAction.value = "wave"
        _avatarExpression.value = "happy"
        val text = "Welcome to Language Guru! Let's learn together!"
        speakCustomText(text) {
            _avatarAction.value = "idle"
        }
    }

    fun getEncouragementPhrase(): String {
        val lang = _selectedLanguage.value?.code ?: "en"
        val tePhrases = listOf(
            "చాలా బాగుంది!",
            "అద్భుతం!",
            "ముందుకు సాగండి!",
            "సూపర్!",
            "చాలా మంచిది!",
            "నువ్వు చేయగలవు!",
            "అద్భుతమైనది!",
            "మరికొంత నేర్చుకుందాం!"
        )
        val hiPhrases = listOf(
            "बहुत बढ़िया!",
            "उत्कृष्ट!",
            "आगे बढ़ते रहो!",
            "बहुत अच्छे!",
            "तुम कर सकते हो!",
            "शानदार!",
            "चलो और सीखें!"
        )
        val enPhrases = listOf(
            "Great Job!",
            "Excellent!",
            "Keep Going!",
            "Awesome!",
            "Very Good!",
            "You Can Do It!",
            "Fantastic!",
            "Let's Learn More!"
        )

        return when (lang) {
            "te" -> tePhrases.random()
            "hi" -> hiPhrases.random()
            else -> enPhrases.random()
        }
    }

    fun triggerCorrectAnswerAnimation(onDone: () -> Unit = {}) {
        recordQuizCompletion()
        viewModelScope.launch {
            _avatarExpression.value = "excited"
            _avatarAction.value = "jump"
            sfxHelper?.playCorrect()
            val correctMsg = when (_selectedLanguage.value?.code ?: "en") {
                "te" -> "సరైన సమాధానం! అద్భుతం!"
                "hi" -> "सही उत्तर! बहुत अच्छे!"
                else -> "Correct! Perfect job!"
            }
            speakCustomText(correctMsg) {
                _avatarExpression.value = "happy"
                _avatarAction.value = "idle"
                onDone()
            }
        }
    }

    fun triggerWrongAnswerAnimation(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            _avatarExpression.value = "thinking"
            _avatarAction.value = "idle"
            sfxHelper?.playWrong()
            val wrongMsg = when (_selectedLanguage.value?.code ?: "en") {
                "te" -> "దాదాపుగా! మళ్లీ ప్రయత్నించండి, మీరు చేయగలరు!"
                "hi" -> "लगभग सही! फिर से कोशिश करें, आप कर सकते हैं!"
                else -> "Almost! Try again, you can do it!"
            }
            speakCustomText(wrongMsg) {
                _avatarExpression.value = "happy"
                onDone()
            }
        }
    }

    fun playCorrectSound() {
        sfxHelper?.playCorrect()
    }

    fun playWrongSound() {
        sfxHelper?.playWrong()
    }

    fun resetProgress() {
        progressManager.resetProgress()
        _isDarkMode.value = progressManager.isDarkMode ?: false
        _isVoiceOn.value = progressManager.isVoiceOn
        _useWebSpeechApi.value = progressManager.useWebSpeechApi
        _voiceSpeed.value = progressManager.voiceSpeed
        _isBgmOn.value = progressManager.isBgmOn
        _voiceCharacter.value = progressManager.voiceCharacter
        _totalStars.value = progressManager.totalStars

        _isAvatarEnabled.value = progressManager.isAvatarEnabled
        _avatarSize.value = progressManager.avatarSize
        _avatarPosition.value = progressManager.avatarPosition
        _isAvatarMuted.value = progressManager.isAvatarMuted
        _isAvatarFemale.value = progressManager.isAvatarFemale
        _avatarType.value = progressManager.avatarType

        _avatarExpression.value = "happy"
        _avatarAction.value = "idle"
        _avatarIsSpeaking.value = false
        learnedItemsCountSinceLastEncouragement = 0

        if (_isBgmOn.value) {
            sfxHelper?.startBackgroundMusic()
        } else {
            sfxHelper?.stopBackgroundMusic()
        }

        setActiveCategory(null)
        setActiveItemIndex(0)
        _isLessonCompleted.value = false

        // Back to welcome or keep same? After reset, send to welcome screen so they start fresh!
        _selectedLanguage.value = null
        setScreen(Screen.Welcome)

        playClickSound()
        updateAchievements()
    }

    private fun setScreen(screen: Screen) {
        _currentScreen.value = screen
        progressManager.currentScreen = when (screen) {
            is Screen.Welcome -> "welcome"
            is Screen.Dashboard -> "dashboard"
            is Screen.Learning -> "learning"
            is Screen.Badges -> "badges"
        }
    }

    private fun setActiveCategory(category: LessonCategory?) {
        _activeCategory.value = category
        progressManager.activeCategory = category?.name ?: ""
    }

    private fun setActiveItemIndex(index: Int) {
        _activeItemIndex.value = index
        progressManager.activeItemIndex = index

        // Save completed step and last opened lesson
        val lang = _selectedLanguage.value?.code ?: ""
        val cat = _activeCategory.value?.name ?: ""
        if (lang.isNotEmpty() && cat.isNotEmpty()) {
            progressManager.completeStep(lang, cat, index)
            getCurrentItem()?.let { item ->
                progressManager.lastOpenedLesson = item.display
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLessonTimer()
        ttsHelper?.shutdown()
        sfxHelper?.release()
    }
}
