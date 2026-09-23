package com.fahim.mad_lab_compose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fahim.mad_lab_compose.ui.AppScreen
import com.fahim.mad_lab_compose.ui.ChatScreen
import com.fahim.mad_lab_compose.ui.ChatViewModel
import com.fahim.mad_lab_compose.ui.ChatViewModelFactory
import com.fahim.mad_lab_compose.ui.MemoryScreen
import com.fahim.mad_lab_compose.ui.theme.GeminiChatbotTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val app = application as ChatbotApplication
            val chatViewModel: ChatViewModel = viewModel(
                factory = ChatViewModelFactory(app.repository, applicationContext)
            )

            GeminiChatbotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatbotApp(viewModel = chatViewModel)
                }
            }
        }
    }
}

@Composable
fun ChatbotApp(viewModel: ChatViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    val speechIntentLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.onInputTextChanged(spokenText)
            }
        }
    }

    val micPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {
                viewModel.startVoiceRecognition()
            } else {
                try {
                    val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak to Gemini Chatbot...")
                    }
                    speechIntentLauncher.launch(intent)
                } catch (e: Exception) {
                    viewModel.dismissError()
                    android.widget.Toast.makeText(context, "Voice input not available on this device", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            viewModel.dismissError()
            android.widget.Toast.makeText(
                context,
                "Microphone permission is required for voice input",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    val handleVoiceClick = {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (android.speech.SpeechRecognizer.isRecognitionAvailable(context)) {
                viewModel.startVoiceRecognition()
            } else {
                try {
                    val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak to Gemini Chatbot...")
                    }
                    speechIntentLauncher.launch(intent)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Voice input not available on this device", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle back button when on Memory screen
    BackHandler(enabled = state.currentScreen == AppScreen.MEMORY) {
        viewModel.navigateTo(AppScreen.CHAT)
    }

    Crossfade(
        targetState = state.currentScreen,
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.CHAT -> {
                ChatScreen(
                    state = state,
                    onInputTextChanged = { text -> viewModel.onInputTextChanged(text) },
                    onSendMessage = { explicit -> viewModel.sendMessage(explicit) },
                    onOpenMemories = { viewModel.navigateTo(AppScreen.MEMORY) },
                    onOpenApiKeyDialog = { viewModel.setShowApiKeyDialog(true) },
                    onShowClearChatDialog = { show -> viewModel.setShowClearChatDialog(show) },
                    onConfirmClearChat = { viewModel.clearChatHistory() },
                    onDismissError = { viewModel.dismissError() },
                    onSaveApiKey = { key -> viewModel.setApiKey(key) },
                    onShareConversation = { viewModel.shareConversation() },
                    onDismissShareDialog = { viewModel.dismissShareDialog() },
                    getFormattedConversation = { viewModel.getFormattedConversation() },
                    onStartVoiceRecognition = handleVoiceClick,
                    onStopVoiceRecognition = { viewModel.stopVoiceRecognition() },
                    onSpeakText = { text -> viewModel.speakText(text) },
                    onStopSpeaking = { viewModel.stopSpeaking() },
                    onGenerateSummary = { viewModel.generateSummary() },
                    onSaveSummary = { title -> viewModel.saveSummary(title) },
                    onDismissSummaryDialog = { viewModel.dismissSummaryDialog() }
                )
            }
            AppScreen.MEMORY -> {
                MemoryScreen(
                    state = state,
                    onBack = { viewModel.navigateTo(AppScreen.CHAT) },
                    onDeleteMemory = { mem -> viewModel.deleteMemory(mem) },
                    onAddMemory = { k, v -> viewModel.addOrUpdateMemory(k, v) },
                    onClearAllMemories = { viewModel.clearAllMemories() },
                    onShowAddDialog = { show -> viewModel.setShowAddMemoryDialog(show) },
                    onShowClearDialog = { show -> viewModel.setShowClearMemoriesDialog(show) }
                )
            }
            AppScreen.SUMMARIES -> {
                viewModel.navigateTo(AppScreen.CHAT)
            }
        }
    }
}