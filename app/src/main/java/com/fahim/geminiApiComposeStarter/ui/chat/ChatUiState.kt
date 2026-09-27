package com.fahim.geminiApiComposeStarter.ui.chat

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

/** Immutable UI state for the chat screen. */
data class ChatUiState(
    val prompt: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val promptError: PromptError? = null,
    val errorMessage: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

enum class PromptError { EMPTY }
