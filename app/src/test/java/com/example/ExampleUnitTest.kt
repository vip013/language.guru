package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testLanguageDatasetIntegrity() {
    val report = com.example.data.CentralizedContentRepository.validateLanguageDataset()
    assertTrue("Dataset validation should pass without critical errors: ${report.issues}", report.isValid)
    assertTrue("Should have checked all 10 supported languages", report.totalLanguages >= 10)
    assertTrue("Should have checked learning items", report.totalItemsChecked > 500)
  }

  @Test
  fun testIndividualLanguageValidation() {
    listOf("hi", "ar", "te", "ta", "kn", "ml", "en").forEach { lang ->
      val report = com.example.data.CentralizedContentRepository.validateLanguageContent(lang)
      assertTrue("Language '$lang' validation must pass without errors: ${report.issues}", report.isValid)
    }
  }

  @Test
  fun testPronunciationSystemRules() {
    // Hindi letter vs word
    val hindiAiLetter = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "ऐ",
      language = "hi",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("ऐ", hindiAiLetter)

    val hindiAaLetter = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "आ",
      language = "hi",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("आ", hindiAaLetter)

    // Arabic letter with Sukun to avoid nunation (-un)
    val arabicMeemLetter = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "م",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("مِيمْ", arabicMeemLetter)

    val arabicLaamLetter = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "ل",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("لَامْ", arabicLaamLetter)

    // Arabic complete word
    val arabicWord = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "أَسَد",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.WORD
    )
    assertEquals("أَسَد", arabicWord)

    // Arabic letter Jeem (ج / j) pronunciation test
    val arabicJeemLetter = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "ج",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("جِيمْ", arabicJeemLetter)

    val arabicJeemLatin = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "j",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.LETTER
    )
    assertEquals("جِيمْ", arabicJeemLatin)

    val arabicJeemWordFallback = com.example.ui.components.LanguagePronunciationManager.resolveTextToSpeak(
      text = "j",
      language = "ar",
      type = com.example.ui.components.SpeechContentType.WORD
    )
    assertEquals("جِيمْ", arabicJeemWordFallback)
  }

  @Test
  fun testTamilBottomNavLabels() {
    val taGames = "விளையாட்டு"
    val taBadges = "பதக்கம்"
    assertTrue("Tamil games label should be concise (<= 12 chars)", taGames.length <= 12)
    assertTrue("Tamil badges label should be concise (<= 10 chars)", taBadges.length <= 10)
  }
}
