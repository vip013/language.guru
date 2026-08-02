package com.example.data

data class LearningItem(
    val display: String,         // Large text to show (e.g., "అ" or "A")
    val subtitle: String,        // Pronunciation / spelling help (e.g., "a" or "Apple")
    val voiceText: String,       // Text to be spoken aloud (e.g., "అ" or "Apple")
    val visualEmoji: String = "" // Educational illustration emoji
)

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

fun LessonCategory.getDifficultyLevel(): DifficultyLevel {
    return when (this) {
        LessonCategory.LETTERS,
        LessonCategory.LETTER_WORD,
        LessonCategory.NUMBERS,
        LessonCategory.SHAPES,
        LessonCategory.FRUITS_FLOWERS,
        LessonCategory.SCHOOL_OBJECTS -> DifficultyLevel.BEGINNER

        LessonCategory.CONSONANTS,
        LessonCategory.SPICES,
        LessonCategory.VEGETABLES,
        LessonCategory.RELATIONSHIPS,
        LessonCategory.BODY_PARTS,
        LessonCategory.WEATHER -> DifficultyLevel.INTERMEDIATE

        else -> DifficultyLevel.ADVANCED
    }
}

enum class LessonCategory {
    // Beginner / Basic (6 items)
    LETTERS, LETTER_WORD, NUMBERS, SHAPES, FRUITS_FLOWERS, SCHOOL_OBJECTS,

    // Intermediate / Middle (7 items)
    CONSONANTS, SPICES, MONTHS, VEGETABLES, RELATIONSHIPS, BODY_PARTS, WEATHER,

    // Advanced / High (6 items)
    POEMS, STATES_CAPITALS, NATIONAL_SYMBOLS, FOODS, BASIC_MATH, DIRECTIONS;

    fun getTitle(langCode: String): String = getRawTitle(langCode).substringBefore(" (")

    fun getRawTitle(langCode: String): String = when (this) {
        LETTERS -> when (langCode) {
            "te" -> "అక్షరాలు"
            "ta" -> "எழுத்துக்கள்"
            "hi" -> "वर्णमाला"
            "ar" -> "الحروف"
            "kn" -> "ಅಕ್ಷರಮಾಲೆ (Letters)"
            "ml" -> "അക്ഷരമാല (Letters)"
            "bn" -> "বর্ণমালা (Letters)"
            "mr" -> "वर्णमाला (Letters)"
            "gu" -> "વર્ણમાલા (Letters)"
            else -> "Letters"
        }
        LETTER_WORD -> when (langCode) {
            "te" -> "అక్షరం - పదం (Words)"
            "ta" -> "எழுத்தும் சொல்லும் (Words)"
            "hi" -> "वर्ण और शब्द (Words)"
            "ar" -> "الحرف والكلمة (Words)"
            "kn" -> "ಅಕ್ಷರ ಮತ್ತು ಪದ (Words)"
            "ml" -> "അക്ഷരവും വാക്കും (Words)"
            "bn" -> "বর্ণ ও শব্দ (Words)"
            "mr" -> "वर्ण आणि शब्द (Words)"
            "gu" -> "વર્ણ અને શબ્દ (Words)"
            else -> "Letter with Word"
        }
        NUMBERS -> when (langCode) {
            "te" -> "అంకెలు (Numbers)"
            "ta" -> "எண்கள் (Numbers)"
            "hi" -> "संख्या (Numbers)"
            "ar" -> "الأرقام (Numbers)"
            "kn" -> "ಸಂಖ್ಯೆಗಳು (Numbers)"
            "ml" -> "അക്കങ്ങൾ (Numbers)"
            "bn" -> "সংখ্যা (Numbers)"
            "mr" -> "संख्या (Numbers)"
            "gu" -> "સંખ્યાઓ (Numbers)"
            else -> "Numbers"
        }
        CONSONANTS -> when (langCode) {
            "te" -> "100 సాధారణ పదాలు (100 Basic Words)"
            "ta" -> "100 அடிப்படை வார்த்தைகள் (100 Basic Words)"
            "hi" -> "100 बुनियादी शब्द (100 Basic Words)"
            "ar" -> "100 كلمة أساسية (100 Basic Words)"
            "kn" -> "100 ಮೂಲ ಪದಗಳು (100 Basic Words)"
            "ml" -> "100 അടിസ്ഥാന വാക്കുകൾ (100 Basic Words)"
            "bn" -> "১০০ মৌলিক শব্দ (100 Basic Words)"
            "mr" -> "१०० मूलभूत शब्द (100 Basic Words)"
            "gu" -> "૧૦૦ મૂળભૂત શબ્દો (100 Basic Words)"
            else -> "100 Basic Words"
        }
        SPICES -> when (langCode) {
            "te" -> "రంగులు (Colors)"
            "ta" -> "வண்ணங்கள் (Colors)"
            "hi" -> "रंग (Colors)"
            "ar" -> "الألوان (Colors)"
            "kn" -> "ಬಣ್ಣಗಳು (Colors)"
            "ml" -> "നിറങ്ങൾ (Colors)"
            "bn" -> "রং (Colors)"
            "mr" -> "रंग (Colors)"
            "gu" -> "રંગો (Colors)"
            else -> "Colors"
        }
        MONTHS -> when (langCode) {
            "te" -> "నెలలు (Months)"
            "ta" -> "மாதங்கள் (Months)"
            "hi" -> "महीने (Months)"
            "ar" -> "الشهور (Months)"
            "kn" -> "ತಿಂಗಳುಗಳು (Months)"
            "ml" -> "മാസങ്ങൾ (Months)"
            "bn" -> "মাস (Months)"
            "mr" -> "महिने (Months)"
            "gu" -> "મહિનાઓ (Months)"
            else -> "Months"
        }
        POEMS -> when (langCode) {
            "te" -> "జంతువులు & పక్షులు (Animals & Birds)"
            "ta" -> "விலங்குகள் மற்றும் பறவைகள் (Animals & Birds)"
            "hi" -> "जानवर और पक्षी (Animals & Birds)"
            "ar" -> "الحيوانات والطيور (Animals & Birds)"
            "kn" -> "ಪ್ರಾಣಿಗಳು ಮತ್ತು ಪಕ್ಷಿಗಳು (Animals & Birds)"
            "ml" -> "മൃഗങ്ങളും പക്ഷികളും (Animals & Birds)"
            "bn" -> "পশু ও পাখি (Animals & Birds)"
            "mr" -> "प्राणी आणि पक्षी (Animals & Birds)"
            "gu" -> "પ્રાણીઓ અને પક્ષીઓ (Animals & Birds)"
            else -> "Animals & Birds"
        }
        VEGETABLES -> when (langCode) {
            "te" -> "కూరగాయలు (Vegetables)"
            "ta" -> "காய்கறிகள் (Vegetables)"
            "hi" -> "सब्जियां (Vegetables)"
            "ar" -> "خضروات (Vegetables)"
            "kn" -> "ತರಕಾರಿಗಳು (Vegetables)"
            "ml" -> "പച്ചക്കറികൾ (Vegetables)"
            "bn" -> "শাকসবজি (Vegetables)"
            "mr" -> "भाज्या (Vegetables)"
            "gu" -> "શાકભાજી (Vegetables)"
            else -> "Vegetables"
        }
        STATES_CAPITALS -> when (langCode) {
            "te" -> "రాష్ట్రాలు & రాజధానులు (States & Capitals)"
            "ta" -> "மாநிலங்கள் & தலைநகரங்கள் (States & Capitals)"
            "hi" -> "राज्य और राजधानियाँ (States & Capitals)"
            "ar" -> "الولايات والعواصم (States & Capitals)"
            "kn" -> "ರಾಜ್ಯಗಳು ಮತ್ತು ರಾಜಧಾನಿಗಳು (States & Capitals)"
            "ml" -> "സംസ്ഥാനങ്ങളും തലസ്ഥാനങ്ങളും (States & Capitals)"
            "bn" -> "রাজ্য ও রাজধানী (States & Capitals)"
            "mr" -> "राज्ये आणि राजधान्या (States & Capitals)"
            "gu" -> "રાજ્યો અને રાજધાનીઓ (States & Capitals)"
            else -> "States & Capitals"
        }
        NATIONAL_SYMBOLS -> when (langCode) {
            "te" -> "జాతీయ చిహ్నాలు (National Symbols)"
            "ta" -> "தேசிய சின்னங்கள் (National Symbols)"
            "hi" -> "राष्ट्रीय प्रतीक (National Symbols)"
            "ar" -> "الرموز الوطنية (National Symbols)"
            "kn" -> "ರಾಷ್ಟ್ರೀಯ ಚಿಹ್ನೆಗಳು (National Symbols)"
            "ml" -> "ദേശീയ ചിഹ്നങ്ങൾ (National Symbols)"
            "bn" -> "জাতীয় প্রতীক (National Symbols)"
            "mr" -> "राष्ट्रीय चिन्हे (National Symbols)"
            "gu" -> "રાષ્ટ્રીય પ્રતીકો (National Symbols)"
            else -> "National Symbols"
        }
        RELATIONSHIPS -> when (langCode) {
            "te" -> "బంధుత్వాలు (Relationships)"
            "ta" -> "உறவுகள் (Relationships)"
            "hi" -> "रिश्ते (Relationships)"
            "ar" -> "العلاقات (Relationships)"
            "kn" -> "ಸಂಬಂಧಗಳು (Relationships)"
            "ml" -> "ബന്ധങ്ങൾ (Relationships)"
            "bn" -> "সম্পর্ক (Relationships)"
            "mr" -> "नातेसंबंध (Relationships)"
            "gu" -> "સંબંધો (Relationships)"
            else -> "Relationships"
        }
        FOODS -> when (langCode) {
            "te" -> "ఆహారపదార్థాలు (Foods)"
            "ta" -> "உணவுகள் (Foods)"
            "hi" -> "खाद्य पदार्थ (Foods)"
            "ar" -> "الأطعمة (Foods)"
            "kn" -> "ಆಹಾರಗಳು (Foods)"
            "ml" -> "ഭക്ഷണങ്ങൾ (Foods)"
            "bn" -> "খাবার (Foods)"
            "mr" -> "अन्नपदार्थ (Foods)"
            "gu" -> "ખોરાક (Foods)"
            else -> "Foods"
        }
        FRUITS_FLOWERS -> when (langCode) {
            "te" -> "పండ్లు & పువ్వులు (Fruits & Flowers)"
            "ta" -> "பழங்கள் & மலர்கள் (Fruits & Flowers)"
            "hi" -> "फल और फूल (Fruits & Flowers)"
            "ar" -> "الفواكه والزهور (Fruits & Flowers)"
            "kn" -> "ಹಣ್ಣುಗಳು ಮತ್ತು ಹೂವುಗಳು (Fruits & Flowers)"
            "ml" -> "പഴങ്ങളും പൂക്കളും (Fruits & Flowers)"
            "bn" -> "ফল ও ফুল (Fruits & Flowers)"
            "mr" -> "फळे आणि फुले (Fruits & Flowers)"
            "gu" -> "ફળો અને ફૂલો (Fruits & Flowers)"
            else -> "Fruits & Flowers"
        }
        BODY_PARTS -> when (langCode) {
            "te" -> "శరీర భాగాలు (Body Parts)"
            "ta" -> "உடல் உறுப்புகள் (Body Parts)"
            "hi" -> "शरीर के अंग (Body Parts)"
            "ar" -> "أجزاء الجسم (Body Parts)"
            "kn" -> "ದೇಹದ ಭಾಗಗಳು (Body Parts)"
            "ml" -> "ശരീര ഭാഗങ്ങൾ (Body Parts)"
            "bn" -> "শরীরের অংশ (Body Parts)"
            "mr" -> "शरीराचे अवयव (Body Parts)"
            "gu" -> "શરીરના અંગો (Body Parts)"
            else -> "Body Parts"
        }
        SHAPES -> when (langCode) {
            "te" -> "ఆకారాలు (Shapes)"
            "ta" -> "வடிவங்கள் (Shapes)"
            "hi" -> "आकृतियाँ (Shapes)"
            "ar" -> "الأشكال (Shapes)"
            "kn" -> "ಆಕಾರಗಳು (Shapes)"
            "ml" -> "രൂപങ്ങൾ (Shapes)"
            "bn" -> "আকৃতি (Shapes)"
            "mr" -> "आकार (Shapes)"
            "gu" -> "આકારો (Shapes)"
            else -> "Shapes"
        }
        BASIC_MATH -> when (langCode) {
            "te" -> "ప్రాథమిక గణితం (Basic Math)"
            "ta" -> "அடிப்படை கணிதம் (Basic Math)"
            "hi" -> "बुनियादी गणित (Basic Math)"
            "ar" -> "الرياضيات الأساسية (Basic Math)"
            "kn" -> "ಮೂಲ ಗಣಿತ (Basic Math)"
            "ml" -> "അടിസ്ഥാന ഗണിതം (Basic Math)"
            "bn" -> "প্রাথমিক গণিত (Basic Math)"
            "mr" -> "मूलभूत गणित (Basic Math)"
            "gu" -> "મૂળભૂત ગણિત (Basic Math)"
            else -> "Basic Math"
        }
        DIRECTIONS -> when (langCode) {
            "te" -> "దిక్కులు (Directions)"
            "ta" -> "திசைகள் (Directions)"
            "hi" -> "दिशाएँ (Directions)"
            "ar" -> "الاتجاهات (Directions)"
            "kn" -> "ದಿಕ್ಕುಗಳು (Directions)"
            "ml" -> "ദിശകൾ (Directions)"
            "bn" -> "দিকসমূহ (Directions)"
            "mr" -> "दिशा (Directions)"
            "gu" -> "દિશાઓ (Directions)"
            else -> "Directions"
        }
        SCHOOL_OBJECTS -> when (langCode) {
            "te" -> "పాఠశాల వస్తువులు (School Objects)"
            "ta" -> "பள்ளி பொருட்கள் (School Objects)"
            "hi" -> "स्कूल की वस्तुएँ (School Objects)"
            "ar" -> "أدوات المدرسة (School Objects)"
            "kn" -> "ಶಾಲా ವಸ್ತುಗಳು (School Objects)"
            "ml" -> "സ്കൂൾ വസ്തുക്കൾ (School Objects)"
            "bn" -> "স্কুলের জিনিসপত্র (School Objects)"
            "mr" -> "शालेय वस्तू (School Objects)"
            "gu" -> "શાળાની વસ્તુઓ (School Objects)"
            else -> "School Objects"
        }
        WEATHER -> when (langCode) {
            "te" -> "వాతావరణం (Weather)"
            "ta" -> "வானிலை (Weather)"
            "hi" -> "मौसम (Weather)"
            "ar" -> "الطقس (Weather)"
            "kn" -> "ಹವಾಮಾನ (Weather)"
            "ml" -> "കാലാവസ്ഥ (Weather)"
            "bn" -> "ആবহাওয়া (Weather)"
            "mr" -> "हवामान (Weather)"
            "gu" -> "હવામાન (Weather)"
            else -> "Weather"
        }
    }

    val colorHex: String
        get() = when (this) {
            LETTERS -> "#E53935"
            LETTER_WORD -> "#3949AB"
            NUMBERS -> "#00897B"
            CONSONANTS -> "#F4511E"
            SPICES -> "#D81B60"
            MONTHS -> "#0288D1"
            POEMS -> "#E040FB"
            VEGETABLES -> "#2E7D32"
            STATES_CAPITALS -> "#8D6E63"
            NATIONAL_SYMBOLS -> "#1565C0"
            RELATIONSHIPS -> "#FF8F00"
            FOODS -> "#43A047"
            FRUITS_FLOWERS -> "#EC407A"
            BODY_PARTS -> "#7B1FA2"
            SHAPES -> "#FBC02D"
            BASIC_MATH -> "#1976D2"
            DIRECTIONS -> "#C2185B"
            SCHOOL_OBJECTS -> "#388E3C"
            WEATHER -> "#00ACC1"
        }

    fun getIcon(langCode: String): String = when (this) {
        LETTERS -> when (langCode) {
            "te" -> "అ"
            "ta" -> "అ"
            "hi" -> "అ"
            "kn" -> "ಅ"
            "ml" -> "അ"
            "bn" -> "অ"
            "mr" -> "अ"
            "gu" -> "અ"
            "ar" -> "أ"
            else -> "A"
        }
        LETTER_WORD -> "🍎"
        NUMBERS -> "🔢"
        CONSONANTS -> "🗣️"
        SPICES -> "🌈"
        MONTHS -> "📅"
        POEMS -> "🦁"
        VEGETABLES -> "🥦"
        STATES_CAPITALS -> "🗺️"
        NATIONAL_SYMBOLS -> "🇮🇳"
        RELATIONSHIPS -> "👨‍👩‍👧‍👦"
        FOODS -> "🍛"
        FRUITS_FLOWERS -> "🌸"
        BODY_PARTS -> "🖐️"
        SHAPES -> "🔺"
        BASIC_MATH -> "📐"
        DIRECTIONS -> "🧭"
        SCHOOL_OBJECTS -> "🎒"
        WEATHER -> "🌦️"
    }

    val icon: String
        get() = getIcon("")
}

fun getNewCategoryLessons(langCode: String): Map<LessonCategory, List<LearningItem>> {
    return mapOf(
        LessonCategory.SHAPES to when (langCode) {
            "te" -> listOf(
                LearningItem("త్రికోణం", "Triangle", "త్రికోణం", "🔺"),
                LearningItem("గుండ్రం", "Circle", "గుండ్రం", "🔴"),
                LearningItem("చతురస్రం", "Square", "చతురస్రం", "🟩"),
                LearningItem("దీర్ఘచతురస్రం", "Rectangle", "దీర్ఘచతురస్రం", "█"),
                LearningItem("నక్షత్రం", "Star", "నక్షత్రం", "⭐")
            )
            "hi" -> listOf(
                LearningItem("त्रिकोण", "Trikon (Triangle)", "त्रिकोण", "🔺"),
                LearningItem("वृत्त", "Vritta (Circle)", "वृत्त", "🔴"),
                LearningItem("वर्ग", "Varg (Square)", "वर्ग", "🟩"),
                LearningItem("आयत", "Aayat (Rectangle)", "आयत", "█"),
                LearningItem("तारा", "Tara (Star)", "तारा", "⭐")
            )
            "ta" -> listOf(
                LearningItem("முக்கோணம்", "Mukkōṇam (Triangle)", "முக்கோணம்", "🔺"),
                LearningItem("வட்டம்", "Vaṭṭam (Circle)", "வட்டம்", "🔴"),
                LearningItem("சதுரம்", "Sathuram (Square)", "சதுரம்", "🟩"),
                LearningItem("செவ்வகம்", "Sevvagam (Rectangle)", "செவ்வகம்", "█"),
                LearningItem("நட்சத்திரம்", "Natchathiram (Star)", "நட்சத்திரம்", "⭐")
            )
            "kn" -> listOf(
                LearningItem("ತ್ರಿಕೋನ", "Trikōna (Triangle)", "ತ್ರಿಕೋನ", "🔺"),
                LearningItem("ವೃತ್ತ", "Vritta (Circle)", "ವೃತ್ತ", "🔴"),
                LearningItem("ಚೌಕ", "Chauka (Square)", "ಚೌಕ", "🟩"),
                LearningItem("ಆಯತ", "Āyata (Rectangle)", "ಆಯತ", "█"),
                LearningItem("ನಕ್ಷತ್ರ", "Nakshatra (Star)", "ನಕ್ಷತ್ರ", "⭐")
            )
            "ml" -> listOf(
                LearningItem("ത്രികോണം", "Thrikonam (Triangle)", "ത്രികോണം", "🔺"),
                LearningItem("വട്ടം", "Vattam (Circle)", "വട്ടം", "🔴"),
                LearningItem("ചതുരം", "Chathuram (Square)", "ചതുരം", "🟩"),
                LearningItem("ദീർഘചതുരം", "Deerghachathuram (Rectangle)", "ദീർഘചതുരം", "█"),
                LearningItem("നಕ್ಷത്രം", "Nakshathram (Star)", "നಕ್ಷത്രം", "⭐")
            )
            else -> listOf(
                LearningItem("Triangle", "Triangle", "Triangle", "🔺"),
                LearningItem("Circle", "Circle", "Circle", "🔴"),
                LearningItem("Square", "Square", "Square", "🟩"),
                LearningItem("Rectangle", "Rectangle", "Rectangle", "█"),
                LearningItem("Star", "Star", "Star", "⭐")
            )
        },
        LessonCategory.BASIC_MATH to when (langCode) {
            "te" -> listOf(
                LearningItem("కూడిక (+)", "Koodika (Addition)", "కూడిక", "➕"),
                LearningItem("తీసివేత (-)", "Theesivetha (Subtraction)", "తీసివేత", "➖"),
                LearningItem("గుణకారం (×)", "Gunakaram (Multiplication)", "గుణకారం", "✖️"),
                LearningItem("భాగాహారం (÷)", "Bhagaharam (Division)", "భాగాహారం", "➗"),
                LearningItem("సమానం (=)", "Samanam (Equals)", "సమానం", "🟰")
            )
            "hi" -> listOf(
                LearningItem("जोड़ (+)", "Jodh (Addition)", "जोड़", "➕"),
                LearningItem("घटाव (-)", "Ghatav (Subtraction)", "घटाव", "➖"),
                LearningItem("गुणा (×)", "Guna (Multiplication)", "गुणा", "✖️"),
                LearningItem("भाग (÷)", "Bhag (Division)", "भाग", "➗"),
                LearningItem("बराबर (=)", "Barabar (Equals)", "बराबर", "🟰")
            )
            "ta" -> listOf(
                LearningItem("கூட்டல் (+)", "Koottal (Addition)", "கூட்டல்", "➕"),
                LearningItem("கழித்தல் (-)", "Kazhithal (Subtraction)", "கழித்தல்", "➖"),
                LearningItem("பெருக்கல் (×)", "Perukkal (Multiplication)", "பெருக்கல்", "✖️"),
                LearningItem("வகுத்தல் (÷)", "Vaguthal (Division)", "வகுத்தல்", "➗"),
                LearningItem("சமம் (=)", "Samam (Equals)", "சமம்", "🟰")
            )
            "kn" -> listOf(
                LearningItem("ಸಂಕಲನ (+)", "Sankalana (Addition)", "ಸಂಕಲನ", "➕"),
                LearningItem("ವ್ಯವಕಲನ (-)", "Vyavakalana (Subtraction)", "ವ್ಯವಕಲನ", "➖"),
                LearningItem("ಗುಣಾಕಾರ (×)", "Gunakaara (Multiplication)", "ಗುಣಾಕಾರ", "✖️"),
                LearningItem("ಭಾಗಾಕಾರ (÷)", "Bhaagaakaara (Division)", "ಭಾಗಾಕಾರ", "➗"),
                LearningItem("ಸಮಾನ (=)", "Samaana (Equals)", "ಸಮಾನ", "🟰")
            )
            "ml" -> listOf(
                LearningItem("கூட்டல் (+)", "Koottal (Addition)", "கூட்டல்", "➕"),
                LearningItem("കുറയ്ക്കல் (-)", "Kuraykkal (Subtraction)", "കുറയ്ക്കல்", "➖"),
                LearningItem("గుണനം (×)", "Gunanam (Multiplication)", "గుണനം", "✖️"),
                LearningItem("ഹരണം (÷)", "Haranam (Division)", "ഹരണം", "➗"),
                LearningItem("തുല്യം (=)", "Thulyam (Equals)", "തുല്യം", "🟰")
            )
            else -> listOf(
                LearningItem("Addition (+)", "Addition", "Addition", "➕"),
                LearningItem("Subtraction (-)", "Subtraction", "Subtraction", "➖"),
                LearningItem("Multiplication (×)", "Multiplication", "Multiplication", "✖️"),
                LearningItem("Division (÷)", "Division", "Division", "➗"),
                LearningItem("Equals (=)", "Equals", "Equals", "🟰")
            )
        },
        LessonCategory.DIRECTIONS to when (langCode) {
            "te" -> listOf(
                LearningItem("తూర్పు", "Thoorpu (East)", "తూర్పు", "🌅 ➡️"),
                LearningItem("పడమర", "Padamara (West)", "పడమర", "🌇 ⬅️"),
                LearningItem("ఉత్తరం", "Uttaram (North)", "ఉత్తరం", "🏔️ ⬆️"),
                LearningItem("దక్షిణం", "Dakshinam (South)", "దక్షిణం", "🌊 ⬇️")
            )
            "hi" -> listOf(
                LearningItem("पूर्व", "Purv (East)", "पूर्व", "🌅 ➡️"),
                LearningItem("पश्चिम", "Paschim (West)", "पश्चिम", "🌇 ⬅️"),
                LearningItem("उत्तर", "Uttar (North)", "उत्तर", "🏔️ ⬆️"),
                LearningItem("दक्षिण", "Dakshin (South)", "दक्षिण", "🌊 ⬇️")
            )
            "ta" -> listOf(
                LearningItem("கிழக்கு", "Kizhakku (East)", "கிழக்கு", "🌅 ➡️"),
                LearningItem("மேற்கு", "Merku (West)", "மேற்கு", "🌇 ⬅️"),
                LearningItem("வடக்கு", "Vadakku (North)", "வடக்கு", "🏔️ ⬆️"),
                LearningItem("தெற்கு", "Therku (South)", "தெற்கு", "🌊 ⬇️")
            )
            "kn" -> listOf(
                LearningItem("ಪೂರ್ವ", "Poorva (East)", "ಪೂರ್ವ", "🌅 ➡️"),
                LearningItem("ಪಶ್ಚಿಮ", "Pashchima (West)", "ಪಶ್ಚಿಮ", "🌇 ⬅️"),
                LearningItem("ಉತ್ತರ", "Uttara (North)", "ಉತ್ತರ", "🏔️ ⬆️"),
                LearningItem("ದಕ್ಷಿಣ", "Dakshina (South)", "ದಕ್ಷಿಣ", "🌊 ⬇️")
            )
            "ml" -> listOf(
                LearningItem("കിഴക്ക്", "Kizhakku (East)", "കിഴക്ക്", "🌅 ➡️"),
                LearningItem("പടിഞ്ഞാറ്", "Padinjaru (West)", "പടിഞ്ഞാറ്", "🌇 ⬅️"),
                LearningItem("വടക്ക്", "Vadakku (North)", "വടക്ക്", "🏔️ ⬆️"),
                LearningItem("தெക്ക്", "Thekku (South)", "தெക്ക്", "🌊 ⬇️")
            )
            else -> listOf(
                LearningItem("East", "East", "East", "🌅 ➡️"),
                LearningItem("West", "West", "West", "🌇 ⬅️"),
                LearningItem("North", "North", "North", "🏔️ ⬆️"),
                LearningItem("South", "South", "South", "🌊 ⬇️")
            )
        },
        LessonCategory.SCHOOL_OBJECTS to when (langCode) {
            "te" -> listOf(
                LearningItem("పుస్తకం", "Pustakam (Book)", "పుస్తకం", "📖"),
                LearningItem("కలము", "Kalamu (Pen)", "కలము", "🖊️"),
                LearningItem("సంచి", "Sanchi (School Bag)", "సంచి", "🎒"),
                LearningItem("పలక", "Palaka (Slate)", "పలక", "📝"),
                LearningItem("రబ్బరు", "Rabbaru (Eraser)", "రబ్బరు", "🧽")
            )
            "hi" -> listOf(
                LearningItem("किताब", "Kitab (Book)", "किताब", "📖"),
                LearningItem("कलम", "Kalam (Pen)", "कलम", "🖊️"),
                LearningItem("बस्ता", "Basta (Bag)", "बस्ता", "🎒"),
                LearningItem("कॉपी", "Copy (Notebook)", "कॉपी", "📝"),
                LearningItem("रबर", "Rabar (Eraser)", "रबर", "🧽")
            )
            "ta" -> listOf(
                LearningItem("புத்தகம்", "Puthagam (Book)", "புத்தகம்", "📖"),
                LearningItem("பேனா", "Pena (Pen)", "பேனா", "🖊️"),
                LearningItem("பள்ளிப் பை", "Palli Pai (Bag)", "பள்ளிப் பை", "🎒"),
                LearningItem("நோட்டுப் புத்தகம்", "Nottu Puthagam (Notebook)", "நோட்டுப் புத்தகம்", "📝"),
                LearningItem("அழிப்பான்", "Azhappan (Eraser)", "அழிப்பான்", "🧽")
            )
            "kn" -> listOf(
                LearningItem("ಪುಸ್ತಕ", "Pustaka (Book)", "ಪುಸ್ತಕ", "📖"),
                LearningItem("ಪೇನಾ", "Pena (Pen)", "ಪೇನಾ", "🖊️"),
                LearningItem("ಬ್ಯಾಗ್", "Bag (School Bag)", "ಬ್ಯಾಗ್", "🎒"),
                LearningItem("ಬರೆವ ಪುಸ್ತಕ", "Bareva Pustaka (Notebook)", "ಬರೆವ ಪುಸ್ತಕ", "📝"),
                LearningItem("ರಬ್ಬರ್", "Rabbar (Eraser)", "ರಬ್ಬರ್", "🧽")
            )
            "ml" -> listOf(
                LearningItem("പുസ്തകം", "Pusthakam (Book)", "പുസ്തകം", "📖"),
                LearningItem("പേന", "Pena (Pen)", "പേന", "🖊️"),
                LearningItem("ബാഗ്", "Bag (School Bag)", "ബാഗ്", "🎒"),
                LearningItem("നോട്ട്ബുക്ക്", "Notebook", "നോട്ട്ബുക്ക്", "📝"),
                LearningItem("റബ്ബർ", "Rabbar (Eraser)", "റബ്ബർ", "🧽")
            )
            else -> listOf(
                LearningItem("Book", "Book", "Book", "📖"),
                LearningItem("Pen", "Pen", "Pen", "🖊️"),
                LearningItem("Bag", "Bag", "Bag", "🎒"),
                LearningItem("Notebook", "Notebook", "Notebook", "📝"),
                LearningItem("Eraser", "Eraser", "Eraser", "🧽")
            )
        },
        LessonCategory.WEATHER to when (langCode) {
            "te" -> listOf(
                LearningItem("ఎండ", "Enda (Sunny)", "ఎండ", "☀️"),
                LearningItem("వాన", "Vaana (Rainy)", "వాన", "🌧️"),
                LearningItem("గాలి", "Gaali (Windy)", "గాలి", "💨"),
                LearningItem("మబ్బు", "Mabbu (Cloudy)", "మబ్బు", "☁️"),
                LearningItem("చలి", "Chali (Cold)", "చలి", "❄️")
            )
            "hi" -> listOf(
                LearningItem("धूप", "Dhoop (Sunny)", "धूप", "☀️"),
                LearningItem("बारिश", "Barish (Rainy)", "बारिश", "🌧️"),
                LearningItem("हवा", "Hawa (Windy)", "हवा", "💨"),
                LearningItem("बादल", "Badal (Cloudy)", "बादल", "☁️"),
                LearningItem("सर्दी", "Sardi (Cold)", "सर्दी", "❄️")
            )
            "ta" -> listOf(
                LearningItem("வெயில்", "Veyil (Sunny)", "வெயில்", "☀️"),
                LearningItem("மழை", "Mazhai (Rainy)", "மழை", "🌧️"),
                LearningItem("காற்று", "Kattru (Windy)", "காற்று", "💨"),
                LearningItem("மேகம்", "Megam (Cloudy)", "மேகம்", "☁️"),
                LearningItem("குளிர்", "Kulir (Cold)", "குளிர்", "❄️")
            )
            "kn" -> listOf(
                LearningItem("ಬಿಸಿಲು", "Bisilu (Sunny)", "ಬಿಸಿಲು", "☀️"),
                LearningItem("ಮಳೆ", "Male (Rainy)", "ಮಳೆ", "🌧️"),
                LearningItem("ಗಾಳಿ", "Gaali (Windy)", "ಗಾಳಿ", "💨"),
                LearningItem("ಮೋಡ", "Moda (Cloudy)", "ಮೋಡ", "☁️"),
                LearningItem("ಚಳಿ", "Chali (Cold)", "ಚಳಿ", "❄️")
            )
            "ml" -> listOf(
                LearningItem("വെയിൽ", "Veyil (Sunny)", "വെയിൽ", "☀️"),
                LearningItem("മഴ", "Mazha (Rainy)", "മഴ", "🌧️"),
                LearningItem("കാറ്റ്", "Kattu (Windy)", "കാറ്റ്", "💨"),
                LearningItem("മേഘം", "Megham (Cloudy)", "മേഘം", "☁️"),
                LearningItem("തണുപ്പ്", "Thanuppu (Cold)", "തണുപ്പ്", "❄️")
            )
            else -> listOf(
                LearningItem("Sunny", "Sunny", "Sunny", "☀️"),
                LearningItem("Rainy", "Rainy", "Rainy", "🌧️"),
                LearningItem("Windy", "Windy", "Windy", "💨"),
                LearningItem("Cloudy", "Cloudy", "Cloudy", "☁️"),
                LearningItem("Cold", "Cold", "Cold", "❄️")
            )
        }
    )
}

class LanguageConfig(
    val code: String,
    val nativeName: String,
    val flag: String,
    val title: String,
    lessons: Map<LessonCategory, List<LearningItem>>
) {
    val lessons: Map<LessonCategory, List<LearningItem>> = lessons + getNewCategoryLessons(code)
}

// Global lists of vowels and consonants for each language
val teluguVowels = listOf(
    LearningItem("అ", "a (short)", "అ"), LearningItem("ఆ", "aa (long)", "ఆఆ"),
    LearningItem("ఇ", "i (short)", "ఇ"), LearningItem("ఈ", "ee (long)", "ఈఈ"),
    LearningItem("ఉ", "u (short)", "ఉ"), LearningItem("ఊ", "oo (long)", "ఊఊ"),
    LearningItem("ఋ", "ru", "ఋ"), LearningItem("ఎ", "e (short)", "ఎ"),
    LearningItem("ఏ", "ae (long)", "ఏఏ"), LearningItem("ఐ", "ai", "ఐ"),
    LearningItem("ఒ", "o (short)", "ఒ"), LearningItem("ఓ", "oo (long)", "ఓఓ"),
    LearningItem("ఔ", "au", "ఔ"), LearningItem("అం", "am", "అం"), LearningItem("అః", "aha", "అః")
)

val teluguConsonants = listOf(
    LearningItem("క", "ka", "క"), LearningItem("ఖ", "kha (long)", "ఖ"), LearningItem("గ", "ga", "గ"), LearningItem("ఘ", "gha (long)", "ఘ"), LearningItem("ఙ", "gna", "ఙ"),
    LearningItem("చ", "cha", "చ"), LearningItem("ఛ", "chha (long)", "ఛ"), LearningItem("జ", "ja", "జ"), LearningItem("ఝ", "jha (long)", "ఝ"), LearningItem("ఞ", "nya", "ఞ"),
    LearningItem("ట", "ta", "ట"), LearningItem("ఠ", "tha (long)", "ఠ"), LearningItem("డ", "da", "డ"), LearningItem("ఢ", "dha (long)", "ఢ"), LearningItem("ణ", "na", "ణ"),
    LearningItem("త", "ta", "త"), LearningItem("థ", "tha (long)", "థ"), LearningItem("ద", "da", "ద"), LearningItem("ధ", "dha (long)", "ధ"), LearningItem("న", "na", "న"),
    LearningItem("ప", "pa", "ప"), LearningItem("ఫ", "pha (long)", "ఫ"), LearningItem("బ", "ba", "బ"), LearningItem("భ", "bha (long)", "భ"), LearningItem("మ", "ma", "మ"),
    LearningItem("య", "ya", "య"), LearningItem("ర", "ra", "ర"), LearningItem("ల", "la", "ల"), LearningItem("వ", "va", "వ"),
    LearningItem("శ", "sha", "శ"), LearningItem("ష", "shha", "ష"), LearningItem("స", "sa", "స"), LearningItem("హ", "ha", "హ"),
    LearningItem("ళ", "la", "ళ"), LearningItem("క్ష", "ksha (long)", "క్ష"), LearningItem("ఱ", "rra (Bandi Ra)", "బండిరా")
)

val englishLetters = listOf(
    LearningItem("A", "ai", "A"), LearningItem("B", "bee", "B"), LearningItem("C", "cee", "C"), LearningItem("D", "dee", "D"), LearningItem("E", "ee", "E"),
    LearningItem("F", "ef", "F"), LearningItem("G", "jee", "G"), LearningItem("H", "aych", "H"), LearningItem("I", "eye", "I"), LearningItem("J", "jay", "J"),
    LearningItem("K", "kay", "K"), LearningItem("L", "el", "L"), LearningItem("M", "em", "M"), LearningItem("N", "en", "N"), LearningItem("O", "oh", "O"),
    LearningItem("P", "pee", "P"), LearningItem("Q", "cue", "Q"), LearningItem("R", "ar", "R"), LearningItem("S", "es", "S"), LearningItem("T", "tee", "T"),
    LearningItem("U", "you", "U"), LearningItem("V", "vee", "V"), LearningItem("W", "double-u", "W"), LearningItem("X", "ex", "X"), LearningItem("Y", "why", "Y"), LearningItem("Z", "zed", "Z")
)

val tamilVowels = listOf(
    LearningItem("அ", "a (short)", "அ"), LearningItem("ஆ", "aa (long)", "ஆ"),
    LearningItem("இ", "i (short)", "இ"), LearningItem("ஈ", "ee (long)", "ஈ"),
    LearningItem("உ", "u (short)", "உ"), LearningItem("ஊ", "oo (long)", "ஊ"),
    LearningItem("எ", "e (short)", "எ"), LearningItem("ஏ", "ae (long)", "ஏ"),
    LearningItem("ஐ", "ai", "ஐ"), LearningItem("ஒ", "o (short)", "ஒ"),
    LearningItem("ஓ", "oo (long)", "ஓ"), LearningItem("ஔ", "au", "ஔ"), LearningItem("ஃ", "ah", "ஃ")
)

val tamilConsonants = listOf(
    LearningItem("க்", "ik", "க்"), LearningItem("ங்", "ing", "ங்"),
    LearningItem("ச்", "ich", "ச்"), LearningItem("ஞ்", "inj", "ஞ்"),
    LearningItem("ட்", "it", "ட்"), LearningItem("ண்", "inn (long)", "ண்"),
    LearningItem("த்", "ith", "த்"), LearningItem("ந்", "inth", "ந்"),
    LearningItem("ப்", "ip", "ப்"), LearningItem("ம்", "im", "ம்"),
    LearningItem("ய்", "iy", "ய்"), LearningItem("ர்", "ir", "ர்"),
    LearningItem("ல்", "il", "ல்"), LearningItem("வ்", "iv", "வ்"),
    LearningItem("ழ்", "izh (long)", "ழ்"), LearningItem("ள்", "ill (long)", "ள்"),
    LearningItem("ற்", "irr", "ற்"), LearningItem("ன்", "inn", "ன்")
)

val hindiVowels = listOf(
    LearningItem("अ", "a (short)", "अ"), LearningItem("आ", "aa (long)", "आ"),
    LearningItem("इ", "i (short)", "इ"), LearningItem("ई", "ee (long)", "ई"),
    LearningItem("उ", "u (short)", "उ"), LearningItem("ऊ", "oo (long)", "ऊ"),
    LearningItem("ऋ", "ri", "ऋ"), LearningItem("ए", "e", "ए"),
    LearningItem("ऐ", "ai", "ऐ"), LearningItem("ओ", "o", "ओ"),
    LearningItem("औ", "au", "औ"), LearningItem("अं", "am", "अं"), LearningItem("अः", "aha", "अः")
)

val hindiConsonants = listOf(
    LearningItem("क", "ka", "क"), LearningItem("ख", "kha (long)", "ख"), LearningItem("ग", "ga", "ग"), LearningItem("घ", "gha (long)", "घ"), LearningItem("ङ", "nga", "ङ"),
    LearningItem("च", "cha", "च"), LearningItem("छ", "chha (long)", "छ"), LearningItem("ज", "ja", "ज"), LearningItem("झ", "jha (long)", "झ"), LearningItem("ञ", "nya", "ञ"),
    LearningItem("ट", "ta", "ट"), LearningItem("ठ", "tha (long)", "ठ"), LearningItem("ड", "da", "ड"), LearningItem("ढ", "dha (long)", "ढ"), LearningItem("ण", "na", "ण"),
    LearningItem("त", "ta", "त"), LearningItem("थ", "tha (long)", "थ"), LearningItem("द", "da", "द"), LearningItem("ध", "dha (long)", "ध"), LearningItem("न", "na", "न"),
    LearningItem("प", "pa", "प"), LearningItem("फ", "pha (long)", "फ"), LearningItem("ब", "ba", "ब"), LearningItem("भ", "bha (long)", "भ"), LearningItem("म", "ma", "म"),
    LearningItem("य", "ya", "य"), LearningItem("र", "ra", "र"), LearningItem("ल", "la", "ल"), LearningItem("व", "va", "व"),
    LearningItem("श", "sha", "श"), LearningItem("ष", "shha", "ष"), LearningItem("स", "sa", "स"), LearningItem("ह", "ha", "ह")
)

val arabicVowels = listOf(
    LearningItem("ا", "alif", "أَلِفْ"), LearningItem("ب", "baa", "بَاءْ"), LearningItem("ت", "taa", "تَاءْ"), LearningItem("ث", "thaa", "ثَاءْ"),
    LearningItem("ج", "jeem", "جِيمْ"), LearningItem("ح", "haa", "حَاءْ"), LearningItem("خ", "khaa", "خَاءْ"), LearningItem("د", "daal", "دَالْ")
)

val arabicConsonants = listOf(
    LearningItem("ذ", "thaal", "ذَالْ"), LearningItem("ر", "raa", "رَاءْ"), LearningItem("ز", "zay", "زَايْ"),
    LearningItem("س", "seen", "سِينْ"), LearningItem("ش", "sheen", "شِينْ"), LearningItem("ص", "saad", "صَادْ"),
    LearningItem("ض", "daad", "ضَادْ"), LearningItem("ط", "taa", "طَاءْ"), LearningItem("ظ", "zaa", "ظَاءْ"),
    LearningItem("ع", "ayn", "عَيْنْ"), LearningItem("غ", "ghayn", "غَيْنْ"), LearningItem("ف", "faa", "فَاءْ"),
    LearningItem("ق", "qaaf", "قَافْ"), LearningItem("ك", "kaaf", "كَافْ"), LearningItem("ل", "laam", "لَامْ"),
    LearningItem("م", "meem", "مِيمْ"), LearningItem("ن", "noon", "نُونْ"), LearningItem("هـ", "haa", "هَاءْ"),
    LearningItem("و", "waw", "وَاوْ"), LearningItem("ي", "yaa", "يَاءْ")
)

val kannadaVowels = listOf(
    LearningItem("ಅ", "a (short)", "ಅ"), LearningItem("ಆ", "aa (long)", "ಆ"),
    LearningItem("ಇ", "i (short)", "ಇ"), LearningItem("ಈ", "ee (long)", "ಈ"),
    LearningItem("ಉ", "u (short)", "ಉ"), LearningItem("ಊ", "oo (long)", "ಊ"),
    LearningItem("ಋ", "ru", "ಋ"), LearningItem("ಎ", "e (short)", "ಎ"),
    LearningItem("ಏ", "ae (long)", "ಏ"), LearningItem("ಐ", "ai", "ಐ"),
    LearningItem("ಒ", "o (short)", "ಒ"), LearningItem("ಓ", "oo (long)", "ಓ"),
    LearningItem("ಔ", "au", "ಔ"), LearningItem("ಅಂ", "am", "ಅಂ"), LearningItem("ಅಃ", "aha", "ಅಃ")
)

val kannadaConsonants = listOf(
    LearningItem("ಕ", "ka", "ಕ"), LearningItem("ಖ", "kha (long)", "ಖ"), LearningItem("ಗ", "ga", "ಗ"), LearningItem("ಘ", "gha (long)", "ಘ"), LearningItem("ಙ", "gna", "ಙ"),
    LearningItem("ಚ", "cha", "ಚ"), LearningItem("ಛ", "chha (long)", "ಛ"), LearningItem("ಜ", "ja", "ಜ"), LearningItem("ಝ", "jha (long)", "ಝ"), LearningItem("ಞ", "nya", "ಞ"),
    LearningItem("ಟ", "ta", "ಟ"), LearningItem("ಠ", "tha (long)", "ಠ"), LearningItem("ಡ", "da", "ಡ"), LearningItem("ಢ", "dha (long)", "ಢ"), LearningItem("ಣ", "na", "ಣ"),
    LearningItem("ತ", "ta", "ತ"), LearningItem("ಥ", "tha (long)", "ಥ"), LearningItem("ದ", "da", "ದ"), LearningItem("ಧ", "dha (long)", "ಧ"), LearningItem("ನ", "na", "ನ"),
    LearningItem("ಪ", "pa", "ಪ"), LearningItem("ಫ", "pha (long)", "ಫ"), LearningItem("ಬ", "ba", "ಬ"), LearningItem("ಭ", "bha (long)", "ಭ"), LearningItem("ಮ", "ma", "ಮ"),
    LearningItem("ಯ", "ya", "ಯ"), LearningItem("ರ", "ra", "ರ"), LearningItem("ಲ", "la", "ಲ"), LearningItem("ವ", "va", "ವ"),
    LearningItem("ಶ", "sha", "ಶ"), LearningItem("ಷ", "shha", "ಷ"), LearningItem("ಸ", "sa", "ಸ"), LearningItem("ಹ", "ha", "ಹ"),
    LearningItem("ಳ", "la", "ಳ")
)

val malayalamVowels = listOf(
    LearningItem("അ", "a (short)", "അ"), LearningItem("ആ", "aa (long)", "ആ"),
    LearningItem("ഇ", "i (short)", "ഇ"), LearningItem("ഈ", "ee (long)", "ഈ"),
    LearningItem("ഉ", "u (short)", "ഉ"), LearningItem("ഊ", "oo (long)", "ഊ"),
    LearningItem("ഋ", "ru", "ഋ"), LearningItem("എ", "e (short)", "എ"),
    LearningItem("ഏ", "ae (long)", "ഏ"), LearningItem("ഐ", "ai", "ഐ"),
    LearningItem("ഒ", "o (short)", "ഒ"), LearningItem("ഓ", "oo (long)", "ഓ"),
    LearningItem("ಔ", "au", "ಔ"), LearningItem("അം", "am", "അം"), LearningItem("അഃ", "aha", "അഃ")
)

val malayalamConsonants = listOf(
    LearningItem("ക", "ka", "ക"), LearningItem("ഖ", "kha (long)", "ക"), LearningItem("ഗ", "ga", "ഗ"), LearningItem("ಘ", "gha (long)", "ಘ"), LearningItem("ങ", "nga", "ങ"),
    LearningItem("ച", "cha", "ച"), LearningItem("ഛ", "chha (long)", "ഛ"), LearningItem("ಜ", "ja", "ജ"), LearningItem("ഝ", "jha (long)", "ഝ"), LearningItem("ഞ", "nya", "ഞ"),
    LearningItem("ಟ", "ta", "ಟ"), LearningItem("ഠ", "tha (long)", "ഠ"), LearningItem("ಡ", "da", "ഡ"), LearningItem("ഢ", "dha (long)", "ഢ"), LearningItem("ണ", "na", "ണ"),
    LearningItem("ത", "ta", "ത"), LearningItem("ഥ", "tha (long)", "ഥ"), LearningItem("ദ", "da", "ദ"), LearningItem("ധ", "dha (long)", "ധ"), LearningItem("ന", "na", "ന"),
    LearningItem("പ", "pa", "പ"), LearningItem("ഫ", "pha (long)", "ഫ"), LearningItem("ಬ", "ba", "ബ"), LearningItem("ഭ", "bha (long)", "ഭ"), LearningItem("മ", "ma", "മ"),
    LearningItem("യ", "ya", "യ"), LearningItem("ರ", "ra", "ര"), LearningItem("ല", "la", "ല"), LearningItem("വ", "va", "വ"),
    LearningItem("ശ", "sha", "ശ"), LearningItem("ഷ", "shha", "ഷ"), LearningItem("സ", "sa", "സ"), LearningItem("ഹ", "ha", "ഹ"),
    LearningItem("ള", "la", "ള"), LearningItem("ഴ", "zha (long)", "ഴ"), LearningItem("റ", "ra", "റ")
)

val bengaliVowels = listOf(
    LearningItem("অ", "o", "অ"), LearningItem("আ", "aa", "আ"),
    LearningItem("ই", "i", "ই"), LearningItem("ঈ", "ee", "ঈ"),
    LearningItem("উ", "u", "উ"), LearningItem("ঊ", "oo", "ঊ"),
    LearningItem("ঋ", "ri", "ঋ"), LearningItem("এ", "e", "এ"),
    LearningItem("ঐ", "oi", "ঐ"), LearningItem("ও", "o", "ও"),
    LearningItem("ঔ", "ou", "ঔ")
)

val bengaliConsonants = listOf(
    LearningItem("ক", "ko", "ক"), LearningItem("খ", "kho", "খ"), LearningItem("গ", "go", "গ"), LearningItem("ঘ", "gho", "ঘ"), LearningItem("ঙ", "ungo", "ঙ"),
    LearningItem("চ", "cho", "চ"), LearningItem("ছ", "chho", "ছ"), LearningItem("জ", "jo", "জ"), LearningItem("ঝ", "jho", "ঝ"), LearningItem("ঞ", "nyo", "ঞ"),
    LearningItem("ট", "to", "ট"), LearningItem("ঠ", "tho", "ঠ"), LearningItem("ড", "do", "ড"), LearningItem("ঢ", "dho", "ঢ"), LearningItem("ণ", "nno", "ণ"),
    LearningItem("ত", "to", "ত"), LearningItem("থ", "tho", "থ"), LearningItem("দ", "do", "দ"), LearningItem("ধ", "dho", "ধ"), LearningItem("ন", "no", "ন"),
    LearningItem("প", "po", "প"), LearningItem("ফ", "pho", "ফ"), LearningItem("ব", "bo", "ব"), LearningItem("ভ", "bho", "ভ"), LearningItem("ম", "mo", "ম"),
    LearningItem("য", "yo", "য"), LearningItem("র", "ro", "র"), LearningItem("ল", "lo", "ল"), LearningItem("শ", "sho", "শ"), LearningItem("ষ", "sso", "ষ"),
    LearningItem("স", "so", "স"), LearningItem("হ", "ho", "হ"), LearningItem("ড়", "rro", "ড়"), LearningItem("ঢ়", "rrho", "ঢ়"), LearningItem("য়", "yyo", "য়")
)

val marathiVowels = listOf(
    LearningItem("अ", "a", "अ"), LearningItem("आ", "aa", "आ"),
    LearningItem("इ", "i", "इ"), LearningItem("ई", "ee", "ई"),
    LearningItem("उ", "u", "उ"), LearningItem("ऊ", "oo", "ऊ"),
    LearningItem("ऋ", "ru", "ऋ"), LearningItem("ए", "e", "ए"),
    LearningItem("ऐ", "ai", "ऐ"), LearningItem("ओ", "o", "ओ"),
    LearningItem("औ", "au", "औ"), LearningItem("अं", "am", "अं"), LearningItem("अः", "aha", "अः")
)

val marathiConsonants = listOf(
    LearningItem("क", "ka", "क"), LearningItem("ख", "kha", "ख"), LearningItem("ग", "ga", "ग"), LearningItem("घ", "gha", "घ"), LearningItem("ङ", "gna", "ङ"),
    LearningItem("च", "cha", "च"), LearningItem("छ", "chha", "छ"), LearningItem("ज", "ja", "ज"), LearningItem("झ", "jha", "झ"), LearningItem("ञ", "nya", "ञ"),
    LearningItem("ट", "ta", "ट"), LearningItem("ठ", "tha", "ठ"), LearningItem("ड", "da", "ड"), LearningItem("ढ", "dha", "ढ"), LearningItem("ण", "na", "ण"),
    LearningItem("त", "ta", "त"), LearningItem("थ", "tha", "थ"), LearningItem("द", "da", "द"), LearningItem("ध", "dha", "ध"), LearningItem("न", "na", "न"),
    LearningItem("प", "pa", "प"), LearningItem("फ", "pha", "फ"), LearningItem("ब", "ba", "ब"), LearningItem("भ", "bha", "भ"), LearningItem("म", "ma", "म"),
    LearningItem("य", "ya", "य"), LearningItem("र", "ra", "र"), LearningItem("ल", "la", "ल"), LearningItem("व", "va", "व"),
    LearningItem("श", "sha", "श"), LearningItem("ष", "sha", "ष"), LearningItem("स", "sa", "स"), LearningItem("ह", "ha", "ह"),
    LearningItem("ळ", "la", "ळ"), LearningItem("क्ष", "ksha", "क्ष"), LearningItem("ज्ञ", "dnya", "ज्ञ")
)

val gujaratiVowels = listOf(
    LearningItem("અ", "a", "અ"), LearningItem("આ", "aa", "આ"),
    LearningItem("ઇ", "i", "ઇ"), LearningItem("ઈ", "ee", "ઈ"),
    LearningItem("ઉ", "u", "ઉ"), LearningItem("ઊ", "oo", "ઊ"),
    LearningItem("ઋ", "ru", "ઋ"), LearningItem("એ", "e", "એ"),
    LearningItem("ઐ", "ai", "ઐ"), LearningItem("ઓ", "o", "ઓ"),
    LearningItem("ઔ", "au", "ઔ"), LearningItem("અં", "am", "અં"), LearningItem("અઃ", "aha", "અઃ")
)

val gujaratiConsonants = listOf(
    LearningItem("ક", "ka", "ક"), LearningItem("ખ", "kha", "ખ"), LearningItem("ગ", "ga", "ગ"), LearningItem("ઘ", "gha", "ઘ"), LearningItem("ઙ", "gna", "ઙ"),
    LearningItem("ચ", "cha", "ચ"), LearningItem("છ", "chha", "છ"), LearningItem("જ", "ja", "જ"), LearningItem("ઝ", "jha", "ઝ"), LearningItem("ઞ", "nya", "ઞ"),
    LearningItem("ટ", "ta", "ટ"), LearningItem("ઠ", "tha", "ઠ"), LearningItem("ડ", "da", "ડ"), LearningItem("ઢ", "dha", "ઢ"), LearningItem("ણ", "na", "ણ"),
    LearningItem("ત", "ta", "ત"), LearningItem("થ", "tha", "થ"), LearningItem("દ", "da", "દ"), LearningItem("ધ", "dha", "ધ"), LearningItem("ન", "na", "ન"),
    LearningItem("પ", "pa", "પ"), LearningItem("ફ", "pha", "ફ"), LearningItem("બ", "ba", "બ"), LearningItem("ભ", "bha", "ભ"), LearningItem("મ", "ma", "મ"),
    LearningItem("ય", "ya", "ય"), LearningItem("ર", "ra", "ર"), LearningItem("લ", "la", "લ"), LearningItem("વ", "va", "વ"),
    LearningItem("શ", "sha", "શ"), LearningItem("ષ", "sha", "ષ"), LearningItem("સ", "sa", "સ"), LearningItem("હ", "ha", "હ"),
    LearningItem("ળ", "la", "ળ"), LearningItem("ક્ષ", "ksha", "ક્ષ"), LearningItem("જ્ઞ", "gnya", "જ્ઞ")
)

fun generateEnglishBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("Come", "come", "🚶"),
        Triple("Go", "go", "🏃"),
        Triple("What", "what", "❓"),
        Triple("Yes", "yes", "✅"),
        Triple("No", "no", "❌"),
        Triple("Hello", "hello", "👋"),
        Triple("Please", "please", "🥺"),
        Triple("Thanks", "thanks", "🙏"),
        Triple("Good", "good", "👍"),
        Triple("Bad", "bad", "👎"),
        Triple("Happy", "happy", "😊"),
        Triple("Sad", "sad", "😢"),
        Triple("Big", "big", "🐘"),
        Triple("Small", "small", "🐭"),
        Triple("Hot", "hot", "🔥"),
        Triple("Cold", "cold", "❄️"),
        Triple("Water", "water", "💧"),
        Triple("Food", "food", "🍎"),
        Triple("Friend", "friend", "🤝"),
        Triple("Home", "home", "🏠"),
        Triple("Book", "book", "📖"),
        Triple("Pen", "pen", "✍️"),
        Triple("School", "school", "🏫"),
        Triple("Love", "love", "❤️"),
        Triple("Smile", "smile", "😀"),
        Triple("Day", "day", "☀️"),
        Triple("Night", "night", "🌙"),
        Triple("Sun", "sun", "🌞"),
        Triple("Moon", "moon", "🌛"),
        Triple("Star", "star", "⭐"),
        Triple("Sky", "sky", "☁️"),
        Triple("Tree", "tree", "🌳"),
        Triple("Flower", "flower", "🌸"),
        Triple("Fruit", "fruit", "🍓"),
        Triple("Animal", "animal", "🦁"),
        Triple("Bird", "bird", "🐦"),
        Triple("Fish", "fish", "🐟"),
        Triple("Boy", "boy", "👦"),
        Triple("Girl", "girl", "👧"),
        Triple("Father", "father", "👨"),
        Triple("Mother", "mother", "👩"),
        Triple("Brother", "brother", "👦"),
        Triple("Sister", "sister", "👧"),
        Triple("Play", "play", "🎮"),
        Triple("Run", "run", "🏃"),
        Triple("Walk", "walk", "🚶"),
        Triple("Sleep", "sleep", "😴"),
        Triple("Eat", "eat", "🍽️"),
        Triple("Drink", "drink", "🥤"),
        Triple("See", "see", "👁️"),
        Triple("Hear", "hear", "👂"),
        Triple("Speak", "speak", "🗣️"),
        Triple("Write", "write", "📝"),
        Triple("Read", "read", "📖"),
        Triple("Learn", "learn", "🧠"),
        Triple("Work", "work", "💼"),
        Triple("Time", "time", "⏰"),
        Triple("Today", "today", "📅"),
        Triple("Now", "now", "⏰"),
        Triple("Here", "here", "📍"),
        Triple("There", "there", "👉"),
        Triple("Who", "who", "❓"),
        Triple("Where", "where", "🗺️"),
        Triple("When", "when", "🕒"),
        Triple("Why", "why", "❓"),
        Triple("How", "how", "⚙️"),
        Triple("River", "river", "🏞️"),
        Triple("Mountain", "mountain", "⛰️"),
        Triple("Sea", "sea", "🌊"),
        Triple("Chair", "chair", "🪑"),
        Triple("Table", "table", "🪵"),
        Triple("Light", "light", "💡"),
        Triple("Door", "door", "🚪"),
        Triple("Key", "key", "🔑"),
        Triple("Paper", "paper", "📄"),
        Triple("Stone", "stone", "🪨"),
        Triple("Morning", "morning", "🌅"),
        Triple("Evening", "evening", "🌇"),
        Triple("Help", "help", "🆘"),
        Triple("Stop", "stop", "🛑"),
        Triple("Start", "start", "🎬"),
        Triple("Open", "open", "🔓"),
        Triple("Close", "close", "🔒"),
        Triple("Give", "give", "🤲"),
        Triple("Take", "take", "🫴"),
        Triple("Fire", "fire", "🔥"),
        Triple("Wind", "wind", "💨"),
        Triple("Earth", "earth", "🌍"),
        Triple("Rain", "rain", "🌧️"),
        Triple("Cloud", "cloud", "☁️"),
        Triple("Life", "life", "🌱"),
        Triple("Name", "name", "🏷️"),
        Triple("City", "city", "🏙️"),
        Triple("Road", "road", "🛣️"),
        Triple("Car", "car", "🚗"),
        Triple("Milk", "milk", "🥛"),
        Triple("Tea", "tea", "☕"),
        Triple("Bread", "bread", "🍞"),
        Triple("Child", "child", "👶"),
        Triple("Family", "family", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateTeluguBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("రా", "Raa (Come)", "🚶"),
        Triple("వెళ్ళు", "Vellu (Go)", "🏃"),
        Triple("ఏమిటి", "Eamiti (What)", "❓"),
        Triple("అవును", "Avunu (Yes)", "✅"),
        Triple("కాదు", "Kaadu (No)", "❌"),
        Triple("నమస్కారం", "Namaskaram (Hello)", "👋"),
        Triple("దయచేసి", "Dayachesi (Please)", "🥺"),
        Triple("ధన్యవాదాలు", "Dhanyavadalu (Thanks)", "🙏"),
        Triple("మంచి", "Manchi (Good)", "👍"),
        Triple("చెడు", "Chedu (Bad)", "👎"),
        Triple("సంతోషం", "Santhosham (Happy)", "😊"),
        Triple("బాధ", "Baadha (Sad)", "😢"),
        Triple("పెద్ద", "Pedda (Big)", "🐘"),
        Triple("చిన్న", "Chinna (Small)", "🐭"),
        Triple("వేడి", "Vedi (Hot)", "🔥"),
        Triple("చల్లని", "Challani (Cold)", "❄️"),
        Triple("నీరు", "Neeru (Water)", "💧"),
        Triple("ఆహారం", "Aaharam (Food)", "🍎"),
        Triple("స్నేహితుడు", "Snehitudu (Friend)", "🤝"),
        Triple("ఇల్లు", "Illu (Home)", "🏠"),
        Triple("పుస్తకం", "Pustakam (Book)", "📖"),
        Triple("కలం", "Kalam (Pen)", "✍️"),
        Triple("బడి", "Badi (School)", "🏫"),
        Triple("ప్రేమ", "Prema (Love)", "❤️"),
        Triple("నవ్వు", "Navvu (Smile)", "😀"),
        Triple("పగలు", "Pagalu (Day)", "☀️"),
        Triple("రాత్రి", "Raatri (Night)", "🌙"),
        Triple("సూర్యుడు", "Sooryudu (Sun)", "🌞"),
        Triple("చంద్రుడు", "Chandrudu (Moon)", "🌛"),
        Triple("నక్షత్రం", "Nakshatram (Star)", "⭐"),
        Triple("ఆకాశం", "Aakasham (Sky)", "☁️"),
        Triple("చెట్టు", "Chettu (Tree)", "🌳"),
        Triple("పువ్వు", "Puvvu (Flower)", "🌸"),
        Triple("పండు", "Pandu (Fruit)", "🍓"),
        Triple("జంతువు", "Janthuvu (Animal)", "🦁"),
        Triple("పక్షి", "Pakshi (Bird)", "🐦"),
        Triple("చేప", "Chepa (Fish)", "🐟"),
        Triple("అబ్బాయి", "Abbaayi (Boy)", "👦"),
        Triple("అమ్మాయి", "Ammaayi (Girl)", "👧"),
        Triple("తండ్రి", "Tandri (Father)", "👨"),
        Triple("తల్లి", "Talli (Mother)", "👩"),
        Triple("సోదరుడు", "Sodarudu (Brother)", "👦"),
        Triple("సోదరి", "Sodari (Sister)", "👧"),
        Triple("ఆట", "Aata (Play)", "🎮"),
        Triple("పరుగు", "Parugu (Run)", "🏃"),
        Triple("నడక", "Nadaka (Walk)", "🚶"),
        Triple("నిద్ర", "Nidra (Sleep)", "😴"),
        Triple("తిను", "Tinu (Eat)", "🍽️"),
        Triple("త్రాగు", "Thraagu (Drink)", "🥤"),
        Triple("చూడు", "Choodu (See)", "👁️"),
        Triple("విను", "Vinu (Hear)", "👂"),
        Triple("మాట్లాడు", "Maatlaadu (Speak)", "🗣️"),
        Triple("రాయి", "Raayi (Write)", "📝"),
        Triple("చదువు", "Chaduvu (Read)", "📖"),
        Triple("నేర్చుకో", "Nerchuko (Learn)", "🧠"),
        Triple("పని", "Pani (Work)", "💼"),
        Triple("సమయం", "Samayam (Time)", "⏰"),
        Triple("ఈరోజు", "Eeroju (Today)", "📅"),
        Triple("ఇప్పుడు", "Ippudu (Now)", "⏰"),
        Triple("ఇక్కడ", "Ikkada (Here)", "📍"),
        Triple("అక్కడ", "Akkada (There)", "👉"),
        Triple("ఎవరు", "Evaru (Who)", "❓"),
        Triple("ఎక్కడ", "Ekkada (Where)", "🗺️"),
        Triple("ఎప్పుడు", "Eppudu (When)", "🕒"),
        Triple("ఎందుకు", "Enduku (Why)", "❓"),
        Triple("ఎలా", "Elaa (How)", "⚙️"),
        Triple("నది", "Nadi (River)", "🏞️"),
        Triple("పర్వతం", "Parvatham (Mountain)", "⛰️"),
        Triple("సముద్రం", "Samudram (Sea)", "🌊"),
        Triple("కుర్చీ", "Kurchi (Chair)", "🪑"),
        Triple("బల్ల", "Balla (Table)", "🪵"),
        Triple("కాంతి", "Kanthi (Light)", "💡"),
        Triple("తలుపు", "Talupu (Door)", "🚪"),
        Triple("తాళంచెవి", "Thalamchevi (Key)", "🔑"),
        Triple("కాగితం", "Kaagitham (Paper)", "📄"),
        Triple("రాయి", "Raayi (Stone)", "🪨"),
        Triple("ఉదయం", "Udayam (Morning)", "🌅"),
        Triple("సాయంత్రం", "Saayantram (Evening)", "🌇"),
        Triple("సహాయం", "Sahaayam (Help)", "🆘"),
        Triple("ఆగు", "Aagu (Stop)", "🛑"),
        Triple("ప్రారంభించు", "Praarambhinchu (Start)", "🎬"),
        Triple("తెరువు", "Theruvu (Open)", "🔓"),
        Triple("మూయు", "Mooyu (Close)", "🔒"),
        Triple("ఇవ్వు", "Ivvu (Give)", "🤲"),
        Triple("తీసుకో", "Theesuko (Take)", "🫴"),
        Triple("నిప్పు", "Nippu (Fire)", "🔥"),
        Triple("గాలి", "Gaali (Wind)", "💨"),
        Triple("భూమి", "Bhoomi (Earth)", "🌍"),
        Triple("వర్షం", "Varsham (Rain)", "🌧️"),
        Triple("మబ్బు", "Mabbu (Cloud)", "☁️"),
        Triple("జీవితం", "Jeevitham (Life)", "🌱"),
        Triple("పేరు", "Peru (Name)", "🏷️"),
        Triple("నగరం", "Nagaram (City)", "🏙️"),
        Triple("రోడ్డు", "Roddu (Road)", "🛣️"),
        Triple("కారు", "Kaaru (Car)", "🚗"),
        Triple("పాలు", "Paalu (Milk)", "🥛"),
        Triple("టీ", "Tea (Tea)", "☕"),
        Triple("రొట్టె", "Rotte (Bread)", "🍞"),
        Triple("పిల్లాడు", "Pillaadu (Child)", "👶"),
        Triple("కుటుంబం", "Kutumbam (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateHindiBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("आओ", "Aao (Come)", "🚶"),
        Triple("जाओ", "Jaao (Go)", "🏃"),
        Triple("क्या", "Kya (What)", "❓"),
        Triple("हाँ", "Haan (Yes)", "✅"),
        Triple("नहीं", "Nahin (No)", "❌"),
        Triple("नमस्ते", "Namaste (Hello)", "👋"),
        Triple("कृपया", "Kripya (Please)", "🥺"),
        Triple("धन्यवाद", "Dhanyavaad (Thanks)", "🙏"),
        Triple("अच्छा", "Achha (Good)", "👍"),
        Triple("बुरा", "Bura (Bad)", "👎"),
        Triple("खुश", "Khush (Happy)", "😊"),
        Triple("दुखी", "Dukhee (Sad)", "😢"),
        Triple("बड़ा", "Bada (Big)", "🐘"),
        Triple("छोटा", "Chhota (Small)", "🐭"),
        Triple("गर्म", "Garm (Hot)", "🔥"),
        Triple("ठंडा", "Thanda (Cold)", "❄️"),
        Triple("पानी", "Paanee (Water)", "💧"),
        Triple("खाना", "Khaana (Food)", "🍎"),
        Triple("दोस्त", "Dost (Friend)", "🤝"),
        Triple("घर", "Ghar (Home)", "🏠"),
        Triple("किताब", "Kitaab (Book)", "📖"),
        Triple("कलम", "Kalam (Pen)", "✍️"),
        Triple("स्कूल", "Skoool (School)", "🏫"),
        Triple("प्यार", "Pyaar (Love)", "❤️"),
        Triple("मुस्कान", "Muskaan (Smile)", "😀"),
        Triple("दिन", "Din (Day)", "☀️"),
        Triple("रात", "Raat (Night)", "🌙"),
        Triple("सूरज", "Sooraj (Sun)", "🌞"),
        Triple("चाँद", "Chaand (Moon)", "🌛"),
        Triple("तारा", "Taara (Star)", "⭐"),
        Triple("आसमान", "Aasmaan (Sky)", "☁️"),
        Triple("पेड़", "Ped (Tree)", "🌳"),
        Triple("फूल", "Phool (Flower)", "🌸"),
        Triple("फल", "Phal (Fruit)", "🍓"),
        Triple("जानवर", "Jaanwar (Animal)", "🦁"),
        Triple("पक्षी", "Pakshee (Bird)", "🐦"),
        Triple("मछली", "Machhlee (Fish)", "🐟"),
        Triple("लड़का", "Ladka (Boy)", "👦"),
        Triple("लड़की", "Ladkee (Girl)", "👧"),
        Triple("पिता", "Pita (Father)", "👨"),
        Triple("माता", "Maata (Mother)", "👩"),
        Triple("भाई", "Bhaee (Brother)", "👦"),
        Triple("बहन", "Behan (Sister)", "👧"),
        Triple("खेल", "Khel (Play)", "🎮"),
        Triple("दौड़ना", "Daudna (Run)", "🏃"),
        Triple("चलना", "Chalna (Walk)", "🚶"),
        Triple("सोना", "Sona (Sleep)", "😴"),
        Triple("खाना", "Khaana (Eat)", "🍽️"),
        Triple("पीना", "Peena (Drink)", "🥤"),
        Triple("देखना", "Dekhna (See)", "👁️"),
        Triple("सुनना", "Sunna (Hear)", "👂"),
        Triple("बोलना", "Bolna (Speak)", "🗣️"),
        Triple("लिखना", "Likhna (Write)", "📝"),
        Triple("पढ़ना", "Padhna (Read)", "📖"),
        Triple("सीखना", "Seekhna (Learn)", "🧠"),
        Triple("काम", "Kaam (Work)", "💼"),
        Triple("समय", "Samay (Time)", "⏰"),
        Triple("आज", "Aaj (Today)", "📅"),
        Triple("अब", "Ab (Now)", "⏰"),
        Triple("यहाँ", "Yahaan (Here)", "📍"),
        Triple("वहाँ", "Wahaan (There)", "👉"),
        Triple("कौन", "Kaun (Who)", "❓"),
        Triple("कहाँ", "Kahaan (Where)", "🗺️"),
        Triple("कब", "Kab (When)", "🕒"),
        Triple("क्यों", "Kyon (Why)", "❓"),
        Triple("कैसे", "Kaise (How)", "⚙️"),
        Triple("नदी", "Nadi (River)", "🏞️"),
        Triple("पहाड़", "Pahaad (Mountain)", "⛰️"),
        Triple("समुद्र", "Samudra (Sea)", "🌊"),
        Triple("कुर्सी", "Kursi (Chair)", "🪑"),
        Triple("मेज", "Mej (Table)", "🪵"),
        Triple("रोशनी", "Roshni (Light)", "💡"),
        Triple("दरवाजा", "Darwaja (Door)", "🚪"),
        Triple("चाबी", "Chabi (Key)", "🔑"),
        Triple("कागज", "Kagaz (Paper)", "📄"),
        Triple("पत्थर", "Patthar (Stone)", "🪨"),
        Triple("सुबह", "Subah (Morning)", "🌅"),
        Triple("शाम", "Shaam (Evening)", "🌇"),
        Triple("मदद", "Madad (Help)", "🆘"),
        Triple("रुकना", "Rukna (Stop)", "🛑"),
        Triple("शुरू", "Shuroo (Start)", "🎬"),
        Triple("खोलना", "Kholna (Open)", "🔓"),
        Triple("बंद", "Band (Close)", "🔒"),
        Triple("देना", "Dena (Give)", "🤲"),
        Triple("लेना", "Lena (Take)", "🫴"),
        Triple("आग", "Aag (Fire)", "🔥"),
        Triple("हवा", "Hawa (Wind)", "💨"),
        Triple("धरती", "Dhartee (Earth)", "🌍"),
        Triple("बारिश", "Baarish (Rain)", "🌧️"),
        Triple("बादल", "Baadal (Cloud)", "☁️"),
        Triple("जीवन", "Jeevan (Life)", "🌱"),
        Triple("नाम", "Naam (Name)", "🏷️"),
        Triple("शहर", "Shahar (City)", "🏙️"),
        Triple("सड़क", "Sadak (Road)", "🛣️"),
        Triple("कार", "Kaar (Car)", "🚗"),
        Triple("दूध", "Doodh (Milk)", "🥛"),
        Triple("चाय", "Chaay (Tea)", "☕"),
        Triple("रोटी", "Rotee (Bread)", "🍞"),
        Triple("बच्चा", "Bachha (Child)", "👶"),
        Triple("परिवार", "Parivaar (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateTamilBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("வா", "Vaa (Come)", "🚶"),
        Triple("போ", "Po (Go)", "🏃"),
        Triple("என்ன", "Enna (What)", "❓"),
        Triple("ஆம்", "Aam (Yes)", "✅"),
        Triple("இல்லை", "Illai (No)", "❌"),
        Triple("வணக்கம்", "Vanakkam (Hello)", "👋"),
        Triple("தயவுசெய்து", "Thayavuseythu (Please)", "🥺"),
        Triple("நன்றி", "Nandri (Thanks)", "🙏"),
        Triple("நல்ல", "Nalla (Good)", "👍"),
        Triple("கெட்ட", "Ketta (Bad)", "👎"),
        Triple("மகிழ்ச்சி", "Magizhchi (Happy)", "😊"),
        Triple("சோகம்", "Sogam (Sad)", "😢"),
        Triple("பெரிய", "Periya (Big)", "🐘"),
        Triple("சிறிய", "Siriya (Small)", "🐭"),
        Triple("சூடு", "Soodu (Hot)", "🔥"),
        Triple("குளிர்", "Kulir (Cold)", "❄️"),
        Triple("தண்ணீர்", "Thanneer (Water)", "💧"),
        Triple("உணவு", "Unavu (Food)", "🍎"),
        Triple("நண்பன்", "Nanban (Friend)", "🤝"),
        Triple("வீடு", "Veedu (Home)", "🏠"),
        Triple("புத்தகம்", "Puthagam (Book)", "📖"),
        Triple("பேனா", "Pena (Pen)", "✍️"),
        Triple("பள்ளி", "Palli (School)", "🏫"),
        Triple("அன்பு", "Anbu (Love)", "❤️"),
        Triple("சிரிப்பு", "Sirippu (Smile)", "😀"),
        Triple("பகல்", "Pagal (Day)", "☀️"),
        Triple("இரவு", "Iravu (Night)", "🌙"),
        Triple("சூரியன்", "Sooriyan (Sun)", "🌞"),
        Triple("சந்திரன்", "Chandhiran (Moon)", "🌛"),
        Triple("நட்சத்திரம்", "Natchathiram (Star)", "⭐"),
        Triple("வானம்", "Vaanam (Sky)", "☁️"),
        Triple("மரம்", "Maram (Tree)", "🌳"),
        Triple("பூ", "Poo (Flower)", "🌸"),
        Triple("பழம்", "Pazham (Fruit)", "🍓"),
        Triple("விலங்கு", "Vilangu (Animal)", "🦁"),
        Triple("பறவை", "Paravai (Bird)", "🐦"),
        Triple("மீன்", "Meen (Fish)", "🐟"),
        Triple("பையன்", "Paiyan (Boy)", "👦"),
        Triple("பெண்", "Penn (Girl)", "👧"),
        Triple("அப்பா", "Appa (Father)", "👨"),
        Triple("அம்மா", "Amma (Mother)", "👩"),
        Triple("சகோதரன்", "Sagotharan (Brother)", "👦"),
        Triple("சகோதரி", "Sagothari (Sister)", "👧"),
        Triple("விளையாடு", "Vilaiyaadu (Play)", "🎮"),
        Triple("ஓடு", "Odu (Run)", "🏃"),
        Triple("நடாத்து", "Nadathu (Walk)", "🚶"),
        Triple("தூங்கு", "Thoongu (Sleep)", "😴"),
        Triple("சாப்பிடு", "Sappidu (Eat)", "🍽️"),
        Triple("குடி", "Kudi (Drink)", "🥤"),
        Triple("பார்", "Paar (See)", "👁️"),
        Triple("கேள்", "Kel (Hear)", "👂"),
        Triple("பேசு", "Pesu (Speak)", "🗣️"),
        Triple("எழுது", "Ezhuthu (Write)", "📝"),
        Triple("படி", "Padi (Read)", "📖"),
        Triple("கற்றுக்கொள்", "Katrukkol (Learn)", "🧠"),
        Triple("வேலை", "Velai (Work)", "💼"),
        Triple("நேரம்", "Neram (Time)", "⏰"),
        Triple("இன்று", "Indru (Today)", "📅"),
        Triple("இப்போது", "Ippothu (Now)", "⏰"),
        Triple("இங்கே", "Inge (Here)", "📍"),
        Triple("அங்கே", "Ange (There)", "👉"),
        Triple("யார்", "Yaar (Who)", "❓"),
        Triple("எங்கே", "Enge (Where)", "🗺️"),
        Triple("எப்போது", "Eppothu (When)", "🕒"),
        Triple("ஏன்", "Yen (Why)", "❓"),
        Triple("எப்படி", "Eppadi (How)", "⚙️"),
        Triple("ஆறு", "Aaru (River)", "🏞️"),
        Triple("மலை", "Malai (Mountain)", "⛰️"),
        Triple("கடல்", "Kadal (Sea)", "🌊"),
        Triple("நாற்காலி", "Naarkali (Chair)", "🪑"),
        Triple("மேஜை", "Mejai (Table)", "🪵"),
        Triple("ஒளி", "Oli (Light)", "💡"),
        Triple("கதவு", "Kathavu (Door)", "🚪"),
        Triple("சாவி", "Saavi (Key)", "🔑"),
        Triple("காகிதம்", "Kaagitham (Paper)", "📄"),
        Triple("கல்", "Kal (Stone)", "🪨"),
        Triple("காலை", "Kaalai (Morning)", "🌅"),
        Triple("மாலை", "Maalai (Evening)", "🌇"),
        Triple("உதவி", "Uthavi (Help)", "🆘"),
        Triple("நிறுத்து", "Niruthu (Stop)", "🛑"),
        Triple("தொடங்கு", "Thodangu (Start)", "🎬"),
        Triple("திற", "Thira (Open)", "🔓"),
        Triple("மூடு", "Moodu (Close)", "🔒"),
        Triple("கொடு", "Kodu (Give)", "🤲"),
        Triple("எடு", "Edu (Take)", "🫴"),
        Triple("நெருப்பு", "Neruppu (Fire)", "🔥"),
        Triple("காற்று", "Kaatru (Wind)", "💨"),
        Triple("பூமி", "Boomi (Earth)", "🌍"),
        Triple("மழை", "Mazhai (Rain)", "🌧️"),
        Triple("மேகம்", "Megam (Cloud)", "☁️"),
        Triple("வாழ்க்கை", "Vaazhkay (Life)", "🌱"),
        Triple("பெயர்", "Peyar (Name)", "🏷️"),
        Triple("நகரம்", "Nagaram (City)", "🏙️"),
        Triple("சாலை", "Saalai (Road)", "🛣️"),
        Triple("வண்டி", "Vandi (Car)", "🚗"),
        Triple("பால்", "Paal (Milk)", "🥛"),
        Triple("தேநீர்", "Theneer (Tea)", "☕"),
        Triple("ரொட்டி", "Rotti (Bread)", "🍞"),
        Triple("குழந்தை", "Kuzhandhai (Child)", "👶"),
        Triple("குடும்பம்", "Kudumbam (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateMalayalamBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("വരിക", "Varika (Come)", "🚶"),
        Triple("പോകുക", "Pokuka (Go)", "🏃"),
        Triple("എന്ത്", "Enthu (What)", "❓"),
        Triple("അതെ", "Athe (Yes)", "✅"),
        Triple("അല്ല", "Alla (No)", "❌"),
        Triple("നമസ്കാരം", "Namaskaram (Hello)", "👋"),
        Triple("ദയവായി", "Dayavayi (Please)", "🥺"),
        Triple("നന്ദി", "Nandhi (Thanks)", "🙏"),
        Triple("നല്ലത്", "Nallathu (Good)", "👍"),
        Triple("ചീത്ത", "Cheetha (Bad)", "👎"),
        Triple("സന്തോഷം", "Santhosham (Happy)", "😊"),
        Triple("സങ്കടം", "Sankadam (Sad)", "😢"),
        Triple("വലുത്", "Valuthu (Big)", "🐘"),
        Triple("ചെറുത്", "Cheruthu (Small)", "🐭"),
        Triple("ചൂട്", "Choodu (Hot)", "🔥"),
        Triple("തണുപ്പ്", "Thanooppu (Cold)", "❄️"),
        Triple("വെള്ളം", "Vellam (Water)", "💧"),
        Triple("ഭക്ഷണം", "Bhakshanam (Food)", "🍎"),
        Triple("കൂട്ടുകാരൻ", "Koottukaran (Friend)", "🤝"),
        Triple("വീട്", "Veedu (Home)", "🏠"),
        Triple("പുസ്തകം", "Pusthakam (Book)", "📖"),
        Triple("പേന", "Pena (Pen)", "✍️"),
        Triple("സ്കൂൾ", "School (School)", "🏫"),
        Triple("സ്നേഹം", "Snehama (Love)", "❤️"),
        Triple("ചിരി", "Chiri (Smile)", "😀"),
        Triple("പകൽ", "Pakal (Day)", "☀️"),
        Triple("രാത്രി", "Rathri (Night)", "🌙"),
        Triple("സൂര്യൻ", "Sooryan (Sun)", "🌞"),
        Triple("ചന്ദ്രൻ", "Chandran (Moon)", "🌛"),
        Triple("നക്ഷത്രം", "Nakshathram (Star)", "⭐"),
        Triple("ആകാശം", "Aakasham (Sky)", "☁️"),
        Triple("മരം", "Maram (Tree)", "🌳"),
        Triple("പൂവ്", "Poovu (Flower)", "🌸"),
        Triple("പഴം", "Pazham (Fruit)", "🍓"),
        Triple("മൃഗം", "Mrigam (Animal)", "🦁"),
        Triple("പക്ഷി", "Pakshi (Bird)", "🐦"),
        Triple("മീൻ", "Meen (Fish)", "🐟"),
        Triple("ആൺകുട്ടി", "Aankutty (Boy)", "👦"),
        Triple("പെൺകുട്ടി", "Penkutty (Girl)", "👧"),
        Triple("അച്ഛൻ", "Achhan (Father)", "👨"),
        Triple("അമ്മ", "Amma (Mother)", "👩"),
        Triple("സഹോദരൻ", "Sahodharan (Brother)", "👦"),
        Triple("സഹോദരി", "Sahodhari (Sister)", "👧"),
        Triple("കളിക്കുക", "Kalikkuka (Play)", "🎮"),
        Triple("ഓടുക", "Odukka (Run)", "🏃"),
        Triple("നടക്കുക", "Nadakkuka (Walk)", "🚶"),
        Triple("ഉറങ്ങുക", "Uranguka (Sleep)", "😴"),
        Triple("കഴിക്കുക", "Kazhikkuka (Eat)", "🍽️"),
        Triple("കുടിക്കുക", "Kudikkuka (Drink)", "🥤"),
        Triple("കാണുക", "Kanuka (See)", "👁️"),
        Triple("കേൾക്കുക", "Kelkkuka (Hear)", "👂"),
        Triple("സംസാരിക്കുക", "Samsarikkuka (Speak)", "🗣️"),
        Triple("എഴുതുക", "Ezhuthuka (Write)", "📝"),
        Triple("വായിക്കുക", "Vayikkuka (Read)", "📖"),
        Triple("പഠിക്കുക", "Padhikkuka (Learn)", "🧠"),
        Triple("ജോലി", "Joli (Work)", "💼"),
        Triple("സമയം", "Samayam (Time)", "⏰"),
        Triple("ഇന്ന്", "Innu (Today)", "📅"),
        Triple("ഇപ്പോൾ", "Ippol (Now)", "⏰"),
        Triple("ഇവിടെ", "Ivide (Here)", "📍"),
        Triple("അവിടെ", "Avide (There)", "👉"),
        Triple("ആര്", "Aaru (Who)", "❓"),
        Triple("എവിടെ", "Evide (Where)", "🗺️"),
        Triple("എപ്പോൾ", "Eppol (When)", "🕒"),
        Triple("എന്തുകൊണ്ട്", "Enthukondu (Why)", "❓"),
        Triple("എങ്ങനെ", "Engane (How)", "⚙️"),
        Triple("പുഴ", "Puzha (River)", "🏞️"),
        Triple("മല", "Mala (Mountain)", "⛰️"),
        Triple("കടൽ", "Kadal (Sea)", "🌊"),
        Triple("കസേര", "Kasera (Chair)", "🪑"),
        Triple("മേശ", "Mesha (Table)", "🪵"),
        Triple("വെളിച്ചം", "Velicham (Light)", "💡"),
        Triple("വാതിൽ", "Vaathil (Door)", "🚪"),
        Triple("താക്കോൽ", "Thaakkol (Key)", "🔑"),
        Triple("കടലാസ്", "Kadalaas (Paper)", "📄"),
        Triple("കല്ല്", "Kallu (Stone)", "🪨"),
        Triple("രാവിലെ", "Ravile (Morning)", "🌅"),
        Triple("വൈകുന്നേരം", "Vaikunneram (Evening)", "🌇"),
        Triple("സഹായം", "Sahaayam (Help)", "🆘"),
        Triple("നിർത്തുക", "Nirthuka (Stop)", "🛑"),
        Triple("തുടങ്ങുക", "Thudanguka (Start)", "🎬"),
        Triple("തുറക്കുക", "Thurakkuka (Open)", "🔓"),
        Triple("അടയ്ക്കുക", "Adaykkuka (Close)", "🔒"),
        Triple("നൽകുക", "Nalkuka (Give)", "🤲"),
        Triple("എടുക്കുക", "Edukkuka (Take)", "🫴"),
        Triple("തീ", "Thee (Fire)", "🔥"),
        Triple("കാറ്റ്", "Kattu (Wind)", "💨"),
        Triple("ഭൂമി", "Bhoomi (Earth)", "🌍"),
        Triple("മഴ", "Mazha (Rain)", "🌧️"),
        Triple("മേഘം", "Megham (Cloud)", "☁️"),
        Triple("ജീവിതം", "Jeevitham (Life)", "🌱"),
        Triple("പേര്", "Peru (Name)", "🏷️"),
        Triple("നഗരം", "Nagaram (City)", "🏙️"),
        Triple("റോഡ്", "Road (Road)", "🛣️"),
        Triple("കാർ", "Car (Car)", "🚗"),
        Triple("പാൽ", "Paal (Milk)", "🥛"),
        Triple("ചായ", "Chaya (Tea)", "☕"),
        Triple("റൊട്ടി", "Rotti (Bread)", "🍞"),
        Triple("കുട്ടി", "Kutty (Child)", "👶"),
        Triple("കുടുംബം", "Kudumbam (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateBengaliBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("এসো", "Esho (Come)", "🚶"),
        Triple("যাও", "Yao (Go)", "🏃"),
        Triple("কী", "Kee (What)", "❓"),
        Triple("হ্যাঁ", "Hya (Yes)", "✅"),
        Triple("না", "Na (No)", "❌"),
        Triple("নমস্কার", "Nomoskar (Hello)", "👋"),
        Triple("দয়া করে", "Doya kore (Please)", "🥺"),
        Triple("ধন্যবাদ", "Dhonnobad (Thanks)", "🙏"),
        Triple("ভালো", "Bhalo (Good)", "👍"),
        Triple("খারাপ", "Kharap (Bad)", "👎"),
        Triple("খুশি", "Khushi (Happy)", "😊"),
        Triple("দুঃখী", "Dukkhi (Sad)", "😢"),
        Triple("বড়", "Boro (Big)", "🐘"),
        Triple("ছোট", "Choto (Small)", "🐭"),
        Triple("গরম", "Gorom (Hot)", "🔥"),
        Triple("ঠান্ডা", "Thanda (Cold)", "❄️"),
        Triple("জল", "Jol (Water)", "💧"),
        Triple("খাবার", "Khabar (Food)", "🍎"),
        Triple("বন্ধু", "Bondhu (Friend)", "🤝"),
        Triple("বাড়ি", "Bari (Home)", "🏠"),
        Triple("বই", "Boi (Book)", "📖"),
        Triple("কলম", "Kolom (Pen)", "✍️"),
        Triple("স্কুল", "Skul (School)", "🏫"),
        Triple("ভালোবাসা", "Bhalobasa (Love)", "❤️"),
        Triple("হাসি", "Hasi (Smile)", "😀"),
        Triple("দিন", "Din (Day)", "☀️"),
        Triple("রাত", "Rat (Night)", "🌙"),
        Triple("সূর্য", "Surjo (Sun)", "🌞"),
        Triple("চাঁদ", "Chand (Moon)", "🌛"),
        Triple("তারা", "Tara (Star)", "⭐"),
        Triple("আকাশ", "Akash (Sky)", "☁️"),
        Triple("গাছ", "Gach (Tree)", "🌳"),
        Triple("ফুল", "Phul (Flower)", "🌸"),
        Triple("ফল", "Phol (Fruit)", "🍓"),
        Triple("পশু", "Poshu (Animal)", "🦁"),
        Triple("পাখি", "Pakhi (Bird)", "🐦"),
        Triple("মাছ", "Mach (Fish)", "🐟"),
        Triple("ছেলে", "Chele (Boy)", "👦"),
        Triple("মেয়ে", "Meye (Girl)", "👧"),
        Triple("বাবা", "Baba (Father)", "👨"),
        Triple("মা", "Ma (Mother)", "👩"),
        Triple("ভাই", "Bhai (Brother)", "👦"),
        Triple("বোন", "Bon (Sister)", "👧"),
        Triple("খেলা", "Khela (Play)", "🎮"),
        Triple("দৌড়ানো", "Dourano (Run)", "🏃"),
        Triple("হাঁটা", "Hata (Walk)", "🚶"),
        Triple("ঘুমানো", "Ghumano (Sleep)", "😴"),
        Triple("খাওয়া", "Khawa (Eat)", "🍽️"),
        Triple("পান করা", "Pan kora (Drink)", "🥤"),
        Triple("দেখা", "Dekha (See)", "👁️"),
        Triple("শোনা", "Shona (Hear)", "👂"),
        Triple("বলা", "Bola (Speak)", "🗣️"),
        Triple("লেখা", "Lekha (Write)", "📝"),
        Triple("পড়া", "Pora (Read)", "📖"),
        Triple("শেখা", "Shekha (Learn)", "🧠"),
        Triple("কাজ", "Kaj (Work)", "💼"),
        Triple("সময়", "Somoy (Time)", "⏰"),
        Triple("আজ", "Aj (Today)", "📅"),
        Triple("এখন", "Ekhon (Now)", "⏰"),
        Triple("এখানে", "Ekhane (Here)", "📍"),
        Triple("সেখানে", "Sekhane (There)", "👉"),
        Triple("কে", "Ke (Who)", "❓"),
        Triple("কোথায়", "Kothay (Where)", "🗺️"),
        Triple("কখন", "Kokhon (When)", "🕒"),
        Triple("কেন", "Keno (Why)", "❓"),
        Triple("কীভাবে", "Kibhabe (How)", "⚙️"),
        Triple("নদী", "Nodi (River)", "🏞️"),
        Triple("পাহাড়", "Pahar (Mountain)", "⛰️"),
        Triple("সমুদ্র", "Shomudro (Sea)", "🌊"),
        Triple("চেয়ার", "Cheyar (Chair)", "🪑"),
        Triple("টেবিল", "Tebil (Table)", "🪵"),
        Triple("আলো", "Alo (Light)", "💡"),
        Triple("দরজা", "Dorja (Door)", "🚪"),
        Triple("চাবি", "Chabi (Key)", "🔑"),
        Triple("কাগজ", "Kagoj (Paper)", "📄"),
        Triple("পাথর", "Pathor (Stone)", "🪨"),
        Triple("সকাল", "Sokal (Morning)", "🌅"),
        Triple("সন্ধ্যা", "Sondhya (Evening)", "🌇"),
        Triple("সাহায্য", "Sahajjo (Help)", "🆘"),
        Triple("থামা", "Thama (Stop)", "🛑"),
        Triple("শুরু", "Shuru (Start)", "🎬"),
        Triple("খোলা", "Khola (Open)", "🔓"),
        Triple("বন্ধ", "Bondho (Close)", "🔒"),
        Triple("দেওয়া", "Dewa (Give)", "🤲"),
        Triple("নেওয়া", "Newa (Take)", "🫴"),
        Triple("আগুন", "Agun (Fire)", "🔥"),
        Triple("বাতাস", "Batash (Wind)", "💨"),
        Triple("পৃথিবী", "Prithibi (Earth)", "🌍"),
        Triple("বৃষ্টি", "Brishti (Rain)", "🌧️"),
        Triple("মেঘ", "Megh (Cloud)", "☁️"),
        Triple("জীবন", "Jibon (Life)", "🌱"),
        Triple("নাম", "Nam (Name)", "🏷️"),
        Triple("শহর", "Shohor (City)", "🏙️"),
        Triple("রাস্তা", "Rasta (Road)", "🛣️"),
        Triple("গাড়ি", "Gari (Car)", "🚗"),
        Triple("দুধ", "Dudh (Milk)", "🥛"),
        Triple("চা", "Cha (Tea)", "☕"),
        Triple("রুটি", "Ruti (Bread)", "🍞"),
        Triple("শিশু", "Shishu (Child)", "👶"),
        Triple("পরিবার", "Poribar (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateMarathiBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("ये", "Ye (Come)", "🚶"),
        Triple("जा", "Ja (Go)", "🏃"),
        Triple("काय", "Kay (What)", "❓"),
        Triple("होय", "Hoy (Yes)", "✅"),
        Triple("नाही", "Nahi (No)", "❌"),
        Triple("नमस्कार", "Namaskar (Hello)", "👋"),
        Triple("कृपया", "Krupaya (Please)", "🥺"),
        Triple("धन्यवाद", "Dhanyavad (Thanks)", "🙏"),
        Triple("चांगले", "Changle (Good)", "👍"),
        Triple("वाईट", "Vait (Bad)", "👎"),
        Triple("आनंदी", "Anandi (Happy)", "😊"),
        Triple("दुःखी", "Dukhi (Sad)", "😢"),
        Triple("मोठा", "Motha (Big)", "🐘"),
        Triple("लहान", "Lahan (Small)", "🐭"),
        Triple("गरम", "Garam (Hot)", "🔥"),
        Triple("थंड", "Thand (Cold)", "❄️"),
        Triple("पाणी", "Pani (Water)", "💧"),
        Triple("अन्न", "Anna (Food)", "🍎"),
        Triple("मित्र", "Mitra (Friend)", "🤝"),
        Triple("घर", "Ghar (Home)", "🏠"),
        Triple("पुस्तक", "Pustak (Book)", "📖"),
        Triple("पेन", "Pen (Pen)", "✍️"),
        Triple("शाळा", "Shala (School)", "🏫"),
        Triple("प्रेम", "Prem (Love)", "❤️"),
        Triple("हसू", "Hasu (Smile)", "😀"),
        Triple("दिवस", "Divas (Day)", "☀️"),
        Triple("रात्र", "Ratra (Night)", "🌙"),
        Triple("सूर्य", "Surya (Sun)", "🌞"),
        Triple("चंद्र", "Chandra (Moon)", "🌛"),
        Triple("तारा", "Tara (Star)", "⭐"),
        Triple("आकाश", "Akash (Sky)", "☁️"),
        Triple("झाड", "Zhad (Tree)", "🌳"),
        Triple("फूल", "Phool (Flower)", "🌸"),
        Triple("फळ", "Phal (Fruit)", "🍓"),
        Triple("प्राणी", "Prani (Animal)", "🦁"),
        Triple("पक्षी", "Pakshi (Bird)", "🐦"),
        Triple("मासा", "Masa (Fish)", "🐟"),
        Triple("मुलगा", "Mulga (Boy)", "👦"),
        Triple("मुलगी", "Mulgi (Girl)", "👧"),
        Triple("वडील", "Vadil (Father)", "👨"),
        Triple("आई", "Ai (Mother)", "👩"),
        Triple("भाऊ", "Bhau (Brother)", "👦"),
        Triple("बहीण", "Bahin (Sister)", "👧"),
        Triple("खेळ", "Khel (Play)", "🎮"),
        Triple("धाव", "Dhav (Run)", "🏃"),
        Triple("चाल", "Chal (Walk)", "🚶"),
        Triple("झोप", "Zhop (Sleep)", "😴"),
        Triple("खा", "Kha (Eat)", "🍽️"),
        Triple("पी", "Pee (Drink)", "🥤"),
        Triple("बघ", "Bagh (See)", "👁️"),
        Triple("ऐक", "Aik (Hear)", "👂"),
        Triple("बोल", "Bol (Speak)", "🗣️"),
        Triple("लीह", "Lih (Write)", "📝"),
        Triple("वाच", "Vach (Read)", "📖"),
        Triple("शिक", "Shik (Learn)", "🧠"),
        Triple("काम", "Kam (Work)", "💼"),
        Triple("वेळ", "Vel (Time)", "⏰"),
        Triple("आज", "Aj (Today)", "📅"),
        Triple("आता", "Ata (Now)", "⏰"),
        Triple("येथे", "Yethe (Here)", "📍"),
        Triple("तेथे", "Tethe (There)", "👉"),
        Triple("कोण", "Kon (Who)", "❓"),
        Triple("कोठे", "Kothe (Where)", "🗺️"),
        Triple("केव्हा", "Kevha (When)", "🕒"),
        Triple("का", "Ka (Why)", "❓"),
        Triple("कसे", "Kase (How)", "⚙️"),
        Triple("नदी", "Nadi (River)", "🏞️"),
        Triple("डोंगर", "Dongar (Mountain)", "⛰️"),
        Triple("समुद्र", "Samudra (Sea)", "🌊"),
        Triple("खुर्ची", "Khurchi (Chair)", "🪑"),
        Triple("मेज", "Mej (Table)", "🪵"),
        Triple("प्रकाश", "Prakash (Light)", "💡"),
        Triple("दरवाजा", "Darwaja (Door)", "🚪"),
        Triple("चावी", "Chavi (Key)", "🔑"),
        Triple("कागद", "Kagad (Paper)", "📄"),
        Triple("दगड", "Dagad (Stone)", "🪨"),
        Triple("सकाळ", "Sakal (Morning)", "🌅"),
        Triple("संध्याकाळ", "Sandhyakal (Evening)", "🌇"),
        Triple("मदत", "Madat (Help)", "🆘"),
        Triple("थांब", "Thamb (Stop)", "🛑"),
        Triple("सुरू करा", "Suru kara (Start)", "🎬"),
        Triple("उघडा", "Ughda (Open)", "🔓"),
        Triple("बंद करा", "Band kara (Close)", "🔒"),
        Triple("द्या", "Dya (Give)", "🤲"),
        Triple("घ्या", "Ghya (Take)", "🫴"),
        Triple("आग", "Ag (Fire)", "🔥"),
        Triple("वारा", "Vara (Wind)", "💨"),
        Triple("पृथ्वी", "Pruthvi (Earth)", "🌍"),
        Triple("पाऊस", "Paus (Rain)", "🌧️"),
        Triple("ढग", "Dhag (Cloud)", "☁️"),
        Triple("जीवन", "Jivan (Life)", "🌱"),
        Triple("नाव", "Nav (Name)", "🏷️"),
        Triple("शहर", "Shahar (City)", "🏙️"),
        Triple("रस्ता", "Rasta (Road)", "🛣️"),
        Triple("गाडी", "Gadi (Car)", "🚗"),
        Triple("दूध", "Dudh (Milk)", "🥛"),
        Triple("चहा", "Chaha (Tea)", "☕"),
        Triple("पाव", "Pav (Bread)", "🍞"),
        Triple("बाळ", "Bal (Child)", "👶"),
        Triple("कुटुंब", "Kutumb (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateGujaratiBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("આવ", "Aav (Come)", "🚶"),
        Triple("જા", "Ja (Go)", "🏃"),
        Triple("શું", "Shu (What)", "❓"),
        Triple("હા", "Ha (Yes)", "✅"),
        Triple("ના", "Na (No)", "❌"),
        Triple("નમસ્તે", "Namaste (Hello)", "👋"),
        Triple("કૃપા કરીને", "Krupa karine (Please)", "🥺"),
        Triple("આભાર", "Aabhar (Thanks)", "🙏"),
        Triple("સારું", "Sarun (Good)", "👍"),
        Triple("ખરાબ", "Kharab (Bad)", "👎"),
        Triple("ખુશ", "Khush (Happy)", "😊"),
        Triple("ઉદાસ", "Udas (Sad)", "😢"),
        Triple("મોટું", "Motun (Big)", "🐘"),
        Triple("નાનું", "Nanun (Small)", "🐭"),
        Triple("ગરમ", "Garam (Hot)", "🔥"),
        Triple("ઠંડું", "Thandun (Cold)", "❄️"),
        Triple("પાણી", "Pani (Water)", "💧"),
        Triple("ખોરાક", "Khorak (Food)", "🍎"),
        Triple("મિત્ર", "Mitra (Friend)", "🤝"),
        Triple("ઘર", "Ghar (Home)", "🏠"),
        Triple("પુસ્તક", "Pustak (Book)", "📖"),
        Triple("પેન", "Pen (Pen)", "✍️"),
        Triple("શાળા", "Shala (School)", "🏫"),
        Triple("પ્રેમ", "Prem (Love)", "❤️"),
        Triple("સ્મિત", "Smit (Smile)", "😀"),
        Triple("દિવસ", "Divas (Day)", "☀️"),
        Triple("રાત", "Rat (Night)", "🌙"),
        Triple("સૂર્ય", "Surya (Sun)", "🌞"),
        Triple("ચંદ્ર", "Chandra (Moon)", "🌛"),
        Triple("તારો", "Taro (Star)", "⭐"),
        Triple("આકાશ", "Akash (Sky)", "☁️"),
        Triple("ઝાડ", "Zhad (Tree)", "🌳"),
        Triple("ફૂલ", "Phool (Flower)", "🌸"),
        Triple("ફળ", "Phal (Fruit)", "🍓"),
        Triple("પ્રાણી", "Prani (Animal)", "🦁"),
        Triple("પક્ષી", "Pakshi (Bird)", "🐦"),
        Triple("માછલી", "Machhli (Fish)", "🐟"),
        Triple("છોકરો", "Chhokro (Boy)", "👦"),
        Triple("છોકરી", "Chhokri (Girl)", "👧"),
        Triple("પિતા", "Pita (Father)", "👨"),
        Triple("માતા", "Mata (Mother)", "👩"),
        Triple("ભાઈ", "Bhai (Brother)", "👦"),
        Triple("બહેન", "Bahen (Sister)", "👧"),
        Triple("રમો", "Ramo (Play)", "🎮"),
        Triple("દોડો", "Dodo (Run)", "🏃"),
        Triple("ચાલો", "Chalo (Walk)", "🚶"),
        Triple("ઊંઘો", "Ungho (Sleep)", "😴"),
        Triple("ખાઓ", "Khao (Eat)", "🍽️"),
        Triple("પીઓ", "Pio (Drink)", "🥤"),
        Triple("જુઓ", "Juo (See)", "👁️"),
        Triple("સાંભળો", "Sambhlo (Hear)", "👂"),
        Triple("બોલો", "Bolo (Speak)", "🗣️"),
        Triple("લખો", "Lakho (Write)", "📝"),
        Triple("વાંચો", "Vancho (Read)", "📖"),
        Triple("શીખો", "Shikho (Learn)", "🧠"),
        Triple("કામ", "Kam (Work)", "💼"),
        Triple("સમય", "Samay (Time)", "⏰"),
        Triple("આજે", "Aaje (Today)", "📅"),
        Triple("હવે", "Have (Now)", "⏰"),
        Triple("અહીં", "Ahin (Here)", "📍"),
        Triple("ત્યાં", "Tyan (There)", "👉"),
        Triple("કોણ", "Kon (Who)", "❓"),
        Triple("ક્યાં", "Kyan (Where)", "🗺️"),
        Triple("ક્યારે", "Kyare (When)", "🕒"),
        Triple("શા માટે", "Sha mate (Why)", "❓"),
        Triple("કેવી રીતે", "Kevi rite (How)", "⚙️"),
        Triple("નદી", "Nadi (River)", "🏞️"),
        Triple("પર્વત", "Parvat (Mountain)", "⛰️"),
        Triple("દરિયો", "Dariyo (Sea)", "🌊"),
        Triple("ખુરશી", "Khurshi (Chair)", "🪑"),
        Triple("ટેબલ", "Tebal (Table)", "🪵"),
        Triple("પ્રકાશ", "Prakash (Light)", "💡"),
        Triple("દરવાજો", "Darwajo (Door)", "🚪"),
        Triple("ચાવી", "Chavi (Key)", "🔑"),
        Triple("કાગળ", "Kagal (Paper)", "📄"),
        Triple("પથ્થર", "Paththar (Stone)", "🪨"),
        Triple("સવાર", "Savar (Morning)", "🌅"),
        Triple("સાંજ", "Sanj (Evening)", "🌇"),
        Triple("મદદ", "Madad (Help)", "🆘"),
        Triple("ઊભા રહો", "Ubha raho (Stop)", "🛑"),
        Triple("શરૂ કરો", "Sharu karo (Start)", "🎬"),
        Triple("ખોલો", "Kholo (Open)", "🔓"),
        Triple("બંધ કરો", "Band karo (Close)", "🔒"),
        Triple("આપો", "Aapo (Give)", "🤲"),
        Triple("લો", "Lo (Take)", "🫴"),
        Triple("આગ", "Aag (Fire)", "🔥"),
        Triple("પવન", "Pavan (Wind)", "💨"),
        Triple("પૃથ્વી", "Pruthvi (Earth)", "🌍"),
        Triple("વરસાદ", "Varsad (Rain)", "🌧️"),
        Triple("વાદળ", "Vadal (Cloud)", "☁️"),
        Triple("જીવન", "Jivan (Life)", "🌱"),
        Triple("નામ", "Nam (Name)", "🏷️"),
        Triple("શહેર", "Shahar (City)", "🏙️"),
        Triple("રસ્તો", "Rasto (Road)", "🛣️"),
        Triple("ગાડી", "Gadi (Car)", "🚗"),
        Triple("દૂધ", "Dudh (Milk)", "🥛"),
        Triple("ચા", "Cha (Tea)", "☕"),
        Triple("રોટલી", "Rotli (Bread)", "🍞"),
        Triple("બાળક", "Balak (Child)", "👶"),
        Triple("પરિવાર", "Parivar (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateKannadaBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("ಬಾ", "Baa (Come)", "🚶"),
        Triple("ಹೋಗು", "Hogu (Go)", "🏃"),
        Triple("ಏನು", "Enu (What)", "❓"),
        Triple("ಹೌದು", "Haudu (Yes)", "✅"),
        Triple("ಇಲ್ಲ", "Illa (No)", "❌"),
        Triple("ನಮಸ್ಕಾರ", "Namaskara (Hello)", "👋"),
        Triple("ದಯವಿಟ್ಟು", "Dayavittu (Please)", "🥺"),
        Triple("ಧನ್ಯವಾದಗಳು", "Dhanyavadagalu (Thanks)", "🙏"),
        Triple("ಒಳ್ಳೆಯದು", "Olleyadu (Good)", "👍"),
        Triple("ಕೆಟ್ಟದ್ದು", "Kettaddu (Bad)", "👎"),
        Triple("ಸಂತೋಷ", "Santhosha (Happy)", "😊"),
        Triple("ದುಃಖ", "Dukha (Sad)", "😢"),
        Triple("ದೊಡ್ಡ", "Dodda (Big)", "🐘"),
        Triple("ಸಣ್ಣ", "Sanna (Small)", "🐭"),
        Triple("ಬಿಸಿ", "Bisi (Hot)", "🔥"),
        Triple("ತಣ್ಣಗಿನ", "Tannagina (Cold)", "❄️"),
        Triple("ನೀರು", "Neeru (Water)", "💧"),
        Triple("ಆಹಾರ", "Aahara (Food)", "🍎"),
        Triple("ಸ್ನೇಹಿತ", "Snehitha (Friend)", "🤝"),
        Triple("ಮನೆ", "Mane (Home)", "🏠"),
        Triple("ಪುಸ್ತಕ", "Pustaka (Book)", "📖"),
        Triple("ಪೇನ", "Pena (Pen)", "✍️"),
        Triple("ಶಾಲೆ", "Shaale (School)", "🏫"),
        Triple("ಪ್ರೀತಿ", "Preethi (Love)", "❤️"),
        Triple("ನಗೆ", "Nage (Smile)", "😀"),
        Triple("ಹಗಲು", "Hagalu (Day)", "☀️"),
        Triple("ರಾತ್ರಿ", "Raathri (Night)", "🌙"),
        Triple("ಸೂರ್ಯ", "Soorya (Sun)", "🌞"),
        Triple("ಚಂದ್ರ", "Chandra (Moon)", "🌛"),
        Triple("ನಕ್ಷತ್ರ", "Nakshathra (Star)", "⭐"),
        Triple("ಆಕಾಶ", "Aakaasha (Sky)", "☁️"),
        Triple("ಮರ", "Mara (Tree)", "🌳"),
        Triple("ಹೂವು", "Hoovu (Flower)", "🌸"),
        Triple("ಹಣ್ಣು", "Hannu (Fruit)", "🍓"),
        Triple("ಪ್ರಾಣಿ", "Praani (Animal)", "🦁"),
        Triple("ಹಕ್ಕಿ", "Hakki (Bird)", "🐦"),
        Triple("ಮೀನು", "Meenu (Fish)", "🐟"),
        Triple("ಹುಡುಗ", "Huduga (Boy)", "👦"),
        Triple("ಹುಡುಗಿ", "Hudugi (Girl)", "👧"),
        Triple("ತಂದೆ", "Tandhe (Father)", "👨"),
        Triple("ತಾಯಿ", "Thaayi (Mother)", "👩"),
        Triple("ಸಹೋದರ", "Sahodhara (Brother)", "👦"),
        Triple("ಸಹೋದರಿ", "Sahodhari (Sister)", "👧"),
        Triple("ఆట", "Aata (Play)", "🎮"),
        Triple("ಓಡು", "Odu (Run)", "🏃"),
        Triple("ನಡೆ", "Nade (Walk)", "🚶"),
        Triple("ಮಲಗು", "Malagu (Sleep)", "😴"),
        Triple("ತಿನ್ನು", "Thinnu (Eat)", "🍽️"),
        Triple("ಕುಡಿ", "Kudi (Drink)", "🥤"),
        Triple("ನೋಡು", "Nodu (See)", "👁️"),
        Triple("ಕೇಳು", "Kelu (Hear)", "👂"),
        Triple("ಮಾತನಾಡು", "Maathanaadu (Speak)", "🗣️"),
        Triple("ಬರೆ", "Bare (Write)", "📝"),
        Triple("ಓದು", "Odu (Read)", "📖"),
        Triple("ಕಲಿ", "Kali (Learn)", "🧠"),
        Triple("ಕೆಲಸ", "Kelasa (Work)", "💼"),
        Triple("ಸಮಯ", "Samaya (Time)", "⏰"),
        Triple("ಇಂದು", "Indu (Today)", "📅"),
        Triple("ಈಗ", "Eega (Now)", "⏰"),
        Triple("ಇಲ್ಲಿ", "Illi (Here)", "📍"),
        Triple("ಅಲ್ಲಿ", "Alli (There)", "👉"),
        Triple("ಯಾರು", "Yaaru (Who)", "❓"),
        Triple("ಎಲ್ಲಿ", "Elli (Where)", "🗺️"),
        Triple("ಯಾವಾಗ", "Yaavaaga (When)", "🕒"),
        Triple("ಏಕೆ", "Aeke (Why)", "❓"),
        Triple("ಹೇಗೆ", "Haege (How)", "⚙️"),
        Triple("ನದಿ", "Nadi (River)", "🏞️"),
        Triple("ಪರ್ವತ", "Parvatha (Mountain)", "⛰️"),
        Triple("ಸಮುದ್ರ", "Samudra (Sea)", "🌊"),
        Triple("ಕುರ್ಚಿ", "Kurchi (Chair)", "🪑"),
        Triple("ಮೇಜು", "Meju (Table)", "🪵"),
        Triple("ಬೆಳಕು", "Belaku (Light)", "💡"),
        Triple("ಬಾಗಿಲು", "Baagilu (Door)", "🚪"),
        Triple("ಕೀಲಿ", "Keeli (Key)", "🔑"),
        Triple("ಕಾಗದ", "Kaagada (Paper)", "📄"),
        Triple("ಕಲ್ಲು", "Kallu (Stone)", "🪨"),
        Triple("ಬೆಳಿಗ್ಗೆ", "Beligge (Morning)", "🌅"),
        Triple("ಸಂಜೆ", "Sanje (Evening)", "🌇"),
        Triple("ಸಹಾಯ", "Sahaaya (Help)", "🆘"),
        Triple("ನಿಲ್ಲಿಸು", "Nillisu (Stop)", "🛑"),
        Triple("ಪ್ರಾರಂಭಿಸು", "Praarambhisu (Start)", "🎬"),
        Triple("ತೆರೆ", "Tere (Open)", "🔓"),
        Triple("ಮುಚ್ಚು", "Muchchu (Close)", "🔒"),
        Triple("ಕೊಡು", "Kodu (Give)", "🤲"),
        Triple("ತೆಗೆದುಕೋ", "Tegeduko (Take)", "🫴"),
        Triple("ಬೆಂಕಿ", "Benki (Fire)", "🔥"),
        Triple("ಗಾಳಿ", "Gaali (Wind)", "💨"),
        Triple("ಭೂಮಿ", "Bhoomi (Earth)", "🌍"),
        Triple("ಮಳೆ", "Male (Rain)", "🌧️"),
        Triple("ಮೋಡ", "Moda (Cloud)", "☁️"),
        Triple("ಜೀವನ", "Jeevana (Life)", "🌱"),
        Triple("ಹೆಸರು", "Hesaru (Name)", "🏷️"),
        Triple("ನಗರ", "Nagara (City)", "🏙️"),
        Triple("ರಸ್ತೆ", "Raste (Road)", "🛣️"),
        Triple("ಕಾರು", "Caru (Car)", "🚗"),
        Triple("ಹಾಲು", "Haalu (Milk)", "🥛"),
        Triple("ಚಹಾ", "Chaha (Tea)", "☕"),
        Triple("ರೊಟ್ಟಿ", "Rotti (Bread)", "🍞"),
        Triple("ಮಗು", "Magu (Child)", "👶"),
        Triple("ಕುಟುಂಬ", "Kutumba (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateArabicBasicWords(): List<LearningItem> {
    val words = listOf(
        Triple("تَعَال", "Ta'al (Come)", "🚶"),
        Triple("اِذْهَب", "Ithhab (Go)", "🏃"),
        Triple("مَاذَا", "Matha (What)", "❓"),
        Triple("نَعَم", "Na'am (Yes)", "✅"),
        Triple("لَا", "La (No)", "❌"),
        Triple("مَرْحَبًا", "Marhaban (Hello)", "👋"),
        Triple("مِنْ فَضْلِك", "Min fadlik (Please)", "🥺"),
        Triple("شُكْرًا", "Shukran (Thanks)", "🙏"),
        Triple("جَيِّد", "Jayyid (Good)", "👍"),
        Triple("سَيِّئ", "Sayyi' (Bad)", "👎"),
        Triple("سَعِيد", "Sa'id (Happy)", "😊"),
        Triple("حَزِين", "Hazin (Sad)", "😢"),
        Triple("كَبِير", "Kabir (Big)", "🐘"),
        Triple("صَغِير", "Saghir (Small)", "🐭"),
        Triple("سَاخِن", "Sakhin (Hot)", "🔥"),
        Triple("بَارِد", "Barid (Cold)", "❄️"),
        Triple("مَاء", "Ma' (Water)", "💧"),
        Triple("طَعَام", "Ta'am (Food)", "🍎"),
        Triple("صَدِيق", "Sadiq (Friend)", "🤝"),
        Triple("بَيْت", "Bayt (Home)", "🏠"),
        Triple("كِتَاب", "Kitab (Book)", "📖"),
        Triple("قَلَم", "Qalam (Pen)", "✍️"),
        Triple("مَدْرَسَة", "Madrasah (School)", "🏫"),
        Triple("حُبّ", "Hubb (Love)", "❤️"),
        Triple("ابْتِسَامَة", "Ibtisamah (Smile)", "😀"),
        Triple("يَوْم", "Yawm (Day)", "☀️"),
        Triple("لَيْلَة", "Laylah (Night)", "🌙"),
        Triple("شَمْس", "Shams (Sun)", "🌞"),
        Triple("قَمَر", "Qamar (Moon)", "🌛"),
        Triple("نَجْمَة", "Najmah (Star)", "⭐"),
        Triple("سَمَاء", "Sama' (Sky)", "☁️"),
        Triple("شَجَرَة", "Shajarah (Tree)", "🌳"),
        Triple("زَهْرَة", "Zahrah (Flower)", "🌸"),
        Triple("فَاكِهَة", "Fakihah (Fruit)", "🍓"),
        Triple("حَيَوَان", "Hayawan (Animal)", "🦁"),
        Triple("طَائِر", "Ta'ir (Bird)", "🐦"),
        Triple("سَمَكَة", "Samakah (Fish)", "🐟"),
        Triple("وَلَد", "Walad (Boy)", "👦"),
        Triple("بِنْت", "Bint (Girl)", "👧"),
        Triple("أَب", "Ab (Father)", "👨"),
        Triple("أُمّ", "Umm (Mother)", "👩"),
        Triple("أَخ", "Akh (Brother)", "👦"),
        Triple("أُخْت", "Ukht (Sister)", "👧"),
        Triple("لَعِب", "La'ib (Play)", "🎮"),
        Triple("جَرْي", "Jary (Run)", "🏃"),
        Triple("مَشْي", "Mashy (Walk)", "🚶"),
        Triple("نَوْم", "Nawm (Sleep)", "😴"),
        Triple("أَكْل", "Akl (Eat)", "🍽️"),
        Triple("شُرْب", "Shurb (Drink)", "🥤"),
        Triple("رُؤْيَة", "Ru'yah (See)", "👁️"),
        Triple("سَمْع", "Sam' (Hear)", "👂"),
        Triple("كَلَام", "Kalam (Speak)", "🗣️"),
        Triple("كِتَابَة", "Kitabah (Write)", "📝"),
        Triple("قِرَاءَة", "Qira'ah (Read)", "📖"),
        Triple("تَعَلُّم", "Ta'allum (Learn)", "🧠"),
        Triple("عَمَل", "Amal (Work)", "💼"),
        Triple("وَقْت", "Waqt (Time)", "⏰"),
        Triple("اليَوْم", "Al-yawm (Today)", "📅"),
        Triple("الآن", "Al-an (Now)", "⏰"),
        Triple("هُنَا", "Huna (Here)", "📍"),
        Triple("هُنَاك", "Hunak (There)", "👉"),
        Triple("مَنْ", "Man (Who)", "❓"),
        Triple("أَيْن", "Ayna (Where)", "🗺️"),
        Triple("مَتَى", "Mata (When)", "🕒"),
        Triple("لِمَاذَا", "Limatha (Why)", "❓"),
        Triple("كَيْفَ", "Kayfa (How)", "⚙️"),
        Triple("نَهْر", "Nahr (River)", "🏞️"),
        Triple("جَبَل", "Jabal (Mountain)", "⛰️"),
        Triple("بَحْر", "Bahr (Sea)", "🌊"),
        Triple("كُرْسِي", "Kursi (Chair)", "🪑"),
        Triple("طَاوِلَة", "Tawilah (Table)", "🪵"),
        Triple("ضَوْء", "Daw' (Light)", "💡"),
        Triple("بَاب", "Bab (Door)", "🚪"),
        Triple("مِفْتَاح", "Miftah (Key)", "🔑"),
        Triple("وَرَقَة", "Waraqah (Paper)", "📄"),
        Triple("حَجَر", "Hajar (Stone)", "🪨"),
        Triple("صَبَاح", "Sabah (Morning)", "🌅"),
        Triple("مَسَاء", "Masa' (Evening)", "🌇"),
        Triple("مُسَاعَدَة", "Musa'adah (Help)", "🆘"),
        Triple("قِف", "Qif (Stop)", "🛑"),
        Triple("بَدْء", "Bad' (Start)", "🎬"),
        Triple("فَتْح", "Fath (Open)", "🔓"),
        Triple("إِغْلَاق", "Ighlaq (Close)", "🔒"),
        Triple("إِعْطَاء", "I'ta' (Give)", "🤲"),
        Triple("أَخْذ", "Akhth (Take)", "🫴"),
        Triple("نَار", "Nar (Fire)", "🔥"),
        Triple("رِيح", "Rih (Wind)", "💨"),
        Triple("أَرْض", "Ard (Earth)", "🌍"),
        Triple("مَطَر", "Matar (Rain)", "🌧️"),
        Triple("سَحَاب", "Sahab (Cloud)", "☁️"),
        Triple("حَيَاة", "Hayah (Life)", "🌱"),
        Triple("اسْم", "Ism (Name)", "🏷️"),
        Triple("مَدِينَة", "Madinah (City)", "🏙️"),
        Triple("طَرِيق", "Tariq (Road)", "🛣️"),
        Triple("سَيَّارَة", "Sayyarah (Car)", "🚗"),
        Triple("حَلِيب", "Halib (Milk)", "🥛"),
        Triple("شَاي", "Shay (Tea)", "☕"),
        Triple("خُبْز", "Khubz (Bread)", "🍞"),
        Triple("طِفْل", "Tifl (Child)", "👶"),
        Triple("عَائِلَة", "A'ilah (Family)", "👨‍👩‍👧‍👦")
    )
    return words.map { LearningItem(it.first, it.second, it.first, it.third) }
}

fun generateBasicWords(langCode: String): List<LearningItem> = when (langCode) {
    "te" -> generateTeluguBasicWords()
    "hi" -> generateHindiBasicWords()
    "ta" -> generateTamilBasicWords()
    "kn" -> generateKannadaBasicWords()
    "ml" -> generateMalayalamBasicWords()
    "ar" -> generateArabicBasicWords()
    "bn" -> generateBengaliBasicWords()
    "mr" -> generateMarathiBasicWords()
    "gu" -> generateGujaratiBasicWords()
    else -> generateEnglishBasicWords()
}

fun generateTeluguNumbers(): List<LearningItem> {
    val items = mutableListOf<LearningItem>()
    val tensNames = mapOf(20 to "ఇరవై", 30 to "ముప్పై", 40 to "నలభై", 50 to "యాభై", 60 to "అరవై", 70 to "డెబ్బై", 80 to "ఎనభై", 90 to "తొంభై")
    val tensEnglish = mapOf(20 to "Iravai", 30 to "Muppai", 40 to "Nalabhai", 50 to "Yabhai", 60 to "Aravai", 70 to "Debbai", 80 to "Enabhai", 90 to "Tombhai")
    val onesNames = mapOf(1 to "ఒకటి", 2 to "రెండు", 3 to "మూడు", 4 to "నాలుగు", 5 to "ఐదు", 6 to "ఆరు", 7 to "ఏడు", 8 to "ఎనిమిది", 9 to "తొమ్మిది")
    val onesEnglish = mapOf(1 to "Okati", 2 to "Rendu", 3 to "Moodu", 4 to "Naalugu", 5 to "Aidu", 6 to "Aaru", 7 to "Aedu", 8 to "Enimidi", 9 to "Tommidi")
    
    for (i in 1..9) { items.add(LearningItem("$i", "${onesNames[i]} (${onesEnglish[i]})", onesNames[i]!!, "$i️⃣")) }
    items.add(LearningItem("10", "పది (Padi)", "పది", "🔟"))
    val teens = listOf(
        Pair("పదకొండు", "Padakondu"), Pair("పన్నెండు", "Pannendu"), Pair("పదమూడు", "Padamoodu"),
        Pair("పద్నాలుగు", "Padnaalugu"), Pair("పదిహేను", "Padihenu"), Pair("పదహారు", "Padahaaru"),
        Pair("పదిహేడు", "Padihaedu"), Pair("పద్దెనిమిది", "Paddenimidi"), Pair("పంతొమ్మిది", "Panntommidi")
    )
    for (i in 11..19) {
        val pair = teens[i - 11]
        items.add(LearningItem("$i", "${pair.first} (${pair.second})", pair.first, "$i"))
    }
    for (i in 20..99) {
        val ten = (i / 10) * 10; val one = i % 10
        val tenName = tensNames[ten]!!; val tenEng = tensEnglish[ten]!!
        if (one == 0) {
            items.add(LearningItem("$i", "$tenName ($tenEng)", tenName, "$i"))
        } else {
            items.add(LearningItem("$i", "$tenName ${onesNames[one]!!} ($tenEng ${onesEnglish[one]!!})", "$tenName ${onesNames[one]!!}", "$i"))
        }
    }
    items.add(LearningItem("100", "వంద (Vanda)", "వంద", "💯"))
    return items
}

fun generateEnglishNumbers(): List<LearningItem> {
    val ones = listOf("", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen")
    val tens = listOf("", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety")
    return (1..100).map { i ->
        val name = if (i == 100) "One Hundred" else if (i < 20) ones[i] else {
            val t = i / 10; val o = i % 10
            if (o == 0) tens[t] else "${tens[t]} ${ones[o]}"
        }
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem("$i", name, name, emoji)
    }
}

fun generateTamilNumbers(): List<LearningItem> {
    val ones = listOf("", "ஒன்று", "இரண்டு", "மூன்று", "நான்கு", "ஐந்து", "ஆறு", "ஏழு", "எட்டு", "ஒன்பது")
    val onesEng = listOf("", "Ondru", "Irandu", "Moondru", "Naangu", "Ainthu", "Aaru", "Ezhu", "Ettu", "Onbathu")
    val teens = listOf("", "பதினொன்று", "பன்னிரண்டு", "பதின்மூன்று", "பதினான்கு", "பதினைந்து", "பதினாறு", "பதினேழு", "பதினெட்டு", "பத்தொன்பது")
    val teensEng = listOf("", "Pathinondru", "Pannirandu", "Pathinmoondru", "Pathinaangu", "Pathinainthu", "Pathinaaru", "Pathinezhu", "Pathinettu", "Pathonbathu")
    val tens = mapOf(10 to "பத்து", 20 to "இருபது", 30 to "முப்பது", 40 to "நாற்பது", 50 to "ஐம்பது", 60 to "அறுபது", 70 to "எழுபது", 80 to "எண்பது", 90 to "தொண்ணூறு")
    val tensEng = mapOf(10 to "Pathu", 20 to "Irubathu", 30 to "Muppathu", 40 to "Naarpathu", 50 to "Aimpathu", 60 to "Arupathu", 70 to "Ezhupathu", 80 to "Enpathu", 90 to "Thonnooru")
    val tensComb = mapOf(20 to "இருபத்து ", 30 to "முப்பத்து ", 40 to "நாற்பத்து ", 50 to "ஐம்பத்து ", 60 to "அறுபத்து ", 70 to "எழுபத்து ", 80 to "எண்பத்து ", 90 to "தொண்ணூற்று ")
    val tensCombEng = mapOf(20 to "Irubathu ", 30 to "Muppathu ", 40 to "Naarpathu ", 50 to "Aimpathu ", 60 to "Arupathu ", 70 to "Ezhupathu ", 80 to "Enpathu ", 90 to "Thonnootru ")
    return (1..100).map { i ->
        val name: String; val eng: String
        if (i == 100) { name = "நூறு"; eng = "Nooru" }
        else if (i < 10) { name = ones[i]; eng = onesEng[i] }
        else if (i == 10) { name = "பத்து"; eng = "Pathu" }
        else if (i < 20) { name = teens[i - 10]; eng = teensEng[i - 10] }
        else {
            val t = (i / 10) * 10; val o = i % 10
            if (o == 0) { name = tens[t]!!; eng = tensEng[t]!! }
            else { name = "${tensComb[t]!!}${ones[o]}"; eng = "${tensCombEng[t]!!}${onesEng[o]}" }
        }
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem("$i", "$name ($eng)", name, emoji)
    }
}

fun generateHindiNumbers(): List<LearningItem> {
    val hWords = listOf(
        "एक" to "Ek", "दो" to "Do", "तीन" to "Teen", "चार" to "Chaar", "पाँच" to "Paanch", "छह" to "Chhah", "सात" to "Saat", "आठ" to "Aath", "नौ" to "Nau", "दस" to "Das",
        "ग्यारह" to "Gyarah", "बारह" to "Baarah", "तेरह" to "Terah", "चौदह" to "Chaudah", "पन्द्रह" to "Pandrah", "सोलह" to "Solah", "सत्रह" to "Satrah", "अठारह" to "Atharah", "उन्नीस" to "Unnees", "बीस" to "Bees",
        "इक्कीस" to "Ikkees", "बाईस" to "Bais", "तेईस" to "Tees", "चौबीस" to "Chaubees", "पच्चीस" to "Pachhees", "छब्बीस" to "Chhabbees", "सत्ताईस" to "Sattaees", "अठाईस" to "Athaees", "उनतीस" to "Untees", "तीस" to "Tees",
        "इकतीस" to "Iktees", "बत्तीस" to "Battees", "तैंतीस" to "Taintees", "चौंतीस" to "Chauntees", "पैंतीस" to "Paintees", "छत्तीस" to "Chhattees", "सैंतीस" to "Saintees", "अड़तीस" to "Adtees", "उनतालीस" to "Untalees", "चालीस" to "Chalees",
        "इकतालीस" to "Iktalees", "बयालीस" to "Bayalees", "तैंतालीस" to "Taintalees", "चवालीस" to "Chawalees", "पैंतालीस" to "Paintalees", "छियालीस" to "Chhiyalees", "सैंतालीस" to "Saintalees", "अड़तालीस" to "Adtalees", "उनचास" to "Unchaas", "पचास" to "Pachaas",
        "इक्यावन" to "Ikyawan", "बावन" to "Baawan", "तिरेपन" to "Tirepan", "चौवन" to "Chauwan", "पचपन" to "Pachpan", "छप्पण" to "Chhappan", "सत्तावन" to "Sattawan", "अठावन" to "Athaawan", "उनसठ" to "Unsath", "साठ" to "Saath",
        "इकसठ" to "Iksath", "बासठ" to "Baasath", "तिरसठ" to "Tirsath", "चौंसठ" to "Chaunsath", "पैंसठ" to "Painsath", "छियासठ" to "Chhiyasath", "सरसठ" to "Sarsath", "अड़सठ" to "Adsath", "उनहत्तर" to "Unhattar", "सत्तर" to "Sattar",
        "इकहत्तर" to "Ikhattar", "बहत्तर" to "Bahattar", "तिहत्तर" to "Tihattar", "चौहत्तर" to "Chauhattar", "पचहत्तर" to "Pachhattar", "छिहत्तर" to "Chhihattar", "सतहत्तर" to "Satahattar", "अठहत्तर" to "Athahattar", "उनासी" to "Unaasee", "अस्सी" to "Assee",
        "इक्यासी" to "Ikyaasee", "बयासी" to "Bayaasee", "तिरासी" to "Tiraasee", "चौरासी" to "Chauraasee", "पचासी" to "Pachaasee", "छियासी" to "Chhiyaasee", "सत्तासी" to "Sattaasee", "अठासी" to "Athaasee", "नवासी" to "Nawaasee", "नब्बे" to "Nabbe",
        "इक्यान्वे" to "Ikyanwe", "बयान्वे" to "Bayanwe", "तिरान्वे" to "Tiranwe", "चौरान्वे" to "Chauranwe", "पञ्चान्वे" to "Panchanwe", "छियान्वे" to "Chhiyanwe", "सत्तान्वे" to "Sattanwe", "अठान्वे" to "Athanwe", "निन्यानवे" to "Ninyanwe", "सौ" to "Sau"
    )
    return (1..100).map { i ->
        val p = hWords[i - 1]
        val numStr = i.toString().map { "०१२३४५६७८९"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numStr, "${p.first} (${p.second})", p.first, emoji)
    }
}

fun generateArabicNumbers(): List<LearningItem> {
    val ones = listOf("", "وَاحِدْ", "اِثْنَانْ", "ثَلَاثَة", "أَرْبَعَة", "خَمْسَة", "سِتَّة", "سَبْعَة", "ثَمَانِيَة", "تِسْعَة")
    val onesEng = listOf("", "Wahid", "Ithnan", "Thalathah", "Arba'ah", "Khamsah", "Sittah", "Sab'ah", "Thamaniyah", "Tis'ah")
    val teens = listOf("", "أَحَدَ عَشَر", "اِثْنَا عَشَر", "ثَلَاثَةَ عَشَر", "أَرْبَعَةَ عَشَر", "خَمْسَةَ عَشَر", "سِتَّةَ عَشَر", "سَبْعَةَ عَشَر", "ثَمَانِيَةَ عَشَر", "تِسْعَةَ عَشَر")
    val teensEng = listOf("", "Ahad Ashar", "Ithna Ashar", "Thalathata Ashar", "Arba'ata Ashar", "Khamsata Ashar", "Sittata Ashar", "Sab'ata Ashar", "Thamaniyata Ashar", "Tis'ata Ashar")
    val tens = listOf("", "", "عِشْرُونَ", "ثَلَاثُونَ", "أَرْبَعُونَ", "خَمْسُونَ", "سِتُّونَ", "سَبْعُونَ", "ثَمَانُونَ", "تِسْعُونَ")
    val tensEng = listOf("", "", "Ishrun", "Thalathun", "Arba'un", "Khamsun", "Sittun", "Sab'un", "Thamanun", "Tis'un")
    return (1..100).map { i ->
        val name: String; val eng: String
        if (i == 100) { name = "مِئَة"; eng = "Mi'ah" }
        else if (i < 10) { name = ones[i]; eng = onesEng[i] }
        else if (i == 10) { name = "عَشَرَة"; eng = "Asharah" }
        else if (i < 20) { name = teens[i - 10]; eng = teensEng[i - 10] }
        else {
            val t = i / 10; val o = i % 10
            if (o == 0) { name = tens[t]; eng = tensEng[t] }
            else { name = "${ones[o]} وَ ${tens[t]}"; eng = "${onesEng[o]} wa ${tensEng[t]}" }
        }
        val numeral = i.toString().map { "٠١٢٣٤٥٦٧٨٩"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numeral, "$name ($eng)", name, emoji)
    }
}

fun generateKannadaNumbers(): List<LearningItem> {
    val ones = listOf("", "ಒಂದು", "ಎರಡು", "ಮೂರು", "ನಾಲ್ಕು", "ಐದು", "ಆರು", "ಏಳು", "ಎಂಟು", "ಒಂಬತ್ತು")
    val onesEng = listOf("", "Ondu", "Eradu", "Mooru", "Naalku", "Aidu", "Aaru", "Elu", "Entu", "Ombattu")
    val teens = listOf("", "ಹನ್ನೊಂದು", "ಹನ್ನೆರಡು", "ಹದಿಮೂರು", "ಹದಿನಾಲ್ಕು", "ಹದಿನೈದು", "ಹದಿನಾರು", "ಹದಿನೇಳು", "ಹದಿನೆಂಟು", "ಹತ್ತೊಂಬತ್ತು")
    val teensEng = listOf("", "Hannondu", "Hanneradu", "Hadimooru", "Hadinaalku", "Hadinaidu", "Hadinaaru", "Hadinelu", "Hadinentu", "Hattombattu")
    val tens = mapOf(10 to "ಹತ್ತು", 20 to "ಇಪ್ಪತ್ತು", 30 to "ಮೂವತ್ತು", 40 to "ನಲವತ್ತು", 50 to "ಐವತ್ತು", 60 to "ಅರವತ್ತು", 70 to "ಎಪ್ಪತ್ತು", 80 to "ಎಂಬತ್ತು", 90 to "ತೊಂಬತ್ತು")
    val tensEng = mapOf(10 to "Hattu", 20 to "Ippattu", 30 to "Moovattu", 40 to "Nalavattu", 50 to "Aivattu", 60 to "Aravattu", 70 to "Eppattu", 80 to "Embattu", 90 to "Tombattu")
    val tensComb = mapOf(20 to "ಇಪ್ಪತ್ತ", 30 to "ಮೂವತ್ತ", 40 to "ನಲವತ್ತ", 50 to "ಐವತ್ತ", 60 to "ಅರವತ್ತ", 70 to "ಎಪ್ಪತ್ತ", 80 to "ಎಂಬತ್ತ", 90 to "ತೊಂಬತ್ತ")
    val tensCombEng = mapOf(20 to "Ippatta", 30 to "Moovatta", 40 to "Nalavatta", 50 to "Aivatta", 60 to "Aravatta", 70 to "Eppatta", 80 to "Embatta", 90 to "Tombatta")
    val suffix = mapOf(1 to Pair("ೊಂದು", "ondu"), 2 to Pair("ೆರಡು", "eradu"), 3 to Pair("ಮೂರು", "mooru"), 4 to Pair("ನಾಲ್ಕು", "naalku"), 5 to Pair("ೈದು", "idu"), 6 to Pair("ಾರು", "aaru"), 7 to Pair("ೇಳು", "elu"), 8 to Pair("ೆಂಟು", "entu"), 9 to Pair("ೊಂಬತ್ತು", "ombattu"))
    return (1..100).map { i ->
        val name: String; val eng: String
        if (i == 100) { name = "ನೂರು"; eng = "Nooru" }
        else if (i < 10) { name = ones[i]; eng = onesEng[i] }
        else if (i == 10) { name = "ಹತ್ತು"; eng = "Hattu" }
        else if (i < 20) { name = teens[i - 10]; eng = teensEng[i - 10] }
        else {
            val t = (i / 10) * 10; val o = i % 10
            if (o == 0) { name = tens[t]!!; eng = tensEng[t]!! }
            else {
                val s = suffix[o]!!
                name = tensComb[t]!!.dropLast(1) + s.first
                eng = tensCombEng[t]!!.dropLast(1) + s.second
            }
        }
        val numeral = i.toString().map { "೦೧೨೩೪೫೬೭೮೯"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numeral, "$name ($eng)", name, emoji)
    }
}

fun generateMalayalamNumbers(): List<LearningItem> {
    val ones = listOf("", "ഒന്ന്", "രണ്ട്", "മൂന്ന്", "നാല്", "അഞ്ച്", "ആറ്", "ഏഴ്", "എട്ട്", "ഒൻപത്")
    val onesEng = listOf("", "Onnu", "Randu", "Moonnu", "Naalu", "Anju", "Aaru", "Ezhu", "Ettu", "Onpathu")
    val teens = listOf("", "പതിനൊന്ന്", "പന്ത്രണ്ട്", "പതിമൂന്ന്", "പതിനാല്", "പതിനഞ്ച്", "പതിനാറ്", "പതിനേഴ്", "പതിനെട്ട്", "പത്തൊൻപത്")
    val teensEng = listOf("", "Pathinonnu", "Panthrandu", "Pathimoonnu", "Pathinaalu", "Pathinanju", "Pathinaaru", "Pathinezhu", "Pathinettu", "Pathonpathu")
    val tens = mapOf(10 to "പത്ത്", 20 to "ഇരുപത്", 30 to "മുപ്പത്", 40 to "നാൽപത്", 50 to "അൻപത്", 60 to "അറുപത്", 70 to "എഴുപത്", 80 to "എൺപത്", 90 to "തൊണ്ണൂറ്")
    val tensEng = mapOf(10 to "Pathu", 20 to "Irupathu", 30 to "Muppathu", 40 to "Naalpathu", 50 to "Anpathu", 60 to "Arupathu", 70 to "Ezhupathu", 80 to "Enpathu", 90 to "Thonnooru")
    val tensComb = mapOf(20 to "ഇരുപത്തി ", 30 to "മുപ്പത്തി ", 40 to "നാൽപത്തി ", 50 to "അൻപത്തി ", 60 to "അറുപത്തി ", 70 to "എഴുപത്തി ", 80 to "എൺപത്തി ", 90 to "തൊണ്ണൂറ്റി ")
    val tensCombEng = mapOf(20 to "Irupathhi ", 30 to "Muppathhi ", 40 to "Naalpathhi ", 50 to "Anpathhi ", 60 to "Arupathhi ", 70 to "Ezhupathhi ", 80 to "Enpathhi ", 90 to "Thonnootti ")
    return (1..100).map { i ->
        val name: String; val eng: String
        if (i == 100) { name = "നൂറ്"; eng = "Nooru" }
        else if (i < 10) { name = ones[i]; eng = onesEng[i] }
        else if (i == 10) { name = "പത്ത്"; eng = "Pathu" }
        else if (i < 20) { name = teens[i - 10]; eng = teensEng[i - 10] }
        else {
            val t = (i / 10) * 10; val o = i % 10
            if (o == 0) { name = tens[t]!!; eng = tensEng[t]!! }
            else { name = "${tensComb[t]!!}${ones[o]}"; eng = "${tensCombEng[t]!!}${onesEng[o]}" }
        }
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem("$i", "$name ($eng)", name, emoji)
    }
}

fun generateBengaliNumbers(): List<LearningItem> {
    val bWords = listOf(
        "এক" to "Ek", "দুই" to "Dui", "তিন" to "Tin", "চার" to "Char", "পাঁচ" to "Panch", "ছয়" to "Chhoy", "সাত" to "Sat", "আট" to "At", "নয়" to "Noy", "দশ" to "Dosh",
        "এগারো" to "Egaro", "বারো" to "Baro", "তেরো" to "Tero", "চৌদ্দ" to "Choddo", "পনেরো" to "Ponero", "ষোলো" to "Sholo", "সতেরো" to "Sotero", "আঠারো" to "Atharo", "উনিশ" to "Unish", "বিশ" to "Bish",
        "একুশ" to "Ekush", "বাইশ" to "Baish", "তেইশ" to "Teish", "চব্বিশ" to "Chobbish", "পঁচিশ" to "Ponchish", "ছাব্বিশ" to "Chabbish", "সাতাশ" to "Satash", "আটাশ" to "Atash", "উনত্রিশ" to "Unotrish", "ত্রিশ" to "Trish",
        "একত্রিশ" to "Ekotrish", "বত্রিশ" to "Botrish", "তেত্রিশ" to "Tetrish", "চৌত্রিশ" to "Choutrish", "পঁয়ত্রিশ" to "Poytrish", "ছত্রিশ" to "Chotrish", "সাঁইত্রিশ" to "Shaitrish", "আটত্রিশ" to "Atotrish", "ঊনচল্লিশ" to "Unochollish", "চল্লিশ" to "Chollish",
        "একচল্লিশ" to "Ekochollish", "বিয়াল্লিশ" to "Biyallish", "তেতাল্লিশ" to "Tetallish", "চৌয়াল্লিশ" to "Chouallish", "পঁয়তাল্লিশ" to "Poytallish", "ছেচল্লিশ" to "Chhechollish", "সাতচল্লিশ" to "Satchollish", "আটচল্লিশ" to "Atchollish", "ঊনপঞ্চাশ" to "Unoponchash", "পঞ্চাশ" to "Ponchash",
        "একান্ন" to "Ekanno", "বায়ান্ন" to "Bayanno", "তিপ্পান্ন" to "Tippanno", "চুয়ান্ন" to "Chuyanno", "পঞ্চান্ন" to "Ponchanno", "ছাপ্পান্ন" to "Chhappanno", "সাতান্ন" to "Satanno", "আটান্ন" to "Atanno", "ঊনষাট" to "Unoshat", "ষাট" to "Shat",
        "একষট্টি" to "Ekoshotti", "বাষট্টি" to "Bashotti", "তেষট্টি" to "Teshotti", "চৌষট্টি" to "Choushotti", "পঁয়ষট্টি" to "Poyshotti", "ছেষট্টি" to "Chheshotti", "সাতষট্টি" to "Satshotti", "আটষট্টি" to "Atshotti", "ঊনসত্তর" to "Unosottor", "সত্তর" to "Sottor",
        "একাত্তর" to "Ekattor", "বাহাত্তর" to "Bahattor", "তিয়াত্তর" to "Tiyattor", "চৌয়াত্তর" to "Chouattor", "পঁচাত্তর" to "Ponchattor", "ছিয়াত্তর" to "Chhiyattor", "সাতাত্তর" to "Satattor", "আটাত্তর" to "Atattor", "ঊনআশি" to "Unonashi", "আশি" to "Ashi",
        "একাশি" to "Ekashi", "বিয়াশি" to "Biyashi", "তিরানি" to "Tirashi", "চৌরাশি" to "Chourashi", "পঁচাশী" to "Ponchashi", "ছিয়াশি" to "Chhiyashi", "সাতাশি" to "Satashi", "অষ্টআশি" to "Oshtashi", "ঊননব্বই" to "Unonobboi", "নব্বই" to "Nobboi",
        "একানব্বই" to "Ekanobboi", "বিয়ানব্বই" to "Biyanobboi", "তিরানব্বই" to "Tiranobboi", "চৌরানব্বই" to "Chouranobboi", "পঁচানব্বই" to "Ponchanobboi", "ছিয়ানব্বই" to "Chhiyanobboi", "সাতানব্বই" to "Satanobboi", "আটানব্বই" to "Atanobboi", "নিরানব্বই" to "Niranobboi", "একশো" to "Eksho"
    )
    return (1..100).map { i ->
        val p = bWords[i - 1]
        val numStr = i.toString().map { "০১২৩৪৫৬৭৮৯"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numStr, "${p.first} (${p.second})", p.first, emoji)
    }
}

fun generateMarathiNumbers(): List<LearningItem> {
    val mWords = listOf(
        "एक" to "Ek", "दोन" to "Don", "तीन" to "Teen", "चार" to "Chaar", "पाच" to "Paach", "सहा" to "Saha", "सात" to "Saat", "आठ" to "Aath", "नऊ" to "Nau", "दहा" to "Daha",
        "अकरा" to "Akara", "बारा" to "Bara", "तेरा" to "Tera", "चौदा" to "Chauda", "पंधरा" to "Pandhara", "सोळा" to "Sola", "सतरा" to "Satara", "अठरा" to "Athara", "एकूणवीस" to "Ekonis", "वीस" to "Vis",
        "एकवीस" to "Ekavis", "बावीस" to "Bavis", "तेवीस" to "Tevis", "चोवीस" to "Chovis", "पंचवीस" to "Panchavis", "सव्वीस" to "Savvis", "सत्तावीस" to "Sattavis", "अठ्ठावीस" to "Atthavis", "एकोणतीस" to "Ekonatis", "तीस" to "Tis",
        "एकतीस" to "Ektis", "बत्तीस" to "Battis", "तेहेतीस" to "Tehetis", "चौतीस" to "Chautis", "पस्तीस" to "Pastis", "छत्तीस" to "Chhattis", "सदतीस" to "Sadatis", "अडतीस" to "Adatis", "एकोणचाळीस" to "Ekonachalis", "चाळीस" to "Chalis",
        "एकटचाळीस" to "Ektachalis", "बेचाळीस" to "Bechalis", "तेहेचाळीस" to "Tehechalis", "चौवेचाळीस" to "Chauvechalis", "पंचेचाळीस" to "Panchechalis", "शेचाळीस" to "Shechalis", "सत्तेचाळीस" to "Sattechalis", "अठ्ठेचाळीस" to "Atthechalis", "एकोणपन्नास" to "Ekonapannas", "पन्नास" to "Pannas",
        "एक्कावन" to "Ekkawan", "बावन" to "Bawan", "त्रिपन्न" to "Tripanna", "चौपन" to "Chaupan", "पंचावन" to "Panchawan", "छप्पन्न" to "Chhappanna", "सत्तावन" to "Sattawan", "अठ्ठावन" to "Atthawan", "एकोणसाठ" to "Ekonasath", "साठ" to "Sath",
        "एकसष्ठ" to "Ekasastha", "बासरष्ठ" to "Basastha", "त्रिसष्ठ" to "Trisastha", "चौसष्ठ" to "Chausastha", "पाचसष्ठ" to "Pachasastha", "सहासष्ठ" to "Sahasastha", "सदुसष्ठ" to "Sadusastha", "अडुसष्ठ" to "Adusastha", "एकोणसत्तर" to "Ekonasattar", "सत्तर" to "Sattar",
        "एक्काहत्तर" to "Ekkahattar", "बाहत्तर" to "Bahattar", "त्र्याहत्तर" to "Tryahattar", "चौऱ्याहत्तर" to "Chauryahattar", "पंच्याहत्तर" to "Panchyahattar", "शहात्तर" to "Shahattar", "सत्त्याहत्तर" to "Sattyahattar", "अठ्ठ्याहत्तर" to "Atthyahattar", "एकोणऐंशी" to "Ekonaenshi", "ऐंशी" to "Enshi",
        "एक्क्यांशी" to "Ekkyanshi", "ब्यांशी" to "Byanshi", "त्र्यांशी" to "Tryanshi", "चौऱ्यांशी" to "Chauryanshi", "पंच्यांशी" to "Panchyanshi", "शहांशी" to "Shahanshi", "सत्त्यांशी" to "Sattyanshi", "अठ्ठ्यांशी" to "Atthyanshi", "एकोणनव्वद" to "Ekonanavvad", "नव्वद" to "Navvad",
        "एक्क्याण्णव" to "Ekkyannav", "ब्याण्णव" to "Byannav", "त्र्याण्णव" to "Tryannav", "चौऱ्याण्णव" to "Chauryannav", "पंच्याण्णव" to "Panchyannav", "शहाण्णव" to "Shahannav", "सत्त्याण्णव" to "Sattyannav", "अठ्ठ्याण्णव" to "Atthyannav", "नव्याण्णव" to "Navyannav", "शंभर" to "Shambhar"
    )
    return (1..100).map { i ->
        val p = mWords[i - 1]
        val numStr = i.toString().map { "०१२३४५६७८९"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numStr, "${p.first} (${p.second})", p.first, emoji)
    }
}

fun generateGujaratiNumbers(): List<LearningItem> {
    val gWords = listOf(
        "એક" to "Ek", "બે" to "Be", "ત્રણ" to "Tran", "ચાર" to "Char", "પાંચ" to "Panch", "છ" to "Chha", "સાત" to "Sat", "આઠ" to "Ath", "નવ" to "Nav", "દસ" to "Das",
        "અગિયાર" to "Agiyar", "બાર" to "Bar", "તેર" to "Ter", "ચૌદ" to "Chaud", "પંદર" to "Pandar", "સોળ" to "Sol", "સત્તર" to "Sattar", "અઢાર" to "Adhar", "ઓગણીસ" to "Ognis", "વીસ" to "Vis",
        "એકવીસ" to "Ekvis", "બાવીસ" to "Bavis", "તેવીસ" to "Tevis", "ચોવીસ" to "Chovis", "પચીસ" to "Pachis", "છવીસ" to "Chhavis", "સત્તાવીસ" to "Sattavis", "અઠ્ઠાવીસ" to "Atthavis", "ઓગણત્રીસ" to "Ogantris", "ત્રીસ" to "Tris",
        "એકત્રીસ" to "Ektris", "બત્રીસ" to "Batris", "તેત્રીસ" to "Tetris", "ચોત્રીસ" to "Chotris", "પાંત્રીસ" to "Pantris", "છત્રીસ" to "Chhatris", "સાડત્રીસ" to "Sadtris", "આડત્રીસ" to "Adtris", "ઓગણચાળીસ" to "Oganchalis", "ચાળીસ" to "Chalis",
        "એકતાલીસ" to "Ektalis", "બેતાલીસ" to "Betalis", "તેતાલીસ" to "Tetalis", "ચોતાલીસ" to "Chotalis", "પિસ્તાલીસ" to "Pistalis", "છેતાલીસ" to "Chhetalis", "સુડતાલીસ" to "Sudtalis", "અડતાલીસ" to "Adtalis", "ઓગણપચาส" to "Oganpachas", "પચાસ" to "Pachas",
        "એકાવન" to "Ekawan", "બાવન" to "Bawan", "તરેપન" to "Tarepan", "ચોપન" to "Chopan", "પંચાવન" to "Panchawan", "છપ્પન" to "Chhappan", "સત્તાવન" to "Sattawan", "અઠ્ઠાવન" to "Atthawan", "ઓગણસાઇઠ" to "Ogansait", "સાઇઠ" to "Sait",
        "એકસઠ" to "Eksath", "બાસઠ" to "Basath", "ત્રેસઠ" to "Tresath", "ચોસઠ" to "Chosath", "પાંસઠ" to "Pansath", "છાસઠ" to "Chhasath", "સડસઠ" to "Sadsath", "આડસઠ" to "Adsath", "ઓગણોસિત્તેર" to "Ogonositter", "સิต્તેર" to "Sitter",
        "એકોતેર" to "Ekoter", "બોતેર" to "Boter", "તોતેર" to "Toter", "ચોતેર" to "Choter", "પંચોતેર" to "Panchoter", "છોતેર" to "Chhoter", "સિત્યોતેર" to "Sityoter", "ઈઠ્યોતેર" to "Ithyoter", "ઓગણએંસી" to "Oganensi", "એંસી" to "Ensi",
        "એક્યાસી" to "Ekyasi", "બ્યાસી" to "Byasi", "ત્યાસી" to "Tyasi", "ચોર્યાસી" to "Choryasi", "પંચાસી" to "Panchasi", "છ્યાસી" to "Chhyasi", "સત્યાસી" to "Satyasi", "અઠ્યાસી" to "Athyasi", "નવાસી" to "Navasi", "નવ્વે" to "Navve",
        "એકણું" to "Ekanu", "બાણું" to "Banu", "ત્રાણું" to "Tranu", "ચોરાણું" to "Choranu", "પંચાણું" to "Panchanu", "છન્નું" to "Chhannu", "સત્યાણું" to "Satyanu", "અઠ્યાણું" to "Athyanu", "નવ્વાણું" to "Navvanu", "સો" to "So"
    )
    return (1..100).map { i ->
        val p = gWords[i - 1]
        val numStr = i.toString().map { "૦૧૨૩૪૫૬૭૮૯"[it - '0'] }.joinToString("")
        val emoji = if (i <= 10) "$i️⃣" else if (i == 100) "💯" else "$i"
        LearningItem(numStr, "${p.first} (${p.second})", p.first, emoji)
    }
}

val teluguVegetables = listOf(
    LearningItem("టమోటా", "Tomato", "టమోటా", "🍅"),
    LearningItem("బంగాళదుంప", "Bangaladumpa (Potato)", "బంగాళదుంప", "🥔"),
    LearningItem("ఉల్లిపాయ", "Ullipaya (Onion)", "ఉల్లిపాయ", "🧅"),
    LearningItem("క్యారెట్", "Carrot", "క్యారెట్", "🥕"),
    LearningItem("వంకాయ", "Vankaya (Brinjal)", "వంకాయ", "🍆"),
    LearningItem("బెండకాయ", "Bendakaya (Ladies Finger)", "బెండకాయ", "🫛"),
    LearningItem("గుమ్మడికాయ", "Gummadikaya (Pumpkin)", "గుమ్మడికాయ", "🎃"),
    LearningItem("నిమ్మకాయ", "Nimmakaya (Lemon)", "నిమ్మకాయ", "🍋"),
    LearningItem("పచ్చిమిర్చి", "Pachimirchi (Chilli)", "పచ్చిమిర్చి", "🌶️"),
    LearningItem("దోసకాయ", "Dosakaya (Cucumber)", "దోసకాయ", "🥒")
)

val englishVegetables = listOf(
    LearningItem("Tomato", "Tomato", "Tomato", "🍅"),
    LearningItem("Potato", "Potato", "Potato", "🥔"),
    LearningItem("Onion", "Onion", "Onion", "🧅"),
    LearningItem("Carrot", "Carrot", "Carrot", "🥕"),
    LearningItem("Brinjal", "Eggplant", "Brinjal", "🍆"),
    LearningItem("Ladies Finger", "Okra", "Ladies Finger", "🫛"),
    LearningItem("Pumpkin", "Pumpkin", "Pumpkin", "🎃"),
    LearningItem("Lemon", "Lemon", "Lemon", "🍋"),
    LearningItem("Chilli", "Hot Pepper", "Chilli", "🌶️"),
    LearningItem("Cucumber", "Cucumber", "Cucumber", "🥒")
)

val tamilVegetables = listOf(
    LearningItem("தக்காளி", "Thakkaali (Tomato)", "தக்காளி", "🍅"),
    LearningItem("உருளைக்கிழங்கு", "Urulaikkizhangu (Potato)", "உருளைக்கிழங்கு", "🥔"),
    LearningItem("வெங்காயம்", "Vengaayam (Onion)", "வெங்காயம்", "🧅"),
    LearningItem("கேரட்", "Carrot", "கேரட்", "🥕"),
    LearningItem("கத்தரிக்காய்", "Katharikaai (Brinjal)", "கத்தரிக்காய்", "🍆"),
    LearningItem("வெண்டைக்காய்", "Vendakkaai (Ladies Finger)", "வெண்டைக்காய்", "🫛"),
    LearningItem("பூசணிக்காய்", "Poosanikaai (Pumpkin)", "பூசணிக்காய்", "🎃"),
    LearningItem("எலுமிச்சை", "Elumichai (Lemon)", "எலுமிச்சை", "🍋"),
    LearningItem("பச்சை மிளகாய்", "Pachai Milagaai (Chilli)", "பச்சை மிளகாய்", "🌶️"),
    LearningItem("வெள்ளரிக்காய்", "Vellarikaai (Cucumber)", "வெள்ளரிக்காய்", "🥒")
)

val hindiVegetables = listOf(
    LearningItem("टमाटर", "Tamatar (Tomato)", "टमाटर", "🍅"),
    LearningItem("आलू", "Aaloo (Potato)", "आलू", "🥔"),
    LearningItem("प्याज", "Pyaaz (Onion)", "प्याज", "🧅"),
    LearningItem("गाजर", "Gaajar (Carrot)", "गाजर", "🥕"),
    LearningItem("बैंगन", "Baingan (Brinjal)", "बैंगन", "🍆"),
    LearningItem("भिंडी", "Bhindi (Ladies Finger)", "भिंडी", "🫛"),
    LearningItem("कद्दू", "Kaddu (Pumpkin)", "कद्दू", "🎃"),
    LearningItem("नींबू", "Neembu (Lemon)", "नींबू", "🍋"),
    LearningItem("हरी मिर्च", "Hari Mirch (Chilli)", "हरी मिर्च", "🌶️"),
    LearningItem("खीरा", "Kheera (Cucumber)", "खीरा", "🥒")
)

val arabicVegetables = listOf(
    LearningItem("طماطم", "Tamatim (Tomato)", "طماطم", "🍅"),
    LearningItem("بطاطس", "Batatis (Potato)", "بطاطس", "🥔"),
    LearningItem("بصل", "Basal (Onion)", "بصل", "🧅"),
    LearningItem("جزر", "Jazar (Carrot)", "جزر", "🥕"),
    LearningItem("باذنجان", "Badhinjan (Brinjal)", "باذنجان", "🍆"),
    LearningItem("بامية", "Bamiyah (Ladies Finger)", "بامية", "🫛"),
    LearningItem("يقطين", "Yaqteen (Pumpkin)", "يقطين", "🎃"),
    LearningItem("ليمون", "Laymoon (Lemon)", "ليمون", "🍋"),
    LearningItem("فلفل حار", "Filfil Har (Chilli)", "فلفل حار", "🌶️"),
    LearningItem("خيار", "Khiyar (Cucumber)", "خيار", "🥒")
)

val kannadaVegetables = listOf(
    LearningItem("ಟೊಮೆಟೊ", "Tomato", "ಟೊಮೆಟೊ", "🍅"),
    LearningItem("ಆಲೂಗಡ್ಡೆ", "Aaloogadde (Potato)", "ಆಲೂಗಡ್ಡೆ", "🥔"),
    LearningItem("ಈರುಳ್ಳಿ", "Eerulli (Onion)", "ಈರುಳ್ಳಿ", "🧅"),
    LearningItem("ಕ್ಯಾರೆಟ್", "Carrot", "ಕ್ಯಾರೆಟ್", "🥕"),
    LearningItem("ಬದನೆಕಾಯಿ", "Badanekaayi (Brinjal)", "ಬದನೆಕಾಯಿ", "🍆"),
    LearningItem("ಬೆಂಡೆಕಾಯಿ", "Bendekaayi (Ladies Finger)", "ಬೆಂಡೆಕಾಯಿ", "🫛"),
    LearningItem("ಕುಂಬಳಕಾಯಿ", "Kumbalakaayi (Pumpkin)", "ಕುಂಬಳಕಾಯಿ", "🎃"),
    LearningItem("ನಿಂಬೆಹಣ್ಣು", "Nimbehannu (Lemon)", "ನಿಂಬೆಹಣ್ಣು", "🍋"),
    LearningItem("ಹಸಿಮೆಣಸಿನಕಾಯಿ", "Hasimenasinakaayi (Chilli)", "ಹಸಿಮೆಣಸಿನಕಾಯಿ", "🌶️"),
    LearningItem("ಸೌತೆಕಾಯಿ", "Sautekaayi (Cucumber)", "ಸೌತೆకಾಯಿ", "🥒")
)

val malayalamVegetables = listOf(
    LearningItem("തക്കാളി", "Thakkaali (Tomato)", "തക്കാളി", "🍅"),
    LearningItem("ഉരുളക്കിഴങ്ങ്", "Urulakkizhanghu (Potato)", "ഉരുളക്കിഴങ്ങ്", "🥔"),
    LearningItem("സവാള", "Savaala (Onion)", "സവാള", "🧅"),
    LearningItem("കാരറ്റ്", "Carrot", "കാരറ്റ്", "🥕"),
    LearningItem("വഴുതനങ്ങ", "Vazhuthananga (Brinjal)", "വഴുതനങ്ങ", "🍆"),
    LearningItem("വെണ്ടയ്ക്ക", "Vendakka (Ladies Finger)", "വെണ്ടയ്ക്ക", "🫛"),
    LearningItem("മത്തങ്ങ", "Mathanga (Pumpkin)", "മത്തങ്ങ", "🎃"),
    LearningItem("നാരങ്ങ", "Naaranga (Lemon)", "നാരങ്ങ", "🍋"),
    LearningItem("പച്ചമുളക്", "Pachamulagu (Chilli)", "പച്ചമുളക്", "🌶️"),
    LearningItem("വെള്ളരിക്ക", "Vellarikka (Cucumber)", "വെള്ളരിക്ക", "🥒")
)

val bengaliVegetables = listOf(
    LearningItem("টমেটো", "Tomato", "টমেটো", "🍅"),
    LearningItem("আলু", "Alu (Potato)", "আলু", "🥔"),
    LearningItem("পেঁয়াজ", "Peyaj (Onion)", "পেঁয়াজ", "🧅"),
    LearningItem("গাজর", "Gajor (Carrot)", "গাজর", "🥕"),
    LearningItem("বেগুন", "Begun (Brinjal)", "বেগুন", "🍆"),
    LearningItem("ঢ্যাঁড়শ", "Dherosh (Ladies Finger)", "ঢ্যাঁড়শ", "🫛"),
    LearningItem("মিষ্টি কুমড়ো", "Misti Kumro (Pumpkin)", "মিষ্টি কুমড়ো", "🎃"),
    LearningItem("লেবু", "Lebu (Lemon)", "লেবু", "🍋"),
    LearningItem("কাঁচা লঙ্কা", "Kacha Lonka (Chilli)", "কাঁচা লঙ্কা", "🌶️"),
    LearningItem("শসা", "Sosha (Cucumber)", "শসা", "🥒")
)

val marathiVegetables = listOf(
    LearningItem("टोमॅटो", "Tomato", "टोमॅटो", "🍅"),
    LearningItem("बटाटा", "Batata (Potato)", "बटाटा", "🥔"),
    LearningItem("कांदा", "Kanda (Onion)", "कांदा", "🧅"),
    LearningItem("गाजर", "Gajar (Carrot)", "गाजर", "🥕"),
    LearningItem("वांगे", "Vange (Brinjal)", "वांगे", "🍆"),
    LearningItem("भेंडी", "Bhendi (Ladies Finger)", "भेंडी", "🫛"),
    LearningItem("भोपळा", "Bhopla (Pumpkin)", "भोपळा", "🎃"),
    LearningItem("लिंबू", "Limbu (Lemon)", "लिंबू", "🍋"),
    LearningItem("मिरची", "Mirchi (Chilli)", "मिरची", "🌶️"),
    LearningItem("काकडी", "Kakdi (Cucumber)", "काकडी", "🥒")
)

val gujaratiVegetables = listOf(
    LearningItem("ટામેટાં", "Tameta (Tomato)", "ટામેટાં", "🍅"),
    LearningItem("બટાકા", "Bataka (Potato)", "બટાકા", "🥔"),
    LearningItem("ડુંગળી", "Dungli (Onion)", "ડુંગળી", "🧅"),
    LearningItem("ગાજર", "Gajar (Carrot)", "ગાજર", "🥕"),
    LearningItem("રીંગણ", "Ringan (Brinjal)", "રીંગણ", "🍆"),
    LearningItem("ભીંડો", "Bhindo (Ladies Finger)", "ભીંડો", "🫛"),
    LearningItem("કોળું", "Kolu (Pumpkin)", "કોળું", "🎃"),
    LearningItem("લીંબુ", "Limbu (Lemon)", "લીંબુ", "🍋"),
    LearningItem("મરચું", "Marchu (Chilli)", "મરચું", "🌶️"),
    LearningItem("કાકડી", "Kakdi (Cucumber)", "કાકડી", "🥒")
)

object LanguageData {
    val languages = listOf(
        // Telugu
        LanguageConfig(
            code = "te", nativeName = "తెలుగు", flag = "🇮🇳", title = "అక్షరమాల",
            lessons = mapOf(
                LessonCategory.LETTERS to (teluguVowels + teluguConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("అ - అమ్మ", "Amma (Mother)", "అ అమ్మ", "👩"), LearningItem("ఆ - ఆవు", "Aavu (Cow)", "ఆ ఆవు", "🐄"),
                    LearningItem("ఇ - ఇల్లు", "Illu (House)", "ఇ ఇల్లు", "🏠"), LearningItem("ఈ - ఈగ", "Eega (Housefly)", "ఈ ఈగ", "🪰"),
                    LearningItem("ఉ - ఉడుత", "Uduta (Squirrel)", "ఉ ఉడుత", "🐿️"), LearningItem("ఊ - ఊయల", "Ooyala (Swing)", "ఊ ఊయల", "🎡"),
                    LearningItem("ఋ - ఋషి", "Rushi (Sage)", "ఋ ఋషి", "🧘‍♂️"), LearningItem("ఎ - ఎలుక", "Eluka (Rat)", "ఎ ఎలుక", "🐀"),
                    LearningItem("ఏ - ఏనుగు", "Aenugu (Elephant)", "ఏ ఏనుగు", "🐘"), LearningItem("ఐ - ఐదు", "Aidu (Five)", "ఐ ఐదు", "5️⃣"),
                    LearningItem("ఒ - ఒంటె", "Onte (Camel)", "ఒ ఒంటె", "🐫"), LearningItem("ఓ - ఓడ", "Oda (Ship)", "ఓ ఓడ", "🚢"),
                    LearningItem("ఔ - ఔషధం", "Authadham (Medicine)", "ఔ ఔషధం", "💊"), LearningItem("అం - అందెల", "Andela (Anklets)", "అం అందెల", "🩰"),
                    LearningItem("అః - అంతఃపురం", "Anthahpuram (Palace)", "అః అంతఃపురం", "🏰")
                ),
                LessonCategory.NUMBERS to generateTeluguNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("te"),
                LessonCategory.SPICES to listOf(
                    LearningItem("ఎరుపు", "Erupu (Red)", "ఎరుపు", "🔴"), LearningItem("ఆకుపచ్చ", "Aakupachha (Green)", "ఆకుపచ్చ", "🟢"),
                    LearningItem("నీలం", "Neelam (Blue)", "నీలం", "🔵"), LearningItem("పసుపు", "Pasupu (Yellow)", "పసుపు", "🟡"),
                    LearningItem("నలుపు", "Nalupu (Black)", "నలుపు", "⚫"), LearningItem("తెలుపు", "Telupu (White)", "తెలుపు", "⚪"),
                    LearningItem("నారింజ", "Naarinja (Orange)", "నారింజ", "🟠"), LearningItem("గులాబీ", "Gulaabee (Pink)", "గులాబీ", "🌸"),
                    LearningItem("ఊదా", "Oodhaa (Purple)", "ఊదా", "🍇"), LearningItem("గోధుమ", "Godhuma (Brown)", "గోధుమ", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("చైత్రం", "Chaitram (1st Month)", "చైత్రం", "🌸"),
                    LearningItem("వైశాఖం", "Vaisakham (2nd Month)", "వైశాఖం", "☀️"),
                    LearningItem("జ్యేష్ఠం", "Jyeshtam (3rd Month)", "జ్యేష్ఠం", "🔥"),
                    LearningItem("ఆషాఢం", "Ashadham (4th Month)", "ఆషాఢం", "🌧️"),
                    LearningItem("శ్రావణం", "Sravanam (5th Month)", "శ్రావణం", "⛈️"),
                    LearningItem("భాద్రపదం", "Bhadrapadam (6th Month)", "భాద్రపదం", "🌾"),
                    LearningItem("ఆశ్వయుజం", "Aswayujam (7th Month)", "ఆశ్వయుజం", "🪔"),
                    LearningItem("కార్తీకం", "Karthikam (8th Month)", "కార్తీకం", "🕯️"),
                    LearningItem("మార్గశిరం", "Margasiram (9th Month)", "మార్గశిరం", "❄️"),
                    LearningItem("పుష్యం", "Pushyam (10th Month)", "పుష్యం", "🌬️"),
                    LearningItem("మాఘం", "Magham (11th Month)", "మాఘం", "🍯"),
                    LearningItem("ఫాల్గుణం", "Phalgunam (12th Month)", "ఫాల్గుణం", "🎨")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("సింహం", "Simham (Lion)", "సింహం", "🦁"),
                    LearningItem("పులి", "Puli (Tiger)", "పులి", "🐯"),
                    LearningItem("ఏనుగు", "Aenugu (Elephant)", "ఏనుగు", "🐘"),
                    LearningItem("కోతి", "Kothi (Monkey)", "కోతి", "🐒"),
                    LearningItem("చిలుక", "Chiluka (Parrot)", "చిలుక", "🦜"),
                    LearningItem("నెమలి", "Nemali (Peacock)", "నెమలి", "🦚"),
                    LearningItem("కుక్క", "Kukka (Dog)", "కుక్క", "🐶"),
                    LearningItem("పిల్లి", "Pilli (Cat)", "పిల్లి", "🐱"),
                    LearningItem("ఆవు", "Aavu (Cow)", "ఆవు", "🐄"),
                    LearningItem("గుర్రం", "Gurram (Horse)", "గుర్రం", "🐎"),
                    LearningItem("జిరాఫీ", "Jiraafi (Giraffe)", "జిరాఫీ", "🦒"),
                    LearningItem("జింక", "Jinka (Deer)", "జింక", "🦌"),
                    LearningItem("మోటు", "Motu (Our Mascot)", "మోటు", "🥟"),
                    LearningItem("టీచర్", "Teacher (Instructor)", "టీచర్", "👩‍🏫")
                ),
                LessonCategory.VEGETABLES to teluguVegetables,
                LessonCategory.STATES_CAPITALS to teluguStatesAndCapitals,
                LessonCategory.NATIONAL_SYMBOLS to teluguNationalSymbols,
                LessonCategory.RELATIONSHIPS to teluguRelationships,
                LessonCategory.FOODS to teluguFoods,
                LessonCategory.FRUITS_FLOWERS to teluguFruitsAndFlowers,
                LessonCategory.BODY_PARTS to teluguBodyParts
            )
        ),
        // English
        LanguageConfig(
            code = "en", nativeName = "English", flag = "🇬🇧", title = "Alphabet",
            lessons = mapOf(
                LessonCategory.LETTERS to englishLetters,
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("A - Apple", "Apple", "A for Apple", "🍎"), LearningItem("B - Ball", "Ball", "B for Ball", "⚽"),
                    LearningItem("C - Cat", "Cat", "C for Cat", "🐱"), LearningItem("D - Dog", "Dog", "D for Dog", "🐶"),
                    LearningItem("E - Elephant", "Elephant", "E for Elephant", "🐘"), LearningItem("F - Fish", "Fish", "F for Fish", "🐟"),
                    LearningItem("G - Grapes", "Grapes", "G for Grapes", "🍇"), LearningItem("H - House", "House", "H for House", "🏠"),
                    LearningItem("I - Ice Cream", "Ice Cream", "I for Ice Cream", "🍦"), LearningItem("J - Jug", "Jug", "J for Jug", "🏺"),
                    LearningItem("K - Kite", "Kite", "K for Kite", "🪁"), LearningItem("L - Lion", "Lion", "L for Lion", "🦁"),
                    LearningItem("M - Monkey", "Monkey", "M for Monkey", "🐒"), LearningItem("N - Nest", "Nest", "N for Nest", "🪹"),
                    LearningItem("O - Orange", "Orange", "O for Orange", "🍊"), LearningItem("P - Parrot", "Parrot", "P for Parrot", "🦜"),
                    LearningItem("Q - Queen", "Queen", "Q for Queen", "👸"), LearningItem("R - Rabbit", "Rabbit", "R for Rabbit", "🐇"),
                    LearningItem("S - Sun", "Sun", "S for Sun", "☀️"), LearningItem("T - Tiger", "Tiger", "T for Tiger", "🐯"),
                    LearningItem("U - Umbrella", "Umbrella", "U for Umbrella", "☂️"), LearningItem("V - Van", "Van", "V for Van", "🚐"),
                    LearningItem("W - Watch", "Watch", "W for Watch", "⌚"), LearningItem("X - Xylophone", "Xylophone", "X for Xylophone", "🎼"),
                    LearningItem("Y - Yak", "Yak", "Y for Yak", "🐂"), LearningItem("Z - Zebra", "Zebra", "Z for Zebra", "🦓")
                ),
                LessonCategory.NUMBERS to generateEnglishNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("en"),
                LessonCategory.SPICES to listOf(
                    LearningItem("Red", "Red", "Red", "🔴"), LearningItem("Green", "Green", "Green", "🟢"),
                    LearningItem("Blue", "Blue", "Blue", "🔵"), LearningItem("Yellow", "Yellow", "Yellow", "🟡"),
                    LearningItem("Black", "Black", "Black", "⚫"), LearningItem("White", "White", "White", "⚪"),
                    LearningItem("Orange", "Orange", "Orange", "🟠"), LearningItem("Pink", "Pink", "Pink", "🌸"),
                    LearningItem("Purple", "Purple", "Purple", "🍇"), LearningItem("Brown", "Brown", "Brown", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("January", "January (1st Month)", "January", "❄️"),
                    LearningItem("February", "February (2nd Month)", "February", "💖"),
                    LearningItem("March", "March (3rd Month)", "March", "🌱"),
                    LearningItem("April", "April (4th Month)", "April", "🌸"),
                    LearningItem("May", "May (5th Month)", "May", "☀️"),
                    LearningItem("June", "June (6th Month)", "June", "🏖️"),
                    LearningItem("July", "July (7th Month)", "July", "🍦"),
                    LearningItem("August", "August (8th Month)", "August", "⛱️"),
                    LearningItem("September", "September (9th Month)", "September", "🍁"),
                    LearningItem("October", "October (10th Month)", "October", "🎃"),
                    LearningItem("November", "November (11th Month)", "November", "🍂"),
                    LearningItem("December", "December (12th Month)", "December", "🎄")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("Lion", "Lion", "Lion", "🦁"),
                    LearningItem("Tiger", "Tiger", "Tiger", "🐯"),
                    LearningItem("Elephant", "Elephant", "Elephant", "🐘"),
                    LearningItem("Monkey", "Monkey", "Monkey", "🐒"),
                    LearningItem("Parrot", "Parrot", "Parrot", "🦜"),
                    LearningItem("Peacock", "Peacock", "Peacock", "🦚"),
                    LearningItem("Dog", "Dog", "Dog", "🐶"),
                    LearningItem("Cat", "Cat", "Cat", "🐱"),
                    LearningItem("Cow", "Cow", "Cow", "🐄"),
                    LearningItem("Horse", "Horse", "Horse", "🐎"),
                    LearningItem("Giraffe", "Giraffe", "Giraffe", "🦒"),
                    LearningItem("Deer", "Deer", "Deer", "🦌")
                ),
                LessonCategory.VEGETABLES to englishVegetables,
                LessonCategory.STATES_CAPITALS to englishStatesAndCapitals,
                LessonCategory.NATIONAL_SYMBOLS to englishNationalSymbols,
                LessonCategory.RELATIONSHIPS to englishRelationships,
                LessonCategory.FOODS to englishFoods,
                LessonCategory.FRUITS_FLOWERS to englishFruitsAndFlowers,
                LessonCategory.BODY_PARTS to englishBodyParts
            )
        ),
        // Tamil
        LanguageConfig(
            code = "ta", nativeName = "தமிழ்", flag = "🇮🇳", title = "எழுத்துமாலை",
            lessons = mapOf(
                LessonCategory.LETTERS to (tamilVowels + tamilConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("அ - அம்மா", "Amma (Mother)", "அ அம்மா", "👩"), LearningItem("ஆ - ஆடு", "Aadu (Goat)", "ஆ ஆடு", "🐐"),
                    LearningItem("இ - இலை", "Ilai (Leaf)", "இ இலை", "🍃"), LearningItem("ஈ - ஈட்டி", "Eetti (Spear)", "ஈ ஈட்டி", "🔱"),
                    LearningItem("உ - உரல்", "Ural (Mortar)", "உ உரல்", "🪵"), LearningItem("ஊ - ஊஞ்சல்", "Oonjal (Swing)", "ஊ ஊஞ்சல்", "🎡"),
                    LearningItem("எ - எலி", "Eli (Rat)", "எ எலி", "🐀"), LearningItem("ஏ - ஏணி", "Aeni (Ladder)", "ஏ ஏணி", "🪜"),
                    LearningItem("ஐ - ஐந்து", "Ainthu (Five)", "ஐ ஐந்து", "5️⃣"), LearningItem("ஒ - ஒட்டகம்", "Ottagam (Camel)", "ஒ ஒட்டகம்", "🐫"),
                    LearningItem("ஓ - ஓடம்", "Odam (Boat)", "ஓ ஓடம்", "⛵"), LearningItem("ஔ - ஔவையார்", "Avvaiyar (Poetess)", "ஔ ஔவையார்", "👵")
                ),
                LessonCategory.NUMBERS to generateTamilNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("ta"),
                LessonCategory.SPICES to listOf(
                    LearningItem("சிவப்பு", "Sivappu (Red)", "சிவப்பு", "🔴"), LearningItem("பச்சை", "Pachai (Green)", "பச்சை", "🟢"),
                    LearningItem("நீலம்", "Neelam (Blue)", "நீலம்", "🔵"), LearningItem("மஞ்சள்", "Manjal (Yellow)", "மஞ்சள்", "🟡"),
                    LearningItem("கருப்பு", "Karuppu (Black)", "கருப்பு", "⚫"), LearningItem("வெள்ளை", "Vellai (White)", "வெள்ளை", "⚪"),
                    LearningItem("ஆரஞ்சு", "Aranju (Orange)", "ஆரஞ்சு", "🟠"), LearningItem("இளஞ்சிவப்பு", "Elanchivappu (Pink)", "இளஞ்சிவப்பு", "🌸"),
                    LearningItem("ஊதா", "Ootha (Purple)", "ஊதா", "🍇"), LearningItem("பழுப்பு", "Pazhuppu (Brown)", "பழுப்பு", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("சித்திரை", "Chithirai (1st Month)", "சித்திரை", "🌸"),
                    LearningItem("வைகாசி", "Vaikasi (2nd Month)", "வைகாசி", "☀️"),
                    LearningItem("ஆனி", "Aani (3rd Month)", "ஆனி", "🔥"),
                    LearningItem("ஆடி", "Aadi (4th Month)", "ஆடி", "🌬️"),
                    LearningItem("ஆவணி", "Aavani (5th Month)", "ஆவணி", "🌾"),
                    LearningItem("புரட்டாசி", "Purattasi (6th Month)", "புரட்டாசி", "🛕"),
                    LearningItem("ஐப்பசி", "Aippasi (7th Month)", "ஐப்பசி", "🌧️"),
                    LearningItem("கார்த்திகை", "Karthigai (8th Month)", "கார்த்திகை", "🕯️"),
                    LearningItem("மார்கழி", "Margazhi (9th Month)", "மார்கழி", "❄️"),
                    LearningItem("தை", "Thai (10th Month)", "தை", "🌾"),
                    LearningItem("மாசி", "Maasi (11th Month)", "மாசி", "🎉"),
                    LearningItem("பங்குனி", "Panguni (12th Month)", "பங்குனி", "🎨")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("சிங்கம்", "Singam (Lion)", "சிங்கம்", "🦁"),
                    LearningItem("புலி", "Puli (Tiger)", "புலி", "🐯"),
                    LearningItem("யானை", "Yaanai (Elephant)", "யானை", "🐘"),
                    LearningItem("குரங்கு", "Kurangu (Monkey)", "குரங்கு", "🐒"),
                    LearningItem("கிளி", "Kili (Parrot)", "கிளி", "🦜"),
                    LearningItem("மயில்", "Mayil (Peacock)", "மயில்", "🦚"),
                    LearningItem("நாய்", "Naai (Dog)", "நாய்", "🐶"),
                    LearningItem("பூனை", "Poonai (Cat)", "பூனை", "🐱"),
                    LearningItem("பசு", "Pasu (Cow)", "பசு", "🐄"),
                    LearningItem("குதிரை", "Kuthirai (Horse)", "குதிரை", "🐎"),
                    LearningItem("ஒட்டகச்சிவிங்கி", "Ottagachivingi (Giraffe)", "ஒட்டகச்சிவிங்கி", "🦒"),
                    LearningItem("மான்", "Maan (Deer)", "மான்", "🦌")
                ),
                LessonCategory.VEGETABLES to tamilVegetables
            )
        ),
        // Hindi
        LanguageConfig(
            code = "hi", nativeName = "हिन्दी", flag = "🇮🇳", title = "वर्णमाला",
            lessons = mapOf(
                LessonCategory.LETTERS to (hindiVowels + hindiConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("अ - अनार", "Anaar (Pomegranate)", "अ अनार", "🍎"), LearningItem("आ - आम", "Aam (Mango)", "आ आम", "🥭"),
                    LearningItem("इ - इमली", "Imli (Tamarind)", "इ इमली", "🪵"), LearningItem("ई - ईख", "Eekh (Sugarcane)", "ई ईख", "🌾"),
                    LearningItem("उ - उल्लू", "Ullu (Owl)", "उ उल्लू", "🦉"), LearningItem("ऊ - ऊन", "Oon (Wool)", "ऊ ऊन", "🧶"),
                    LearningItem("ऋ - ऋषि", "Rishi (Sage)", "ऋ ऋषि", "🧘‍♂️"), LearningItem("ए - एड़ी", "Aedi (Heel)", "ए एड़ी", "🦶"),
                    LearningItem("ऐ - ऐनक", "Ainak (Spectacles)", "ऐ ऐनक", "👓"), LearningItem("ओ - ओखली", "Okhli (Mortar)", "ओ ओखली", "🥣"),
                    LearningItem("औ - औरत", "Aurat (Woman)", "औ औरत", "👩"), LearningItem("अं - अंगूर", "Angoor (Grapes)", "अं अंगूर", "🍇"),
                    LearningItem("अः - नमः", "Namaha (Salutation)", "अः नमः", "🙏")
                ),
                LessonCategory.NUMBERS to generateHindiNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("hi"),
                LessonCategory.SPICES to listOf(
                    LearningItem("लाल", "Laal (Red)", "लाल", "🔴"), LearningItem("हरा", "Hara (Green)", "हरा", "🟢"),
                    LearningItem("नीला", "Neela (Blue)", "नीला", "🔵"), LearningItem("पीला", "Peela (Yellow)", "पीला", "🟡"),
                    LearningItem("काला", "Kaala (Black)", "काला", "⚫"), LearningItem("सफेद", "Safed (White)", "सफेद", "⚪"),
                    LearningItem("नारंगी", "Naarangee (Orange)", "नारंगी", "🟠"), LearningItem("गुलाबी", "Gulaabee (Pink)", "गुलाबी", "🌸"),
                    LearningItem("बैंगनी", "Bainganee (Purple)", "बैंगनी", "🍇"), LearningItem("भूरा", "Bhoora (Brown)", "भूरा", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("चैत्र", "Chaitra (1st Month)", "चैत्र", "🌸"),
                    LearningItem("वैशाख", "Vaishakha (2nd Month)", "वैशाख", "☀️"),
                    LearningItem("ज्येष्ठ", "Jyaistha (3rd Month)", "ज्येष्ठ", "🔥"),
                    LearningItem("आषाढ़", "Asadha (4th Month)", "आषाढ़", "🌧️"),
                    LearningItem("श्रावण", "Shravana (5th Month)", "श्रावण", "⛈️"),
                    LearningItem("भाद्रपद", "Bhadrapada (6th Month)", "भाद्रपद", "🌾"),
                    LearningItem("आश्विन", "Ashvina (7th Month)", "आश्विन", "🪔"),
                    LearningItem("कार्तिक", "Kartika (8th Month)", "कार्तिक", "🕯️"),
                    LearningItem("मार्गशीर्ष", "Margashirsha (9th Month)", "मार्गशीर्ष", "❄️"),
                    LearningItem("पौष", "Pausha (10th Month)", "पौष", "🌬️"),
                    LearningItem("माघ", "Magha (11th Month)", "माघ", "🍯"),
                    LearningItem("फाल्गुन", "Phalguna (12th Month)", "फाल्गुन", "🎨")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("शेर", "Sher (Lion)", "शेर", "🦁"),
                    LearningItem("बाघ", "Baagh (Tiger)", "बाघ", "🐯"),
                    LearningItem("हाथी", "Haathi (Elephant)", "हाथी", "🐘"),
                    LearningItem("बंदर", "Bandar (Monkey)", "बंदर", "🐒"),
                    LearningItem("तोता", "Tota (Parrot)", "तोता", "🦜"),
                    LearningItem("मोर", "Mor (Peacock)", "मोर", "🦚"),
                    LearningItem("कुत्ता", "Kutta (Dog)", "कुत्ता", "🐶"),
                    LearningItem("बिल्ली", "Billi (Cat)", "बिल्ली", "🐱"),
                    LearningItem("गाय", "Gaay (Cow)", "गाय", "🐄"),
                    LearningItem("घोड़ा", "Ghoda (Horse)", "घोड़ा", "🐎"),
                    LearningItem("जिराफ़", "Jiraf (Giraffe)", "जिराफ़", "🦒"),
                    LearningItem("हिरण", "Hiran (Deer)", "हिरण", "🦌")
                ),
                LessonCategory.VEGETABLES to hindiVegetables
            )
        ),
        // Arabic
        LanguageConfig(
            code = "ar", nativeName = "العربية", flag = "🇸🇦", title = "الأبجدية",
            lessons = mapOf(
                LessonCategory.LETTERS to (arabicVowels + arabicConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("ا - أَسَد", "Asad (Lion)", "أَلِفْ: أَسَدْ", "🦁"), LearningItem("ب - بَطَّة", "Batta (Duck)", "بَاءْ: بَطَّة", "🦆"),
                    LearningItem("ت - تُفَّاح", "Tuffah (Apple)", "تَاءْ: تُفَّاحْ", "🍎"), LearningItem("ث - ثَعْلَب", "Tha'lab (Fox)", "ثَاءْ: ثَعْلَبْ", "🦊"),
                    LearningItem("ج - جَمَل", "Jamal (Camel)", "جِيمْ: جَمَلْ", "🐪"), LearningItem("ح - حِصَان", "Hisan (Horse)", "حَاءْ: حِصَانْ", "🐎"),
                    LearningItem("خ - خَرُوف", "Kharoof (Sheep)", "خَاءْ: خَرُوفْ", "🐑"), LearningItem("د - دَجَاجَة", "Dajajah (Chicken)", "دَالْ: دَجَاجَة", "🐔"),
                    LearningItem("ذ - ذِئْب", "The'b (Wolf)", "ذَالْ: ذِئْبْ", "🐺"), LearningItem("ر - رُمَّان", "Rumman (Pomegranate)", "رَاءْ: رُمَّانْ", "🍎"),
                    LearningItem("ز - زَرَافَة", "Zarafa (Giraffe)", "زَايْ: زَرَافَة", "🦒"), LearningItem("س - سَمَكَة", "Samaka (Fish)", "سِينْ: سَمَكَة", "🐟"),
                    LearningItem("ش - شَمْس", "Shams (Sun)", "شِينْ: شَمْسْ", "☀️"), LearningItem("ص - صَقْر", "Saqr (Falcon)", "صَادْ: صَقْرْ", "🦅"),
                    LearningItem("ض - ضِفْدَع", "Difda' (Frog)", "ضَادْ: ضِفْدَعْ", "🐸")
                ),
                LessonCategory.NUMBERS to generateArabicNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("ar"),
                LessonCategory.SPICES to listOf(
                    LearningItem("أَحْمَر", "Ahmar (Red)", "أَحْمَر", "🔴"), LearningItem("أَخْضَر", "Akhdar (Green)", "أَخْضَر", "🟢"),
                    LearningItem("أَزْرَق", "Azraq (Blue)", "أَزْرَق", "🔵"), LearningItem("أَصْفَر", "Asfar (Yellow)", "أَصْفَر", "🟡"),
                    LearningItem("أَسْوَد", "Aswad (Black)", "أَسْوَد", "⚫"), LearningItem("أَبْيَض", "Abyad (White)", "أَبْيَض", "⚪"),
                    LearningItem("بُرْتُقَالِي", "Burtuqali (Orange)", "بُرْتُقَالِي", "🟠"), LearningItem("وَرْدِي", "Wardi (Pink)", "وَرْدِي", "🌸"),
                    LearningItem("بَنَفْسَجِي", "Banafsaqi (Purple)", "بَنَفْسَجِيّ", "🍇"), LearningItem("بُنِّي", "Bunni (Brown)", "بُنِّيّ", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("المُحَرَّم", "Muharram (1st Month)", "المحرم", "🌙"),
                    LearningItem("صَفَر", "Safar (2nd Month)", "صفر", "🐫"),
                    LearningItem("رَبِيع الأوَّل", "Rabi' al-awwal (3rd Month)", "ربيع الأول", "🌱"),
                    LearningItem("رَبِيع الثَّانِي", "Rabi' al-thani (4th Month)", "ربيع الثاني", "🌿"),
                    LearningItem("جُمَادَى الأُولَى", "Jumada al-ula (5th Month)", "جمادى الأولى", "❄️"),
                    LearningItem("جُمَادَى الآخِرَة", "Jumada al-akhirah (6th Month)", "جمادى الآخرة", "🌬️"),
                    LearningItem("رَجَب", "Rajab (7th Month)", "رجب", "🕌"),
                    LearningItem("شَعْبَان", "Sha'ban (8th Month)", "شعبان", "✨"),
                    LearningItem("رَمَضَان", "Ramadan (9th Month)", "رمضان", "🕌"),
                    LearningItem("شَوَّال", "Shawwal (10th Month)", "شوال", "🎉"),
                    LearningItem("ذُو القَعْدَة", "Dhu al-qa'dah (11th Month)", "ذو القعدة", "📜"),
                    LearningItem("ذُو الحِجَّة", "Dhu al-hijjah (12th Month)", "ذو الحجة", "🕋")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("أسد", "Asad (Lion)", "أسد", "🦁"),
                    LearningItem("نمر", "Namir (Tiger)", "نمر", "🐯"),
                    LearningItem("فيل", "Feel (Elephant)", "فيل", "🐘"),
                    LearningItem("قرد", "Qird (Monkey)", "قرد", "🐒"),
                    LearningItem("ببّغاء", "Babbagha' (Parrot)", "ببّغاء", "🦜"),
                    LearningItem("طاووس", "Tawous (Peacock)", "طاووس", "🦚"),
                    LearningItem("كلب", "Kalb (Dog)", "كلب", "🐶"),
                    LearningItem("قطّة", "Qittah (Cat)", "قطّة", "🐱"),
                    LearningItem("بقرة", "Baqarah (Cow)", "بقرة", "🐄"),
                    LearningItem("حصان", "Hisan (Horse)", "حصان", "🐎"),
                    LearningItem("زرافة", "Zarafah (Giraffe)", "زرافة", "🦒"),
                    LearningItem("غزال", "Ghazal (Deer)", "غزال", "🦌")
                ),
                LessonCategory.VEGETABLES to arabicVegetables
            )
        ),
        // Kannada
        LanguageConfig(
            code = "kn", nativeName = "ಕನ್ನಡ", flag = "🇮🇳", title = "ಅಕ್ಷರಮಾಲೆ",
            lessons = mapOf(
                LessonCategory.LETTERS to (kannadaVowels + kannadaConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("ಅ - ಅಮ್ಮ", "Amma (Mother)", "ಅ ಅಮ್ಮ", "👩"), LearningItem("ಆ - ಆನೆ", "Aane (Elephant)", "ಆ ಆನೆ", "🐘"),
                    LearningItem("ಇ - ಇಲಿ", "Ili (Rat)", "ಇ ಇಲಿ", "🐭"), LearningItem("ಈ - ಈಜು", "Eeju (Swim)", "ಈ ಈಜು", "🏊"),
                    LearningItem("ಉ - ಉಡುಪು", "Udupu (Dress)", "ಉ ಉಡುಪು", "👗"), LearningItem("ಊ - ಊಟ", "Oota (Meal/Food)", "ಊ ಊಟ", "🍛"),
                    LearningItem("ಋ - ಋಷಿ", "Rushi (Sage)", "ಋ ಋಷಿ", "🧘‍♂️"), LearningItem("ಎ - ಎಲೆ", "Ele (Leaf)", "ಎ ಎಲೆ", "🍃"),
                    LearningItem("ಏ - ಏಣಿ", "Aeni (Ladder)", "ಏ ಏಣಿ", "🪜"), LearningItem("ಐ - ಐದು", "Aidu (Five)", "ಐ ಐದು", "5️⃣"),
                    LearningItem("ಒ - ಒಂಟೆ", "Onte (Camel)", "ಒ ಒಂಟೆ", "🐫"), LearningItem("ಓ - ಓಡ", "Oda (Boat)", "ಓ ಓಡ", "⛵"),
                    LearningItem("ಔ - ಔಷಧ", "Oushadha (Medicine)", "ಔ ಔಷಧ", "💊"), LearningItem("ಅಂ - ಅಂಕೆ", "Anke (Number)", "ಅಂ ಅಂಕೆ", "🔢"),
                    LearningItem("ಅಃ - ದುಃಖ", "Dukha (Sorrow)", "ಅಃ ದುಃಖ", "😢")
                ),
                LessonCategory.NUMBERS to generateKannadaNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("kn"),
                LessonCategory.SPICES to listOf(
                    LearningItem("ಕೆಂಪು", "Kempu (Red)", "ಕೆಂಪು", "🔴"), LearningItem("ಹಸಿರು", "Hasiru (Green)", "ಹಸಿರು", "🟢"),
                    LearningItem("ನೀಲಿ", "Neeli (Blue)", "ನೀಲಿ", "🔵"), LearningItem("ಹಳದಿ", "Haladi (Yellow)", "ಹಳದಿ", "🟡"),
                    LearningItem("ಕಪ್ಪು", "Kappu (Black)", "ಕಪ್ಪು", "⚫"), LearningItem("ಬಿಳಿ", "Bili (White)", "ಬಿಳಿ", "⚪"),
                    LearningItem("ಕಿತ್ತಳೆ", "Kittale (Orange)", "ಕಿತ್ತಳೆ", "🟠"), LearningItem("ಗುಲಾಬಿ", "Gulabi (Pink)", "ಗುಲಾಬಿ", "🌸"),
                    LearningItem("ನೇರಳೆ", "Nerale (Purple)", "ನೇರಳೆ", "🍇"), LearningItem("ಕಂದು", "Kandu (Brown)", "ಕಂದು", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("ಚೈತ್ರ", "Chaitra (1st Month)", "ಚೈತ್ರ", "🌸"),
                    LearningItem("ವೈಶಾಖ", "Vaishakha (2nd Month)", "ವೈಶಾಖ", "☀️"),
                    LearningItem("ಜ್ಯೇಷ್ಠ", "Jyeshtha (3rd Month)", "ಜ್ಯೇಷ್ಠ", "🔥"),
                    LearningItem("ಆಷಾಢ", "Ashadha (4th Month)", "ಆಷಾಢ", "🌧️"),
                    LearningItem("ಶ್ರಾವಣ", "Shravana (5th Month)", "ಶ್ರಾವಣ", "⛈️"),
                    LearningItem("ಭಾದ್ರಪದ", "Bhadrapada (6th Month)", "ಭಾದ್ರಪದ", "🌾"),
                    LearningItem("ಆಶ್ವಯುಜ", "Ashwayuja (7th Month)", "ಆಶ್ವಯುಜ", "🪔"),
                    LearningItem("ಕಾರ್ತೀಕ", "Karthika (8th Month)", "ಕಾರ್ತೀಕ", "🕯️"),
                    LearningItem("ಮಾರ್ಗಶಿರ", "Margashira (9th Month)", "ಮಾರ್ಗಶಿರ", "❄️"),
                    LearningItem("ಪುಷ್ಯ", "Pushya (10th Month)", "ಪುಷ್ಯ", "🌬️"),
                    LearningItem("ಮಾಘ", "Magha (11th Month)", "ಮಾಘ", "🍯"),
                    LearningItem("ಫಾಲ್ಗುಣ", "Phalguna (12th Month)", "ಫಾಲ್ಗುಣ", "🎨")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("ಸಿಂಹ", "Simha (Lion)", "ಸಿಂಹ", "🦁"),
                    LearningItem("ಹುಲಿ", "Huli (Tiger)", "ಹುಲಿ", "🐯"),
                    LearningItem("ಆನೆ", "Aane (Elephant)", "ಆನೆ", "🐘"),
                    LearningItem("ಕೋತಿ", "Kothi (Monkey)", "ಕೋತಿ", "🐒"),
                    LearningItem("ಗಿಳಿ", "Gili (Parrot)", "ಗಿಳಿ", "🦜"),
                    LearningItem("ನವಿಲು", "Navilu (Peacock)", "ನವಿಲು", "🦚"),
                    LearningItem("ನಾಯಿ", "Naayi (Dog)", "ನಾಯಿ", "🐶"),
                    LearningItem("ಬೆಕ್ಕು", "Bekku (Cat)", "ಬೆಕ್ಕು", "🐱"),
                    LearningItem("ಹಸು", "Hasu (Cow)", "ಹಸು", "🐄"),
                    LearningItem("ಕುದುರೆ", "Kudure (Horse)", "ಕುದುರೆ", "🐎"),
                    LearningItem("ಜಿರಾಫೆ", "Jirafe (Giraffe)", "ಜಿರಾಫೆ", "🦒"),
                    LearningItem("ಜಿಂಕೆ", "Jinke (Deer)", "ಜಿಂಕೆ", "🦌")
                ),
                LessonCategory.VEGETABLES to kannadaVegetables
            )
        ),
        // Malayalam
        LanguageConfig(
            code = "ml", nativeName = "മലയാളം", flag = "🇮🇳", title = "അക്ഷരമാല",
            lessons = mapOf(
                LessonCategory.LETTERS to (malayalamVowels + malayalamConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("അ - അമ്മ", "Amma (Mother)", "അ അമ്മ", "👩"), LearningItem("ആ - ആന", "Aana (Elephant)", "ആ ആന", "🐘"),
                    LearningItem("ഇ - ഇല", "Ila (Leaf)", "ഇ ഇല", "🍃"), LearningItem("ഈ - ഈച്ച", "Eecha (Housefly)", "ഈ ഈച്ച", "🪰"),
                    LearningItem("ഉ - ഉപ്പ്", "Uppu (Salt)", "ഉ ഉപ്പ്", "🧂"), LearningItem("ഊ - ഊഞ്ഞാൽ", "Oonjaal (Swing)", "ഊ ഊഞ്ഞാൽ", "🎡"),
                    LearningItem("ഋ - ഋഷി", "Rishi (Sage)", "ഋ ഋഷി", "🧘‍♂️"), LearningItem("എ - എലി", "Eli (Rat)", "എ എലി", "🐀"),
                    LearningItem("ஏ - ஏணி", "Aeni (Ladder)", "ஏ ஏணி", "🪜"), LearningItem("ഐ - ഐസ്", "Ice (Ice)", "ഐ ഐസ്", "🧊"),
                    LearningItem("ഒ - ഒട്ടകം", "Ottakam (Camel)", "ഒ ഒട്ടകം", "🐫"), LearningItem("ഓ - ഓടക്കുഴൽ", "Odakkuzhal (Flute)", "ഓ ഓടക്കുഴൽ", "🪈"),
                    LearningItem("ഔ - ഔഷധം", "Oushadham (Medicine)", "ഔ ഔഷധം", "💊"), LearningItem("അം - അമ്പു", "Ambu (Arrow)", "അം അമ്പു", "🏹"),
                    LearningItem("അഃ - ദുഃഖം", "Dukham (Sorrow)", "അഃ ദുഃഖം", "😢")
                ),
                LessonCategory.NUMBERS to generateMalayalamNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("ml"),
                LessonCategory.SPICES to listOf(
                    LearningItem("ചുവപ്പ്", "Chuvappu (Red)", "ചുവപ്പ്", "🔴"), LearningItem("പച്ച", "Pachha (Green)", "പച്ച", "🟢"),
                    LearningItem("നീല", "Neela (Blue)", "നീല", "🔵"), LearningItem("മഞ്ഞ", "Manja (Yellow)", "മഞ്ഞ", "🟡"),
                    LearningItem("കറുപ്പ്", "Karuppu (Black)", "കറുപ്പ്", "⚫"), LearningItem("വെള്ള", "Vella (White)", "വെള്ള", "⚪"),
                    LearningItem("ഓറഞ്ച്", "Orange (Orange)", "ഓറഞ്ച്", "🟠"), LearningItem("പിങ്ക്", "Pink (Pink)", "പിങ്ക്", "🌸"),
                    LearningItem("വയലറ്റ്", "Violet (Purple)", "വയലറ്റ്", "🍇"), LearningItem("തവിട്ട്", "Thavittu (Brown)", "തവിട്ട്", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("ചിങ്ങം", "Chingam (1st Month)", "ചിങ്ങം", "🌾"),
                    LearningItem("കന്നി", "Kanni (2nd Month)", "കന്നി", "☀️"),
                    LearningItem("തുലാം", "Thulam (3rd Month)", "തുലാം", "⚖️"),
                    LearningItem("vrishchikam", "Vrishchikam (4th Month)", "വൃശ്ചികം", "🦂"),
                    LearningItem("ധനു", "Dhanu (5th Month)", "ധനു", "🏹"),
                    LearningItem("മകരം", "Makaram (6th Month)", "മകരം", "🐊"),
                    LearningItem("കുംഭം", "Kumbham (7th Month)", "കുംഭം", "🍯"),
                    LearningItem("മീനം", "Meenam (8th Month)", "മീനം", "🐟"),
                    LearningItem("മേടം", "Medam (9th Month)", "മേടം", "🌸"),
                    LearningItem("ഇടവം", "Edavam (10th Month)", "ഇടവം", "🐂"),
                    LearningItem("മിഥുനം", "Mithunam (11th Month)", "മിഥുനം", "👥"),
                    LearningItem("കർക്കടകം", "Karkidakam (12th Month)", "കർക്കടകം", "🌧️")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("സിംഹം", "Simham (Lion)", "സിംഹം", "🦁"),
                    LearningItem("പുലി", "Puli (Tiger)", "പുലി", "🐯"),
                    LearningItem("ആന", "Aana (Elephant)", "ആന", "🐘"),
                    LearningItem("കുരങ്ങ്", "Kurangu (Monkey)", "കുരങ്ങ്", "🐒"),
                    LearningItem("തത്ത", "Thatha (Parrot)", "തത്ത", "🦜"),
                    LearningItem("മയിൽ", "Mayil (Peacock)", "മയിൽ", "🦚"),
                    LearningItem("നായ", "Naaya (Dog)", "നായ", "🐶"),
                    LearningItem("പൂച്ച", "Poocha (Cat)", "പൂച്ച", "🐱"),
                    LearningItem("പശു", "Pashu (Cow)", "പശു", "🐄"),
                    LearningItem("കുതിര", "Kuthira (Horse)", "കുതിര", "🐎"),
                    LearningItem("ജിറാഫ്", "Jiraaf (Giraffe)", "ജിറാഫ്", "🦒"),
                    LearningItem("മാൻ", "Maan (Deer)", "മാൻ", "🦌")
                ),
                LessonCategory.VEGETABLES to malayalamVegetables
            )
        ),
        // Bengali
        LanguageConfig(
            code = "bn", nativeName = "বাংলা", flag = "🇮🇳", title = "বর্ণমালা",
            lessons = mapOf(
                LessonCategory.LETTERS to (bengaliVowels + bengaliConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("অ - অজগর", "Ojogor (Python)", "অ অজগর", "🐍"), LearningItem("আ - আম", "Aam (Mango)", "আ আম", "🥭"),
                    LearningItem("ই - ইঁদুর", "Indur (Mouse)", "ই ইঁদুর", "🐭"), LearningItem("ঈ - ঈগল", "Igol (Eagle)", "ঈ ঈগল", "🦅"),
                    LearningItem("উ - উট", "Ut (Camel)", "উ উট", "🐫"), LearningItem("ঊ - ঊষর", "Ushor (Desert)", "ঊ ঊষর", "🏜️"),
                    LearningItem("ঋ - ঋষি", "Rishi (Sage)", "ঋ ঋষি", "🧘‍♂️"), LearningItem("এ - একতারা", "Ektara (Lute)", "এ একতারা", "🪕"),
                    LearningItem("ঐ - ঐরাবত", "Oirabot (Elephant)", "ঐ ঐরাবত", "🐘"), LearningItem("ও - ওল", "Ol (Yam)", "ও ওল", "🥔"),
                    LearningItem("ঔ - ঔষধ", "Oushodh (Medicine)", "ঔ ঔষধ", "💊")
                ),
                LessonCategory.NUMBERS to generateBengaliNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("bn"),
                LessonCategory.SPICES to listOf(
                    LearningItem("লাল", "Lal (Red)", "লাল", "🔴"), LearningItem("সবুজ", "Sobuj (Green)", "সবুজ", "🟢"),
                    LearningItem("নীল", "Neel (Blue)", "নীল", "🔵"), LearningItem("হলুদ", "Holud (Yellow)", "হলুদ", "🟡"),
                    LearningItem("কালো", "Kalo (Black)", "কালো", "⚫"), LearningItem("সাদা", "Sada (White)", "সাদা", "⚪"),
                    LearningItem("কমলা", "Komola (Orange)", "কমলা", "🟠"), LearningItem("গোলাপী", "Golapi (Pink)", "গোলাপী", "🌸"),
                    LearningItem("বেগুনী", "Beguni (Purple)", "বেগুনী", "🍇"), LearningItem("বাদামী", "Badami (Brown)", "বাদামী", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("বৈশাখ", "Boishakh (1st Month)", "বৈশাখ", "🌾"), LearningItem("জৈষ্ঠ্য", "Joishtho (2nd Month)", "জৈষ্ঠ্য", "☀️"),
                    LearningItem("আষাঢ়", "Ashar (3rd Month)", "আষাঢ়", "🌧️"), LearningItem("শ্রাবণ", "Srabon (4th Month)", "শ্রাবণ", "⛈️"),
                    LearningItem("ভাদ্র", "Bhadro (5th Month)", "ভাদ্র", "🌾"), LearningItem("আশ্বিন", "Ashwin (6th Month)", "আশ্বিন", "🌅"),
                    LearningItem("কার্তিক", "Kartik (7th Month)", "কার্তিক", "🪔"), LearningItem("অগ্রহায়ণ", "Agrahayon (8th Month)", "অগ্রহায়ণ", "🌾"),
                    LearningItem("পৌষ", "Poush (9th Month)", "পৌষ", "🌬️"), LearningItem("মাঘ", "Magh (10th Month)", "মাঘ", "🥶"),
                    LearningItem("ফাল্গুন", "Falgun (11th Month)", "ফাল্গুন", "🎨"), LearningItem("চৈত্র", "Choitro (12th Month)", "চৈত্র", "🌸")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("সিংহ", "Singho (Lion)", "সিংহ", "🦁"), LearningItem("বাঘ", "Bagh (Tiger)", "বাঘ", "🐯"),
                    LearningItem("হাতি", "Hati (Elephant)", "হাতি", "🐘"), LearningItem("বানর", "Banor (Monkey)", "বানর", "🐒"),
                    LearningItem("টিয়া", "Tiya (Parrot)", "টিয়া", "🦜"), LearningItem("ময়ূর", "Moyur (Peacock)", "ময়ূর", "🦚"),
                    LearningItem("কুকুর", "Kukur (Dog)", "কুকুর", "🐶"), LearningItem("বেড়াল", "Beral (Cat)", "বেড়াল", "🐱"),
                    LearningItem("গরু", "Goru (Cow)", "গরু", "🐄"), LearningItem("ঘোড়া", "Ghora (Horse)", "ঘোড়া", "🐎"),
                    LearningItem("জিরাফ", "Jiraf (Giraffe)", "জিরাফ", "🦒"), LearningItem("হরিণ", "Horin (Deer)", "হরিণ", "🦌")
                ),
                LessonCategory.VEGETABLES to bengaliVegetables
            )
        ),
        // Marathi
        LanguageConfig(
            code = "mr", nativeName = "मराठी", flag = "🇮🇳", title = "वर्णमाला",
            lessons = mapOf(
                LessonCategory.LETTERS to (marathiVowels + marathiConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("अ - अननस", "Ananas (Pineapple)", "अ अननस", "🍍"), LearningItem("आ - आंबा", "Amba (Mango)", "आ आंबा", "🥭"),
                    LearningItem("इ - इमारत", "Imarat (Building)", "इ इमारत", "🏢"), LearningItem("ई - इडलिंबू", "Idlimbu (Citron)", "ई इडलिंबू", "🍋"),
                    LearningItem("उ - उखळ", "Ukhal (Mortar)", "उ उखळ", "🥣"), LearningItem("ऊ - ऊस", "Oos (Sugarcane)", "ऊ ऊस", "🎋"),
                    LearningItem("ऋ - ऋषी", "Rishi (Sage)", "ऋ ऋषी", "🧘‍♂️"), LearningItem("ए - एडका", "Edka (Ram)", "ए एडका", "🐏"),
                    LearningItem("ऐ - ऐरण", "Airan (Anvil)", "ऐ ऐरण", "⚓"), LearningItem("ओ - ओढा", "Odha (Stream)", "ओ ओढा", "🏞️"),
                    LearningItem("औ - औषध", "Oushadh (Medicine)", "औ औषध", "💊")
                ),
                LessonCategory.NUMBERS to generateMarathiNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("mr"),
                LessonCategory.SPICES to listOf(
                    LearningItem("लाल", "Lal (Red)", "लाल", "🔴"), LearningItem("हिरवा", "Hirva (Green)", "हिरवा", "🟢"),
                    LearningItem("निळा", "Nila (Blue)", "निळा", "🔵"), LearningItem("पिवळा", "Pivla (Yellow)", "पिवळा", "🟡"),
                    LearningItem("काळा", "Kala (Black)", "काळा", "⚫"), LearningItem("पांढरा", "Pandhra (White)", "पांढरा", "⚪"),
                    LearningItem("केशरी", "Keshari (Orange)", "केशरी", "🟠"), LearningItem("गुलाबी", "Gulabi (Pink)", "गुलाबी", "🌸"),
                    LearningItem("जांभळा", "Jambhla (Purple)", "जांभळा", "🍇"), LearningItem("तपकिरी", "Tapkiri (Brown)", "तपकिरी", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("चैत्र", "Chaitra (1st Month)", "चैत्र", "🌸"), LearningItem("वैशाख", "Vaishakha (2nd Month)", "वैशाख", "☀️"),
                    LearningItem("ज्येष्ठ", "Jyeshtha (3rd Month)", "ज्येष्ठ", "🔥"), LearningItem("आषाढ", "Ashadha (4th Month)", "आषाढ", "🌧️"),
                    LearningItem("श्रावण", "Shravana (5th Month)", "श्रावण", "⛈️"), LearningItem("भाद्रपद", "Bhadrapada (6th Month)", "भाद्रपद", "🌾"),
                    LearningItem("अश्विन", "Ashwin (7th Month)", "अश्विन", "🌅"), LearningItem("कार्तिक", "Kartika (8th Month)", "कार्तिक", "🪔"),
                    LearningItem("मार्गशीर्ष", "Margashirsha (9th Month)", "मार्गशीर्ष", "❄️"), LearningItem("पौष", "Poush (10th Month)", "पौष", "🌬️"),
                    LearningItem("माघ", "Magha (11th Month)", "माघ", "🍯"), LearningItem("फाल्गुन", "Phalguna (12th Month)", "फाल्गुन", "🎨")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("सिंह", "Simha (Lion)", "सिंह", "🦁"), LearningItem("वाघ", "Vagh (Tiger)", "वाघ", "🐯"),
                    LearningItem("हत्ती", "Hatti (Elephant)", "हत्ती", "🐘"), LearningItem("माकड", "Makad (Monkey)", "माकड", "🐒"),
                    LearningItem("पोपट", "Popat (Parrot)", "पोपट", "🦜"), LearningItem("मोर", "Mor (Peacock)", "मोर", "🦚"),
                    LearningItem("कुत्रा", "Kutra (Dog)", "कुत्रा", "🐶"), LearningItem("मांजर", "Manjar (Cat)", "मांजर", "🐱"),
                    LearningItem("गाय", "Gay (Cow)", "गाय", "🐄"), LearningItem("घोडा", "Ghoda (Horse)", "घोडा", "🐎"),
                    LearningItem("जिराफ", "Giraffe (Giraffe)", "जिराफ", "🦒"), LearningItem("हरिण", "Harin (Deer)", "हरिण", "🦌")
                ),
                LessonCategory.VEGETABLES to marathiVegetables
            )
        ),
        // Gujarati
        LanguageConfig(
            code = "gu", nativeName = "ગુજરાતી", flag = "🇮🇳", title = "વર્ણમાલા",
            lessons = mapOf(
                LessonCategory.LETTERS to (gujaratiVowels + gujaratiConsonants),
                LessonCategory.LETTER_WORD to listOf(
                    LearningItem("અ - અનાનસ", "Ananas (Pineapple)", "અ અનાનસ", "🍍"), LearningItem("આ - આગગાડી", "Aaggadi (Train)", "આ આગગાડી", "🚂"),
                    LearningItem("ઇ - ઇમારત", "Imarat (Building)", "ઇ ઇમારત", "🏢"), LearningItem("ઈ - ઈશ્વર", "Ishwar (God)", "ઈ ઈશ્વર", "🙏"),
                    LearningItem("ઉ - ઉપવન", "Upavan (Garden)", "ઉ ઉપવન", "🏡"), LearningItem("ઊ - ઊન", "Oon (Wool)", "ઊ ઊન", "🧶"),
                    LearningItem("ઋ - ઋષિ", "Rishi (Sage)", "ઋ ઋષિ", "🧘‍♂️"), LearningItem("એ - એરણ", "Eran (Anvil)", "એ એરણ", "⚓"),
                    LearningItem("ઐ - ઐરાવત", "Airavat (Elephant)", "ઐ ઐરાવત", "🐘"), LearningItem("ઓ - ઓશીકું", "Oshiku (Pillow)", "ઓ ઓશીકું", "🛌"),
                    LearningItem("ઔ - ઔષધ", "Oushadh (Medicine)", "ઔ ઔષધ", "💊")
                ),
                LessonCategory.NUMBERS to generateGujaratiNumbers(),
                LessonCategory.CONSONANTS to generateBasicWords("gu"),
                LessonCategory.SPICES to listOf(
                    LearningItem("લાલ", "Lal (Red)", "લાલ", "🔴"), LearningItem("લીલો", "Lilo (Green)", "લીલો", "🟢"),
                    LearningItem("વાદળી", "Vadali (Blue)", "વાદળી", "🔵"), LearningItem("પીળો", "Pilo (Yellow)", "પીળો", "🟡"),
                    LearningItem("કાળો", "Kalo (Black)", "કાળો", "⚫"), LearningItem("સફેદ", "Safed (White)", "સફેદ", "⚪"),
                    LearningItem("કેસરી", "Kesari (Orange)", "કેસરી", "🟠"), LearningItem("ગુલાબી", "Gulabi (Pink)", "ગુલાબી", "🌸"),
                    LearningItem("જાંબલી", "Jambali (Purple)", "જાંબલી", "🍇"), LearningItem("કથ્થઈ", "Katthai (Brown)", "કથ્થઈ", "🐻")
                ),
                LessonCategory.MONTHS to listOf(
                    LearningItem("કાર્તક", "Kartak (1st Month)", "કાર્તક", "🕯️"), LearningItem("માગશર", "Magshar (2nd Month)", "માગશર", "❄️"),
                    LearningItem("પોષ", "Posh (3rd Month)", "પોષ", "🌬️"), LearningItem("મહા", "Maha (4th Month)", "મહા", "🍯"),
                    LearningItem("ફાગણ", "Fagan (5th Month)", "ફાગણ", "🎨"), LearningItem("ચૈત્ર", "Chaitra (6th Month)", "ચૈત્ર", "🌸"),
                    LearningItem("વૈશાખ", "Vaishakh (7th Month)", "વૈશાખ", "☀️"), LearningItem("જેઠ", "Jeth (8th Month)", "જેઠ", "🔥"),
                    LearningItem("અષાઢ", "Ashadh (9th Month)", "અષાઢ", "🌧️"), LearningItem("શ્રાવણ", "Shravan (10th Month)", "શ્રાવણ", "⛈️"),
                    LearningItem("ભાદરવો", "Bhadarvo (11th Month)", "ભાદરવો", "🌾"), LearningItem("આસો", "Aaso (12th Month)", "આસો", "🪔")
                ),
                LessonCategory.POEMS to listOf(
                    LearningItem("સિંહ", "Sinh (Lion)", "સિંહ", "🦁"), LearningItem("વાઘ", "Vagh (Tiger)", "વાઘ", "🐯"),
                    LearningItem("હાથી", "Hathi (Elephant)", "હાથી", "🐘"), LearningItem("વાંદરો", "Vandro (Monkey)", "વાંદરો", "🐒"),
                    LearningItem("પોપટ", "Popat (Parrot)", "પોપટ", "🦜"), LearningItem("મોર", "Mor (Peacock)", "મોર", "🦚"),
                    LearningItem("કૂતરો", "Kutro (Dog)", "કૂતરો", "🐶"), LearningItem("બિલાડી", "Biladi (Cat)", "બિલાડી", "🐱"),
                    LearningItem("ગાય", "Gay (Cow)", "ગાય", "🐄"), LearningItem("ઘોડો", "Ghodo (Horse)", "ઘોડો", "🐎"),
                    LearningItem("જીરાફ", "Giraffe (Giraffe)", "જીરાફ", "🦒"), LearningItem("હરણ", "Haran (Deer)", "હરણ", "🦌")
                ),
                LessonCategory.VEGETABLES to gujaratiVegetables
            )
        )
    )

    fun getByCode(code: String): LanguageConfig {
        return languages.firstOrNull { it.code == code } ?: languages[1]
    }

    fun getLetterExample(langCode: String, letter: String): LearningItem? {
        val cleanLetter = letter.trim()
        
        // 1. Try to find in LETTER_WORD list of this language
        val config = languages.find { it.code == langCode }
        if (config != null) {
            val letterWordList = config.lessons[LessonCategory.LETTER_WORD]
            if (!letterWordList.isNullOrEmpty()) {
                val found = letterWordList.find { item ->
                    val disp = item.display.trim()
                    // Matches "అ - అమ్మ" for "అ", "A - Apple" for "A"
                    val partBeforeDash = disp.substringBefore("-").trim()
                    partBeforeDash.equals(cleanLetter, ignoreCase = true) || 
                    disp.startsWith(cleanLetter, ignoreCase = true)
                }
                if (found != null) return found
            }
        }
        
        // 2. Fallbacks for consonants or missed characters for each language:
        return when (langCode) {
            "te" -> getTeluguConsonantExample(cleanLetter)
            "hi" -> getHindiConsonantExample(cleanLetter)
            "ta" -> getTamilConsonantExample(cleanLetter)
            "kn" -> getKannadaConsonantExample(cleanLetter)
            "ml" -> getMalayalamConsonantExample(cleanLetter)
            "ar" -> getArabicConsonantExample(cleanLetter)
            "bn" -> getBengaliConsonantExample(cleanLetter)
            "mr" -> getMarathiConsonantExample(cleanLetter)
            "gu" -> getGujaratiConsonantExample(cleanLetter)
            else -> null
        }
    }

    private fun getTeluguConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "క" -> LearningItem("క - కమలం", "Kamalam (Lotus)", "క కమలం", "🪷")
            "ఖ" -> LearningItem("ఖ - ఖడ్గం", "Khadgam (Sword)", "ఖ ఖడ్గం", "⚔️")
            "గ" -> LearningItem("గ - గడియారం", "Gadiyaaram (Clock)", "గ గడియారం", "⏰")
            "ఘ" -> LearningItem("ఘ - ఘటం", "Ghatam (Pot)", "ఘ ఘటం", "🏺")
            "ఙ" -> LearningItem("ఙ - వాఙ్మయం", "Vaangmayam (Literature)", "ఙ వాఙ్మయం", "📚")
            "చ" -> LearningItem("చ - చక్రం", "Chakram (Wheel)", "చ చక్రం", "🛞")
            "ఛ" -> LearningItem("ఛ - ఛత్రి", "Chhatri (Umbrella)", "ఛ ఛత్రి", "☂️")
            "జ" -> LearningItem("జ - జడ", "Jada (Braid)", "జ జడ", "👧")
            "ఝ" -> LearningItem("ఝ - ఝషం", "Jhasham (Fish)", "ఝ ఝషం", "🐟")
            "ఞ" -> LearningItem("ఞ - జ్ఞానం", "Gnyaanam (Knowledge)", "ఞ జ్ఞానం", "🧠")
            "ట" -> LearningItem("ట - టమాట", "Tamaata (Tomato)", "ట టమాట", "🍅")
            "ఠ" -> LearningItem("ఠ - కంఠం", "Kantham (Neck)", "ఠ కంఠం", "🗣️")
            "డ" -> LearningItem("డ - డబ్బా", "Dabba (Box)", "డ డబ్బా", "📦")
            "ఢ" -> LearningItem("ఢ - ఢంకా", "Dhanka (Drum)", "ఢ ఢంకా", "🥁")
            "ణ" -> LearningItem("ణ - బాణం", "Baanam (Arrow)", "ణ బాణం", "🏹")
            "త" -> LearningItem("త - తపాలా", "Tapaala (Post)", "త తపాలా", "✉️")
            "థ" -> LearningItem("థ - రథం", "Ratham (Chariot)", "థ రథం", "🚜")
            "ద" -> LearningItem("ద - దండ", "Danda (Garland)", "ద దండ", "📿")
            "ధ" -> LearningItem("ధ - ధనుస్సు", "Dhanussu (Bow)", "ధ ధనుస్సు", "🏹")
            "న" -> LearningItem("న - నక్క", "Nakka (Fox)", "న నక్క", "🦊")
            "ప" -> LearningItem("ప - పలక", "Palaka (Slate)", "ప పలక", "📋")
            "ఫ" -> LearningItem("ఫ - ఫలం", "Phalam (Fruit)", "ఫ ఫలం", "🍎")
            "బ" -> LearningItem("బ - బంతి", "Banthi (Ball)", "బ బంతి", "⚽")
            "భ" -> LearningItem("భ - భటుడు", "Bhatudu (Soldier)", "భ భటుడు", "💂")
            "మ" -> LearningItem("మ - మంచం", "Mancham (Bed)", "మ మంచం", "🛏️")
            "య" -> LearningItem("య - యంత్రం", "Yanthram (Machine)", "య యంత్రం", "⚙️")
            "ర" -> LearningItem("ర - రైలు", "Railu (Train)", "ర రైలు", "🚂")
            "ల" -> LearningItem("ల - లత", "Latha (Creeper)", "ల లత", "🌿")
            "వ" -> LearningItem("వ - వల", "Vala (Net)", "వ వల", "🕸️")
            "శ" -> LearningItem("శ - శంఖం", "Shankham (Conch)", "శ శంఖం", "🐚")
            "ష" -> LearningItem("ష - షట్కోణం", "Shatkonam (Hexagon)", "ష షట్కోణం", "⬡")
            "స" -> LearningItem("స - సంచి", "Sanchi (Bag)", "స సంచి", "👜")
            "హ" -> LearningItem("హ - హంస", "Hamsa (Swan)", "హ హంస", " Swan 🦢")
            "ళ" -> LearningItem("ళ - తాళం", "Thaalam (Lock)", "ళ తాళం", "🔒")
            "క్ష" -> LearningItem("క్ష - వృక్షం", "Vruksham (Tree)", "క్ష వృక్షం", "🌳")
            "ఱ" -> LearningItem("ఱ - ఱంపం", "Rampam (Saw)", "ఱ ఱంపం", "🪚")
            else -> null
        }
    }

    private fun getHindiConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "क" -> LearningItem("क - कबूतर", "Kabootar (Pigeon)", "क कबूतर", "🐦")
            "ख" -> LearningItem("ख - खरगोश", "Khargosh (Rabbit)", "ख खरगोश", "🐇")
            "ग" -> LearningItem("ग - गमला", "Gamla (Flower Pot)", "ग गमला", "🪴")
            "घ" -> LearningItem("घ - घर", "Ghar (House)", "घ घर", "🏠")
            "ङ" -> LearningItem("ङ - खाली", "Khaali (Empty)", "ङ खाली", "🫙")
            "च" -> LearningItem("च - चम्मच", "Chammach (Spoon)", "च चम्मच", "🥄")
            "छ" -> LearningItem("छ - छतरी", "Chhatri (Umbrella)", "छ छतरी", "☂️")
            "ज" -> LearningItem("ज - जहाज", "Jahaaj (Ship)", "ज जहाज", "🚢")
            "झ" -> LearningItem("झ - झंडा", "Jhanda (Flag)", "झ झंडा", "🇮🇳")
            "ञ" -> LearningItem("ञ - खाली", "Khaali (Empty)", "ञ खाली", "🫙")
            "ट" -> LearningItem("ट - टमाटर", "Tamatar (Tomato)", "ट टमाटर", "🍅")
            "ठ" -> LearningItem("ठ - ठठेरा", "Thathera (Coppersmith)", "ठ ठठेरा", "🔨")
            "ड" -> LearningItem("ड - डमरू", "Damroo (Drum)", "ड डमरू", "🥁")
            "ढ" -> LearningItem("ढ - ढक्कन", "Dhakkan (Lid)", "ढ ढक्कन", "🪪")
            "ण" -> LearningItem("ण - खाली", "Khaali (Empty)", "ण खाली", "🫙")
            "त" -> LearningItem("त - तरबूज", "Tarbooj (Watermelon)", "त तरबूज", "🍉")
            "थ" -> LearningItem("थ - थरमस", "Thermas (Flask)", "थ थरमस", "🧴")
            "द" -> LearningItem("द - दवात", "Dawaat (Inkpot)", "द दवात", "🖋️")
            "ध" -> LearningItem("ध - धनुष", "Dhanush (Bow)", "ध धनुष", "🏹")
            "न" -> LearningItem("न - नल", "Nal (Tap)", "न नल", "🚰")
            "प" -> LearningItem("प - पतंग", "Patang (Kite)", "प पतंग", "🪁")
            "फ" -> LearningItem("फ - फल", "Phal (Fruit)", "फ फल", "🍎")
            "ब" -> LearningItem("ब - बतख", "Batakh (Duck)", "ब बतख", "🦆")
            "भ" -> LearningItem("भ - भालू", "Bhaloo (Bear)", "भ भालू", "🐻")
            "म" -> LearningItem("म - मछली", "Machhli (Fish)", "म मछली", "🐟")
            "य" -> LearningItem("य - यज्ञ", "Yagya (Sacrifice)", "य यज्ञ", "🔥")
            "र" -> LearningItem("र - रथ", "Rath (Chariot)", "र रथ", "🚜")
            "ल" -> LearningItem("ल - लट्टू", "Lattoo (Top)", "ल लट्टू", "🪀")
            "व" -> LearningItem("व - वन", "Van (Forest)", "व वन", "🌲")
            "श" -> LearningItem("श - शलगम", "Shalgam (Turnip)", "श शलगम", "🧅")
            "ष" -> LearningItem("ष - षटकोण", "Shatkon (Hexagon)", "ष षटकोण", "⬡")
            "स" -> LearningItem("स - सपेरा", "Sapera (Snake Charmer)", "स सपेरा", "🐍")
            "ह" -> LearningItem("ह - हाथी", "Haathi (Elephant)", "ह हाथी", "🐘")
            else -> null
        }
    }

    private fun getTamilConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "க்" -> LearningItem("க் - கொக்கு", "Kokku (Crane/Heron)", "க் கொக்கு", "🦩")
            "ங்" -> LearningItem("ங் - சிங்கம்", "Singam (Lion)", "ங் சிங்கம்", "🦁")
            "ச்" -> LearningItem("ச் - எலுமிச்சை", "Elumichai (Lemon)", "ச் எலுமிச்சை", "🍋")
            "ஞ்" -> LearningItem("ஞ் - ஊஞ்சல்", "Oonjal (Swing)", "ஞ் ஊஞ்சல்", "🎡")
            "ட்" -> LearningItem("ட் - பட்டம்", "Pattam (Kite)", "ட் பட்டம்", "🪁")
            "ண்" -> LearningItem("ண் - கண்", "Kann (Eye)", "ண் கண்", "👁️")
            "த்" -> LearningItem("த் - நத்தை", "Nathai (Snail)", "த் நத்தை", "🐌")
            "ந்" -> LearningItem("ந் - நண்டு", "Nandu (Crab)", "ந் நண்டு", "🦀")
            "ப்" -> LearningItem("ப் - கப்பல்", "Kappal (Ship)", "ப் கப்பல்", "🚢")
            "ம்" -> LearningItem("ம் - மரம்", "Maram (Tree)", "ம் மரம்", "🌳")
            "ய்" -> LearningItem("ய் - நாய்", "Naai (Dog)", "ய் நாய்", "🐶")
            "ர்" -> LearningItem("ர் - தேர்", "Ther (Chariot)", "ர் தேர்", "🚜")
            "ல்" -> LearningItem("ல் - சேவல்", "Seval (Rooster)", "ல் சேவல்", "🐓")
            "வ்" -> LearningItem("வ் - செவ்வாழை", "Sevvaazhai (Red Banana)", "வ் செவ்வாழை", "🍌")
            "ழ்" -> LearningItem("ழ் - யாழ்", "Yaazh (Harp)", "ழ் யாழ்", "🪕")
            "ள்" -> LearningItem("ள் - வாள்", "Vaal (Sword)", "ள் வாள்", "⚔️")
            "ற்" -> LearningItem("ற் - நாற்று", "Naatru (Sapling)", "ற் நாற்று", "🌱")
            "ன்" -> LearningItem("ன் - மீன்", "Meen (Fish)", "ன் மீன்", "🐟")
            else -> null
        }
    }

    private fun getKannadaConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "ಕ" -> LearningItem("ಕ - ಕಮಲ", "Kamala (Lotus)", "ಕ ಕಮಲ", "🪷")
            "ಖ" -> LearningItem("ಖ - ಖಡ್ಗ", "Khadga (Sword)", "ಖ ಖಡ್ಗ", "⚔️")
            "ಗ" -> LearningItem("ಗ - ಗಡಿಯಾರ", "Gadiyaara (Clock)", "ಗ ಗಡಿಯಾರ", "⏰")
            "ಘ" -> LearningItem("ಘ - ಘಟ", "Ghata (Pot)", "ಘ ಘಟ", "🏺")
            "ಙ" -> LearningItem("ಙ - ವಾಙ್ಮಯ", "Vaangmaya (Literature)", "ಙ ವಾಙ್ಮಯ", "📚")
            "ಚ" -> LearningItem("ಚ - ಚಮಚ", "Chamacha (Spoon)", "ಚ ಚಮಚ", "🥄")
            "ಛ" -> LearningItem("ಛ - ಛತ್ರಿ", "Chhatri (Umbrella)", "ಛ ಛತ್ರಿ", "☂️")
            "ಜ" -> LearningItem("ಜ - ಜೇನು", "Jeenu (Honey)", "ಜ ಜೇನು", "🍯")
            "ಝ" -> LearningItem("ಝ - ಝಷ", "Jhasha (Fish)", "ಝ ಝಷ", "🐟")
            "ಞ" -> LearningItem("ಞ - ಜ್ಞಾನ", "Gnyaana (Knowledge)", "ಞ ಜ್ಞಾನ", "🧠")
            "ಟ" -> LearningItem("ಟ - ಟೊಮೆಟೊ", "Tomato (Tomato)", "ಟ ಟೊಮೆಟೊ", "🍅")
            "ಠ" -> LearningItem("ಠ - ಕಂಠ", "Kantha (Neck)", "ಠ ಕಂಠ", "🗣️")
            "ಡ" -> LearningItem("ಡ - ಡಬ್ಬಿ", "Dabbi (Box)", "ಡ ಡಬ್ಬಿ", "📦")
            "ಢ" -> LearningItem("ಢ - ಢಕ್ಕೆ", "Dhakke (Drum)", "ಢ ಢಕ್ಕೆ", "🥁")
            "ಣ" -> LearningItem("ಣ - ಬಾಣ", "Baana (Arrow)", "ಣ ಬಾಣ", "🏹")
            "ತ" -> LearningItem("ತ - ತಬಲ", "Tabala (Drum)", "ತ ತಬಲ", "🪘")
            "ಥ" -> LearningItem("ಥ - ರಥ", "Ratha (Chariot)", "ಥ ರಥ", "🚜")
            "ದ" -> LearningItem("ದ - ದೀಪ", "Deepa (Lamp)", "ದ ದೀಪ", "🪔")
            "ಧ" -> LearningItem("ಧ - ಧನುಸ್ಸು", "Dhanussu (Bow)", "ಧ ಧನುಸ್ಸು", "🏹")
            "ನ" -> LearningItem("ನ - ನಾಯಿ", "Naayi (Dog)", "ನ ನಾಯಿ", "🐶")
            "ಪ" -> LearningItem("ಪ - ಪಾರಿವಾಳ", "Paarivaala (Pigeon)", "ಪ ಪಾರಿವಾಳ", "🐦")
            "ф" -> LearningItem("ಫ - ಫಲ", "Phala (Fruit)", "ಫ ಫಲ", "🍎")
            "ಬ" -> LearningItem("ಬ - ಬಲೂನ್", "Balloon", "ಬ ಬಲೂನ್", "🎈")
            "ಭ" -> LearningItem("ಭ - ಭಾಲೂ", "Bhaloo (Bear)", "ಭ ಭಾಲೂ", "🐻")
            "ಮ" -> LearningItem("ಮ - ಮನೆ", "Mane (House)", "ಮ ಮನೆ", "🏠")
            "ಯ" -> LearningItem("ಯ - ಯಂತ್ರ", "Yanthra (Machine)", "ಯ ಯಂತ್ರ", "⚙️")
            "ರ" -> LearningItem("ರ - ರಥ", "Ratha (Chariot)", "ರ ರಥ", "🚜")
            "ಲ" -> LearningItem("ಲ - ಲತಾ", "Latha (Creeper)", "ಲ ಲತಾ", "🌿")
            "ವ" -> LearningItem("ವ - ವನ", "Vana (Forest)", "ವ ವನ", "🌲")
            "ಶ" -> LearningItem("ಶ - ಶಂಖ", "Shankha (Conch)", "ಶ ಶಂಖ", "🐚")
            "ಷ" -> LearningItem("ಷ - ಷಟ್ಕೋನ", "Shatkona (Hexagon)", "ಷ ಷಟ್ಕೋನ", "⬡")
            "ಸ" -> LearningItem("ಸ - ಸೂರ್ಯ", "Soorya (Sun)", "ಸ ಸೂರ್ಯ", "🌞")
            "ಹ" -> LearningItem("ಹ - ಹಂಸ", "Hamsa (Swan)", "ಹ ಹಂಸ", "🦢")
            "ಳ" -> LearningItem("ಳ - ತಾಳ", "Thaala (Lock)", "ಳ ತಾಳ", "🔒")
            else -> null
        }
    }

    private fun getMalayalamConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "ക" -> LearningItem("ക - കൈ", "Kai (Hand)", "ക കൈ", "👋")
            "ഖ" -> LearningItem("ഖ - ഖഡ്ഗം", "Khadgam (Sword)", "ഖ ഖഡ്ഗം", "⚔️")
            "ഗ" -> LearningItem("ഗ - ഗരുഡൻ", "Garudan (Eagle)", "ഗ ഗരുഡൻ", "🦅")
            "ഘ" -> LearningItem("ഘ - ഘടം", "Ghatam (Pot)", "ഘ ഘടം", "🏺")
            "ങ" -> LearningItem("ങ - മാങ്ങ", "Maanga (Mango)", "ങ മാങ്ങ", "🥭")
            "ച" -> LearningItem("ച - ചക്രം", "Chakram (Wheel)", "ച ചക്രം", "🛞")
            "ഛ" -> LearningItem("ഛ - ഛത്രി", "Chhatri (Umbrella)", "ഛ ഛത്രി", "☂️")
            "ജ" -> LearningItem("ജ - ജാലകം", "Jaalakam (Window)", "ജ ജാലകം", "🪟")
            "ഝ" -> LearningItem("ഝ - ഝഷം", "Jhasham (Fish)", "ഝ ഝഷം", "🐟")
            "ഞ" -> LearningItem("ഞ - ഞണ്ട്", "Nhandu (Crab)", "ഞ ഞണ്ട്", "🦀")
            "ട" -> LearningItem("ട - തക്കാളി", "Thakkaali (Tomato)", "ട തക്കാളി", "🍅")
            "ഠ" -> LearningItem("ഠ - കണ്ഠം", "Kantham (Neck)", "ഠ കണ്ഠം", "🗣️")
            "ഡ" -> LearningItem("ഡ - ഡബ്ബ", "Dabba (Box)", "ഡ ഡബ്ബ", "📦")
            "ഢ" -> LearningItem("ഢ - ഢക്ക", "Dhakka (Drum)", "ഢ ഢക്ക", "🥁")
            "ണ" -> LearningItem("ണ - പണം", "Panam (Money)", "ണ പണം", "💵")
            "ത" -> LearningItem("ത - തവള", "Thavala (Frog)", "ത തവള", "🐸")
            "ഥ" -> LearningItem("ഥ - രഥം", "Ratham (Chariot)", "ഥ രഥം", "🚜")
            "ദ" -> LearningItem("ദ - ദീപം", "Deepam (Lamp)", "ദ ദീപം", "🪔")
            "ധ" -> LearningItem("ധ - ധനുസ്സ്", "Dhanussu (Bow)", "ധ ധനുസ്സ്", "🏹")
            "ന" -> LearningItem("ന - നായ", "Naaya (Dog)", "ന നായ", "🐶")
            "പ" -> LearningItem("പ - പന്ത്", "Panthu (Ball)", "പ പന്ത്", "⚽")
            "ഫ" -> LearningItem("ഫ - ഫലം", "Phalam (Fruit)", "ഫ ഫലം", "🍎")
            "ബ" -> LearningItem("ബ - ബലൂൺ", "Balloon", "ബ ബലൂൺ", "🎈")
            "ഭ" -> LearningItem("ഭ - ഭാലൂ", "Bhaloo (Bear)", "ഭ ഭാലൂ", "🐻")
            "മ" -> LearningItem("മ - മരം", "Maram (Tree)", "മ മരം", "🌳")
            "യ" -> LearningItem("യ - യന്ത്രം", "Yanthram (Machine)", "യ യന്ത്രം", "⚙️")
            "ര" -> LearningItem("ര - രഥം", "Ratham (Chariot)", "ര രഥം", "🚜")
            "ല" -> LearningItem("ല - ലത", "Latha (Creeper)", "ല ലത", "🌿")
            "വ" -> LearningItem("വ - വഞ്ചി", "Vanchi (Boat)", "വ വഞ്ചി", "⛵")
            "ശ" -> LearningItem("ശ - ശംഖ്", "Shankhu (Conch)", "ശ ശംഖ്", "🐚")
            "ഷ" -> LearningItem("ഷ - ഷഡ്ഭുജം", "Shadbhujam (Hexagon)", "ഷ ഷഡ്ഭുജം", "⬡")
            "സ" -> LearningItem("സ - സൂര്യൻ", "Sooryan (Sun)", "സ സൂര്യൻ", "🌞")
            "ഹ" -> LearningItem("ഹ - ഹംസം", "Hamsam (Swan)", "ഹ ഹംസം", "🦢")
            "ള" -> LearningItem("ള - വാൾ", "Vaal (Sword)", "ള വാൾ", "⚔️")
            else -> null
        }
    }

    private fun getArabicConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "أ", "ا" -> LearningItem("ا - أَسَد", "Asad (Lion)", "ا أَسَد", "🦁")
            "ب" -> LearningItem("ب - بَطَّة", "Batta (Duck)", "ب بَطَّة", "🦆")
            "ت" -> LearningItem("ت - تُفَّاح", "Tuffah (Apple)", "ت تُفَّاح", "🍎")
            "ث" -> LearningItem("ث - ثَعْلَب", "Tha'lab (Fox)", "ث ثَعْلَب", "🦊")
            "ج" -> LearningItem("ج - جَمَل", "Jamal (Camel)", "ج جَمَل", "🐪")
            "ح" -> LearningItem("ح - حِصَان", "Hisan (Horse)", "ح حِصَان", "🐎")
            "خ" -> LearningItem("خ - خَرُوف", "Kharoof (Sheep)", "خ خَرُوف", "🐑")
            "د" -> LearningItem("د - دَجَاجَة", "Dajajah (Chicken)", "د دَجَاجَة", "🐔")
            "ذ" -> LearningItem("ذ - ذِئْب", "The'b (Wolf)", "ذ ذِئْب", "🐺")
            "ر" -> LearningItem("ر - رُمَّان", "Rumman (Pomegranate)", "ر رُمَّان", "🍎")
            "ز" -> LearningItem("ز - زَرَافَة", "Zarafah (Giraffe)", "ز زَرَافَة", "🦒")
            "س" -> LearningItem("س - سَمَكَة", "Samakah (Fish)", "س سَمَكَة", "🐟")
            "ش" -> LearningItem("ش - شَمْس", "Shams (Sun)", "ش شَمْس", "☀️")
            "ص" -> LearningItem("ص - صَقْر", "Saqr (Falcon)", "ص صَقْر", "🦅")
            "ض" -> LearningItem("ض - ضِفْدَع", "Difda' (Frog)", "ض ضِفْدَع", "🐸")
            "ط" -> LearningItem("ط - طَائِرَة", "Ta'irah (Airplane)", "ط طَائِرَة", "✈️")
            "ظ" -> LearningItem("ظ - ظَرْف", "Zarf (Envelope)", "ظ ظَرْف", "✉️")
            "ع" -> LearningItem("ع - عَصْفُور", "Asfoor (Bird)", "ع عَصْفُور", "🐦")
            "غ" -> LearningItem("غ - غَزَال", "Ghazal (Deer)", "غ غَزَال", "🦌")
            "ف" -> LearningItem("ف - فِيل", "Feel (Elephant)", "ف فِيل", "🐘")
            "ق" -> LearningItem("ق - قِطّ", "Qitt (Cat)", "ق قِطّ", "🐱")
            "ك" -> LearningItem("ك - كِتَاب", "Kitab (Book)", "ك كِتَاب", "📖")
            "ل" -> LearningItem("ل - لَيْمُون", "Laymoon (Lemon)", "ل لَيْمُون", "🍋")
            "م" -> LearningItem("م - مَوْز", "Mawz (Banana)", "م مَوْز", "🍌")
            "ن" -> LearningItem("ن - نَجْمَة", "Najmah (Star)", "ن نَجْمَة", "⭐")
            "ه" -> LearningItem("ه - هِلَال", "Hilal (Crescent)", "ه هِلَال", "🌙")
            "و" -> LearningItem("و - وَرْدَة", "Wardah (Flower)", "و وَرْدَة", "🌹")
            "ي" -> LearningItem("ي - يَد", "Yad (Hand)", "ي يَد", "👋")
            else -> null
        }
    }

    private fun getBengaliConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "ক" -> LearningItem("ক - কলা", "Kola (Banana)", "ক কলা", "🍌")
            "খ" -> LearningItem("খ - খরগোশ", "Khorgosh (Rabbit)", "খ খরগোশ", "🐇")
            "গ" -> LearningItem("গ - গরু", "Goru (Cow)", "গ গরু", "🐄")
            "ঘ" -> LearningItem("ঘ - ঘর", "Ghor (House)", "ঘ ঘর", "🏠")
            "ঙ" -> LearningItem("ঙ - ব্যাঙ", "Beang (Frog)", "ঙ ব্যাঙ", "🐸")
            "চ" -> LearningItem("চ - চামচ", "Chamoch (Spoon)", "চ চামচ", "🥄")
            "ছ" -> LearningItem("ছ - ছাতা", "Chhata (Umbrella)", "ছ ছাতা", "☂️")
            "জ" -> LearningItem("জ - জাহাজ", "Jahaj (Ship)", "জ জাহাজ", "🚢")
            "ঝ" -> LearningItem("ঝ - ঝুড়ি", "Jhuri (Basket)", "ঝ ঝুড়ি", "🧺")
            "ঞ" -> LearningItem("ঞ - মিঞা", "Miah (Sir/Mr.)", "ঞ মিঞা", "👨")
            "ট" -> LearningItem("ট - টমেটো", "Tometo (Tomato)", "ট টমেটো", "🍅")
            "ঠ" -> LearningItem("ঠ - ঠোঁট", "Thot (Lips)", "ঠ ঠোঁট", "👄")
            "ড" -> LearningItem("ড - ডালিম", "Dalim (Pomegranate)", "ড ডালিম", "🍎")
            "ঢ" -> LearningItem("ঢ - ঢাক", "Dhak (Drum)", "ঢ ঢাক", "🥁")
            "ণ" -> LearningItem("ণ - হরিণ", "Horin (Deer)", "ণ হরিণ", "🦌")
            "ত" -> LearningItem("ত - তরমুজ", "Tormuj (Watermelon)", "ত তরমুজ", "🍉")
            "থ" -> LearningItem("থ - থালা", "Thala (Plate)", "থ থালা", "🍽️")
            "দ" -> LearningItem("দ - দই", "Doi (Yogurt)", "দ দই", "🥛")
            "ধ" -> LearningItem("ধ - ধান", "Dhan (Paddy/Rice)", "ধ ধান", "🌾")
            "ন" -> LearningItem("ন - নদী", "Nodi (River)", "ন নদী", "🏞️")
            "প" -> LearningItem("প - পাখি", "Pakshi (Bird)", "প পাখি", "🐦")
            "ফ" -> LearningItem("ফ - ফুল", "Phul (Flower)", "ফ ফুল", "🌹")
            "ব" -> LearningItem("ব - বই", "Boi (Book)", "ব বই", "📖")
            "ভ" -> LearningItem("ভ - ভাল্লুক", "Bhalluk (Bear)", "ভ ভাল্লুক", "🐻")
            "ম" -> LearningItem("ম - মাছ", "Mach (Fish)", "ম মাছ", "🐟")
            "য" -> LearningItem("য - যাতা", "Jata (Grindstone)", "য যাতা", "🪨")
            "র" -> LearningItem("র - রথ", "Roth (Chariot)", "র রথ", "🚜")
            "ল" -> LearningItem("ল - লেবু", "Lebu (Lemon)", "ল লেবু", "🍋")
            "শ" -> LearningItem("শ - শসা", "Sosa (Cucumber)", "শ শসা", "🥒")
            "ষ" -> LearningItem("ষ - ষাঁড়", "Shor (Bull)", "ষ ষাঁড়", "🐂")
            "স" -> LearningItem("স - সিংহ", "Singho (Lion)", "স সিংহ", "🦁")
            "হ" -> LearningItem("হ - হাতি", "Hati (Elephant)", "হ হাতি", "🐘")
            else -> null
        }
    }

    private fun getMarathiConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "क" -> LearningItem("क - कमळ", "Kamal (Lotus)", "क कमळ", "🪷")
            "ख" -> LearningItem("ख - खडू", "Khadu (Chalk)", "ख खडू", "🖍️")
            "ग" -> LearningItem("ग - गणपती", "Ganpati (Ganesha)", "ग गणपती", "🙏")
            "घ" -> LearningItem("घ - घर", "Ghar (House)", "घ घर", "🏠")
            "च" -> LearningItem("च - चमचा", "Chamcha (Spoon)", "च चमचा", "🥄")
            "छ" -> LearningItem("छ - छत्री", "Chhatri (Umbrella)", "छ छत्री", "☂️")
            "ज" -> LearningItem("ज - जहाज", "Jahaj (Ship)", "ज जहाज", "🚢")
            "झ" -> LearningItem("झ - झबले", "Jhable (Baby Frock)", "झ झबले", "👗")
            "ट" -> LearningItem("ट - टरबूज", "Tarbooj (Watermelon)", "ट टरबूज", "🍉")
            "ठ" -> LearningItem("ठ - ठसा", "Thasa (Stamp)", "ठ ठसा", "📥")
            "ड" -> LearningItem("ड - डबा", "Daba (Box/Tiffin)", "ड डबा", "📦")
            "ढ" -> LearningItem("ढ - ढग", "Dhag (Cloud)", "ढ ढग", "☁️")
            "ण" -> LearningItem("ण - बाण", "Baan (Arrow)", "ण बाण", "🏹")
            "त" -> LearningItem("त - तलवार", "Talwar (Sword)", "त तलवार", "⚔️")
            "थ" -> LearningItem("थ - थवा", "Thava (Flock of Birds)", "थ थवा", "🐦")
            "द" -> LearningItem("द - दप्तर", "Daptar (School Bag)", "द दप्तर", "🎒")
            "ध" -> LearningItem("ध - धनुष्य", "Dhanushya (Bow)", "ध धनुष्य", "🏹")
            "न" -> LearningItem("न - नळ", "Nal (Tap)", "न नळ", "🚰")
            "प" -> LearningItem("प - पतंग", "Patang (Kite)", "प पतंग", "🪁")
            "फ" -> LearningItem("फ - फणस", "Fanas (Jackfruit)", "फ फणस", "🍈")
            "ब" -> LearningItem("ब - बदक", "Badak (Duck)", "ब बदक", "🦆")
            "भ" -> LearningItem("भ - भटजी", "Bhatji (Priest)", "भ भटजी", "🧘‍♂️")
            "म" -> LearningItem("म - मगर", "Magar (Crocodile)", "म मगर", "🐊")
            "य" -> LearningItem("य - यज्ञ", "Yajna (Holy Fire)", "य यज्ञ", "🔥")
            "र" -> LearningItem("र - रथ", "Rath (Chariot)", "र रथ", "🚜")
            "ल" -> LearningItem("ल - लसूण", "Lasoon (Garlic)", "ल लसूण", "🧄")
            "व" -> LearningItem("व - वजन", "Vajan (Weight)", "व वजन", "⚖️")
            "श" -> LearningItem("श - शहामृग", "Shahamrug (Ostrich)", "श शहामृग", "🦩")
            "ष" -> LearningItem("ष - षटकोन", "Shatkon (Hexagon)", "ष षटकोन", "🔷")
            "स" -> LearningItem("स - ससा", "Sasa (Rabbit)", "स ससा", "🐇")
            "ह" -> LearningItem("ह - हत्ती", "Hatti (Elephant)", "ह हत्ती", "🐘")
            "ळ" -> LearningItem("ळ - बाळ", "Baal (Baby)", "ळ बाळ", "👶")
            else -> null
        }
    }

    private fun getGujaratiConsonantExample(letter: String): LearningItem? {
        return when (letter) {
            "ક" -> LearningItem("ક - કમળ", "Kamal (Lotus)", "ક કમળ", "🪷")
            "ખ" -> LearningItem("ખ - ખલ", "Khal (Mortar)", "ખ ખલ", "🥣")
            "ગ" -> LearningItem("ગ - ગણપતિ", "Ganpati (Ganesha)", "ગ ગણપતિ", "🙏")
            "ઘ" -> LearningItem("ઘ - ઘર", "Ghar (House)", "ઘ ઘર", "🏠")
            "ચ" -> LearningItem("ચ - ચકલી", "Chakli (Sparrow)", "ચ ચકલી", "🐦")
            "છ" -> LearningItem("છ - છત્રી", "Chhatri (Umbrella)", "છ છત્રી", "☂️")
            "જ" -> LearningItem("જ - જમરૂખ", "Jamrukh (Guava)", "જ જમરૂખ", "🍏")
            "ઝ" -> LearningItem("ઝ - ઝભલું", "Jhablu (Frock)", "ઝ ઝભલું", "👗")
            "ટ" -> LearningItem("ટ - ટામેટું", "Tametu (Tomato)", "ટ ટામેટું", "🍅")
            "ઠ" -> LearningItem("ઠ - ઠળિયો", "Thaliyo (Seed)", "ઠ ઠળિયો", "🍒")
            "ડ" -> LearningItem("ડ - ડબ્બો", "Dabbo (Box)", "ડ ડબ્બો", "📦")
            "ઢ" -> LearningItem("ઢ - ઢગલો", "Dhaglo (Heap)", "ઢ ઢગલો", "⛰️")
            "ણ" -> LearningItem("ણ - બાણ", "Baan (Arrow)", "ણ બાણ", "🏹")
            "ત" -> LearningItem("ત - તલવાર", "Talvar (Sword)", "ત તલવાર", "⚔️")
            "થ" -> LearningItem("થ - થાળી", "Thali (Plate)", "થ થાળી", "🍽️")
            "દ" -> LearningItem("દ - દડો", "Dado (Ball)", "દ દડો", "⚽")
            "ધ" -> LearningItem("ધ - ધનુષ", "Dhanush (Bow)", "ધ ધનુષ", "🏹")
            "ન" -> LearningItem("ન - નળ", "Nal (Tap)", "ન નળ", "🚰")
            "પ" -> LearningItem("પ - પતંગ", "Patang (Kite)", "પ પતંગ", "🪁")
            "ફ" -> LearningItem("ફ - ફટાકડા", "Fatakda (Firecrackers)", "ફ ફટાકડા", "🎆")
            "બ" -> LearningItem("બ - બતક", "Batak (Duck)", "બ બતક", "🦆")
            "ભ" -> LearningItem("ભ - ભમરડો", "Bhamardo (Top)", "ભ ભમરડો", "🪀")
            "મ" -> LearningItem("મ - મરચું", "Marchu (Chilli)", "મ મરચું", "🌶️")
            "ય" -> LearningItem("ય - યતિ", "Yati (Monk)", "ય યતિ", "🧘‍♂️")
            "ર" -> LearningItem("ર - રથ", "Rath (Chariot)", "ર રથ", "🚜")
            "લ" -> LearningItem("લ - લખોટી", "Lakhoti (Marble)", "લ લખોટી", "🔮")
            "વ" -> LearningItem("વ - વહાણ", "Vahan (Ship)", "વ વહાણ", "🚢")
            "શ" -> LearningItem("શ - શરણાઈ", "Sharnai (Clarinet)", "શ શરણાઈ", "🎷")
            "ષ" -> LearningItem("ષ - ષટકોણ", "Shatkon (Hexagon)", "ષ ષટકોણ", "🔷")
            "સ" -> LearningItem("સ - સસલું", "Saslu (Rabbit)", "સ સસલું", "🐇")
            "હ" -> LearningItem("હ - હરણ", "Haran (Deer)", "હ હરણ", "🦌")
            "ળ" -> LearningItem("ળ - નળ", "Nal (Tap)", "ળ નળ", "🚰")
            else -> null
        }
    }
}

val teluguStatesAndCapitals = listOf(
    LearningItem("ఆంధ్రప్రదేశ్ - అమరావతి", "Andhra Pradesh - Amaravati", "ఆంధ్రప్రదేశ్ రాజధాని అమరావతి", "🗺️"),
    LearningItem("అరుణాచల్ ప్రదేశ్ - ఇటానగర్", "Arunachal Pradesh - Itanagar", "అరుణాచల్ ప్రదేశ్ రాజధాని ఇటానగర్", "🏔️"),
    LearningItem("అస్సాం - దిస్పూర్", "Assam - Dispur", "అస్సాం రాజధాని దిస్పూర్", "🍵"),
    LearningItem("బీహార్ - పాట్నా", "Bihar - Patna", "బీహార్ రాజధాని పాట్నా", "📖"),
    LearningItem("ఛత్తీస్‌గఢ్ - రాయ్‌పూర్", "Chhattisgarh - Raipur", "ఛత్తీస్‌గఢ్ రాజధాని రాయ్‌పూర్", "🏭"),
    LearningItem("గోవా - పనాజీ", "Goa - Panaji", "గోవా రాజధాని పనాజీ", "🏖️"),
    LearningItem("గుజరాత్ - గాంధీనగర్", "Gujarat - Gandhinagar", "గుజరాత్ రాజధాని గాంధీనగర్", "🪔"),
    LearningItem("హర్యానా - చండీగఢ్", "Haryana - Chandigarh", "హర్యానా రాజధాని చండీగఢ్", "🌾"),
    LearningItem("హిమాచల్ ప్రదేశ్ - సిమ్లా", "Himachal Pradesh - Shimla", "హిమాచల్ ప్రదేశ్ రాజధాని సిమ్లా", "❄️"),
    LearningItem("జార్ఖండ్ - రాంచీ", "Jharkhand - Ranchi", "జార్ఖండ్ రాజధాని రాంచీ", "⛏️"),
    LearningItem("కర్ణాటక - బెంగళూరు", "Karnataka - Bengaluru", "కర్ణాటక రాజధాని బెంగళూరు", "🍃"),
    LearningItem("కేరళ - తిరువనంతపురం", "Kerala - Thiruvananthapuram", "కేరళ రాజధాని తిరువనంతపురం", "🌴"),
    LearningItem("మధ్యప్రదేశ్ - భోపాల్", "Madhya Pradesh - Bhopal", "మధ్యప్రదేశ్ రాజధాని భోపాల్", "🌳"),
    LearningItem("మహారాష్ట్ర - ముంబై", "Maharashtra - Mumbai", "మహారాష్ట్ర రాజధాని ముంబై", "🦁"),
    LearningItem("మణిపూర్ - ఇంఫాల్", "Manipur - Imphal", "మణిపూర్ రాజధాని ఇంఫాల్", "🎭"),
    LearningItem("మేఘాలయ - షిల్లాంగ్", "Meghalaya - Shillong", "మేఘాలయ రాజధాని షిల్లాంగ్", "🌧️"),
    LearningItem("మిజోరాం - ఐజ్వాల్", "Mizoram - Aizawl", "మిజోరాం రాజధాని ఐజ్వాల్", "⛰️"),
    LearningItem("నాగాలాండ్ - కోహిమా", "Nagaland - Kohima", "నాగాలాండ్ రాజధాని కోహిమా", "🏹"),
    LearningItem("ఒడిశా - భువనేశ్వర్", "Odisha - Bhubaneswar", "ఒడిశా రాజధాని భువనేశ్వర్", "🛕"),
    LearningItem("పంజాబ్ - చండీగఢ్", "Punjab - Chandigarh", "పంజాబ్ రాజధాని చండీగఢ్", "🚜"),
    LearningItem("రాజస్థాన్ - జైపూర్", "Rajasthan - Jaipur", "రాజస్థాన్ రాజధాని జైపూర్", "🐪"),
    LearningItem("సిక్కిం - గ్యాంగ్‌టక్", "Sikkim - Gangtok", "సిక్కిం రాజధాని గ్యాంగ్‌టక్", "🏔️"),
    LearningItem("తమిళనాడు - చెన్నై", "Tamil Nadu - Chennai", "తమిళనాడు రాజధాని చెన్నై", "🏛️"),
    LearningItem("తెలంగాణ - హైదరాబాద్", "Telangana - Hyderabad", "తెలంగాణ రాజధాని హైదరాబాద్", "🏙️"),
    LearningItem("త్రిపుర - అగర్తలా", "Tripura - Agartala", "త్రిపుర రాజధాని అగర్తలా", "🎋"),
    LearningItem("ఉత్తరప్రదేశ్ - లక్నో", "Uttar Pradesh - Lucknow", "ఉత్తరప్రదేశ్ రాజధాని లక్నో", "🕌"),
    LearningItem("ఉత్తరాఖండ్ - డెహ్రాడూన్", "Uttarakhand - Dehradun", "ఉత్తరాఖండ్ రాజధాని డెహ్రాడూన్", "🏔️"),
    LearningItem("పశ్చిమ బెంగాల్ - కోల్‌కతా", "West Bengal - Kolkata", "పశ్చిమ బెంగాల్ రాజధాని కోల్‌కతా", "🐯"),
    LearningItem("భారతదేశం - న్యూఢిల్లీ", "India - New Delhi (National Capital)", "భారతదేశం రాజధాని న్యూఢిల్లీ", "🇮🇳")
)

val englishStatesAndCapitals = listOf(
    LearningItem("Andhra Pradesh - Amaravati", "Amaravati", "Andhra Pradesh capital is Amaravati", "🗺️"),
    LearningItem("Arunachal Pradesh - Itanagar", "Itanagar", "Arunachal Pradesh capital is Itanagar", "🏔️"),
    LearningItem("Assam - Dispur", "Dispur", "Assam capital is Dispur", "🍵"),
    LearningItem("Bihar - Patna", "Patna", "Bihar capital is Patna", "📖"),
    LearningItem("Chhattisgarh - Raipur", "Raipur", "Chhattisgarh capital is Raipur", "🏭"),
    LearningItem("Goa - Panaji", "Panaji", "Goa capital is Panaji", "🏖️"),
    LearningItem("Gujarat - Gandhinagar", "Gandhinagar", "Gujarat capital is Gandhinagar", "🪔"),
    LearningItem("Haryana - Chandigarh", "Chandigarh", "Haryana capital is Chandigarh", "🌾"),
    LearningItem("Himachal Pradesh - Shimla", "Shimla", "Himachal Pradesh capital is Shimla", "❄️"),
    LearningItem("Jharkhand - Ranchi", "Ranchi", "Jharkhand capital is Ranchi", "⛏️"),
    LearningItem("Karnataka - Bengaluru", "Bengaluru", "Karnataka capital is Bengaluru", "🍃"),
    LearningItem("Kerala - Thiruvananthapuram", "Thiruvananthapuram", "Kerala capital is Thiruvananthapuram", "🌴"),
    LearningItem("Madhya Pradesh - Bhopal", "Bhopal", "Madhya Pradesh capital is Bhopal", "🌳"),
    LearningItem("Maharashtra - Mumbai", "Mumbai", "Maharashtra capital is Mumbai", "🦁"),
    LearningItem("Manipur - Imphal", "Imphal", "Manipur capital is Imphal", "🎭"),
    LearningItem("Meghalaya - Shillong", "Shillong", "Meghalaya capital is Shillong", "🌧️"),
    LearningItem("Mizoram - Aizawl", "Aizawl", "Mizoram capital is Aizawl", "⛰️"),
    LearningItem("Nagaland - Kohima", "Kohima", "Nagaland capital is Kohima", "🏹"),
    LearningItem("Odisha - Bhubaneswar", "Bhubaneswar", "Odisha capital is Bhubaneswar", "🛕"),
    LearningItem("Punjab - Chandigarh", "Chandigarh", "Punjab capital is Chandigarh", "🚜"),
    LearningItem("Rajasthan - Jaipur", "Jaipur", "Rajasthan capital is Jaipur", "🐪"),
    LearningItem("Sikkim - Gangtok", "Gangtok", "Sikkim capital is Gangtok", "🏔️"),
    LearningItem("Tamil Nadu - Chennai", "Chennai", "Tamil Nadu capital is Chennai", "🏛️"),
    LearningItem("Telangana - Hyderabad", "Hyderabad", "Telangana capital is Hyderabad", "🏙️"),
    LearningItem("Tripura - Agartala", "Agartala", "Tripura capital is Agartala", "🎋"),
    LearningItem("Uttar Pradesh - Lucknow", "Lucknow", "Uttar Pradesh capital is Lucknow", "🕌"),
    LearningItem("Uttarakhand - Dehradun", "Dehradun", "Uttarakhand capital is Dehradun", "🏔️"),
    LearningItem("West Bengal - Kolkata", "Kolkata", "West Bengal capital is Kolkata", "🐯"),
    LearningItem("India - New Delhi", "New Delhi (National Capital)", "India capital is New Delhi", "🇮🇳")
)

val teluguNationalSymbols = listOf(
    LearningItem("జాతీయ జంతువు - పులి", "National Animal - Tiger", "జాతీయ జంతువు పెద్ద పులి", "🐯"),
    LearningItem("జాతీయ పక్షి - నెమలి", "National Bird - Peacock", "జాతీయ పక్షి నెమలి", "🦚"),
    LearningItem("జాతీయ పుష్పం - తామర", "National Flower - Lotus", "జాతీయ పుష్పం తామర పువ్వు", "🪷"),
    LearningItem("జాతీయ ఫలం - మామిడి", "National Fruit - Mango", "జాతీయ ఫలం మామిడి పండు", "🥭"),
    LearningItem("జాతీయ వృక్షం - మర్రి చెట్టు", "National Tree - Banyan Tree", "జాతీయ వృక్షం మర్రి చెట్టు", "🌳"),
    LearningItem("జాతీయ నది - గంగ", "National River - Ganges", "జాతీయ నది గంగా నది", "🌊"),
    LearningItem("జాతీయ క్రీడ - హాకీ", "National Game - Hockey", "జాతీయ క్రీడ హాకీ", "🏑"),
    LearningItem("జాతీయ గీతం - జనగణమన", "National Anthem - Jana Gana Mana", "జాతీయ గీతం జనగణమన", "🎵"),
    LearningItem("జాతీయ గేయం - వందేమాతరం", "National Song - Vande Mataram", "జాతీయ గేయం వందేమాతరం", "🎶"),
    LearningItem("జాతీయ ముద్ర - అశోక చిహ్నం", "National Emblem - Ashoka Lion Capital", "జాతీయ చిహ్నం అశోక ముద్ర", "🦁"),
    LearningItem("జాతీయ జలచరం - డాల్ఫిన్", "National Aquatic Animal - Dolphin", "జాతీయ జలచరం గంగా డాల్ఫిన్", "🐬"),
    LearningItem("జాతీయ వారసత్వ జంతువు - ఏనుగు", "National Heritage Animal - Elephant", "జాతీయ వారసత్వ జంతువు ఏనుగు", "🐘"),
    LearningItem("జాతీయ సరీసృపం - రాజనాగు", "National Reptile - King Cobra", "జాతీయ సరీసృపం రాజనాగు", "🐍"),
    LearningItem("జాతీయ కరెన్సీ - రూపాయి", "National Currency - Rupee", "జాతీయ కరెన్సీ రూపాయి", "🪙"),
    LearningItem("జాతీయ క్యాలెండర్ - శక క్యాలెండర్", "National Calendar - Saka", "జాతీయ క్యాలెండర్ శక క్యాలెండర్", "📅")
)

val englishNationalSymbols = listOf(
    LearningItem("National Animal - Tiger", "Tiger", "National Animal is Tiger", "🐯"),
    LearningItem("National Bird - Peacock", "Peacock", "National Bird is Peacock", "🦚"),
    LearningItem("National Flower - Lotus", "Lotus", "National Flower is Lotus", "🪷"),
    LearningItem("National Fruit - Mango", "Mango", "National Fruit is Mango", "🥭"),
    LearningItem("National Tree - Banyan Tree", "Banyan", "National Tree is Banyan Tree", "🌳"),
    LearningItem("National River - Ganges", "Ganges", "National River is Ganges", "🌊"),
    LearningItem("National Game - Hockey", "Hockey", "National Game is Hockey", "🏑"),
    LearningItem("National Anthem - Jana Gana Mana", "Anthem", "National Anthem is Jana Gana Mana", "🎵"),
    LearningItem("National Song - Vande Mataram", "Song", "National Song is Vande Mataram", "🎶"),
    LearningItem("National Emblem - Ashoka Lion Capital", "Emblem", "National Emblem is Ashoka Lion Capital", "🦁"),
    LearningItem("National Aquatic Animal - Dolphin", "Dolphin", "National Aquatic Animal is Dolphin", "🐬"),
    LearningItem("National Heritage Animal - Elephant", "Elephant", "National Heritage Animal is Elephant", "🐘"),
    LearningItem("National Reptile - King Cobra", "King Cobra", "National Reptile is King Cobra", "🐍"),
    LearningItem("National Currency - Rupee", "Rupee", "National Currency is Indian Rupee", "🪙"),
    LearningItem("National Calendar - Saka", "Saka", "National Calendar is Saka Calendar", "📅")
)

val teluguRelationships = listOf(
    LearningItem("అమ్మ", "Amma (Mother)", "అమ్మ", "👩"),
    LearningItem("నాన్న", "Nanna (Father)", "నాన్న", "👨"),
    LearningItem("అక్క", "Akka (Elder Sister)", "అక్క", "👧"),
    LearningItem("చెల్లి", "Chelli (Younger Sister)", "చెల్లి", "👶"),
    LearningItem("అన్న", "Anna (Elder Brother)", "అన్న", "👦"),
    LearningItem("తమ్ముడు", "Thammudu (Younger Brother)", "తమ్ముడు", "👦"),
    LearningItem("తాతయ్య", "Thathayya (Grandfather)", "తాతయ్య", "👴"),
    LearningItem("అమ్మమ్మ", "Ammamma (Maternal Grandmother)", "అమ్మమ్మ", "👵"),
    LearningItem("నానమ్మ", "Naanamma (Paternal Grandmother)", "నానమ్మ", "👵"),
    LearningItem("బాబాయ్", "Baabaai (Uncle)", "బాబాయ్", "👨"),
    LearningItem("పిన్ని", "Pinni (Aunt)", "పిన్ని", "👩"),
    LearningItem("అత్త", "Attha (Aunt)", "అత్త", "👩"),
    LearningItem("మామ", "Maama (Uncle)", "మామ", "👨"),
    LearningItem("కొడుకు", "Koduku (Son)", "కొడుకు", "👦"),
    LearningItem("కూతురు", "Koothuru (Daughter)", "కూతురు", "👧"),
    LearningItem("స్నేహితుడు", "Snehithudu (Friend)", "స్నేహితుడు", "🤝")
)

val englishRelationships = listOf(
    LearningItem("Mother", "Mother (Amma)", "Mother", "👩"),
    LearningItem("Father", "Father (Nanna)", "Father", "👨"),
    LearningItem("Elder Sister", "Elder Sister (Akka)", "Elder Sister", "👧"),
    LearningItem("Younger Sister", "Younger Sister (Chelli)", "Younger Sister", "👶"),
    LearningItem("Elder Brother", "Elder Brother (Anna)", "Elder Brother", "👦"),
    LearningItem("Younger Brother", "Younger Brother (Thammudu)", "Younger Brother", "👦"),
    LearningItem("Grandfather", "Grandfather (Thatha)", "Grandfather", "👴"),
    LearningItem("Grandmother", "Grandmother (Ammamma/Naanamma)", "Grandmother", "👵"),
    LearningItem("Uncle", "Uncle (Babai/Mama)", "Uncle", "👨"),
    LearningItem("Aunt", "Aunt (Pinni/Atta)", "Aunt", "👩"),
    LearningItem("Son", "Son (Koduku)", "Son", "👦"),
    LearningItem("Daughter", "Daughter (Kuthuru)", "Daughter", "👧"),
    LearningItem("Friend", "Friend (Snehithudu)", "Friend", "🤝")
)

val teluguFoods = listOf(
    LearningItem("ఇడ్లీ", "Idli (Rice Cakes)", "ఇడ్లీ", "🍛"),
    LearningItem("దోశ", "Dosa (Crepe)", "దోశ", "🥞"),
    LearningItem("వడ", "Vada (Savory Donut)", "వడ", "🍩"),
    LearningItem("అన్నం", "Annam (Rice)", "అన్నం", "🍚"),
    LearningItem("సాంబారు", "Sambar (Lentil Soup)", "సాంబారు", "🥣"),
    LearningItem("బిర్యానీ", "Biryani (Spiced Rice)", "బిర్యానీ", "🍲"),
    LearningItem("చపాతీ", "Chapati (Flatbread)", "చపాతీ", "🫓"),
    LearningItem("పూరీ", "Poori (Puffed Bread)", "పూరీ", "🥞"),
    LearningItem("పప్పు", "Pappu (Dal)", "పప్పు", "🥣"),
    LearningItem("పెరుగు", "Perugu (Curd)", "పెరుగు", "🥛"),
    LearningItem("నెయ్యి", "Neyyi (Ghee)", "నెయ్యి", "🧈"),
    LearningItem("పచ్చడి", "Pacchadi (Pickle)", "పచ్చడి", "🏺"),
    LearningItem("పాయసం", "Payasam (Sweet Kheer)", "పాయసం", "🥛"),
    LearningItem("లడ్డు", "Laddu (Sweet Ball)", "లడ్డు", "🟡"),
    LearningItem("సమోసా", "Samosa (Savory Pastry)", "సమోసా", "📐")
)

val englishFoods = listOf(
    LearningItem("Idli", "Idli (Rice Cakes)", "Idli", "🍛"),
    LearningItem("Dosa", "Dosa (Savory Crepe)", "Dosa", "🥞"),
    LearningItem("Vada", "Vada (Savory Lentil Donut)", "Vada", "🍩"),
    LearningItem("Rice", "Rice (Annam)", "Rice", "🍚"),
    LearningItem("Sambar", "Sambar (Lentil Stew)", "Sambar", "🥣"),
    LearningItem("Biryani", "Biryani (Spiced Mixed Rice)", "Biryani", "🍲"),
    LearningItem("Chapati", "Chapati (Wheat Flatbread)", "Chapati", "🫓"),
    LearningItem("Poori", "Poori (Fried Puffed Bread)", "Poori", "🥞"),
    LearningItem("Dal", "Dal (Pappu)", "Dal", "🥣"),
    LearningItem("Curd", "Curd / Yogurt (Perugu)", "Curd", "🥛"),
    LearningItem("Ghee", "Clarified Butter (Neyyi)", "Ghee", "🧈"),
    LearningItem("Pickle", "Pickle (Pacchadi)", "Pickle", "🏺"),
    LearningItem("Kheer", "Sweet Pudding (Payasam)", "Kheer", "🥛"),
    LearningItem("Laddu", "Sweet Round Laddu", "Laddu", "🟡"),
    LearningItem("Samosa", "Crispy Samosa", "Samosa", "📐")
)

val teluguFruitsAndFlowers = listOf(
    LearningItem("మామిడి పండు", "Mamidi Pandu (Mango)", "మామిడి పండు", "🥭"),
    LearningItem("అరటి పండు", "Arati Pandu (Banana)", "అరటి పండు", "🍌"),
    LearningItem("సీతాఫలం", "Seethaphalam (Custard Apple)", "సీతాఫలం", "🍈"),
    LearningItem("దానిమ్మ పండు", "Danimma Pandu (Pomegranate)", "దానిమ్మ పండు", "🍎"),
    LearningItem("నారింజ పండు", "Naarinja Pandu (Orange)", "నారింజ పండు", "🍊"),
    LearningItem("బొప్పాయి పండు", "Boppaayi Pandu (Papaya)", "బొప్పాయి పండు", "🍐"),
    LearningItem("పుచ్చకాయ", "Puchakaaya (Watermelon)", "పుచ్చకాయ", "🍉"),
    LearningItem("తామర పువ్వు", "Thámara Puvvu (Lotus)", "తామర పువ్వు", "🪷"),
    LearningItem("గులాబీ పువ్వు", "Gulaabee Puvvu (Rose)", "గులాబీ పువ్వు", "🌹"),
    LearningItem("మల్లెపువ్వు", "Malle Puvvu (Jasmine)", "మల్లెపువ్వు", "🌸"),
    LearningItem("బంతిపువ్వు", "Banthi Puvvu (Marigold)", "బంతిపువ్వు", "🌼"),
    LearningItem("చామంతి పువ్వు", "Chaamanthi Puvvu (Chrysanthemum)", "చామంతి పువ్వు", "🏵️"),
    LearningItem("మందార పువ్వు", "Mandhaara Puvvu (Hibiscus)", "మందార పువ్వు", "🌺"),
    LearningItem("సూర్యకాంతి పువ్వు", "Sooryakaanthi Puvvu (Sunflower)", "సూర్యకాంతి పువ్వు", "🌻"),
    LearningItem("కలువ పువ్వు", "Kaluva Puvvu (Lily)", "కలువ పువ్వు", "🪻")
)

val englishFruitsAndFlowers = listOf(
    LearningItem("Mango", "Mango (Mamidi Pandu)", "Mango", "🥭"),
    LearningItem("Banana", "Banana (Arati Pandu)", "Banana", "🍌"),
    LearningItem("Custard Apple", "Custard Apple (Seethaphalam)", "Custard Apple", "🍈"),
    LearningItem("Pomegranate", "Pomegranate (Danimma)", "Pomegranate", "🍎"),
    LearningItem("Orange", "Orange (Narinja)", "Orange", "🍊"),
    LearningItem("Papaya", "Papaya (Boppayi)", "Papaya", "🍐"),
    LearningItem("Watermelon", "Watermelon (Puchakaya)", "Watermelon", "🍉"),
    LearningItem("Lotus", "Lotus (Thamara Puvvu)", "Lotus", "🪷"),
    LearningItem("Rose", "Rose (Gulabi)", "Rose", "🌹"),
    LearningItem("Jasmine", "Jasmine (Mallepuvvu)", "Jasmine", "🌸"),
    LearningItem("Marigold", "Marigold (Banthipuvvu)", "Marigold", "🌼"),
    LearningItem("Chrysanthemum", "Chrysanthemum (Chamanthipuvvu)", "Chrysanthemum", "🏵️"),
    LearningItem("Hibiscus", "Hibiscus (Mandharam)", "Hibiscus", "🌺"),
    LearningItem("Sunflower", "Sunflower (Suryakanthi)", "Sunflower", "🌻"),
    LearningItem("Lily", "Lily (Kaluva Puvvu)", "Lily", "🪻")
)

val teluguBodyParts = listOf(
    LearningItem("కళ్ళు", "Kallu (Eyes)", "కళ్ళు", "👀"),
    LearningItem("ముక్కు", "Mukku (Nose)", "ముక్కు", "👃"),
    LearningItem("నోరు", "Noru (Mouth)", "నోరు", "👄"),
    LearningItem("చేతులు", "Chethulu (Hands)", "చేతులు", "🖐️"),
    LearningItem("చెవులు", "Chevulu (Ears)", "చెవులు", "👂"),
    LearningItem("తల", "Thala (Head)", "తల", "👤"),
    LearningItem("జుట్టు", "Juttu (Hair)", "జుట్టు", "💇"),
    LearningItem("నాలుక", "Naaluka (Tongue)", "నాలుక", "👅"),
    LearningItem("పళ్ళు", "Pallu (Teeth)", "పళ్ళు", "🦷"),
    LearningItem("కాళ్ళు", "Kaallu (Legs)", "కాళ్ళు", "🦵"),
    LearningItem("పాదాలు", "Paadaalu (Feet)", "పాదాలు", "👣"),
    LearningItem("వేళ్ళు", "Vellu (Fingers)", "వేళ్ళు", "☝️"),
    LearningItem("ముఖం", "Mukham (Face)", "ముఖం", "👦")
)

val englishBodyParts = listOf(
    LearningItem("Eyes", "Eyes (Kallu)", "Eyes", "👀"),
    LearningItem("Nose", "Nose (Mukku)", "Nose", "👃"),
    LearningItem("Mouth", "Mouth (Noru)", "Mouth", "👄"),
    LearningItem("Hands", "Hands (Chethulu)", "Hands", "🖐️"),
    LearningItem("Ears", "Ears (Chevulu)", "Ears", "👂"),
    LearningItem("Head", "Head (Thala)", "Head", "👤"),
    LearningItem("Hair", "Hair (Juttu)", "Hair", "💇"),
    LearningItem("Tongue", "Tongue (Naaluka)", "Tongue", "👅"),
    LearningItem("Teeth", "Teeth (Pallu)", "Teeth", "🦷"),
    LearningItem("Legs", "Legs (Kaallu)", "Legs", "🦵"),
    LearningItem("Feet", "Feet (Paadaalu)", "Feet", "👣"),
    LearningItem("Fingers", "Fingers (Vellu)", "Fingers", "☝️"),
    LearningItem("Face", "Face (Mukham)", "Face", "👦")
)

val LanguageConfig.actualTitle: String
    get() = if (code == "ml") "അക്ഷരമാല" else title
