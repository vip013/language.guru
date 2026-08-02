package com.example.data

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("language_learning_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_VOICE_ON = "voice_on"
        private const val KEY_USE_WEB_SPEECH_API = "use_web_speech_api"
        private const val KEY_VOICE_SPEED = "voice_speed"
        private const val KEY_BGM_ON = "bgm_on"
        private const val KEY_VOICE_CHARACTER = "voice_character"
        private const val KEY_AVATAR_ENABLED = "avatar_enabled"
        private const val KEY_AVATAR_SIZE = "avatar_size"
        private const val KEY_AVATAR_POSITION = "avatar_position"
        private const val KEY_AVATAR_MUTED = "avatar_muted"
        private const val KEY_AVATAR_FEMALE = "avatar_female"
        private const val KEY_AVATAR_TYPE = "avatar_type"
        private const val PREFIX_LESSON_COMPLETED = "completed_"
        private const val PREFIX_LESSON_STARS = "stars_"
        private const val KEY_STARS_TOTAL = "stars_total"
        
        // Premium and Diamond keys
        private const val KEY_DIAMONDS = "diamonds"
        private const val KEY_IS_PREMIUM_PURCHASED = "is_premium_purchased"
        private const val KEY_TRIAL_START_TIME = "trial_start_time"
        private const val KEY_UNLOCKED_AVATARS = "unlocked_avatars"
        private const val KEY_UNLOCKED_THEMES = "unlocked_themes"
        private const val KEY_BOUGHT_CERTIFICATES = "bought_certificates"
        private const val KEY_CURRENT_SCREEN = "current_screen"
        private const val KEY_ACTIVE_CATEGORY = "active_category"
        private const val KEY_ACTIVE_ITEM_INDEX = "active_item_index"
        private const val KEY_PROFILE_NAME = "profile_name"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
        private const val KEY_CURRENT_THEME = "current_theme"

        // Progress Save System Keys
        private const val KEY_LAST_OPENED_LESSON = "last_opened_lesson"
        private const val KEY_QUIZ_PROGRESS = "quiz_progress"
        private const val KEY_WRITING_PRACTICE_PROGRESS = "writing_practice_progress"
        private const val KEY_COMPLETED_STEPS = "completed_steps"
        private const val KEY_COMPLETED_LEVELS = "completed_levels"
        private const val KEY_PROGRESS_PERCENTAGE = "progress_percentage"
        private const val KEY_COMPLETED_QUIZZES = "completed_quizzes"
        private const val KEY_COMPLETED_TRACINGS = "completed_tracings"
    }

    var lastOpenedLesson: String
        get() = prefs.getString(KEY_LAST_OPENED_LESSON, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_OPENED_LESSON, value).apply()

    var quizProgress: Int
        get() = prefs.getInt(KEY_QUIZ_PROGRESS, 0)
        set(value) = prefs.edit().putInt(KEY_QUIZ_PROGRESS, value).apply()

    var writingPracticeProgress: Int
        get() = prefs.getInt(KEY_WRITING_PRACTICE_PROGRESS, 0)
        set(value) = prefs.edit().putInt(KEY_WRITING_PRACTICE_PROGRESS, value).apply()

    var completedSteps: Set<String>
        get() = prefs.getStringSet(KEY_COMPLETED_STEPS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_COMPLETED_STEPS, value).apply()

    fun isStepCompleted(language: String, category: String, stepIndex: Int): Boolean {
        return completedSteps.contains("${language}_${category}_$stepIndex")
    }

    fun completeStep(language: String, category: String, stepIndex: Int) {
        val current = completedSteps.toMutableSet()
        current.add("${language}_${category}_$stepIndex")
        completedSteps = current
    }

    var completedLevels: Set<String>
        get() = prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_COMPLETED_LEVELS, value).apply()

    fun completeLevel(levelName: String) {
        val current = completedLevels.toMutableSet()
        current.add(levelName)
        completedLevels = current
    }

    var progressPercentage: Float
        get() = prefs.getFloat(KEY_PROGRESS_PERCENTAGE, 0f)
        set(value) = prefs.edit().putFloat(KEY_PROGRESS_PERCENTAGE, value).apply()

    var completedQuizzes: Set<String>
        get() = prefs.getStringSet(KEY_COMPLETED_QUIZZES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_COMPLETED_QUIZZES, value).apply()

    var completedTracings: Set<String>
        get() = prefs.getStringSet(KEY_COMPLETED_TRACINGS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_COMPLETED_TRACINGS, value).apply()

    // Diamonds count (default to 20 for new users to start with)
    var diamonds: Int
        get() {
            if (!prefs.contains(KEY_DIAMONDS)) {
                // Initialize with 20 starter diamonds
                prefs.edit().putInt(KEY_DIAMONDS, 20).apply()
                return 20
            }
            return prefs.getInt(KEY_DIAMONDS, 20)
        }
        set(value) {
            prefs.edit().putInt(KEY_DIAMONDS, value).apply()
        }

    // Premium status (purchased or within 7-day free trial)
    var isPremiumPurchased: Boolean
        get() = prefs.getBoolean(KEY_IS_PREMIUM_PURCHASED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_IS_PREMIUM_PURCHASED, value).apply()
        }

    // Trial start time (long timestamp)
    var trialStartTime: Long
        get() {
            var time = prefs.getLong(KEY_TRIAL_START_TIME, 0L)
            if (time == 0L) {
                time = System.currentTimeMillis()
                prefs.edit().putLong(KEY_TRIAL_START_TIME, time).apply()
            }
            return time
        }
        set(value) {
            prefs.edit().putLong(KEY_TRIAL_START_TIME, value).apply()
        }

    fun isTrialActive(): Boolean {
        val startTime = trialStartTime
        val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
        return System.currentTimeMillis() - startTime < sevenDaysInMillis
    }

    fun getTrialTimeLeftMillis(): Long {
        val startTime = trialStartTime
        val elapsed = System.currentTimeMillis() - startTime
        val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
        val remaining = sevenDaysInMillis - elapsed
        return if (remaining < 0L) 0L else remaining
    }

    val isPremium: Boolean
        get() = true

    // Unlocked Premium Items
    var unlockedAvatars: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_AVATARS, setOf("girl", "boy")) ?: setOf("girl", "boy")
        set(value) {
            prefs.edit().putStringSet(KEY_UNLOCKED_AVATARS, value).apply()
        }

    var unlockedThemes: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_THEMES, setOf("default", "jungle")) ?: setOf("default", "jungle")
        set(value) {
            prefs.edit().putStringSet(KEY_UNLOCKED_THEMES, value).apply()
        }

    var currentTheme: String
        get() = prefs.getString(KEY_CURRENT_THEME, "jungle") ?: "jungle"
        set(value) = prefs.edit().putString(KEY_CURRENT_THEME, value).apply()

    var boughtCertificates: Set<String>
        get() = prefs.getStringSet(KEY_BOUGHT_CERTIFICATES, emptySet()) ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_BOUGHT_CERTIFICATES, value).apply()
        }

    fun unlockAvatar(avatarId: String) {
        val current = unlockedAvatars.toMutableSet()
        current.add(avatarId)
        unlockedAvatars = current
    }

    fun unlockTheme(themeId: String) {
        val current = unlockedThemes.toMutableSet()
        current.add(themeId)
        unlockedThemes = current
    }

    fun unlockCertificate(courseId: String) {
        val current = boughtCertificates.toMutableSet()
        current.add(courseId)
        boughtCertificates = current
    }

    var lastLoginDate: Long
        get() = prefs.getLong("last_login_date", 0L)
        set(value) = prefs.edit().putLong("last_login_date", value).apply()

    var lastChallengeDate: Long
        get() = prefs.getLong("last_challenge_date", 0L)
        set(value) = prefs.edit().putLong("last_challenge_date", value).apply()

    var lastAiQuestionDate: Long
        get() = prefs.getLong("last_ai_question_date", 0L)
        set(value) = prefs.edit().putLong("last_ai_question_date", value).apply()

    var aiQuestionsCountToday: Int
        get() = prefs.getInt("ai_questions_count_today", 0)
        set(value) = prefs.edit().putInt("ai_questions_count_today", value).apply()

    fun earnDiamonds(amount: Int) {
        diamonds += amount
    }

    fun spendDiamonds(amount: Int): Boolean {
        if (diamonds >= amount) {
            diamonds -= amount
            return true
        }
        return false
    }

    fun buyStarsWithDiamonds(starCount: Int, diamondCost: Int): Boolean {
        if (diamonds >= diamondCost) {
            diamonds -= diamondCost
            totalStars += starCount
            return true
        }
        return false
    }

    fun deductStars(amount: Int): Boolean {
        if (totalStars >= amount) {
            totalStars -= amount
            return true
        }
        return false
    }

    var currentScreen: String
        get() = prefs.getString(KEY_CURRENT_SCREEN, "welcome") ?: "welcome"
        set(value) {
            prefs.edit().putString(KEY_CURRENT_SCREEN, value).apply()
        }

    var activeCategory: String
        get() = prefs.getString(KEY_ACTIVE_CATEGORY, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_ACTIVE_CATEGORY, value).apply()
        }

    var activeItemIndex: Int
        get() = prefs.getInt(KEY_ACTIVE_ITEM_INDEX, 0)
        set(value) {
            prefs.edit().putInt(KEY_ACTIVE_ITEM_INDEX, value).apply()
        }

    var selectedLanguage: String
        get() = prefs.getString(KEY_SELECTED_LANGUAGE, "") ?: ""
        set(value) {
            prefs.edit().putString(KEY_SELECTED_LANGUAGE, value).apply()
        }

    var isDarkMode: Boolean?
        get() = if (prefs.contains(KEY_DARK_MODE)) prefs.getBoolean(KEY_DARK_MODE, false) else null
        set(value) {
            if (value == null) {
                prefs.edit().remove(KEY_DARK_MODE).apply()
            } else {
                prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()
            }
        }

    var isVoiceOn: Boolean
        get() = prefs.getBoolean(KEY_VOICE_ON, true)
        set(value) {
            prefs.edit().putBoolean(KEY_VOICE_ON, value).apply()
        }

    var useWebSpeechApi: Boolean
        get() = prefs.getBoolean(KEY_USE_WEB_SPEECH_API, false)
        set(value) {
            prefs.edit().putBoolean(KEY_USE_WEB_SPEECH_API, value).apply()
        }

    var voiceSpeed: Float
        get() = prefs.getFloat(KEY_VOICE_SPEED, 1.0f)
        set(value) {
            prefs.edit().putFloat(KEY_VOICE_SPEED, value).apply()
        }

    var isBgmOn: Boolean
        get() = prefs.getBoolean(KEY_BGM_ON, false)
        set(value) {
            prefs.edit().putBoolean(KEY_BGM_ON, value).apply()
        }

    var voiceCharacter: String
        get() = prefs.getString(KEY_VOICE_CHARACTER, "guru") ?: "guru"
        set(value) {
            prefs.edit().putString(KEY_VOICE_CHARACTER, value).apply()
        }

    var isAvatarEnabled: Boolean
        get() = prefs.getBoolean(KEY_AVATAR_ENABLED, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AVATAR_ENABLED, value).apply()
        }

    var avatarSize: String
        get() = prefs.getString(KEY_AVATAR_SIZE, "medium") ?: "medium"
        set(value) {
            prefs.edit().putString(KEY_AVATAR_SIZE, value).apply()
        }

    var avatarPosition: String
        get() = prefs.getString(KEY_AVATAR_POSITION, "right") ?: "right"
        set(value) {
            prefs.edit().putString(KEY_AVATAR_POSITION, value).apply()
        }

    var isAvatarMuted: Boolean
        get() = prefs.getBoolean(KEY_AVATAR_MUTED, false)
        set(value) {
            prefs.edit().putBoolean(KEY_AVATAR_MUTED, value).apply()
        }

    var isAvatarFemale: Boolean
        get() = prefs.getBoolean(KEY_AVATAR_FEMALE, true)
        set(value) {
            prefs.edit().putBoolean(KEY_AVATAR_FEMALE, value).apply()
        }

    var avatarType: String
        get() = prefs.getString(KEY_AVATAR_TYPE, "girl") ?: "girl"
        set(value) {
            prefs.edit().putString(KEY_AVATAR_TYPE, value).apply()
        }

    var totalStars: Int
        get() = prefs.getInt(KEY_STARS_TOTAL, 0)
        set(value) {
            prefs.edit().putInt(KEY_STARS_TOTAL, value).apply()
        }

    var dailyStreak: Int
        get() = prefs.getInt("daily_streak", 1)
        set(value) {
            prefs.edit().putInt("daily_streak", value).apply()
        }

    var lastGoalResetTime: Long
        get() = prefs.getLong("last_goal_reset_time", 0L)
        set(value) = prefs.edit().putLong("last_goal_reset_time", value).apply()

    var lessonsCompletedToday: Int
        get() {
            checkAndResetDailyGoal()
            return prefs.getInt("lessons_completed_today", 0)
        }
        set(value) {
            prefs.edit().putInt("lessons_completed_today", value).apply()
        }

    var dailyGoalTarget: Int
        get() = prefs.getInt("daily_goal_target", 2)
        set(value) = prefs.edit().putInt("daily_goal_target", value).apply()

    fun checkAndResetDailyGoal() {
        val now = System.currentTimeMillis()
        val lastReset = prefs.getLong("last_goal_reset_time", 0L)
        val oneDayMillis = 24L * 60 * 60 * 1000
        if (lastReset == 0L || (now - lastReset) >= oneDayMillis) {
            prefs.edit()
                .putInt("lessons_completed_today", 0)
                .putLong("last_goal_reset_time", now)
                .apply()
        }
    }

    fun isLessonCompleted(language: String, category: LessonCategory): Boolean {
        return prefs.getBoolean("$PREFIX_LESSON_COMPLETED${language}_${category.name}", false)
    }

    fun getLessonStars(language: String, category: LessonCategory): Int {
        return prefs.getInt("$PREFIX_LESSON_STARS${language}_${category.name}", 0)
    }

    fun completeLesson(language: String, category: LessonCategory, starsAwarded: Int) {
        val wasCompleted = isLessonCompleted(language, category)
        val previousStars = getLessonStars(language, category)

        // Increment lessons completed today
        lessonsCompletedToday += 1

        prefs.edit().apply {
            putBoolean("$PREFIX_LESSON_COMPLETED${language}_${category.name}", true)
            if (starsAwarded > previousStars) {
                putInt("$PREFIX_LESSON_STARS${language}_${category.name}", starsAwarded)
                // Update total stars
                val difference = starsAwarded - previousStars
                putInt(KEY_STARS_TOTAL, totalStars + difference)
            }
        }.apply()
    }

    fun earnMiniGameStar() {
        val current = totalStars
        prefs.edit().putInt(KEY_STARS_TOTAL, current + 1).apply()
    }

    fun getEarnedBadges(): List<Badge> {
        val badges = mutableListOf<Badge>()
        val totalCompletedCount = LessonCategory.values().count { cat ->
            LanguageData.languages.any { lang -> isLessonCompleted(lang.code, cat) }
        }

        // Add badges based on criteria
        badges.add(
            Badge(
                id = "first_step",
                title = "First Step!",
                description = "Started your learning journey!",
                icon = "🌱",
                unlocked = totalStars > 0
            )
        )

        // Check if finished any Letters lesson
        val hasCompletedLetters = LanguageData.languages.any { isLessonCompleted(it.code, LessonCategory.LETTERS) }
        badges.add(
            Badge(
                id = "alphabet_master",
                title = "Alphabet Master",
                description = "Completed any Letters lesson!",
                icon = "🅰️",
                unlocked = hasCompletedLetters
            )
        )

        // Check if finished any Numbers lesson
        val hasCompletedNumbers = LanguageData.languages.any { isLessonCompleted(it.code, LessonCategory.NUMBERS) }
        badges.add(
            Badge(
                id = "math_genius",
                title = "Number Whiz",
                description = "Completed any Numbers lesson!",
                icon = "🔢",
                unlocked = hasCompletedNumbers
            )
        )

        // Check if finished any Colors lesson
        val hasCompletedSpices = LanguageData.languages.any { isLessonCompleted(it.code, LessonCategory.SPICES) }
        badges.add(
            Badge(
                id = "spice_explorer",
                title = "Color Explorer",
                description = "Completed any Colors lesson!",
                icon = "🌈",
                unlocked = hasCompletedSpices
            )
        )

        // Check if finished any Months lesson
        val hasCompletedMonths = LanguageData.languages.any { isLessonCompleted(it.code, LessonCategory.MONTHS) }
        badges.add(
            Badge(
                id = "calendar_whiz",
                title = "Calendar Whiz",
                description = "Completed any Months lesson!",
                icon = "📅",
                unlocked = hasCompletedMonths
            )
        )

        // Super Badge: Multilingual Hero (completed lessons in 3+ different languages)
        val completedLanguagesCount = LanguageData.languages.count { lang ->
            LessonCategory.values().any { cat -> isLessonCompleted(lang.code, cat) }
        }
        badges.add(
            Badge(
                id = "multilingual_hero",
                title = "Language Hero",
                description = "Learned words in 3 or more languages!",
                icon = "🏆",
                unlocked = completedLanguagesCount >= 3
            )
        )

        // Step-specific gold medal completion badges
        LessonCategory.values().forEach { category ->
            val nameFormatted = category.name.lowercase().split('_').joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
            val title = "$nameFormatted Master 🏅"
            val isCatCompleted = LanguageData.languages.any { lang -> isLessonCompleted(lang.code, category) }
            badges.add(
                Badge(
                    id = "step_${category.name.lowercase()}",
                    title = title,
                    description = "Completed the $nameFormatted learning path step!",
                    icon = "🏅",
                    unlocked = isCatCompleted
                )
            )
        }

        return badges
    }

    fun getWeakWords(): Set<String> {
        return prefs.getStringSet("weak_words", emptySet()) ?: emptySet()
    }

    fun addWeakWord(word: String) {
        val current = getWeakWords().toMutableSet()
        current.add(word)
        prefs.edit().putStringSet("weak_words", current).apply()
    }

    fun removeWeakWord(word: String) {
        val current = getWeakWords().toMutableSet()
        current.remove(word)
        prefs.edit().putStringSet("weak_words", current).apply()
    }

    fun clearWeakWords() {
        prefs.edit().remove("weak_words").apply()
    }

    var profileName: String
        get() = prefs.getString(KEY_PROFILE_NAME, "Chintu") ?: "Chintu"
        set(value) = prefs.edit().putString(KEY_PROFILE_NAME, value).apply()

    var profileImageUri: String
        get() = prefs.getString(KEY_PROFILE_IMAGE_URI, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PROFILE_IMAGE_URI, value).apply()

    fun resetProgress() {
        val editor = prefs.edit()
        // Save preferences like dark mode, music, etc., but clear learning progress and selected language
        val darkModeValue = isDarkMode
        val voiceOnValue = isVoiceOn
        val useWebSpeechValue = useWebSpeechApi
        val voiceSpeedValue = voiceSpeed
        val bgmOnValue = isBgmOn
        val voiceCharacterValue = voiceCharacter
        val isAvatarEnabledVal = isAvatarEnabled
        val avatarSizeVal = avatarSize
        val avatarPositionVal = avatarPosition
        val isAvatarMutedVal = isAvatarMuted
        val isAvatarFemaleVal = isAvatarFemale
        val avatarTypeVal = avatarType
        val profileNameVal = profileName
        val profileImageUriVal = profileImageUri

        editor.clear().apply()

        // Restore UI preferences
        isDarkMode = darkModeValue
        isVoiceOn = voiceOnValue
        useWebSpeechApi = useWebSpeechValue
        voiceSpeed = voiceSpeedValue
        isBgmOn = bgmOnValue
        voiceCharacter = voiceCharacterValue
        isAvatarEnabled = isAvatarEnabledVal
        avatarSize = avatarSizeVal
        avatarPosition = avatarPositionVal
        isAvatarMuted = isAvatarMutedVal
        isAvatarFemale = isAvatarFemaleVal
        avatarType = avatarTypeVal
        profileName = profileNameVal
        profileImageUri = profileImageUriVal
    }
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val unlocked: Boolean
)
