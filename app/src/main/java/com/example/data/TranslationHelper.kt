package com.example.data

fun localizeItem(item: LearningItem, category: LessonCategory, langCode: String): LearningItem {
    if (langCode == "te" || langCode == "en") return item
    
    return when (category) {
        LessonCategory.BODY_PARTS -> {
            val translation = getBodyPartTranslation(item.display, langCode)
            if (translation != null) {
                LearningItem(
                    display = translation,
                    subtitle = translation,
                    voiceText = translation,
                    visualEmoji = item.visualEmoji
                )
            } else item
        }
        LessonCategory.RELATIONSHIPS -> {
            val translation = getRelationshipTranslation(item.display, langCode)
            if (translation != null) {
                LearningItem(
                    display = translation,
                    subtitle = translation,
                    voiceText = translation,
                    visualEmoji = item.visualEmoji
                )
            } else item
        }
        LessonCategory.FOODS -> {
            val translation = getFoodTranslation(item.display, langCode)
            if (translation != null) {
                LearningItem(
                    display = translation,
                    subtitle = translation,
                    voiceText = translation,
                    visualEmoji = item.visualEmoji
                )
            } else item
        }
        LessonCategory.FRUITS_FLOWERS -> {
            val translation = getFruitsFlowersTranslation(item.display, langCode)
            if (translation != null) {
                LearningItem(
                    display = translation,
                    subtitle = translation,
                    voiceText = translation,
                    visualEmoji = item.visualEmoji
                )
            } else item
        }
        LessonCategory.NATIONAL_SYMBOLS -> {
            // Find matched national symbol translation
            val key = item.display.substringAfter(" - ").trim() // e.g., "Tiger" from "National Animal - Tiger"
            val translation = getNationalSymbolTranslation(key, langCode)
            if (translation != null) {
                val prefix = getNationalSymbolPrefix(item.display.substringBefore(" - ").trim(), langCode)
                val fullText = if (prefix != null) "$prefix - $translation" else translation
                val speechText = getNationalSymbolSpeech(prefix ?: "", translation, langCode)
                LearningItem(
                    display = fullText,
                    subtitle = translation,
                    voiceText = speechText,
                    visualEmoji = item.visualEmoji
                )
            } else item
        }
        LessonCategory.STATES_CAPITALS -> {
            val parts = item.display.split(" - ")
            if (parts.size == 2) {
                val state = parts[0].trim()
                val capital = parts[1].trim()
                val translatedState = getStateTranslation(state, langCode)
                val translatedCapital = getCapitalTranslation(capital, langCode)
                if (translatedState != null && translatedCapital != null) {
                    val fullText = "$translatedState - $translatedCapital"
                    val speechText = getStateCapitalSpeech(translatedState, translatedCapital, langCode)
                    LearningItem(
                        display = fullText,
                        subtitle = translatedCapital,
                        voiceText = speechText,
                        visualEmoji = item.visualEmoji
                    )
                } else item
            } else item
        }
        else -> item
    }
}

private fun getBodyPartTranslation(name: String, langCode: String): String? {
    val map = when (name) {
        "Eyes" -> mapOf(
            "ta" to "கண்கள்", "hi" to "आँखें", "ar" to "عيون", "kn" to "ಕಣ್ಣುಗಳು",
            "ml" to "കണ്ണുകൾ", "bn" to "চোখ", "mr" to "डोळे", "gu" to "આંખો"
        )
        "Nose" -> mapOf(
            "ta" to "மூக்கு", "hi" to "नाक", "ar" to "أنف", "kn" to "ಮೂಗು",
            "ml" to "മൂക്ക്", "bn" to "নাক", "mr" to "नाक", "gu" to "નાક"
        )
        "Mouth" -> mapOf(
            "ta" to "வாய்", "hi" to "मुँह", "ar" to "فم", "kn" to "ಬಾಯಿ",
            "ml" to "വായ", "bn" to "মুখ", "mr" to "तोंड", "gu" to "મોં"
        )
        "Hands" -> mapOf(
            "ta" to "கைகள்", "hi" to "हाथ", "ar" to "أيدي", "kn" to "ಕೈಗಳು",
            "ml" to "കൈകൾ", "bn" to "হাত", "mr" to "हात", "gu" to "હાથ"
        )
        "Ears" -> mapOf(
            "ta" to "காதுகள்", "hi" to "कान", "ar" to "آذان", "kn" to "ಕಿವಿಗಳು",
            "ml" to "ചെവികൾ", "bn" to "কান", "mr" to "कान", "gu" to "કાન"
        )
        "Head" -> mapOf(
            "ta" to "தலை", "hi" to "सिर", "ar" to "رأس", "kn" to "ತಲೆ",
            "ml" to "തല", "bn" to "মাথা", "mr" to "डोके", "gu" to "માથું"
        )
        "Hair" -> mapOf(
            "ta" to "முடி", "hi" to "बाल", "ar" to "شعر", "kn" to "ಕೂದಲು",
            "ml" to "മുടി", "bn" to "চুল", "mr" to "केस", "gu" to "વાળ"
        )
        "Tongue" -> mapOf(
            "ta" to "நாக்கு", "hi" to "जीभ", "ar" to "لسان", "kn" to "ನಾಲಿಗೆ",
            "ml" to "നാവ്", "bn" to "জিভ", "mr" to "जीभ", "gu" to "જીભ"
        )
        "Teeth" -> mapOf(
            "ta" to "பற்கள்", "hi" to "दाँत", "ar" to "أسنان", "kn" to "ಹಲ್ಲುಗಳು",
            "ml" to "പല്ലുകൾ", "bn" to "দাঁত", "mr" to "दात", "gu" to "દાંત"
        )
        "Legs" -> mapOf(
            "ta" to "கால்கள்", "hi" to "पैर", "ar" to "أرجل", "kn" to "ಕಾಲುಗಳು",
            "ml" to "കാലുകൾ", "bn" to "পা", "mr" to "पाय", "gu" to "પગ"
        )
        "Feet" -> mapOf(
            "ta" to "பாதங்கள்", "hi" to "चरण", "ar" to "أقدام", "kn" to "ಪಾದಗಳು",
            "ml" to "പാദങ്ങൾ", "bn" to "পা", "mr" to "पाऊल", "gu" to "પગ"
        )
        "Fingers" -> mapOf(
            "ta" to "விரல்கள்", "hi" to "उँगलियाँ", "ar" to "أصابع", "kn" to "ಬೆರಳುಗಳು",
            "ml" to "വിരലുകൾ", "bn" to "আঙুল", "mr" to "बोटे", "gu" to "આંગળીઓ"
        )
        "Face" -> mapOf(
            "ta" to "முகம்", "hi" to "चेहरा", "ar" to "وجه", "kn" to "ಮುಖ",
            "ml" to "മുഖം", "bn" to "মুখমণ্ডল", "mr" to "चेहरा", "gu" to "ચહેરો"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getRelationshipTranslation(name: String, langCode: String): String? {
    val map = when (name) {
        "Mother" -> mapOf(
            "ta" to "அம்மா", "hi" to "माँ", "ar" to "أم", "kn" to "ಅಮ್ಮ",
            "ml" to "അമ്മ", "bn" to "মা", "mr" to "आई", "gu" to "માતા"
        )
        "Father" -> mapOf(
            "ta" to "அப்பா", "hi" to "पिताजी", "ar" to "أب", "kn" to "ತಂದೆ",
            "ml" to "അച്ഛൻ", "bn" to "बाबा", "mr" to "वडील", "gu" to "પિતા"
        )
        "Elder Sister" -> mapOf(
            "ta" to "அக்கா", "hi" to "दीदी", "ar" to "أخت كبرى", "kn" to "ಅಕ್ಕ",
            "ml" to "ചേച്ചി", "bn" to "বড় বোন", "mr" to "मोठी बहीण", "gu" to "મોટી બહેન"
        )
        "Younger Sister" -> mapOf(
            "ta" to "தங்கை", "hi" to "छोटी बहन", "ar" to "أخت صغرى", "kn" to "ತಂಗಿ",
            "ml" to "അനിയത്തി", "bn" to "ছোট বোন", "mr" to "लहान बहीण", "gu" to "નાની બહેન"
        )
        "Elder Brother" -> mapOf(
            "ta" to "அண்ணன்", "hi" to "भैया", "ar" to "أخ أكبر", "kn" to "ಅಣ್ಣ",
            "ml" to "ചേട്ടൻ", "bn" to "বড় ভাই", "mr" to "मोठा भाऊ", "gu" to "મોટો ભાઈ"
        )
        "Younger Brother" -> mapOf(
            "ta" to "தம்பி", "hi" to "छोटा भाई", "ar" to "أخ أصغر", "kn" to "ತಮ್ಮ",
            "ml" to "അനിയൻ", "bn" to "ছোট ভাই", "mr" to "लहान भाऊ", "gu" to "નાનો ભાઈ"
        )
        "Grandfather" -> mapOf(
            "ta" to "தாத்தா", "hi" to "दादाजी", "ar" to "جد", "kn" to "ತಾತ",
            "ml" to "മുത്തശ്ശൻ", "bn" to "ঠাকুরদা", "mr" to "आजोबा", "gu" to "દાદા"
        )
        "Grandmother", "Maternal Grandmother", "Paternal Grandmother" -> mapOf(
            "ta" to "பாட்டி", "hi" to "दादीजी", "ar" to "جدة", "kn" to "ಅಜ್ಜಿ",
            "ml" to "മുത്തശ്ശി", "bn" to "ঠাকুমা", "mr" to "आजी", "gu" to "દાદી"
        )
        "Uncle" -> mapOf(
            "ta" to "மாமா", "hi" to "चाचाजी", "ar" to "عم", "kn" to "ಮಾವ",
            "ml" to "അമ്മാവൻ", "bn" to "কাকা", "mr" to "काका", "gu" to "કાકા"
        )
        "Aunt" -> mapOf(
            "ta" to "அத்தை", "hi" to "चाचीजी", "ar" to "عمة", "kn" to "ಅತ್ತೆ",
            "ml" to "അമ്മായി", "bn" to "কাকিমা", "mr" to "काकू", "gu" to "કાકી"
        )
        "Son" -> mapOf(
            "ta" to "மகன்", "hi" to "बेटा", "ar" to "ابن", "kn" to "ಮಗ",
            "ml" to "മകൻ", "bn" to "ছেলে", "mr" to "मुलगा", "gu" to "દીકરો"
        )
        "Daughter" -> mapOf(
            "ta" to "மகள்", "hi" to "बेटी", "ar" to "ابنة", "kn" to "ಮಗಳು",
            "ml" to "മകൾ", "bn" to "মেয়ে", "mr" to "मुलगी", "gu" to "દીકરી"
        )
        "Friend" -> mapOf(
            "ta" to "நண்பன்", "hi" to "मित्र", "ar" to "صديق", "kn" to "ಸ್ನೇಹಿತ",
            "ml" to "കൂട്ടുകാരൻ", "bn" to "বন্ধু", "mr" to "मित्र", "gu" to "મિત્ર"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getFoodTranslation(name: String, langCode: String): String? {
    val map = when (name) {
        "Rice" -> mapOf(
            "ta" to "சாதம்", "hi" to "चावल", "ar" to "أرز", "kn" to "ಅನ್ನ",
            "ml" to "ചോറ്", "bn" to "ভাত", "mr" to "भात", "gu" to "ભાત"
        )
        "Roti" -> mapOf(
            "ta" to "ரொட்டி", "hi" to "रोटी", "ar" to "خبز", "kn" to "ರೊಟ್ಟಿ",
            "ml" to "റൊട്ടി", "bn" to "রুটি", "mr" to "चपाती", "gu" to "રોટલી"
        )
        "Dal" -> mapOf(
            "ta" to "பருப்பு", "hi" to "दाल", "ar" to "عدس", "kn" to "ಬೇಳೆ",
            "ml" to "പരിപ്പ്", "bn" to "ডাল", "mr" to "वरण", "gu" to "દાળ"
        )
        "Curry" -> mapOf(
            "ta" to "கறி", "hi" to "करी", "ar" to "كاري", "kn" to "ಸಾರು",
            "ml" to "കറി", "bn" to "তরকারি", "mr" to "भाजी", "gu" to "શાક"
        )
        "Biryani" -> mapOf(
            "ta" to "பிரியாணி", "hi" to "बिरयानी", "ar" to "برياني", "kn" to "ಬಿರಿಯಾನಿ",
            "ml" to "ബിരിയാണി", "bn" to "বিরিয়ানি", "mr" to "बिर्याणी", "gu" to "બિરયાની"
        )
        "Idli" -> mapOf(
            "ta" to "இட்லி", "hi" to "इडली", "ar" to "إدلي", "kn" to "ಇಡ್ಲಿ",
            "ml" to "ഇഡ്ഡലി", "bn" to "ইডলি", "mr" to "इडली", "gu" to "ઇડલી"
        )
        "Dosa" -> mapOf(
            "ta" to "தோசை", "hi" to "डोसा", "ar" to "دوسا", "kn" to "ದೋಸೆ",
            "ml" to "ദോശ", "bn" to "দোসা", "mr" to "डोसा", "gu" to "ઢોસા"
        )
        "Sambar" -> mapOf(
            "ta" to "சாம்பார்", "hi" to "सांभर", "ar" to "سامبار", "kn" to "ಸಾಂಬಾರ್",
            "ml" to "സാമ്പാർ", "bn" to "সাম্বার", "mr" to "सांभार", "gu" to "સાંભાર"
        )
        "Laddu" -> mapOf(
            "ta" to "லட்டு", "hi" to "लड्डू", "ar" to "لادو", "kn" to "ಲಾಡು",
            "ml" to "ലഡ്ഡു", "bn" to "লাড্ডু", "mr" to "लाडू", "gu" to "લાડુ"
        )
        "Payasam", "Kheer" -> mapOf(
            "ta" to "பாயசம்", "hi" to "खीर", "ar" to "بودينغ الأرز", "kn" to "ಪಾಯಸ",
            "ml" to "പായസം", "bn" to "পায়েস", "mr" to "खीर", "gu" to "ખીર"
        )
        "Vada" -> mapOf(
            "ta" to "வடை", "hi" to "वड़ा", "ar" to "فادا", "kn" to "ವಡೆ",
            "ml" to "വട", "bn" to "বড়া", "mr" to "वडा", "gu" to "વડા"
        )
        "Chapati" -> mapOf(
            "ta" to "சப்பாத்தி", "hi" to "चपाती", "ar" to "تشاباتي", "kn" to "ಚಪಾತಿ",
            "ml" to "ചപ്പാത്തി", "bn" to "চাপাতি", "mr" to "चपाती", "gu" to "ચપાતી"
        )
        "Poori" -> mapOf(
            "ta" to "பூரி", "hi" to "पूरी", "ar" to "بوري", "kn" to "ಪೂರಿ",
            "ml" to "പൂരി", "bn" to "লুচি", "mr" to "पुरी", "gu" to "પૂરી"
        )
        "Curd" -> mapOf(
            "ta" to "தயிர்", "hi" to "दही", "ar" to "لبن", "kn" to "ಮೊಸರು",
            "ml" to "തൈര്", "bn" to "দই", "mr" to "दही", "gu" to "દહીં"
        )
        "Ghee" -> mapOf(
            "ta" to "நெய்", "hi" to "घी", "ar" to "سمن", "kn" to "ತುಪ್ಪ",
            "ml" to "നെയ്യ്", "bn" to "ঘি", "mr" to "तूप", "gu" to "ઘી"
        )
        "Pickle" -> mapOf(
            "ta" to "ஊறுகாய்", "hi" to "अचार", "ar" to "مخلل", "kn" to "ಉಪ್ಪಿನಕಾಯಿ",
            "ml" to "അച്ചാർ", "bn" to "আচার", "mr" to "लोणचे", "gu" to "અથાણું"
        )
        "Samosa" -> mapOf(
            "ta" to "சமோசா", "hi" to "समोसा", "ar" to "سمبوسة", "kn" to "ಸಮೋಸಾ",
            "ml" to "സമോസ", "bn" to "সিঙাড়া", "mr" to "समोसा", "gu" to "समोसा"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getFruitsFlowersTranslation(name: String, langCode: String): String? {
    val map = when (name) {
        "Mango" -> mapOf(
            "ta" to "மாம்பழம்", "hi" to "आम", "ar" to "مانجو", "kn" to "ಮಾವಿನ ಹಣ್ಣು",
            "ml" to "മാമ്പഴം", "bn" to "আম", "mr" to "आंबा", "gu" to "કેરી"
        )
        "Banana" -> mapOf(
            "ta" to "வாழைப்பழம்", "hi" to "केला", "ar" to "موز", "kn" to "ಬಾಳೆಹಣ್ಣು",
            "ml" to "പഴം", "bn" to "কলা", "mr" to "केले", "gu" to "કેળું"
        )
        "Apple" -> mapOf(
            "ta" to "ஆப்பிள்", "hi" to "सेब", "ar" to "تفاح", "kn" to "ಸೇಬು",
            "ml" to "ആപ്പിൾ", "bn" to "আপেল", "mr" to "सफरचंद", "gu" to "સફરજન"
        )
        "Orange" -> mapOf(
            "ta" to "ஆரஞ்சு", "hi" to "संतरा", "ar" to "برتقال", "kn" to "ಕಿತ್ತಳೆ",
            "ml" to "ഓറഞ്ച്", "bn" to "কমলা", "mr" to "सन्त्रे", "gu" to "સંતરું"
        )
        "Grapes" -> mapOf(
            "ta" to "திராட்சை", "hi" to "अंगूर", "ar" to "عنب", "kn" to "ದ್ರಾಕ್ಷಿ",
            "ml" to "മുന്തിരി", "bn" to "আঙুর", "mr" to "द्राक्षे", "gu" to "દ્રાક્ષ"
        )
        "Papaya" -> mapOf(
            "ta" to "பப்பாளி", "hi" to "पपीता", "ar" to "بابايا", "kn" to "ಪರಂಗಿ ಹಣ್ಣು",
            "ml" to "പപ്പായ", "bn" to "পেঁপে", "mr" to "पपई", "gu" to "પપૈયું"
        )
        "Guava" -> mapOf(
            "ta" to "கொய்யா", "hi" to "अमरूद", "ar" to "جوافة", "kn" to "ಸೀಬೆಹಣ್ಣು",
            "ml" to "പേരയ്ക്ക", "bn" to "পেয়রা", "mr" to "पेरू", "gu" to "જામફળ"
        )
        "Coconut" -> mapOf(
            "ta" to "தேங்காய்", "hi" to "नारियल", "ar" to "جوز الهند", "kn" to "ತೆಂಗಿನಕಾಯಿ",
            "ml" to "തേങ്ങ", "bn" to "নারকেল", "mr" to "नारळ", "gu" to "નારિયેળ"
        )
        "Rose" -> mapOf(
            "ta" to "ரோஜா", "hi" to "गुलाब", "ar" to "وردة", "kn" to "ಗುಲಾಬಿ",
            "ml" to "റോസാപ്പൂവ്", "bn" to "গোলাপ", "mr" to "गुलाब", "gu" to "ગુલાબ"
        )
        "Jasmine" -> mapOf(
            "ta" to "மல்லிகை", "hi" to "चमेली", "ar" to "ياسمين", "kn" to "ಮಲ್ಲಿಗೆ",
            "ml" to "മല്ലിപ്പൂവ്", "bn" to "জুঁই", "mr" to "मोगरा", "gu" to "ચમેલી"
        )
        "Lotus" -> mapOf(
            "ta" to "தாமரை", "hi" to "कमल", "ar" to "لوتس", "kn" to "ಕಮಲ",
            "ml" to "താമര", "bn" to "পদ্ম", "mr" to "कमळ", "gu" to "કમળ"
        )
        "Marigold" -> mapOf(
            "ta" to "சாமந்தி", "hi" to "गेंदा", "ar" to "قطيفة", "kn" to "ಚೆಂಡುಹೂವು",
            "ml" to "ചെണ്ടുമല്ലി", "bn" to "গাঁদা", "mr" to "झेंडू", "gu" to "ગલગોટો"
        )
        "Hibiscus" -> mapOf(
            "ta" to "செம்பருத்தி", "hi" to "गुड़हल", "ar" to "كركديه", "kn" to "ದಾಸವಾಳ",
            "ml" to "ചെമ്പരത്തി", "bn" to "জবা", "mr" to "जास्वंद", "gu" to "જાસુદ"
        )
        "Sunflower" -> mapOf(
            "ta" to "சூரியகாந்தி", "hi" to "सूरजमुखी", "ar" to "دوّار الشمس", "kn" to "ಸೂರ್ಯಕಾಂತಿ",
            "ml" to "സൂര്യകാന്തി", "bn" to "সূর্যমুখী", "mr" to "सूर्यफूल", "gu" to "સૂર્યમુખી"
        )
        "Lily" -> mapOf(
            "ta" to "அல்லி", "hi" to "कुमुदिनी", "ar" to "زنبق", "kn" to "ನೈದಿಲೆ",
            "ml" to "ആമ്പൽ", "bn" to "শালুক", "mr" to "कमलिनी", "gu" to "પોયણું"
        )
        "Custard Apple" -> mapOf(
            "ta" to "சீதாப்பழம்", "hi" to "शरीफा", "ar" to "قشطة", "kn" to "ಸೀತಾಫಲ",
            "ml" to "ആത്തച്ചക്ക", "bn" to "আতা", "mr" to "सीताफळ", "gu" to "સીતાફળ"
        )
        "Pomegranate" -> mapOf(
            "ta" to "மாதுளம்பழம்", "hi" to "अनार", "ar" to "رمان", "kn" to "ದಾಳಿಂಬೆ",
            "ml" to "മാതളനാരങ്ങ", "bn" to "বেদানা", "mr" to "डाळिंब", "gu" to "દાડમ"
        )
        "Watermelon" -> mapOf(
            "ta" to "தர்பூசணி", "hi" to "तरबूज", "ar" to "بطيخ", "kn" to "ಕಲ್ಲಂಗಡಿ",
            "ml" to "തണ്ണിമത്തൻ", "bn" to "তরমুজ", "mr" to "कलिंगड", "gu" to "તરબૂચ"
        )
        "Chrysanthemum" -> mapOf(
            "ta" to "செவ்வந்தி", "hi" to "गुलदाउदी", "ar" to "أقحوان", "kn" to "ಶೇವಂತಿ",
            "ml" to "ശേവന്തി", "bn" to "চন্দ্রমল্লিকা", "mr" to "शेवंती", "gu" to "ગુલદાવદી"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getNationalSymbolTranslation(key: String, langCode: String): String? {
    val map = when (key) {
        "Tiger" -> mapOf(
            "ta" to "புலி", "hi" to "बाघ", "ar" to "نمر", "kn" to "ಹುಲಿ",
            "ml" to "കടുവ", "bn" to "বাঘ", "mr" to "वाघ", "gu" to "વાઘ"
        )
        "Peacock" -> mapOf(
            "ta" to "மயில்", "hi" to "मोर", "ar" to "طاووس", "kn" to "ನವಿಲು",
            "ml" to "മയിൽ", "bn" to "ময়ূর", "mr" to "मोर", "gu" to "મોર"
        )
        "Lotus" -> mapOf(
            "ta" to "தாமரை", "hi" to "कमल", "ar" to "لوتس", "kn" to "ಕಮಲ",
            "ml" to "താമര", "bn" to "পদ্ম", "mr" to "कमळ", "gu" to "કમળ"
        )
        "Mango" -> mapOf(
            "ta" to "மாம்பழம்", "hi" to "आम", "ar" to "مانجو", "kn" to "ಮಾವು",
            "ml" to "മാമ്പഴം", "bn" to "আম", "mr" to "आंबा", "gu" to "કેરી"
        )
        "Banyan", "Banyan Tree" -> mapOf(
            "ta" to "ஆலமரம்", "hi" to "बरगद", "ar" to "تين بنغالي", "kn" to "ಆಲದ ಮರ",
            "ml" to "പേരാൽ", "bn" to "বটগাছ", "mr" to "वटवृक्ष", "gu" to "વડ"
        )
        "Ganges" -> mapOf(
            "ta" to "கங்கை", "hi" to "गंगा", "ar" to "نهر الغانج", "kn" to "ಗಂಗಾ",
            "ml" to "ഗംഗ", "bn" to "গঙ্গা", "mr" to "गंगा", "gu" to "ગંગા"
        )
        "Hockey" -> mapOf(
            "ta" to "ஹாக்கி", "hi" to "हॉकी", "ar" to "هوكي", "kn" to "ಹಾಕಿ",
            "ml" to "ഹോക്കി", "bn" to "হকি", "mr" to "हॉकी", "gu" to "હોકી"
        )
        "Anthem", "Jana Gana Mana" -> mapOf(
            "ta" to "ஜன கண மன", "hi" to "जन गण मन", "ar" to "جانا غانا مانا", "kn" to "ಜನ ಗಣ ಮನ",
            "ml" to "ജന ഗണ മന", "bn" to "জন গণ মন", "mr" to "जन गण मन", "gu" to "જન ગણ મન"
        )
        "Song", "Vande Mataram" -> mapOf(
            "ta" to "வந்தே மாதரம்", "hi" to "वंदे मातरम", "ar" to "فاندي ماتارام", "kn" to "ವಂದೇ ಮಾತರಂ",
            "ml" to "വന്ദേ മാതരം", "bn" to "বন্দে মাতরম", "mr" to "वंदे मातरम", "gu" to "વંદે માતરમ"
        )
        "Emblem", "Ashoka Lion Capital" -> mapOf(
            "ta" to "அசோக முத்திரை", "hi" to "अशोक चिह्न", "ar" to "عمود أشوكا", "kn" to "ಅಶೋಕ ಸ್ತಂಭ",
            "ml" to "അശോക സ്തംഭം", "bn" to "অশোক স্তম্ভ", "mr" to "राजमुद्रा", "gu" to "અશોક સ્તંભ"
        )
        "Dolphin" -> mapOf(
            "ta" to "டால்பின்", "hi" to "डॉल्फ़िन", "ar" to "دولفين", "kn" to "ಡಾಲ್ಫಿನ್",
            "ml" to "ഡോൾഫിൻ", "bn" to "ডলফিন", "mr" to "डॉल्फिन", "gu" to "ડોલ્ફિન"
        )
        "Elephant" -> mapOf(
            "ta" to "யானை", "hi" to "हाथी", "ar" to "فيل", "kn" to "ಆನೆ",
            "ml" to "ആന", "bn" to "হাতি", "mr" to "हत्ती", "gu" to "હાથી"
        )
        "King Cobra" -> mapOf(
            "ta" to "ராஜநாகம்", "hi" to "किंग कोबरा", "ar" to "الكوبرا الملك", "kn" to "ಕಾಳಿಂಗ ಸರ್ಪ",
            "ml" to "രാജവെമ്പാല", "bn" to "শঙ্খচূড়", "mr" to "किंग कोब्रा", "gu" to "કિંગ કોબ્રા"
        )
        "Rupee" -> mapOf(
            "ta" to "ரூபாய்", "hi" to "रुपया", "ar" to "روبية", "kn" to "ರೂಪಾಯಿ",
            "ml" to "രൂപ", "bn" to "রুপি", "mr" to "रुपया", "gu" to "રૂપિયો"
        )
        "Saka" -> mapOf(
            "ta" to "சகா நாட்காட்டி", "hi" to "शक कैलेंडर", "ar" to "تقويم ساكا", "kn" to "ಶಕ ಕ್ಯಾಲೆಂಡರ್",
            "ml" to "ശക കലണ്ടർ", "bn" to "শক পঞ্জিকা", "mr" to "शक संवत", "gu" to "શક કેલેન્ડર"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getNationalSymbolPrefix(englishPrefix: String, langCode: String): String? {
    val map = when (englishPrefix) {
        "National Animal" -> mapOf(
            "ta" to "தேசிய விலங்கு", "hi" to "राष्ट्रीय पशु", "ar" to "الحيوان الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಪ್ರಾಣಿ",
            "ml" to "ദേശീയ മൃഗം", "bn" to "জাতীয় পশু", "mr" to "राष्ट्रीय प्राणी", "gu" to "રાષ્ટ્રીય પ્રાણી"
        )
        "National Bird" -> mapOf(
            "ta" to "தேசிய பறவை", "hi" to "राष्ट्रीय पक्षी", "ar" to "الطائر الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಪಕ್ಷಿ",
            "ml" to "ദേശീയ പักษി", "bn" to "জাতীয় পাখি", "mr" to "राष्ट्रीय पक्षी", "gu" to "રાષ્ટ્રીય પક્ષી"
        )
        "National Flower" -> mapOf(
            "ta" to "தேசிய மலர்", "hi" to "राष्ट्रीय फूल", "ar" to "الزهرة الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಹೂವು",
            "ml" to "ദേശീയ പുഷ്പം", "bn" to "জাতীয় ফুল", "mr" to "राष्ट्रीय फूल", "gu" to "રાષ્ટ્રીય ફૂલ"
        )
        "National Fruit" -> mapOf(
            "ta" to "தேசிய பழம்", "hi" to "राष्ट्रीय फल", "ar" to "الفاكهة الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಹಣ್ಣು",
            "ml" to "ദേശീയ ഫലം", "bn" to "জাতীয় ফল", "mr" to "राष्ट्रीय फळ", "gu" to "રાષ્ટ્રીય ફળ"
        )
        "National Tree" -> mapOf(
            "ta" to "தேசிய மரம்", "hi" to "राष्ट्रीय वृक्ष", "ar" to "الشجرة الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಮರ",
            "ml" to "ദേശീയ വൃക്ഷം", "bn" to "জাতীয় গাছ", "mr" to "राष्ट्रीय वृक्ष", "gu" to "રાષ્ટ્રીય વૃક્ષ"
        )
        "National River" -> mapOf(
            "ta" to "தேசிய நதி", "hi" to "राष्ट्रीय नदी", "ar" to "النهر الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ನದಿ",
            "ml" to "ദേശീയ നദി", "bn" to "জাতীয় নদী", "mr" to "राष्ट्रीय नदी", "gu" to "રાષ્ટ્રીય નદી"
        )
        "National Game" -> mapOf(
            "ta" to "தேசிய விளையாட்டு", "hi" to "राष्ट्रीय खेल", "ar" to "اللعبة الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಕ್ರೀಡೆ",
            "ml" to "ദേശീയ കായികം", "bn" to "জাতীয় খেলা", "mr" to "राष्ट्रीय खेळ", "gu" to "રાષ્ટ્રીય રમત"
        )
        "National Anthem" -> mapOf(
            "ta" to "தேசிய கீதம்", "hi" to "राष्ट्रगान", "ar" to "النشيد الوطني", "kn" to "ರಾಷ್ಟ್ರಗೀತೆ",
            "ml" to "ദേശീയഗാനം", "bn" to "জাতীয় সঙ্গীত", "mr" to "राष्ट्रगीत", "gu" to "રાષ્ટ્રગીત"
        )
        "National Song" -> mapOf(
            "ta" to "தேசிய பாடல்", "hi" to "राष्ट्रीय गीत", "ar" to "الأغنية الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಗೀತೆ",
            "ml" to "ദേശീയ ഗീതം", "bn" to "জাতীয় গীতি", "mr" to "राष्ट्रीय गीत", "gu" to "રાષ્ટ્રીય ગીત"
        )
        "National Emblem" -> mapOf(
            "ta" to "தேசிய முத்திரை", "hi" to "राष्ट्रीय प्रतीक", "ar" to "الشعار الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಲಾಂಛನ",
            "ml" to "ദേശീയ മുദ്ര", "bn" to "জাতীয় প্রতীক", "mr" to "राष्ट्रीय चिन्ह", "gu" to "રાષ્ટ્રીય પ્રતીક"
        )
        "National Aquatic Animal" -> mapOf(
            "ta" to "தேசிய நீர்வாழ் விலங்கு", "hi" to "राष्ट्रीय जलचर", "ar" to "الحيوان المائي الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಜಲಚರ",
            "ml" to "ദേശീയ ജലജീവി", "bn" to "জাতীয় জলচর", "mr" to "राष्ट्रीय जलचर", "gu" to "રાષ્ટ્રીય જળચર પ્રાણી"
        )
        "National Heritage Animal" -> mapOf(
            "ta" to "தேசிய பாரம்பரிய விலங்கு", "hi" to "राष्ट्रीय विरासत पशु", "ar" to "حيوان التراث الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಪರಂಪರೆಯ ಪ್ರಾಣಿ",
            "ml" to "ദേശീയ പൈതൃക മൃഗം", "bn" to "জাতীয় ঐতিহ্যবাহী পশু", "mr" to "राष्ट्रीय वारसा प्राणी", "gu" to "રાષ્ટ્રીય વિરાસત પ્રાણી"
        )
        "National Reptile" -> mapOf(
            "ta" to "தேசிய ஊர்வன", "hi" to "राष्ट्रीय सरीसृप", "ar" to "الزواحف الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಸರೀಸೃಪ",
            "ml" to "ദേശീയ ഉരഗം", "bn" to "জাতীয় সরীসৃপ", "mr" to "राष्ट्रीय सरपटणारा प्राणी", "gu" to "રાષ્ટ્રીય સરિસૃપ"
        )
        "National Currency" -> mapOf(
            "ta" to "தேசிய நாணயம்", "hi" to "राष्ट्रीय मुद्रा", "ar" to "العملة الوطنية", "kn" to "ರಾಷ್ಟ್ರೀಯ ಕರೆನ್ಸಿ",
            "ml" to "ദേശീയ നാണയം", "bn" to "জাতীয় মুদ্রা", "mr" to "राष्ट्रीय चलन", "gu" to "રાષ્ટ્રીય ચલણ"
        )
        "National Calendar" -> mapOf(
            "ta" to "தேசிய நாட்காட்டி", "hi" to "राष्ट्रीय कैलेंडर", "ar" to "التقويم الوطني", "kn" to "ರಾಷ್ಟ್ರೀಯ ಕ್ಯಾಲೆಂಡರ್",
            "ml" to "ദേശീയ കലണ്ടർ", "bn" to "জাতীয় পঞ্জিকা", "mr" to "राष्ट्रीय दिनदर्शिका", "gu" to "રાષ્ટ્રીય કેલેન્ડર"
        )
        else -> null
    }
    return map?.get(langCode)
}

private fun getNationalSymbolSpeech(prefix: String, name: String, langCode: String): String {
    return when (langCode) {
        "ta" -> "${prefix} ${name}"
        "hi" -> "${prefix} ${name} है"
        "ar" -> "${prefix} هو ${name}"
        "kn" -> "${prefix} ${name}"
        "ml" -> "${prefix} ${name} ആണ്"
        "bn" -> "${prefix} হলো ${name}"
        "mr" -> "${prefix} ${name} आहे"
        "gu" -> "${prefix} ${name} છે"
        else -> "${prefix} ${name}"
    }
}

private fun getStateTranslation(state: String, langCode: String): String? {
    val map = when (state) {
        "Andhra Pradesh" -> mapOf("ta" to "ஆந்திரப் பிரதேசம்", "hi" to "आंध्रा प्रदेश", "ar" to "أندرا براديش", "kn" to "ಆಂಧ್ರ ಪ್ರದೇಶ", "ml" to "ആന്ധ്രാപ്രദേശ്", "bn" to "অন্ধ্রপ্রদেশ", "mr" to "आंध्र प्रदेश", "gu" to "આંધ્ર પ્રદેશ")
        "Arunachal Pradesh" -> mapOf("ta" to "அருணாச்சல பிரதேசம்", "hi" to "अरुणाचल प्रदेश", "ar" to "أروناجل براديش", "kn" to "ಅರುಣಾಚಲ ಪ್ರದೇಶ", "ml" to "അരുണാചൽ പ്രദേശ്", "bn" to "অরুণাচল প্রদেশ", "mr" to "अरुणाचल प्रदेश", "gu" to "અરુણાચલ પ્રદેશ")
        "Assam" -> mapOf("ta" to "அசாம்", "hi" to "असम", "ar" to "أسام", "kn" to "ಅಸ್ಸಾಂ", "ml" to "അസം", "bn" to "আসাম", "mr" to "आसाम", "gu" to "આસામ")
        "Bihar" -> mapOf("ta" to "பீகார்", "hi" to "बिहार", "ar" to "بيهار", "kn" to "ಬಿಹಾರ", "ml" to "ബിഹാർ", "bn" to "বিহার", "mr" to "बिहार", "gu" to "બિહાર")
        "Chhattisgarh" -> mapOf("ta" to "சத்தீஸ்கர்", "hi" to "छत्तीसगढ़", "ar" to "تشاتيسغار", "kn" to "ಛತ್ತೀಸ್‌ಗಢ್", "ml" to "ഛത്തീസ്ഗഡ്", "bn" to "ছত্তিশগড়", "mr" to "छत्तीसगड", "gu" to "છત્તીસગઢ")
        "Goa" -> mapOf("ta" to "கோவா", "hi" to "गोवा", "ar" to "غوا", "kn" to "ಗೋವಾ", "ml" to "ഗോവ", "bn" to "গোয়া", "mr" to "गोवा", "gu" to "ગોવા")
        "Gujarat" -> mapOf("ta" to "குஜராத்", "hi" to "गुजरात", "ar" to "غوجارات", "kn" to "ಗುಜರಾತ್", "ml" to "ഗുജറാത്ത്", "bn" to "গুজরাট", "mr" to "गुजरात", "gu" to "ગુજરાત")
        "Haryana" -> mapOf("ta" to "ஹரியானா", "hi" to "हरियाणा", "ar" to "هاريانا", "kn" to "ಹರಿಯಾಣ", "ml" to "ഹരിയാന", "bn" to "হরিয়ানা", "mr" to "हरियाणा", "gu" to "હરિયાણા")
        "Himachal Pradesh" -> mapOf("ta" to "இமாச்சல பிரதேசம்", "hi" to "हिमाचल प्रदेश", "ar" to "هيماجل براديش", "kn" to "ಹಿಮಾಚಲ್ ಪ್ರದೇಶ", "ml" to "ഹിമാചൽപ്രദേശ്", "bn" to "হিমাচল প্রদেশ", "mr" to "हिमाचल प्रदेश", "gu" to "હિમાચલપ્રદેશ")
        "Jharkhand" -> mapOf("ta" to "ஜார்கண்ட்", "hi" to "झारखण्ड", "ar" to "جهارخاند", "kn" to "ಜಾರ್ಖಂಡ್", "ml" to "ജാർഖണ്ഡ്", "bn" to "ঝাড়খণ্ড", "mr" to "झारखंड", "gu" to "ઝારખંડ")
        "Karnataka" -> mapOf("ta" to "கர்நாடகா", "hi" to "कर्नाटक", "ar" to "كارناتاكا", "kn" to "ಕರ್ನಾಟಕ", "ml" to "കർണാടക", "bn" to "কর্ণাটক", "mr" to "कर्नाटक", "gu" to "કર્ણાટક")
        "Kerala" -> mapOf("ta" to "கேரளா", "hi" to "केरल", "ar" to "كيرالا", "kn" to "ಕೇರಳ", "ml" to "കേരളം", "bn" to "কেরল", "mr" to "केरळ", "gu" to "કેરળ")
        "Madhya Pradesh" -> mapOf("ta" to "மத்திய பிரதேசம்", "hi" to "मध्य प्रदेश", "ar" to "مدهيا براديش", "kn" to "ಮಧ್ಯ ಪ್ರದೇಶ", "ml" to "മധ്യപ്രദേശ്", "bn" to "मध्यप्रदेश", "mr" to "मध्य प्रदेश", "gu" to "મધ્ય પ્રદેશ")
        "Maharashtra" -> mapOf("ta" to "மகாராஷ்டிரா", "hi" to "महाराष्ट्र", "ar" to "ماهاراشترا", "kn" to "ಮಹಾರಾಷ್ಟ್ರ", "ml" to "മഹാരാഷ്ട്ര", "bn" to "মহারাষ্ট্র", "mr" to "महाराष्ट्र", "gu" to "મહારાષ્ટ્ર")
        "Manipur" -> mapOf("ta" to "மணிப்பூர்", "hi" to "मणिपुर", "ar" to "مانيبور", "kn" to "ಮಣಿಪುರ", "ml" to "മണിപ്പൂർ", "bn" to "মণিপুর", "mr" to "मणिपूर", "gu" to "મણિપુર")
        "Meghalaya" -> mapOf("ta" to "மேகாலயா", "hi" to "मेघालय", "ar" to "ميغالايا", "kn" to "ಮೇಘಾಲಯ", "ml" to "മേഘാലയ", "bn" to "মেঘালয়", "mr" to "मेघालय", "gu" to "મેઘાલય")
        "Mizoram" -> mapOf("ta" to "மிசோரம்", "hi" to "मिज़ोरम", "ar" to "ميزورام", "kn" to "ಮಿಜೋರಾಂ", "ml" to "മിസോറാം", "bn" to "মিজোরাম", "mr" to "मिझोरम", "gu" to "મિઝોરમ")
        "Nagaland" -> mapOf("ta" to "நாகாலாந்து", "hi" to "नागालैंड", "ar" to "नागालैंड", "kn" to "ನಾಗಾಲ್ಯಾಂಡ್", "ml" to "നാഗാലാൻഡ്", "bn" to "নাগাল্যান্ড", "mr" to "नागालँड", "gu" to "નાગાલેન્ડ")
        "Odisha" -> mapOf("ta" to "ஒடிசா", "hi" to "ओडिशा", "ar" to "أوديشا", "kn" to "ಒಡಿಶಾ", "ml" to "ഒഡീഷ", "bn" to "ওড়িশা", "mr" to "ओडिशा", "gu" to "ઓડિશા")
        "Punjab" -> mapOf("ta" to "பஞ்சாப்", "hi" to "पंजाब", "ar" to "بنجاب", "kn" to "ಪಂಜಾಬ್", "ml" to "പഞ്ചാബ്", "bn" to "পাঞ্জাব", "mr" to "पंजाब", "gu" to "પંજાબ")
        "Rajasthan" -> mapOf("ta" to "ராஜஸ்தான்", "hi" to "राजस्थान", "ar" to "راجستان", "kn" to "ರಾಜಸ್ಥಾನ", "ml" to "രാജസ്ഥാൻ", "bn" to "রাজস্থান", "mr" to "राजस्थान", "gu" to "રાજસ્થાન")
        "Sikkim" -> mapOf("ta" to "சிக்கிம்", "hi" to "सिक्किम", "ar" to "سيكيم", "kn" to "ಸಿಕ್ಕಿಂ", "ml" to "സിക്കിം", "bn" to "সিকিম", "mr" to "सिक्कीम", "gu" to "સિક્કિમ")
        "Tamil Nadu" -> mapOf("ta" to "தமிழ்நாடு", "hi" to "तमिलनाडु", "ar" to "تاميل نادو", "kn" to "ತಮಿಳುನಾಡು", "ml" to "തമിഴ്നാട്", "bn" to "তামিলনাড়ু", "mr" to "तमिळनाडू", "gu" to "તમિલનાડુ")
        "Telangana" -> mapOf("ta" to "தெலுங்கானா", "hi" to "तेलंगाना", "ar" to "تيلانغانا", "kn" to "ತೆಲಂಗಾಣ", "ml" to "തെലങ്കാന", "bn" to "তেলেঙ্গানা", "mr" to "तेलंगणा", "gu" to "તેલંગાણા")
        "Tripura" -> mapOf("ta" to "திரிபுரா", "hi" to "त्रिपुरा", "ar" to "تريبورا", "kn" to "ತ್ರಿಪುರ", "ml" to "ത്രിപുര", "bn" to "ত্রিপুরা", "mr" to "त्रिपुरा", "gu" to "ત્રિપુરા")
        "Uttar Pradesh" -> mapOf("ta" to "உத்திர பிரதேசம்", "hi" to "उत्तर प्रदेश", "ar" to "أوتار براديش", "kn" to "ಉತ್ತರ ಪ್ರದೇಶ", "ml" to "ഉത്തർപ്രദേശ്", "bn" to "উত্তরপ্রদেশ", "mr" to "उत्तर प्रदेश", "gu" to "ઉત્તર પ્રદેશ")
        "Uttarakhand" -> mapOf("ta" to "உத்தராகண்ட்", "hi" to "उत्तराखण्ड", "ar" to "أوتاراخاند", "kn" to "ಉತ್ತರಾಖಂಡ", "ml" to "ഉത്തരാഖണ്ഡ്", "bn" to "উত্তরাখণ্ড", "mr" to "उत्तराखंड", "gu" to "ઉત્તરાખંડ")
        "West Bengal" -> mapOf("ta" to "மேற்கு வங்கம்", "hi" to "पश्चिम बंगाल", "ar" to "البنغال الغربية", "kn" to "ಪಶ್ಚಿಮ ಬಂಗಾಳ", "ml" to "പശ്ചിമ ബംഗാൾ", "bn" to "পশ্চিমবঙ্গ", "mr" to "पश्चिम बंगाल", "gu" to "પશ્ચિમ બંગાળ")
        "India" -> mapOf("ta" to "இந்தியா", "hi" to "भारत", "ar" to "الهند", "kn" to "ಭಾರತ", "ml" to "ഇന്ത്യ", "bn" to "ভারত", "mr" to "भारत", "gu" to "ભારત")
        else -> null
    }
    return map?.get(langCode)
}

private fun getCapitalTranslation(capital: String, langCode: String): String? {
    val map = when (capital) {
        "Amaravati" -> mapOf("ta" to "அமராவதி", "hi" to "अमरावती", "ar" to "أمرافاتي", "kn" to "ಅಮರಾವತಿ", "ml" to "അമരാവതി", "bn" to "অমরাবতী", "mr" to "अमरावती", "gu" to "અમરાવતી")
        "Itanagar" -> mapOf("ta" to "இடாநகர்", "hi" to "ईटानगर", "ar" to "إيتاناغار", "kn" to "ಇಟಾನಗರ", "ml" to "ഇറ്റാനഗർ", "bn" to "ইটানগর", "mr" to "इटानगर", "gu" to "ઈટાનગર")
        "Dispur" -> mapOf("ta" to "திஸ்பூர்", "hi" to "दिसपुर", "ar" to "ديسبور", "kn" to "ದಿಸ್ಪುರ್", "ml" to "ദിസ്പൂർ", "bn" to "দিসপুর", "mr" to "दिसपूर", "gu" to "દિસપુર")
        "Patna" -> mapOf("ta" to "பாட்னா", "hi" to "पटना", "ar" to "باتنا", "kn" to "ಪಾಟ್ನಾ", "ml" to "പട്ന", "bn" to "পাটনা", "mr" to "पाटणा", "gu" to "પટના")
        "Raipur" -> mapOf("ta" to "ராய்ப்பூர்", "hi" to "रायपुर", "ar" to "رايبور", "kn" to "ರಾಯ್‌ಪುರ್", "ml" to "റായ്പൂർ", "bn" to "রায়পুর", "mr" to "रायपूर", "gu" to "રાયપુર")
        "Panaji" -> mapOf("ta" to "பனாஜி", "hi" to "पणजी", "ar" to "باناجي", "kn" to "ಪಣಜಿ", "ml" to "പനജി", "bn" to "পানাজি", "mr" to "पणजी", "gu" to "પણજી")
        "Gandhinagar" -> mapOf("ta" to "காந்திநகர்", "hi" to "गांधीनगर", "ar" to "غانديناغار", "kn" to "ಗಾಂಧಿನಗರ", "ml" to "ഗാന്ധിനഗർ", "bn" to "গান্ধীনগর", "mr" to "गांधीनगर", "gu" to "ગાંધીનગર")
        "Chandigarh" -> mapOf("ta" to "சண்டிகர்", "hi" to "चंडीगढ़", "ar" to "شانديغار", "kn" to "ಚಂಡೀಗಢ್", "ml" to "ചണ്ഡീഗഡ്", "bn" to "চণ্ডীগড়", "mr" to "चंदीगड", "gu" to "ચંદીગઢ")
        "Shimla" -> mapOf("ta" to "சிம்லா", "hi" to "शिमला", "ar" to "شيملا", "kn" to "ಶಿಮ್ಲಾ", "ml" to "ഷിംല", "bn" to "শিমলা", "mr" to "शिमला", "gu" to "શિમલા")
        "Ranchi" -> mapOf("ta" to "ராஞ்சி", "hi" to "राँची", "ar" to "رانشي", "kn" to "ರಾಂಚಿ", "ml" to "റാഞ്ചി", "bn" to "রাঁচি", "mr" to "रांची", "gu" to "રાંચી")
        "Bengaluru" -> mapOf("ta" to "பெங்களூரு", "hi" to "बेंगलुरु", "ar" to "بنغالور", "kn" to "ಬೆಂಗಳೂರು", "ml" to "ബംഗളൂരു", "bn" to "বেঙ্গালুরু", "mr" to "बेंगळुरू", "gu" to "બેંગલુરુ")
        "Thiruvananthapuram" -> mapOf("ta" to "திருவனந்தபுரம்", "hi" to "तिरुवनंतपुरम", "ar" to "تيروفانانثابورام", "kn" to "ತಿರುವನಂತಪುರಂ", "ml" to "തിരുവനന്തപുരം", "bn" to "তিরুবনন্তপুরম", "mr" to "तिरुवनंतपुरम", "gu" to "તિરુવનંતપુરમ")
        "Bhopal" -> mapOf("ta" to "போபால்", "hi" to "भोपाल", "ar" to "بوبال", "kn" to "ಭೋಪಾಲ್", "ml" to "ഭോപ്പാൽ", "bn" to "ভোপাল", "mr" to "भोपाळ", "gu" to "ભોપાલ")
        "Mumbai" -> mapOf("ta" to "மும்பை", "hi" to "मुंबई", "ar" to "مومباي", "kn" to "ಮುಂಬೈ", "ml" to "മുംബൈ", "bn" to "মুম্বই", "mr" to "मुंबई", "gu" to "મુંબઈ")
        "Imphal" -> mapOf("ta" to "இம்பால்", "hi" to "इम्फाल", "ar" to "إمفال", "kn" to "ಇಂಫಾಲ", "ml" to "ഇംഫാൽ", "bn" to "ইম্ফল", "mr" to "इम्फाल", "gu" to "ઇમ્ફાલ")
        "Shillong" -> mapOf("ta" to "ஷில்லாங்", "hi" to "शिलांग", "ar" to "شيلونغ", "kn" to "ಶಿಲ್ಲಾಂಗ್", "ml" to "ഷില്ലോങ്", "bn" to "শিলং", "mr" to "शिलॉन्ग", "gu" to "શિલોંગ")
        "Aizawl" -> mapOf("ta" to "அய்சால்", "hi" to "आइज़ोल", "ar" to "أيزاول", "kn" to "ಐಜ್ವಾಲ್", "ml" to "ഐസ്വാൾ", "bn" to "আইজল", "mr" to "ऐझॉल", "gu" to "આઇઝોલ")
        "Kohima" -> mapOf("ta" to "கோஹிமா", "hi" to "कोहिमा", "ar" to "كوهيما", "kn" to "ಕೊಹಿಮಾ", "ml" to "കോഹിമ", "bn" to "কোহিমা", "mr" to "कोहिमा", "gu" to "કોહિમા")
        "Bhubaneswar" -> mapOf("ta" to "புவனேசுவரம்", "hi" to "भुवनेश्वर", "ar" to "بوبانشوار", "kn" to "ಭುವನೇಶ್ವರ", "ml" to "ഭുവനേശ്വർ", "bn" to "ভুবনেশ্বর", "mr" to "भुवनेश्वर", "gu" to "ભુવનેશ્વર")
        "Jaipur" -> mapOf("ta" to "ஜெய்ப்பூர்", "hi" to "जयपुर", "ar" to "جايبور", "kn" to "ಜೈಪುರ", "ml" to "ജയ്പൂർ", "bn" to "জয়পুর", "mr" to "जयपूर", "gu" to "જયપુર")
        "Gangtok" -> mapOf("ta" to "கேங்டாக்", "hi" to "गंगटोक", "ar" to "غانغتوك", "kn" to "ಗ್ಯಾಂಗ್ಟಕ್", "ml" to "ഗാങ്‌ടോക്ക്", "bn" to "গ্যাংটক", "mr" to "गंगटोक", "gu" to "ગંગટોક")
        "Chennai" -> mapOf("ta" to "சென்னை", "hi" to "चेन्नई", "ar" to "تشيناي", "kn" to "ಚೆನ್ನೈ", "ml" to "ചെന്നൈ", "bn" to "চেন্নাই", "mr" to "चेन्नई", "gu" to "ચેન્નઈ")
        "Hyderabad" -> mapOf("ta" to "ஹைதராபாத்", "hi" to "हैदराबाद", "ar" to "حيدر آباد", "kn" to "ಹೈದರಾಬಾದ್", "ml" to "ഹൈദരാബാദ്", "bn" to "হায়দ্রাবাদ", "mr" to "हैदराबाद", "gu" to "હૈદરાબાદ")
        "Agartala" -> mapOf("ta" to "அகர்தலா", "hi" to "अगरतला", "ar" to "أغارتالا", "kn" to "ಅಗರ್ತಲಾ", "ml" to "അഗർത്തല", "bn" to "আগরতলা", "mr" to "अगरतळा", "gu" to "અગરતલા")
        "Lucknow" -> mapOf("ta" to "லக்னோ", "hi" to "लखनऊ", "ar" to "لكناو", "kn" to "ಲಕ್ನೋ", "ml" to "ലഖ്‌നൗ", "bn" to "লখনউ", "mr" to "लखनऊ", "gu" to "લખનૌ")
        "Dehradun" -> mapOf("ta" to "டேராடூன்", "hi" to "देहरादून", "ar" to "دهرادون", "kn" to "ಡೆಹ್ರಾಡೂನ್", "ml" to "ഡെഹ്‌റാഡൂൺ", "bn" to "দেরাদুন", "mr" to "डेहराडून", "gu" to "દહેરાદૂન")
        "Kolkata" -> mapOf("ta" to "கொல்கத்தா", "hi" to "कोलकाता", "ar" to "كولكاتا", "kn" to "ಕೋಲ್ಕತಾ", "ml" to "കൊൽക്കത്ത", "bn" to "কলকাতা", "mr" to "कोलकाता", "gu" to "કોલકાતા")
        "New Delhi (National Capital)", "New Delhi" -> mapOf("ta" to "புது தில்லி", "hi" to "नई दिल्ली", "ar" to "نيودلهي", "kn" to "ನವದೆಹಲಿ", "ml" to "ന്യൂഡൽഹി", "bn" to "নয়াদিল্লি", "mr" to "नवी दिल्ली", "gu" to "નવી દિલ્હી")
        else -> null
    }
    return map?.get(langCode)
}

private fun getStateCapitalSpeech(state: String, capital: String, langCode: String): String {
    return when (langCode) {
        "ta" -> "${state} தலைநகரம் ${capital}"
        "hi" -> "${state} की राजधानी ${capital} है"
        "ar" -> "عاصمة ${state} هي ${capital}"
        "kn" -> "${state} ರಾಜಧಾನಿ ${capital}"
        "ml" -> "${state}യുടെ തലസ്ഥാനം ${capital} ആണ്"
        "bn" -> "${state} এর রাজধানী হলো ${capital}"
        "mr" -> "${state} ची राजधानी ${capital} आहे"
        "gu" -> "${state} ની રાજધાની ${capital} છે"
        else -> "${state} capital is ${capital}"
    }
}
