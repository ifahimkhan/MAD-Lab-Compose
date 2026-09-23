package com.fahim.mad_lab_compose.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fahim.mad_lab_compose.data.database.MemoryEntity
import com.fahim.mad_lab_compose.data.database.SummaryEntity
import com.fahim.mad_lab_compose.data.repository.ChatRepository
import com.fahim.mad_lab_compose.data.repository.ResultState
import com.fahim.mad_lab_compose.voice.VoiceHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository,
    private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatState(isApiKeyConfigured = repository.isApiKeyConfigured())
    )
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    private var voiceHelper: VoiceHelper? = null

    init {
        observeMessages()
        observeMemories()
        observeSummaries()
        initializeVoiceHelper()
    }

    private fun initializeVoiceHelper() {
        voiceHelper = VoiceHelper(context)
        viewModelScope.launch {
            voiceHelper?.recognitionState?.collect { state ->
                when (state) {
                    is com.fahim.mad_lab_compose.voice.RecognitionState.Success -> {
                        _uiState.update {
                            it.copy(
                                voiceRecognitionState = state,
                                inputText = state.text
                            )
                        }
                    }
                    is com.fahim.mad_lab_compose.voice.RecognitionState.Partial -> {
                        _uiState.update {
                            it.copy(
                                voiceRecognitionState = state,
                                inputText = state.text
                            )
                        }
                    }
                    is com.fahim.mad_lab_compose.voice.RecognitionState.Error -> {
                        _uiState.update {
                            it.copy(
                                voiceRecognitionState = state,
                                errorMessage = state.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update { it.copy(voiceRecognitionState = state) }
                    }
                }
            }
        }
        viewModelScope.launch {
            voiceHelper?.ttsState?.collect { state ->
                _uiState.update { it.copy(ttsState = state) }
            }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            repository.getAllMessages()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = "Failed to load messages: ${e.message}") }
                }
                .collect { messagesList ->
                    _uiState.update { it.copy(messages = messagesList) }
                }
        }
    }

    private fun observeMemories() {
        viewModelScope.launch {
            repository.getAllMemories()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = "Failed to load memories: ${e.message}") }
                }
                .collect { memoriesList ->
                    _uiState.update { it.copy(memories = memoriesList) }
                }
        }
    }

    private fun observeSummaries() {
        viewModelScope.launch {
            repository.getAllSummaries()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = "Failed to load summaries: ${e.message}") }
                }
                .collect { summariesList ->
                    _uiState.update { it.copy(summaries = summariesList) }
                }
        }
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(explicitText: String? = null) {
        val messageToSend = (explicitText ?: _uiState.value.inputText).trim()
        if (messageToSend.isBlank() || _uiState.value.isLoading) return

        // Clear input text and set loading
        _uiState.update {
            it.copy(
                inputText = "",
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            when (val result = repository.sendMessage(messageToSend)) {
                is ResultState.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isApiKeyConfigured = repository.isApiKeyConfigured()
                        )
                    }
                }
            }
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            repository.deleteMemory(memory)
        }
    }

    fun deleteMemoryById(id: Long) {
        viewModelScope.launch {
            repository.deleteMemoryById(id)
        }
    }

    fun addOrUpdateMemory(key: String, value: String) {
        val trimmedKey = key.trim()
        val trimmedValue = value.trim()
        if (trimmedKey.isBlank() || trimmedValue.isBlank()) return

        viewModelScope.launch {
            repository.saveOrUpdateMemory(trimmedKey, trimmedValue)
            setShowAddMemoryDialog(false)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            repository.clearAllMemories()
            setShowClearMemoriesDialog(false)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
            setShowClearChatDialog(false)
        }
    }

    fun setApiKey(apiKey: String) {
        repository.setCustomApiKey(apiKey)
        _uiState.update {
            it.copy(
                isApiKeyConfigured = repository.isApiKeyConfigured(),
                showApiKeyDialog = false,
                errorMessage = null
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun setShowApiKeyDialog(show: Boolean) {
        _uiState.update { it.copy(showApiKeyDialog = show) }
    }

    fun setShowAddMemoryDialog(show: Boolean) {
        _uiState.update { it.copy(showAddMemoryDialog = show) }
    }

    fun setShowClearChatDialog(show: Boolean) {
        _uiState.update { it.copy(showClearChatDialog = show) }
    }

    fun setShowClearMemoriesDialog(show: Boolean) {
        _uiState.update { it.copy(showClearMemoriesDialog = show) }
    }

    // Summary Methods
    fun generateSummary() {
        if (_uiState.value.messages.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "No conversation to summarize") }
            return
        }

        _uiState.update { it.copy(isGeneratingSummary = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = repository.generateSummary()) {
                is ResultState.Success -> {
                    _uiState.update {
                        it.copy(
                            isGeneratingSummary = false,
                            currentSummary = result.data,
                            showSummaryDialog = true
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiState.update {
                        it.copy(
                            isGeneratingSummary = false,
                            errorMessage = result.message,
                            isApiKeyConfigured = repository.isApiKeyConfigured()
                        )
                    }
                }
            }
        }
    }

    fun saveSummary(title: String) {
        val summary = _uiState.value.currentSummary
        if (summary.isNullOrBlank()) return

        viewModelScope.launch {
            repository.saveSummary(title, summary)
            _uiState.update {
                it.copy(
                    currentSummary = null,
                    showSummaryDialog = false
                )
            }
        }
    }

    fun dismissSummaryDialog() {
        _uiState.update {
            it.copy(
                currentSummary = null,
                showSummaryDialog = false,
                isGeneratingSummary = false
            )
        }
    }

    fun deleteSummary(summary: SummaryEntity) {
        viewModelScope.launch {
            repository.deleteSummary(summary)
        }
    }

    fun deleteSummaryById(id: Long) {
        viewModelScope.launch {
            repository.deleteSummaryById(id)
        }
    }

    fun clearAllSummaries() {
        viewModelScope.launch {
            repository.clearAllSummaries()
        }
    }

    // Share Methods
    fun shareConversation() {
        if (_uiState.value.messages.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "No conversation to share") }
            return
        }
        _uiState.update { it.copy(showShareDialog = true) }
    }

    fun dismissShareDialog() {
        _uiState.update { it.copy(showShareDialog = false) }
    }

    fun getFormattedConversation(): String {
        val messages = _uiState.value.messages
        val sb = StringBuilder()
        sb.append("MemoryBot Conversation\n")
        sb.append("Generated on ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())}\n\n")
        
        messages.forEach { message ->
            val sender = if (message.sender == "user") "User" else "Bot"
            sb.append("$sender:\n")
            sb.append("${message.message}\n\n")
        }
        
        return sb.toString()
    }

    // Voice Recognition Methods
    fun startVoiceRecognition() {
        voiceHelper?.startListening()
    }

    fun stopVoiceRecognition() {
        voiceHelper?.stopListening()
    }

    fun speakText(text: String) {
        voiceHelper?.speak(text)
    }

    fun stopSpeaking() {
        voiceHelper?.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        voiceHelper?.release()
    }
}

class ChatViewModelFactory(
    private val repository: ChatRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}