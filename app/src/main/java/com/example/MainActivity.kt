package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LearningScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.BadgesDialog
import com.example.ui.screens.SettingsDialog
import com.example.ui.components.VoiceChatDialog
import com.example.ui.components.MiniGamesPlayground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LearningViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: LearningViewModel = viewModel()
      val isDarkModePref by viewModel.isDarkMode.collectAsState()

      MyApplicationTheme(darkTheme = isDarkModePref) {
        val currentScreen by viewModel.currentScreen.collectAsState()

        var previousScreen by remember { mutableStateOf<Screen?>(null) }
        var exitCount by remember { mutableStateOf(0) }
        LaunchedEffect(currentScreen) {
          val prev = previousScreen
          if (prev is Screen.Learning && currentScreen is Screen.Dashboard) {
            exitCount++
            if (exitCount % 2 == 0) {
              com.example.data.AdMobHelper.showInterstitialAd(this@MainActivity) {
                android.util.Log.d("MainActivity", "Interstitial ad completed or failed")
              }
            }
          }
          previousScreen = currentScreen
        }

        Box(modifier = Modifier.fillMaxSize()) {
          when (currentScreen) {
            is Screen.Welcome -> WelcomeScreen(
              onLanguageSelected = { langCode -> viewModel.selectLanguage(langCode) },
              isDarkMode = isDarkModePref,
              onToggleDarkMode = { viewModel.toggleDarkMode() },
              viewModel = viewModel
            )
            is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
            is Screen.Learning -> LearningScreen(viewModel = viewModel)
            else -> WelcomeScreen(
              onLanguageSelected = { langCode -> viewModel.selectLanguage(langCode) },
              isDarkMode = isDarkModePref,
              onToggleDarkMode = { viewModel.toggleDarkMode() },
              viewModel = viewModel
            )
          }

          // Global Dialog Overlays for persistent Bottom Navigation Bar
          val showVoiceChat by viewModel.showVoiceChatGlobal.collectAsState()
          val showMiniGames by viewModel.showMiniGamesGlobal.collectAsState()
          val showBadges by viewModel.showBadgesGlobal.collectAsState()
          val showSettings by viewModel.showSettingsGlobal.collectAsState()

          if (showVoiceChat) {
            VoiceChatDialog(
              viewModel = viewModel,
              onDismiss = { viewModel.setShowVoiceChat(false) }
            )
          }

          if (showMiniGames) {
            MiniGamesPlayground(
              viewModel = viewModel,
              onDismiss = { viewModel.setShowMiniGames(false) }
            )
          }

          if (showBadges) {
            val badges by viewModel.badges.collectAsState()
            BadgesDialog(
              badges = badges,
              onDismiss = { viewModel.setShowBadges(false) }
            )
          }

          if (showSettings) {
            SettingsDialog(
              viewModel = viewModel,
              onDismiss = { viewModel.setShowSettings(false) }
            )
          }
        }
      }
    }
  }
}
