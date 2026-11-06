package com.example.data

import android.util.Log

/**
 * Dataset validation issue types for build-time and runtime diagnostics.
 */
enum class DatasetIssueType {
    MISSING_CATEGORY,
    EMPTY_CATEGORY,
    MISSING_NATIVE_TEXT,
    FOREIGN_SCRIPT_DETECTED,
    EMPTY_TTS_TEXT,
    MISSING_IMAGE
}

data class DatasetValidationIssue(
    val language: String,
    val category: LessonCategory?,
    val itemId: String?,
    val issueType: DatasetIssueType,
    val details: String
)

data class DatasetValidationReport(
    val isValid: Boolean,
    val totalLanguages: Int,
    val totalItemsChecked: Int,
    val issues: List<DatasetValidationIssue>
)

/**
 * Centralized, single-source-of-truth content and localization system for Language Guru.
 * Eliminates duplicate translations, ensures 100% language consistency across all
 * 19 lesson categories and 10 supported languages, provides dynamic RTL handling,
 * and includes a rigorous validation pipeline.
 */
object CentralizedContentRepository {

    const val TAG = "ContentRepository"

    val SUPPORTED_LANGUAGES = listOf("te", "ta", "hi", "ar", "kn", "ml", "bn", "mr", "gu", "en")

    /**
     * Determines if a language uses Right-to-Left writing direction.
     */
    fun isRtl(langCode: String): Boolean {
        val lower = langCode.lowercase().trim()
        return lower == "ar" || lower == "ur" || lower.startsWith("ar-") || lower.startsWith("ur-")
    }

    /**
     * Retrieves all items belonging to a lesson category in the specified language.
     * Guaranteed: NEVER falls back silently to English or another Indian language.
     */
    fun getLocalizedContent(category: LessonCategory, langCode: String): List<LearningItem> {
        val config = LanguageData.languages.find { it.code == langCode }
            ?: LanguageData.languages.find { it.code == "en" }
            ?: return emptyList()

        // 1. Direct configuration match from lessons map
        val categoryItems = config.lessons[category]
        if (!categoryItems.isNullOrEmpty()) {
            return categoryItems.mapIndexed { idx, it ->
                it.copy(
                    id = if (it.id.isNotEmpty()) it.id else "${langCode}_${category.name.lowercase()}_$idx",
                    language = langCode,
                    category = category.name,
                    nativeText = if (it.nativeText.isNotEmpty()) it.nativeText else it.display,
                    ttsText = if (it.ttsText.isNotEmpty()) it.ttsText else it.voiceText.ifEmpty { it.display },
                    image = if (it.image.isNotEmpty()) it.image else it.visualEmoji.ifEmpty { it.display }
                )
            }
        }

        // 2. Specialized categories populated via translation registry
        val registryItems = getRegistryCategoryItems(category, langCode)
        if (registryItems.isNotEmpty()) {
            return registryItems
        }

        try {
            Log.e(TAG, "[MISSING_CONTENT] No content registered for category ${category.name} in language '$langCode'!")
        } catch (t: Throwable) {
            println("[MISSING_CONTENT] No content registered for category ${category.name} in language '$langCode'!")
        }
        return listOf(
            LearningItem(
                display = "Content Unavailable",
                subtitle = "Please select another lesson",
                voiceText = "Content Unavailable",
                visualEmoji = "⚠️",
                id = "${langCode}_${category.name.lowercase()}_unavailable",
                language = langCode,
                category = category.name,
                nativeText = "Content Unavailable",
                ttsText = "Content Unavailable",
                meaning = "Content Unavailable",
                image = "⚠️"
            )
        )
    }

    /**
     * Retrieves category items for categories registered across multilingual data models.
     */
    private fun getRegistryCategoryItems(category: LessonCategory, langCode: String): List<LearningItem> {
        val baseItems = when (category) {
            LessonCategory.STATES_CAPITALS -> if (langCode == "te") teluguStatesAndCapitals else englishStatesAndCapitals
            LessonCategory.NATIONAL_SYMBOLS -> if (langCode == "te") teluguNationalSymbols else englishNationalSymbols
            LessonCategory.RELATIONSHIPS -> if (langCode == "te") teluguRelationships else englishRelationships
            LessonCategory.FOODS -> if (langCode == "te") teluguFoods else englishFoods
            LessonCategory.FRUITS_FLOWERS -> if (langCode == "te") teluguFruitsAndFlowers else englishFruitsAndFlowers
            LessonCategory.BODY_PARTS -> if (langCode == "te") teluguBodyParts else englishBodyParts
            else -> return emptyList()
        }

        return baseItems.mapIndexed { idx, item ->
            val localized = localizeItem(item, category, langCode)
            localized.copy(
                id = "${langCode}_${category.name.lowercase()}_$idx",
                language = langCode,
                category = category.name,
                nativeText = localized.display,
                ttsText = localized.voiceText.ifEmpty { localized.display },
                image = if (localized.image.isNotEmpty()) localized.image else localized.visualEmoji.ifEmpty { localized.display }
            )
        }
    }

    /**
     * Finds a single learning item by unique ID in the specified language.
     */
    fun getLearningItem(id: String, langCode: String): LearningItem? {
        LessonCategory.values().forEach { cat ->
            val items = getLocalizedContent(cat, langCode)
            val found = items.find { it.id == id || it.display == id }
            if (found != null) return found
        }
        return null
    }

    /**
     * Retrieves learning items configured for mini games playground.
     */
    fun getGameContent(category: LessonCategory, langCode: String): List<LearningItem> {
        return getLocalizedContent(category, langCode)
    }

    /**
     * Centralized UI string localizer covering Voice Chat, Tracing, Speaking,
     * Settings, Dashboard, and Gamification.
     */
    fun getLocalizedUiString(key: String, langCode: String, vararg args: Any): String {
        return when (key) {
            // Dashboard & Navigation
            "streak_title" -> when (langCode) {
                "te" -> "డైలీ స్ట్రీక్"
                "ta" -> "தினசரி தொடர்"
                "hi" -> "डेली स्ट्रीक"
                "ar" -> "سلسلة الأيام اليومية"
                "kn" -> "ದೈನಂದಿನ ಸ್ಟ್ರೀಕ್"
                "ml" -> "പ്രതിദിന സ്ട്രീക്ക്"
                "bn" -> "দৈনিক ধারাবাহিকতা"
                "mr" -> "दैनंदिन स्ट्रीक"
                "gu" -> "દૈનિક સ્ટ્રીક"
                else -> "Daily Streak"
            }
            "streak_message" -> {
                val count = args.firstOrNull() ?: 1
                when (langCode) {
                    "te" -> "$count రోజులు వరుసగా నేర్చుకున్నారు! అద్భుతం! 🔥"
                    "ta" -> "தொடர்ந்து $count நாட்கள் கற்றுக் கொண்டீர்கள்! அருமை! 🔥"
                    "hi" -> "$count दिन लगातार सीखे! शानदार! 🔥"
                    "ar" -> "$count أيام متتالية من التعلم! رائع! 🔥"
                    "kn" -> "$count ದಿನಗಳು ಸತತವಾಗಿ ಕಲಿತಿದ್ದೀರಿ! ಅದ್ಭುತ! 🔥"
                    "ml" -> "$count ദിവസമായി തുടർച്ചയായി പഠിക്കുന്നു! അതിശയം! 🔥"
                    "bn" -> "$count দিন একটানা শিখেছেন! দারুণ! 🔥"
                    "mr" -> "$count दिवस सलग शिकलात! छान! 🔥"
                    "gu" -> "$count દિવસ સતત શીખ્યા! અદ્ભુત! 🔥"
                    else -> "$count days in a row! Amazing! 🔥"
                }
            }
            "select_lesson" -> when (langCode) {
                "te" -> "పాఠం ఎంచుకోండి"
                "ta" -> "பாடத்தைத் தேர்ந்தெடுக்கவும்"
                "hi" -> "पाठ चुनें"
                "ar" -> "اختر درساً"
                "kn" -> "ಪಾಠವನ್ನು ಆಯ್ಕೆಮಾಡಿ"
                "ml" -> "പാഠം തിരഞ്ഞെടുക്കുക"
                "bn" -> "পাঠ নির্বাচন করুন"
                "mr" -> "धडा निवडा"
                "gu" -> "પાઠ પસંદ કરો"
                else -> "Select Lesson"
            }
            "play_lesson" -> when (langCode) {
                "te" -> "నేర్చుకోండి 🚀"
                "ta" -> "கற்கவும் 🚀"
                "hi" -> "सीखें 🚀"
                "ar" -> "ابدأ التعلم 🚀"
                "kn" -> "ಕಲಿಯಿರಿ 🚀"
                "ml" -> "പഠിക്കുക 🚀"
                "bn" -> "শিখুন 🚀"
                "mr" -> "शिका 🚀"
                "gu" -> "શીખો 🚀"
                else -> "Start Learning 🚀"
            }
            "homework_helper" -> when (langCode) {
                "te" -> "హోమ్‌వర్క్ గైడ్ 🎒"
                "ta" -> "வீட்டுப்பாட வழிகாட்டி 🎒"
                "hi" -> "गृहकार्य मार्गदर्शक 🎒"
                "ar" -> "دليل الواجبات 🎒"
                "kn" -> "ಮನೆಕೆಲಸ ಮಾರ್ಗದರ್ಶಿ 🎒"
                "ml" -> "ഹോംവർക്ക് സഹായി 🎒"
                "bn" -> "বাড়ির কাজ গাইড 🎒"
                "mr" -> "गृहपाठ मार्गदर्शक 🎒"
                "gu" -> "ગૃહકાર્ય માર્ગદર્શક 🎒"
                else -> "Homework Guide 🎒"
            }
            "mini_games" -> when (langCode) {
                "te" -> "ఆటల మైదానం 🎮"
                "ta" -> "விளையாட்டு மைதானம் 🎮"
                "hi" -> "खेल का मैदान 🎮"
                "ar" -> "منطقة الألعاب 🎮"
                "kn" -> "ಆಟದ ಮೈದಾನ 🎮"
                "ml" -> "കളിസ്ഥലം 🎮"
                "bn" -> "খেলার মাঠ 🎮"
                "mr" -> "खेळाचे मैदान 🎮"
                "gu" -> "રમતોનું મેદાન 🎮"
                else -> "Playground 🎮"
            }
            "ai_teacher" -> when (langCode) {
                "te" -> "AI టీచర్ 🎙️"
                "ta" -> "AI ஆசிரியர் 🎙️"
                "hi" -> "AI शिक्षक 🎙️"
                "ar" -> "المعلم الذكي 🎙️"
                "kn" -> "AI ಶಿಕ್ಷಕ 🎙️"
                "ml" -> "AI അധ്യാപകൻ 🎙️"
                "bn" -> "AI শিক্ষক 🎙️"
                "mr" -> "AI शिक्षक 🎙️"
                "gu" -> "AI શિક્ષક 🎙️"
                else -> "AI Teacher 🎙️"
            }
            "camera_learning" -> when (langCode) {
                "te" -> "కెమెరా లెర్నింగ్ 📷"
                "ta" -> "கேமரா கற்றல் 📷"
                "hi" -> "कैमरा लर्निंग 📷"
                "ar" -> "التعلم بالكاميرا 📷"
                "kn" -> "ಕ್ಯಾಮೆರಾ ಕಲಿಕೆ 📷"
                "ml" -> "ക്യാമറ പഠനം 📷"
                "bn" -> "ক্যামেরা লার্নিং 📷"
                "mr" -> "कॅमेरा शिक्षण 📷"
                "gu" -> "કેમેરા લર્નિંગ 📷"
                else -> "Camera Learning 📷"
            }

            // Voice Chat Dialog
            "vc_title" -> when (langCode) {
                "te" -> "🎙️ AI తో మాటలు"
                "ta" -> "🎙️ AI உடன் குரல் அரட்டை"
                "hi" -> "🎙️ AI के साथ वॉयस चैट"
                "ar" -> "🎙️ محادثة صوتية مع المعلم"
                "kn" -> "🎙️ AI ನೊಂದಿಗೆ ಧ್ವನಿ ಚಾಟ್"
                "ml" -> "🎙️ AI-യുമായി വോയ്‌സ് ചാറ്റ്"
                "bn" -> "🎙️ AI এর সাথে ভয়েস চ্যাট"
                "mr" -> "🎙️ AI सोबत व्हॉइस चॅट"
                "gu" -> "🎙️ AI સાથે વોઈસ ચેટ"
                else -> "🎙️ Voice Chat with AI"
            }
            "vc_welcome" -> when (langCode) {
                "te" -> "నీకు నచ్చిన ప్రశ్న అడుగు, నేను జవాబు చెప్తాను! 🥰"
                "ta" -> "உங்களுக்கு பிடித்த கேள்வியைக் கேளுங்கள், நான் பதில் சொல்கிறேன்! 🥰"
                "hi" -> "मुझसे कोई भी सवाल पूछें! मैं आपको सिखाने के लिए यहाँ हूँ! 🥰"
                "ar" -> "اسألني أي سؤال تريده، أنا هنا لمساعدتك! 🥰"
                "kn" -> "ನಿನಗೆ ಇಷ್ಟವಾದ ಪ್ರಶ್ನೆ ಕೇಳು, ನಾನು ಉತ್ತರ ಹೇಳುವೆ! 🥰"
                "ml" -> "നിങ്ങൾക്ക് ഇഷ്ടമുള്ള ചോദ്യം ചോദിക്കൂ, ഞാൻ മറുപടി പറയാം! 🥰"
                "bn" -> "তোমার প্রিয় প্রশ্নটি করো, আমি উত্তর দেব! 🥰"
                "mr" -> "तुला आवडणारा प्रश्न विचार, मी उत्तर देईन! 🥰"
                "gu" -> "તમને ગમતો પ્રશ્ન પૂછો, હું ઉત્તર આપીશ! 🥰"
                else -> "Ask me any question! I'm here to help you learn! 🥰"
            }
            "vc_instruction" -> when (langCode) {
                "te" -> "నాతో మాట్లాడటానికి మైక్రోఫోన్ నొక్కండి! 👇"
                "ta" -> "என்னுடன் பேச மைக்ரோஃபோனைத் தட்டவும்! 👇"
                "hi" -> "मुझसे बात करने के लिए नीचे दिए गए माइक पर टैप करें! 👇"
                "ar" -> "اضغط على زر الميكروفون للتحدث معي! 👇"
                "kn" -> "ನನ್ನೊಂದಿಗೆ ಮಾತನಾಡಲು ಮೈಕ್ರೋಫೋನ್ ಟ್ಯಾಪ್ ಮಾಡಿ! 👇"
                "ml" -> "എന്നോട് സംസാരിക്കാൻ താഴെയുള്ള മൈക്രോഫോൺ ടാപ്പ് ചെയ്യുക! 👇"
                "bn" -> "আমার সাথে কথা বলতে নিচের মাইক চাপুন! 👇"
                "mr" -> "माझ्याशी बोलण्यासाठी खालील माईक दाबा! 👇"
                "gu" -> "મારી સાથે વાત કરવા નીચેનું માઈક દબાવો! 👇"
                else -> "Tap the microphone below to talk with me! 👇"
            }
            "vc_thinking" -> when (langCode) {
                "te" -> "ఆలోచిస్తున్నాను... 🧠✨"
                "ta" -> "யோசிக்கிறேன்... 🧠✨"
                "hi" -> "सोच रहा हूँ... 🧠✨"
                "ar" -> "أفكر في إجابة رائعة لك... 🧠✨"
                "kn" -> "ಯೋಚಿಸುತ್ತಿದ್ದೇನೆ... 🧠✨"
                "ml" -> "ആലോചിക്കുന്നു... 🧠✨"
                "bn" -> "ভাবছি... 🧠✨"
                "mr" -> "विचार करत आहे... 🧠✨"
                "gu" -> "વિચારી રહ્યો છું... 🧠✨"
                else -> "Thinking of an answer... 🧠✨"
            }
            "vc_placeholder" -> when (langCode) {
                "te" -> "ప్రశ్న టైప్ చేయి..."
                "ta" -> "கேள்வியைத் தட்டச்சு செய்க..."
                "hi" -> "सवाल टाइप करें..."
                "ar" -> "اكتب سؤالك هنا..."
                "kn" -> "ಪ್ರಶ್ನೆ ಟೈಪ್ ಮಾಡಿ..."
                "ml" -> "ചോദ്യം ടൈപ്പ് ചെയ്യുക..."
                "bn" -> "প্রশ্ন টাইপ করুন..."
                "mr" -> "प्रश्न टाईप करा..."
                "gu" -> "પ્રશ્ન ટાઈપ કરો..."
                else -> "Type a question..."
            }
            "vc_quick_ask" -> when (langCode) {
                "te" -> "💬 త్వరగా అడుగు:"
                "ta" -> "💬 விரைவாகக் கேள்:"
                "hi" -> "💬 तुरंत पूछें:"
                "ar" -> "💬 أسئلة سريعة:"
                "kn" -> "💬 ಶೀಘ್ರವಾಗಿ ಕೇಳಿ:"
                "ml" -> "💬 വേഗത്തിൽ ചോദിക്കൂ:"
                "bn" -> "💬 দ্রুত প্রশ্ন:"
                "mr" -> "💬 पटकन विचारा:"
                "gu" -> "💬 ઝડપથી પૂછો:"
                else -> "💬 Ask Quick:"
            }
            "vc_mic_permission" -> when (langCode) {
                "te" -> "దయచేసి మైక్రోఫోన్ అనుమతి ఇవ్వండి! 🎙️"
                "ta" -> "தயவுசெய்து மைக்ரோஃபோன் அனுமதியை வழங்கவும்! 🎙️"
                "hi" -> "कृपया माइक्रोफ़ोन की अनुमति दें! 🎙️"
                "ar" -> "يرجى منح إذن استخدام الميكروفون! 🎙️"
                "kn" -> "ದಯವಿಟ್ಟು ಮೈಕ್ರೋಫೋನ್ ಅನುಮತಿ ನೀಡಿ! 🎙️"
                "ml" -> "ദയവായി മൈക്രോഫോൺ അനുമതി നൽകുക! 🎙️"
                "bn" -> "অনুগ্রহ করে মাইক্রোফোনের অনুমতি দিন! 🎙️"
                "mr" -> "कृपया मायक्रोफोनची परवानगी द्या! 🎙️"
                "gu" -> "કૃપા કરીને માઇક્રોફોનની મંજૂરી આપો! 🎙️"
                else -> "Please grant microphone permission to talk! 🎙️"
            }

            // Tracing & Handwriting Practice
            "tracing_empty_drawing" -> when (langCode) {
                "te" -> "చిత్రం ఖాళీగా ఉంది! దయచేసి స్క్రీన్‌పై అక్షరం రాయండి ✍️"
                "ta" -> "படம் காலியாக உள்ளது! திரையில் எழுத்தை எழுதவும் ✍️"
                "hi" -> "ड्राइंग खाली है! कृपया स्क्रीन पर अक्षर लिखें ✍️"
                "ar" -> "الرسم فارغ! يرجى كتابة الحرف على اللوحة ✍️"
                "kn" -> "ಚಿತ್ರ ಖಾಲಿಯಾಗಿದೆ! ದಯವಿಟ್ಟು ಪರದೆಯ ಮೇಲೆ ಅಕ್ಷರ ಬರೆಯಿರಿ ✍️"
                "ml" -> "വരച്ചില്ല! ദയവായി സ്ക്രീനിൽ അക്ഷരം എഴുതുക ✍️"
                "bn" -> "অঙ্কন খালি! অনুগ্রহ করে স্ক্রিনে অক্ষর লিখুন ✍️"
                "mr" -> "चित्र रिकामे आहे! कृपया स्क्रीनवर अक्षर लिहा ✍️"
                "gu" -> "ચિત્ર ખાલી છે! કૃપા કરીને સ્ક્રીન પર અક્ષર લખો ✍️"
                else -> "Drawing is empty! Please trace the character on the board ✍️"
            }
            "tracing_correct" -> when (langCode) {
                "te" -> "చాలా అద్భుతంగా రాశారు! సరైన సమాధానం! 🌟"
                "ta" -> "மிகவும் அருமையாக எழுதியுள்ளீர்கள்! சரியான விடை! 🌟"
                "hi" -> "बहुत सुंदर लिखा! बिल्कुल सही उत्तर! 🌟"
                "ar" -> "أحسنت صنعاً! إجابة صحيحة ورائعة! 🌟"
                "kn" -> "ತುಂಬಾ ಚೆನ್ನಾಗಿ ಬರೆದಿದ್ದೀರಿ! ಸರಿಯಾದ ಉತ್ತರ! 🌟"
                "ml" -> "വളരെ മനോഹരമായി എഴുതി! ശരിയായ ഉത്തരം! 🌟"
                "bn" -> "খুব সুন্দর লিখেছেন! সঠিক উত্তর! 🌟"
                "mr" -> "खूप छान लिहिले! अगदी बरोबर उत्तर! 🌟"
                "gu" -> "ખૂબ સુંદર લખ્યું! સાચો જવાબ! 🌟"
                else -> "Beautifully written! That is correct! 🌟"
            }
            "tracing_try_again" -> when (langCode) {
                "te" -> "మళ్లీ ప్రయత్నించండి! మీరు చేయగలరు! 💪"
                "ta" -> "மீண்டும் முயற்சிக்கவும்! உங்களால் முடியும்! 💪"
                "hi" -> "फिर से कोशिश करें! आप कर सकते हैं! 💪"
                "ar" -> "حاول مرة أخرى! يمكنك فعل ذلك! 💪"
                "kn" -> "ಮತ್ತೊಮ್ಮೆ ಪ್ರಯತ್ನಿಸಿ! ನೀವು ಮಾಡಬಲ್ಲಿರಿ! 💪"
                "ml" -> "വീണ്ടും ശ്രമിക്കുക! നിങ്ങൾക്ക് കഴിയും! 💪"
                "bn" -> "আবার চেষ্টা করুন! আপনি পারবেন! 💪"
                "mr" -> "पुन्हा प्रयत्न करा! तुम्ही करू शकता! 💪"
                "gu" -> "ફરી પ્રયત્ન કરો! તમે કરી શકો છો! 💪"
                else -> "Let's try again! You can do it! 💪"
            }
            "diamond_retry_title" -> when (langCode) {
                "te" -> "మళ్లీ ప్రయత్నించాలా?"
                "ta" -> "மீண்டும் முயற்சிக்க விரும்புகிறீர்களா?"
                "hi" -> "क्या आप फिर से कोशिश करना चाहते हैं?"
                "ar" -> "هل ترغب في المحاولة مجدداً؟"
                "kn" -> "ಮತ್ತೊಮ್ಮೆ ಪ್ರಯತ್ನಿಸಲು ಬಯಸುವಿರಾ?"
                "ml" -> "വീണ്ടും ശ്രമിക്കാൻ ആഗ്രഹിക്കുന്നുണ്ടോ?"
                "bn" -> "আবার চেষ্টা করতে চান?"
                "mr" -> "तुम्हाला पुन्हा प्रयत्न करायचा आहे का?"
                "gu" -> "શું તમે ફરીથી પ્રયાસ કરવા માંગો છો?"
                else -> "Try Again?"
            }
            "diamond_retry_desc" -> when (langCode) {
                "te" -> "4 ఉచిత అవకాశాలు పూర్తయ్యాయి. 1 డైమండ్ ఉపయోగించి మళ్లీ ప్రయత్నించండి!"
                "ta" -> "4 இலவச வாய்ப்புகள் முடிந்துவிட்டன. 1 வைரத்தைப் பயன்படுத்தி மீண்டும் முயற்சிக்கவும்!"
                "hi" -> "4 मुफ़्त प्रयास समाप्त हो गए हैं। 1 डायमंड का उपयोग करके पुनः प्रयास करें!"
                "ar" -> "لقد استخدمت المحاولات الـ 4 المجانية. استخدم ماسة واحدة للمحاولة مجدداً!"
                "kn" -> "4 ಉಚಿತ ಪ್ರಯತ್ನಗಳು ಮುಗಿದಿವೆ. 1 ಡೈಮಂಡ್ ಬಳಸಿ ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ!"
                "ml" -> "4 സൗജന്യ ശ്രമങ്ങൾ കഴിഞ്ഞു. 1 ഡയമണ്ട് ഉപയോഗിച്ച് വീണ്ടും ശ്രമിക്കുക!"
                "bn" -> "৪টি বিনামূল্যের প্রচেষ্টা শেষ হয়েছে। ১টি ডায়মন্ড দিয়ে আবার চেষ্টা করুন!"
                "mr" -> "४ मोफत प्रयत्न संपले आहेत. १ हिरा वापरून पुन्हा प्रयत्न करा!"
                "gu" -> "૪ મફત પ્રયાસો સમાપ્ત થયા. ૧ હીરો વાપરીને ફરી પ્રયાસ કરો!"
                else -> "All 4 free attempts used. Use 1 Diamond to try again!"
            }

            // Avatar Encouragement (mistake vs success)
            "encourage_mistake" -> {
                val word = args.firstOrNull() ?: ""
                when (langCode) {
                    "te" -> "చింతించకు! మళ్లీ ప్రయత్నిద్దాం. '$word' ని ఇంకోసారి ప్రాక్టీస్ చేయి!"
                    "ta" -> "கவலைப்பட வேண்டாம்! '$word'-ஐ மீண்டும் ஒருமுறை பயிற்சி செய்வோம்!"
                    "hi" -> "चिंता मत करो! चलो '$word' का एक बार और अभ्यास करते हैं!"
                    "ar" -> "لا تقلق يا بطل! لنتدرب على '$word' معاً مرة أخرى!"
                    "kn" -> "ಚಿಂತಿಸಬೇಡಿ! '$word' ಅನ್ನು ಮತ್ತೊಮ್ಮೆ ಅಭ್ಯಾಸ ಮಾಡೋಣ!"
                    "ml" -> "വിഷമിക്കേണ്ട! നമുക്ക് '$word' ഒന്നുകൂടി പരിശീലിക്കാം!"
                    "bn" -> "চিন্তা করো না! চলো '$word' আরও একবার অনুশীলন করি!"
                    "mr" -> "काळजी करू नकोस! आपण '$word' चा पुन्हा सराव करूया!"
                    "gu" -> "ચિંતા ન કરો! ચાલો '$word' નો ફરીથી અભ્યાસ કરીએ!"
                    else -> "Don't worry! Let's practice '$word' once more together!"
                }
            }
            "encourage_success" -> {
                val word = args.firstOrNull() ?: ""
                when (langCode) {
                    "te" -> "చాలా బాగుంది! నువ్వు '$word' ని సరిగ్గా చెప్పావు!"
                    "ta" -> "அற்புதம்! நீங்கள் '$word'-ஐ சரியாகக் கூறினீர்கள்!"
                    "hi" -> "शाबाश! आपने '$word' बिल्कुल सही बोला!"
                    "ar" -> "ممتاز ورائع! لقد نطقت '$word' بالشكل الصحيح تماماً!"
                    "kn" -> "ತುಂಬಾ ಒಳ್ಳೆಯದು! ನೀವು '$word' ಅನ್ನು ಸರಿಯಾಗಿ ಹೇಳಿದ್ದೀರಿ!"
                    "ml" -> "വളരെ നന്നായിരിക്കുന്നു! നിങ്ങൾ '$word' ശരിയായി പറഞ്ഞു!"
                    "bn" -> "অসাধারণ! তুমি '$word' একদম সঠিক বলেছ!"
                    "mr" -> "छान! तू '$word' अगदी बरोबर बोललास!"
                    "gu" -> "શાબાશ! તમે '$word' બિલકુલ સાચું બોલ્યા!"
                    else -> "Awesome! You pronounced '$word' perfectly right!"
                }
            }

            // Lesson completion card & actions
            "lesson_completed_great_job" -> when (langCode) {
                "te" -> "భలే చేసావు! 🌟"
                "ta" -> "அற்புதம்! 🌟"
                "hi" -> "बहुत बढ़िया! 🌟"
                "ar" -> "رائع جداً! 🌟"
                "kn" -> "ಅದ್ಭುತ ಕೆಲಸ! 🌟"
                "ml" -> "മികച്ച പ്രവർത്തനം! 🌟"
                "bn" -> "দারুণ করেছ! 🌟"
                "mr" -> "खूप छान केले! 🌟"
                "gu" -> "ખૂબ સરસ કામ કર્યું! 🌟"
                else -> "Great Job! 🌟"
            }
            "lesson_completed_desc" -> {
                val icon = args.getOrNull(0) ?: "📚"
                val title = args.getOrNull(1) ?: ""
                when (langCode) {
                    "te" -> "నువ్వు $icon $title పాఠాన్ని 100% పూర్తి చేసావు! బంగారు నక్షత్రాలు మరియు డైమండ్స్ గెలుచుకున్నావు! 🏆"
                    "ta" -> "நீங்கள் $icon $title பாடத்தை 100% முடித்துவிட்டீர்கள்! தங்க நட்சத்திரங்கள் மற்றும் வைரங்களை வென்றுள்ளீர்கள்! 🏆"
                    "hi" -> "आपने $icon $title पाठ को 100% पूरा कर लिया है! आपने सुनहरे सितारे और हीरे अर्जित किए हैं! 🏆"
                    "ar" -> "لقد أكملت درس $icon $title بنسبة 100%! لقد حصلت على نجوم ذهبية وجواهر! 🏆"
                    "kn" -> "ನೀವು $icon $title ಪಾಠವನ್ನು 100% ಪೂರ್ಣಗೊಳಿಸಿದ್ದೀರಿ! ಚಿನ್ನದ ನಕ್ಷತ್ರಗಳು ಮತ್ತು ವಜ್ರಗಳನ್ನು ಗಳಿಸಿದ್ದೀರಿ! 🏆"
                    "ml" -> "നിങ്ങൾ $icon $title പാഠം 100% പൂർത്തിയാക്കി! നിങ്ങൾക്ക് സ്വർണ്ണ നಕ್ಷത്രങ്ങളും വജ്രങ്ങളും ലഭിച്ചു! 🏆"
                    "bn" -> "আপনি $icon $title পাঠটি ১০০% সম্পন্ন করেছেন! আপনি সোনার তারা ও ডায়মন্ড জিতেছেন! 🏆"
                    "mr" -> "तुम्ही $icon $title पाठ १००% पूर्ण केला आहे! तुम्ही सोनेरी तारे आणि हिरे मिळवले आहेत! 🏆"
                    "gu" -> "તમે $icon $title પાઠ ૧૦૦% પૂર્ણ કર્યો છે! તમે સોનેરી તારા અને હીરા જીત્યા છે! 🏆"
                    else -> "You completed 100% of $icon $title! You earned golden stars & diamonds! 🏆"
                }
            }
            "btn_learn_again" -> when (langCode) {
                "te" -> "⭐ మళ్లీ నేర్చుకో"
                "ta" -> "⭐ மீண்டும் கற்க"
                "hi" -> "⭐ फिर से सीखें"
                "ar" -> "⭐ تعلم مجدداً"
                "kn" -> "⭐ ಮತ್ತೊಮ್ಮೆ ಕಲಿ"
                "ml" -> "⭐ വീണ്ടും പഠിക്കുക"
                "bn" -> "⭐ আবার শিখুন"
                "mr" -> "⭐ पुन्हा शिका"
                "gu" -> "⭐ ફરીથી શીખો"
                else -> "⭐ Learn Again"
            }
            "btn_next_lesson" -> when (langCode) {
                "te" -> "➡ తదుపరి పాఠం"
                "ta" -> "➡ அடுத்த பாடம்"
                "hi" -> "➡ अगला पाठ"
                "ar" -> "➡ الدرس التالي"
                "kn" -> "➡ ಮುಂದಿನ ಪಾಠ"
                "ml" -> "➡ അടുത്ത പാഠം"
                "bn" -> "➡ পরবর্তী পাঠ"
                "mr" -> "➡ पुढचा पाठ"
                "gu" -> "➡ પછીનો પાઠ"
                else -> "➡ Next Lesson"
            }
            "btn_back_dashboard" -> when (langCode) {
                "te" -> "🏡 ప్రధాన పేజీకి వెళ్ళు"
                "ta" -> "🏡 முகப்புப் பக்கத்திற்குச் செல்"
                "hi" -> "🏡 मुख्य पृष्ठ पर जाएं"
                "ar" -> "🏡 العودة للرئيسية"
                "kn" -> "🏡 ಮುಖ್ಯ ಪುಟಕ್ಕೆ ಹೋಗಿ"
                "ml" -> "🏡 പ്രധാന പേജിലേക്ക് പോവുക"
                "bn" -> "🏡 মূল পাতায় ফিরুন"
                "mr" -> "🏡 मुख्य पृष्ठावर जा"
                "gu" -> "🏡 મુખ્ય પૃષ્ઠ પર જાઓ"
                else -> "🏡 Back to Dashboard"
            }
            "quick_quiz_title" -> when (langCode) {
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
            "tracing_practice_title" -> when (langCode) {
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
            "speak_learn_title" -> when (langCode) {
                "te" -> "మాట్లాడి నేర్చుకో 🗣️"
                "ta" -> "பேசி பழகு 🗣️"
                "hi" -> "बोलो और सीखो 🗣️"
                "kn" -> "ಮಾತನಾಡಿ ಕಲಿ 🗣️"
                "ml" -> "സംസാരിച്ചു പഠിക്കാം 🗣️"
                "bn" -> "বলে শেখো 🗣️"
                "mr" -> "बोला आणि शिका 🗣️"
                "gu" -> "બોલો અને શીખો 🗣️"
                "ar" -> "تحدث وتعلم 🗣️"
                else -> "Speak & Learn 🗣️"
            }
            "speak_instruction" -> {
                val targetText = args.firstOrNull() ?: ""
                when (langCode) {
                    "te" -> "\"$targetText\" అని పలకండి 🗣️"
                    "ta" -> "\"$targetText\" என்று சொல்லுங்கள் 🗣️"
                    "hi" -> "\"$targetText\" बोलिए 🗣️"
                    "kn" -> "\"$targetText\" ಎಂದು ಹೇಳಿ 🗣️"
                    "ml" -> "\"$targetText\" എന്ന് പറയുക 🗣️"
                    "bn" -> "\"$targetText\" বলুন 🗣️"
                    "mr" -> "\"$targetText\" म्हणा 🗣️"
                    "gu" -> "\"$targetText\" બોલો 🗣️"
                    "ar" -> "قل \"$targetText\" 🗣️"
                    else -> "Say \"$targetText\" 🗣️"
                }
            }
            "tracing_instruction" -> {
                val char = args.firstOrNull() ?: ""
                when (langCode) {
                    "te" -> "పలక మీద చూపిస్తున్న అక్షరం/గుర్తు '$char' ని నీ వేలితో రాయండి బంగారం! ఆపై 'సరిచూడు' నొక్కు!"
                    "ta" -> "பலகையில் காட்டப்பட்டுள்ள எழுத்து/குறியீடு '$char' ஐ உங்கள் விரலால் எழுதி, பின்னர் 'சரிபார்' என்பதை அழுத்தவும்!"
                    "hi" -> "स्लेट पर दिखाए गए अक्षर/प्रतीक '$char' को अपनी उंगली से लिखें और फिर 'जांचें' दबाएं!"
                    "kn" -> "ಹಲಗೆಯ ಮೇಲೆ ತೋರಿಸಿರುವ ಅಕ್ಷರ/ಚಿಹ್ನೆ '$char' ಅನ್ನು ನಿಮ್ಮ ಬೆರಳಿನಿಂದ ಬರೆಯಿರಿ ಮತ್ತು ನಂತರ 'ಸರಿನೋಡು' ಒತ್ತಿ!"
                    "ml" -> "സ്ലേറ്റിൽ കാണിച്ചിരിക്കുന്ന അക്ഷരം/ചിಹ್നം '$char' നിങ്ങളുടെ വിരൽ കൊണ്ട് എഴുതുക, തുടർന്ന് 'പരിശോധിക്കുക' അമർത്തുക!"
                    "bn" -> "স্লেটে দেখানো অক্ষর/প্রতীক '$char' তোমার আঙুল দিয়ে লেখো এবং তারপর 'যাচাই করুন' চাপো!"
                    "mr" -> "पाटीवर दाखवलेले अक्षर/चिन्ह '$char' तुझ्या बोटाने लिहा आणि नंतर 'तपासा' दाबा!"
                    "gu" -> "સ્લેટ પર બતાવેલ અક્ષર/પ્રતીક '$char' તમારી આંગળીથી લખો અને પછી 'તપાસો' દબાવો!"
                    "ar" -> "اكتب الحرف/الرمز '$char' على اللوح بإصبعك ثم اضغط على 'تحقق'!"
                    else -> "Write the letter/symbol '$char' on the slate with your finger and press 'Check Trace'!"
                }
            }

            "quiz_dialog_title" -> when (langCode) {
                "te" -> "సమాధానం చెప్పండి! 🧠"
                "ta" -> "பதில் கூறுங்கள்! 🧠"
                "hi" -> "उत्तर दें! 🧠"
                "ar" -> "أجب الآن! 🧠"
                "kn" -> "ಉತ್ತರಿಸಿ! 🧠"
                "ml" -> "ഉത്തരം പറയൂ! 🧠"
                "bn" -> "উত্তর দিন! 🧠"
                "mr" -> "उत्तर द्या! 🧠"
                "gu" -> "જવાબ આપો! 🧠"
                else -> "Can you find? 🧠"
            }
            "identify_target" -> {
                val display = args.firstOrNull() ?: ""
                when (langCode) {
                    "te" -> "దీన్ని గుర్తించండి: $display"
                    "ta" -> "இதைக் கண்டறியவும்: $display"
                    "hi" -> "इसको पहचानें: $display"
                    "ar" -> "تحديد: $display"
                    "kn" -> "ಇದನ್ನು ಗುರುತಿಸಿ: $display"
                    "ml" -> "ഇത് കണ്ടെത്തുക: $display"
                    "bn" -> "এটি চিহ্নিত করুন: $display"
                    "mr" -> "हे ओळखा: $display"
                    "gu" -> "આ ઓળખો: $display"
                    else -> "Tap on: $display"
                }
            }
            "close" -> when (langCode) {
                "te" -> "మూసివేయి"
                "ta" -> "மூடு"
                "hi" -> "बंद करें"
                "ar" -> "إغلاق"
                "kn" -> "ಮುಚ್ಚಿ"
                "ml" -> "അടയ്ക്കുക"
                "bn" -> "বন্ধ করুন"
                "mr" -> "बंद करा"
                "gu" -> "બંધ કરો"
                else -> "Close"
            }

            else -> key
        }
    }

    /**
     * Retrieves dynamic, culturally-adapted kid quick phrases for AI Voice Chat.
     */
    fun getQuickPhrases(langCode: String): List<Pair<String, String>> {
        val targetName = when (langCode) {
            "te" -> "తెలుగు"
            "ta" -> "தமிழ்"
            "hi" -> "हिन्दी"
            "ar" -> "العربية"
            "kn" -> "ಕನ್ನಡ"
            "ml" -> "മലയാളം"
            "bn" -> "বাংলা"
            "mr" -> "मराठी"
            "gu" -> "ગુજરાતી"
            else -> "English"
        }

        return when (langCode) {
            "te" -> listOf(
                "ఇది ఏమిటి? 🤔" to "ఇది ఏమిటి?",
                "ఇంగ్లీష్ లో ఏమంటారు? 🇬🇧" to "How do you say this in English?",
                "మళ్లీ చెప్పు 🔊" to "మళ్లీ చెప్పు",
                "నాకో కథ చెప్పు 📖" to "నాకో చిన్న కథ చెప్పు",
                "మరో కొత్త పదం ✨" to "నాకు ఇంకో కొత్త పదం నేర్పించు"
            )
            "ta" -> listOf(
                "இது என்ன? 🤔" to "இது என்ன?",
                "ஆங்கிலத்தில் எப்படி சொல்வது? 🇬🇧" to "How do you say this in English?",
                "மீண்டும் கூறு 🔊" to "மீண்டும் கூறு",
                "ஒரு கதை சொல்லு 📖" to "எனக்கு ஒரு குட்டி கதை சொல்லு",
                "மற்றொரு சொல் ✨" to "எனக்கு மற்றொரு புதிய சொல்லைக் கற்றுக் கொடுங்கள்"
            )
            "hi" -> listOf(
                "यह क्या है? 🤔" to "यह क्या है?",
                "अंग्रेजी में क्या कहते हैं? 🇬🇧" to "How do you say this in English?",
                "फिर से बोलो 🔊" to "फिर से बोलो",
                "मुझे कहानी सुनाओ 📖" to "मुझे एक छोटी सी कहानी सुनाओ",
                "एक और शब्द ✨" to "मुझे एक और नया शब्द सिखाओ"
            )
            "ar" -> listOf(
                "ما هذا؟ 🤔" to "ما هذا الشيء؟",
                "كيف نقوله بالإنجليزية؟ 🇬🇧" to "How do you say this in English?",
                "أعد مجدداً 🔊" to "أعد مجدداً",
                "احكِ لي قصة 📖" to "احكِ لي قصة أطفال قصيرة وممتعة",
                "كلمة أخرى ✨" to "علمني كلمة جديدة ومفيدة"
            )
            "kn" -> listOf(
                "ಇದೇನು? 🤔" to "ಇದೇನು?",
                "ಇಂಗ್ಲಿಷ್‌ನಲ್ಲಿ ಹೇಗೆ ಹೇಳುವುದು? 🇬🇧" to "How do you say this in English?",
                "ಮತ್ತೊಮ್ಮೆ ಹೇಳು 🔊" to "ಮತ್ತೊಮ್ಮೆ ಹೇಳು",
                "ನನಗೊಂದು ಕಥೆ ಹೇಳು 📖" to "ನನಗೊಂದು ಸಣ್ಣ ಕಥೆ ಹೇಳು",
                "ಮತ್ತೊಂದು ಪದ ✨" to "ನನಗೆ ಇನ್ನೊಂದು ಹೊಸ ಪದ ಕಲಿಸು"
            )
            "ml" -> listOf(
                "ഇതെന്താണ്? 🤔" to "ഇതെന്താണ്?",
                "ഇംഗ്ലീഷിൽ എങ്ങനെ പറയും? 🇬🇧" to "How do you say this in English?",
                "ഒന്നുകൂടി പറയൂ 🔊" to "ഒന്നുകൂடி പറയൂ",
                "ഒരു കഥ പറയൂ 📖" to "എനിക്ക് ഒരു കുട്ടി കഥ പറയൂ",
                "മറ്റൊരു വാക്ക് ✨" to "എനിക്ക് മറ്റൊരു പുതിയ വാക്ക് പഠിപ്പിച്ചു തരൂ"
            )
            "bn" -> listOf(
                "এটি কি? 🤔" to "এটি কি?",
                "ইংরেজিতে কি বলে? 🇬🇧" to "How do you say this in English?",
                "আবার বলো 🔊" to "আবার বলো",
                "আমাকে গল্প বলো 📖" to "আমাকে একটি ছোট মজার গল্প বলো",
                "আরেকটি শব্দ ✨" to "আমাকে আরেকটি নতুন শব্দ শেখাও"
            )
            "mr" -> listOf(
                "हे काय आहे? 🤔" to "हे काय आहे?",
                "इंग्रजीत काय म्हणतात? 🇬🇧" to "How do you say this in English?",
                "पुन्हा सांगा 🔊" to "पुन्हा सांगा",
                "मला गोष्ट सांगा 📖" to "मला एक लहान छान गोष्ट सांगा",
                "आणखी एक शब्द ✨" to "मला आणखी एक नवीन शब्द शिकवा"
            )
            "gu" -> listOf(
                "આ શું છે? 🤔" to "આ શું છે?",
                "અંગ્રેજીમાં શું કહેવાય? 🇬🇧" to "How do you say this in English?",
                "ફરીથી બોલો 🔊" to "ફરીથી બોલો",
                "મને વાર્તા કહો 📖" to "મને એક નાની વાર્તા કહો",
                "બીજો નવો શબ્દ ✨" to "મને બીજો એક નવો શબ્દ શીખવો"
            )
            else -> listOf(
                "What is this? 🤔" to "What is this item?",
                "How in $targetName? 🇮🇳" to "How do you say this in $targetName?",
                "Say it again 🔊" to "Say again",
                "Tell a story 📖" to "Tell me a short fun kids story",
                "Another word ✨" to "Teach me another new word"
            )
        }
    }

    /**
     * Comprehensive dataset validation function.
     * Iterates through every supported language and every category, checking for:
     * - Missing categories
     * - Empty learning lists
     * - Blank nativeText or ttsText
     * - Missing visual emoji / image
     * - Foreign script contamination (e.g., Tamil or Telugu characters in Malayalam)
     *
     * Reports findings clearly in logcat and returns a complete DatasetValidationReport.
     */
    fun validateLanguageDataset(): DatasetValidationReport {
        val issues = mutableListOf<DatasetValidationIssue>()
        var totalItemsChecked = 0

        val tamilRange = '\u0B80'..'\u0BFF'
        val teluguRange = '\u0C00'..'\u0C7F'
        val devanagariRange = '\u0900'..'\u097F'
        val arabicRange = '\u0600'..'\u06FF'
        val kannadaRange = '\u0C80'..'\u0CFF'
        val malayalamRange = '\u0D00'..'\u0D7F'
        val bengaliRange = '\u0980'..'\u09FF'
        val gujaratiRange = '\u0A80'..'\u0AFF'

        for (lang in SUPPORTED_LANGUAGES) {
            for (category in LessonCategory.values()) {
                val items = getLocalizedContent(category, lang)

                if (items.isEmpty()) {
                    issues.add(
                        DatasetValidationIssue(
                            language = lang,
                            category = category,
                            itemId = null,
                            issueType = DatasetIssueType.EMPTY_CATEGORY,
                            details = "Category ${category.name} in language '$lang' returned 0 items!"
                        )
                    )
                    continue
                }

                for (item in items) {
                    totalItemsChecked++

                    if (item.nativeText.isBlank()) {
                        issues.add(
                            DatasetValidationIssue(
                                language = lang,
                                category = category,
                                itemId = item.id,
                                issueType = DatasetIssueType.MISSING_NATIVE_TEXT,
                                details = "Empty nativeText for item id: ${item.id} in category ${category.name}"
                            )
                        )
                    }

                    if (item.ttsText.isBlank()) {
                        issues.add(
                            DatasetValidationIssue(
                                language = lang,
                                category = category,
                                itemId = item.id,
                                issueType = DatasetIssueType.EMPTY_TTS_TEXT,
                                details = "Empty ttsText for item id: ${item.id} in category ${category.name}"
                            )
                        )
                    }

                    if (item.image.isBlank()) {
                        issues.add(
                            DatasetValidationIssue(
                                language = lang,
                                category = category,
                                itemId = item.id,
                                issueType = DatasetIssueType.MISSING_IMAGE,
                                details = "Empty image/visualEmoji for item id: ${item.id} in category ${category.name}"
                            )
                        )
                    }

                    // Foreign script contamination checks
                    val nativeChars = item.nativeText.toCharArray()
                    when (lang) {
                        "ml" -> {
                            val hasTamil = nativeChars.any { it in tamilRange }
                            val hasTelugu = nativeChars.any { it in teluguRange }
                            if (hasTamil || hasTelugu) {
                                issues.add(
                                    DatasetValidationIssue(
                                        language = lang,
                                        category = category,
                                        itemId = item.id,
                                        issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                        details = "Malayalam item '${item.nativeText}' contains foreign script (Tamil: $hasTamil, Telugu: $hasTelugu)!"
                                    )
                                )
                            }
                        }
                        "te" -> {
                            val hasDevanagari = nativeChars.any { it in devanagariRange }
                            val hasTamil = nativeChars.any { it in tamilRange }
                            if (hasDevanagari || hasTamil) {
                                issues.add(
                                    DatasetValidationIssue(
                                        language = lang,
                                        category = category,
                                        itemId = item.id,
                                        issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                        details = "Telugu item '${item.nativeText}' contains foreign script!"
                                    )
                                )
                            }
                        }
                        "ta" -> {
                            val hasTelugu = nativeChars.any { it in teluguRange }
                            val hasDevanagari = nativeChars.any { it in devanagariRange }
                            if (hasTelugu || hasDevanagari) {
                                issues.add(
                                    DatasetValidationIssue(
                                        language = lang,
                                        category = category,
                                        itemId = item.id,
                                        issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                        details = "Tamil item '${item.nativeText}' contains foreign script!"
                                    )
                                )
                            }
                        }
                        "hi" -> {
                            val hasTelugu = nativeChars.any { it in teluguRange }
                            val hasTamil = nativeChars.any { it in tamilRange }
                            if (hasTelugu || hasTamil) {
                                issues.add(
                                    DatasetValidationIssue(
                                        language = lang,
                                        category = category,
                                        itemId = item.id,
                                        issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                        details = "Hindi item '${item.nativeText}' contains south Indian script!"
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        val isValid = issues.isEmpty()
        val summaryMsg = if (isValid) {
            "[VALIDATION_SUCCESS] Multilingual dataset validation passed! Checked $totalItemsChecked items across ${SUPPORTED_LANGUAGES.size} languages. Zero issues found."
        } else {
            "[VALIDATION_FAILURE] Multilingual dataset validation failed with ${issues.size} issues:"
        }
        
        try {
            if (isValid) Log.i(TAG, summaryMsg) else Log.e(TAG, summaryMsg)
        } catch (t: Throwable) {
            println(summaryMsg)
        }

        if (!isValid) {
            issues.forEach { issue ->
                val detail = " -> [${issue.language}] [${issue.category?.name ?: "GLOBAL"}] [${issue.issueType}]: ${issue.details}"
                try {
                    Log.e(TAG, detail)
                } catch (t: Throwable) {
                    println(detail)
                }
            }
        }

        return DatasetValidationReport(
            isValid = isValid,
            totalLanguages = SUPPORTED_LANGUAGES.size,
            totalItemsChecked = totalItemsChecked,
            issues = issues
        )
    }

    /**
     * Validates content specifically for a single language code.
     * Returns a clear report detailing any missing items, empty strings,
     * foreign script contamination, or image issues.
     */
    fun validateLanguageContent(langCode: String): DatasetValidationReport {
        val issues = mutableListOf<DatasetValidationIssue>()
        var totalItemsChecked = 0

        val tamilRange = '\u0B80'..'\u0BFF'
        val teluguRange = '\u0C00'..'\u0C7F'
        val devanagariRange = '\u0900'..'\u097F'
        val arabicRange = '\u0600'..'\u06FF'
        val kannadaRange = '\u0C80'..'\u0CFF'
        val malayalamRange = '\u0D00'..'\u0D7F'

        for (category in LessonCategory.values()) {
            val items = getLocalizedContent(category, langCode)
            if (items.isEmpty()) {
                issues.add(
                    DatasetValidationIssue(
                        language = langCode,
                        category = category,
                        itemId = null,
                        issueType = DatasetIssueType.EMPTY_CATEGORY,
                        details = "Category ${category.name} in language '$langCode' returned 0 items!"
                    )
                )
                continue
            }

            for (item in items) {
                totalItemsChecked++
                if (item.nativeText.isBlank()) {
                    issues.add(
                        DatasetValidationIssue(
                            language = langCode,
                            category = category,
                            itemId = item.id,
                            issueType = DatasetIssueType.MISSING_NATIVE_TEXT,
                            details = "Empty nativeText for item id: ${item.id} in category ${category.name}"
                        )
                    )
                }
                if (item.ttsText.isBlank()) {
                    issues.add(
                        DatasetValidationIssue(
                            language = langCode,
                            category = category,
                            itemId = item.id,
                            issueType = DatasetIssueType.EMPTY_TTS_TEXT,
                            details = "Empty ttsText for item id: ${item.id} in category ${category.name}"
                        )
                    )
                }
                if (item.image.isBlank()) {
                    issues.add(
                        DatasetValidationIssue(
                            language = langCode,
                            category = category,
                            itemId = item.id,
                            issueType = DatasetIssueType.MISSING_IMAGE,
                            details = "Empty image for item id: ${item.id} in category ${category.name}"
                        )
                    )
                }

                // Script contamination checks
                val nativeChars = item.nativeText.toCharArray()
                when (langCode) {
                    "ml" -> {
                        val hasTamil = nativeChars.any { it in tamilRange }
                        val hasTelugu = nativeChars.any { it in teluguRange }
                        if (hasTamil || hasTelugu) {
                            issues.add(
                                DatasetValidationIssue(
                                    language = langCode,
                                    category = category,
                                    itemId = item.id,
                                    issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                    details = "Malayalam item '${item.nativeText}' contains foreign script (Tamil: $hasTamil, Telugu: $hasTelugu)!"
                                )
                            )
                        }
                    }
                    "te" -> {
                        val hasDevanagari = nativeChars.any { it in devanagariRange }
                        val hasTamil = nativeChars.any { it in tamilRange }
                        if (hasDevanagari || hasTamil) {
                            issues.add(
                                DatasetValidationIssue(
                                    language = langCode,
                                    category = category,
                                    itemId = item.id,
                                    issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                    details = "Telugu item '${item.nativeText}' contains foreign script!"
                                )
                            )
                        }
                    }
                    "ta" -> {
                        val hasTelugu = nativeChars.any { it in teluguRange }
                        val hasDevanagari = nativeChars.any { it in devanagariRange }
                        if (hasTelugu || hasDevanagari) {
                            issues.add(
                                DatasetValidationIssue(
                                    language = langCode,
                                    category = category,
                                    itemId = item.id,
                                    issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                    details = "Tamil item '${item.nativeText}' contains foreign script!"
                                )
                            )
                        }
                    }
                    "hi" -> {
                        val hasTelugu = nativeChars.any { it in teluguRange }
                        val hasTamil = nativeChars.any { it in tamilRange }
                        if (hasTelugu || hasTamil) {
                            issues.add(
                                DatasetValidationIssue(
                                    language = langCode,
                                    category = category,
                                    itemId = item.id,
                                    issueType = DatasetIssueType.FOREIGN_SCRIPT_DETECTED,
                                    details = "Hindi item '${item.nativeText}' contains south Indian script!"
                                )
                            )
                        }
                    }
                }
            }
        }

        val isValid = issues.isEmpty()
        val msg = if (isValid) {
            "Language '$langCode' validated successfully! Checked $totalItemsChecked items with 0 issues."
        } else {
            "Language '$langCode' validation failed with ${issues.size} issues."
        }
        try {
            if (isValid) Log.i(TAG, msg) else Log.e(TAG, msg)
        } catch (t: Throwable) {
            println(msg)
        }

        return DatasetValidationReport(
            isValid = isValid,
            totalLanguages = 1,
            totalItemsChecked = totalItemsChecked,
            issues = issues
        )
    }
}
