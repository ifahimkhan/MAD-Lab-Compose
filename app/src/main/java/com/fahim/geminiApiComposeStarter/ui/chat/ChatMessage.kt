package com.fahim.geminiApiComposeStarter.ui.chat
/** A single message in the conversation, either from the user or from Gemini. */
data class ChatMessage(
    val id: Long,
    val text: String,
    val isFromUser: Boolean,
)