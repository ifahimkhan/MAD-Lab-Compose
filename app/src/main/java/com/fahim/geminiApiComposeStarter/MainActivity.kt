package com.fahim.geminiApiComposeStarter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fahim.geminiApiComposeStarter.data.GeminiRepositoryImpl
import com.fahim.geminiApiComposeStarter.data.SecureKeyStore
import com.fahim.geminiApiComposeStarter.ui.chat.ChatRoute
import com.fahim.geminiApiComposeStarter.ui.chat.ChatViewModel
import com.fahim.geminiApiComposeStarter.ui.chat.ThemeMode
import com.fahim.geminiApiComposeStarter.ui.theme.GeminiApiComposeStarterTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by viewModels {
        val secureKeyStore = SecureKeyStore(applicationContext)

        // First launch: no encrypted key saved yet, so migrate the one from BuildConfig.
        if (!secureKeyStore.hasApiKey() && BuildConfig.GEMINI_API_KEY.isNotBlank()) {
            secureKeyStore.saveApiKey(BuildConfig.GEMINI_API_KEY)
        }

        val apiKey = secureKeyStore.getApiKey().orEmpty()
        val database = com.fahim.geminiApiComposeStarter.data.AppDatabase.getInstance(applicationContext)

        ChatViewModel.factory(
            repository = GeminiRepositoryImpl(apiKey = apiKey),
            hasApiKey = apiKey.isNotBlank(),
            chatMessageDao = database.chatMessageDao(),
        )
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = when (state.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            GeminiApiComposeStarterTheme(darkTheme = darkTheme) {
                val windowSizeClass = androidx.compose.material3.windowsizeclass.calculateWindowSizeClass(this)
                ChatRoute(viewModel = viewModel, windowWidthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}
