package com.fahim.mad_lab_compose.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fahim.mad_lab_compose.data.database.MessageEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: ChatState,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: (String?) -> Unit,
    onOpenMemories: () -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    onShowClearChatDialog: (Boolean) -> Unit,
    onConfirmClearChat: () -> Unit,
    onDismissError: () -> Unit,
    onSaveApiKey: (String) -> Unit,
    onShareConversation: () -> Unit,
    onDismissShareDialog: () -> Unit,
    getFormattedConversation: () -> String,
    onStartVoiceRecognition: () -> Unit,
    onStopVoiceRecognition: () -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    onGenerateSummary: () -> Unit,
    onSaveSummary: (String) -> Unit,
    onDismissSummaryDialog: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Auto-scroll to bottom on new messages or loading state
    LaunchedEffect(state.messages.size, state.isLoading) {
        if (state.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(state.messages.size)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "Bot Avatar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gemini Chatbot",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (state.isApiKeyConfigured) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    modifier = Modifier.size(7.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (state.isApiKeyConfigured) "Memory Enabled" else "API Key Needed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Share Button
                    if (state.messages.isNotEmpty()) {
                        IconButton(onClick = onShareConversation) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Chat"
                            )
                        }
                    }

                    // Memory Button with Badge
                    IconButton(onClick = onOpenMemories) {
                        BadgedBox(
                            badge = {
                                if (state.memories.isNotEmpty()) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text("${state.memories.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "Memories",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Settings / API Key Button
                    IconButton(onClick = onOpenApiKeyDialog) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key Settings"
                        )
                    }

                    // Clear Chat History Button
                    if (state.messages.isNotEmpty()) {
                        IconButton(onClick = { onShowClearChatDialog(true) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Chat"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = state.inputText,
                isLoading = state.isLoading,
                voiceRecognitionState = state.voiceRecognitionState,
                onInputTextChanged = onInputTextChanged,
                onSendMessage = { onSendMessage(null) },
                onQuickPromptClicked = { prompt ->
                    onSendMessage(prompt)
                },
                onStartVoiceRecognition = onStartVoiceRecognition,
                onStopVoiceRecognition = onStopVoiceRecognition
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error Banner
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                state.errorMessage?.let { error ->
                    ErrorBanner(
                        errorMessage = error,
                        onDismiss = onDismissError,
                        onConfigureApiKey = {
                            onDismissError()
                            onOpenApiKeyDialog()
                        }
                    )
                }
            }

            // Message List or Welcome Screen
            if (state.messages.isEmpty()) {
                WelcomeScreen(
                    onQuickPromptClick = { prompt ->
                        onSendMessage(prompt)
                    },
                    onOpenMemories = onOpenMemories,
                    memoryCount = state.memories.size
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.messages,
                        key = { it.id }
                    ) { message ->
                        ChatMessageBubble(
                            message = message,
                            onCopyText = { text ->
                                copyToClipboard(context, text)
                            },
                            onSpeakText = onSpeakText,
                            onStopSpeaking = onStopSpeaking,
                            ttsState = state.ttsState
                        )
                    }

                    // Loading Bubble
                    if (state.isLoading) {
                        item(key = "loading_bubble") {
                            LoadingBotBubble()
                        }
                    }
                }
            }
        }
    }

    // Clear Chat Dialog
    if (state.showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { onShowClearChatDialog(false) },
            title = { Text("Clear Chat History?") },
            text = {
                Text("This will clear the conversation messages. Your saved memories in the Room database will NOT be deleted.")
            },
            confirmButton = {
                Button(
                    onClick = onConfirmClearChat,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { onShowClearChatDialog(false) }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // API Key Dialog
    if (state.showApiKeyDialog) {
        ApiKeyDialog(
            onDismiss = { onOpenApiKeyDialog() },
            onSaveKey = { key ->
                onSaveApiKey(key)
            }
        )
    }

    // Summary Dialog
    if (state.showSummaryDialog) {
        SummaryDialog(
            summary = state.currentSummary,
            isGenerating = state.isGeneratingSummary,
            onSave = { title -> onSaveSummary(title) },
            onDismiss = onDismissSummaryDialog
        )
    }

    // Share Dialog
    if (state.showShareDialog) {
        ShareDialog(
            onDismiss = onDismissShareDialog,
            getFormattedConversation = getFormattedConversation
        )
    }
}

@Composable
fun WelcomeScreen(
    onQuickPromptClick: (String) -> Unit,
    onOpenMemories: () -> Unit,
    memoryCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome to Gemini Chatbot!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "I remember your personal details across sessions using Room Database.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (memoryCount > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenMemories() },
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$memoryCount memory facts remembered",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Try these prompts:",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        val samplePrompts = listOf(
            "My name is Charmy.",
            "I study Computer Engineering at NMIMS.",
            "What is my name?",
            "Where do I study?",
            "My favourite subject is Operating Systems.",
            "What should I focus on improving?"
        )

        samplePrompts.forEach { prompt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onQuickPromptClick(prompt) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: MessageEntity,
    onCopyText: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onStopSpeaking: () -> Unit,
    ttsState: com.fahim.mad_lab_compose.voice.TTSState
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Bot",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                color = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                },
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )

                    if (!isUser) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { onSpeakText(message.message) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (ttsState is com.fahim.mad_lab_compose.voice.TTSState.Speaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = if (ttsState is com.fahim.mad_lab_compose.voice.TTSState.Speaking) "Stop speaking" else "Speak message",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            IconButton(
                                onClick = { onCopyText(message.message) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = formatMessageTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .padding(bottom = 2.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingBotBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AnimatedDot(delayMs = 0)
                AnimatedDot(delayMs = 200)
                AnimatedDot(delayMs = 400)
            }
        }
    }
}

@Composable
fun AnimatedDot(delayMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = delayMs),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Surface(
        modifier = Modifier
            .size(8.dp)
            .scale(scale),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary
    ) {}
}

@Composable
fun ErrorBanner(
    errorMessage: String,
    onDismiss: () -> Unit,
    onConfigureApiKey: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                if (errorMessage.contains("API key", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to configure API key",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.clickable { onConfigureApiKey() }
                    )
                }
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    inputText: String,
    isLoading: Boolean,
    voiceRecognitionState: com.fahim.mad_lab_compose.voice.RecognitionState,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickPromptClicked: (String) -> Unit,
    onStartVoiceRecognition: () -> Unit,
    onStopVoiceRecognition: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            // Quick suggestions chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                val chips = listOf(
                    "My name is Charmy",
                    "What is my name?",
                    "Where do I study?",
                    "What are my goals?"
                )
                items(chips) { chipText ->
                    FilterChip(
                        selected = false,
                        onClick = { onQuickPromptClicked(chipText) },
                        label = { Text(chipText, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputTextChanged,
                    placeholder = {
                        Text(
                            when (voiceRecognitionState) {
                                is com.fahim.mad_lab_compose.voice.RecognitionState.Listening -> "Listening..."
                                is com.fahim.mad_lab_compose.voice.RecognitionState.Partial -> voiceRecognitionState.text
                                else -> "Ask anything or share a fact..."
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (voiceRecognitionState is com.fahim.mad_lab_compose.voice.RecognitionState.Listening) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Microphone Button
                IconButton(
                    onClick = {
                        when (voiceRecognitionState) {
                            is com.fahim.mad_lab_compose.voice.RecognitionState.Listening -> onStopVoiceRecognition()
                            else -> onStartVoiceRecognition()
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (voiceRecognitionState is com.fahim.mad_lab_compose.voice.RecognitionState.Listening) {
                            Icons.Default.Stop
                        } else {
                            Icons.Default.Mic
                        },
                        contentDescription = if (voiceRecognitionState is com.fahim.mad_lab_compose.voice.RecognitionState.Listening) "Stop" else "Microphone",
                        tint = if (voiceRecognitionState is com.fahim.mad_lab_compose.voice.RecognitionState.Listening) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }

                IconButton(
                    onClick = onSendMessage,
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isLoading) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerHighest
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isLoading) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Chat Message", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}

@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    getFormattedConversation: () -> String
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Share Conversation") },
        text = {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    "Share this conversation with another app:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        androidx.compose.foundation.lazy.LazyColumn {
                            items(
                                getFormattedConversation().lines().take(10).toList()
                            ) { line ->
                                Text(
                                    text = line,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val conversationText = getFormattedConversation()
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(android.content.Intent.EXTRA_TEXT, conversationText)
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "MemoryBot Conversation")
                    }
                    val chooser = android.content.Intent.createChooser(shareIntent, "Share Conversation")
                    context.startActivity(chooser)
                    onDismiss()
                }
            ) {
                Text("Share")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun SummaryDialog(
    summary: String?,
    isGenerating: Boolean,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conversation Summary") },
        text = {
            if (isGenerating) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Generating summary...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Summary Title") },
                        placeholder = { Text("e.g., Study Discussion — 23 Sep 2026") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            androidx.compose.foundation.lazy.LazyColumn {
                                items(
                                    summary?.lines() ?: emptyList()
                                ) { line ->
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isGenerating && summary != null) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSave(title)
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Save Summary")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isGenerating) "Cancel" else "Discard")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Interactive Chat Preview (Testing Mode)")
@Composable
fun InteractiveChatPreview() {
    var previewState by remember {
        mutableStateOf(
            ChatState(
                isApiKeyConfigured = true,
                memories = listOf(
                    com.fahim.mad_lab_compose.data.database.MemoryEntity(id = 1, key = "name", value = "Charmy"),
                    com.fahim.mad_lab_compose.data.database.MemoryEntity(id = 2, key = "college", value = "NMIMS")
                )
            )
        )
    }

    com.fahim.mad_lab_compose.ui.theme.GeminiChatbotTheme {
        ChatScreen(
            state = previewState,
            onInputTextChanged = { text ->
                previewState = previewState.copy(inputText = text)
            },
            onSendMessage = { explicitText ->
                val text = (explicitText ?: previewState.inputText).trim()
                if (text.isNotBlank()) {
                    val userMsg = MessageEntity(
                        id = System.currentTimeMillis(),
                        sender = "user",
                        message = text,
                        timestamp = System.currentTimeMillis()
                    )
                    val botReply = MessageEntity(
                        id = System.currentTimeMillis() + 1,
                        sender = "bot",
                        message = "I remember you! You said: \"$text\"",
                        timestamp = System.currentTimeMillis() + 1000
                    )
                    previewState = previewState.copy(
                        inputText = "",
                        messages = previewState.messages + userMsg + botReply
                    )
                }
            },
            onOpenMemories = {},
            onOpenApiKeyDialog = {
                previewState = previewState.copy(showApiKeyDialog = true)
            },
            onShowClearChatDialog = { show ->
                previewState = previewState.copy(showClearChatDialog = show)
            },
            onConfirmClearChat = {
                previewState = previewState.copy(messages = emptyList(), showClearChatDialog = false)
            },
            onDismissError = {
                previewState = previewState.copy(errorMessage = null)
            },
            onSaveApiKey = {
                previewState = previewState.copy(isApiKeyConfigured = true, showApiKeyDialog = false)
            },
            onShareConversation = {
                previewState = previewState.copy(showShareDialog = true)
            },
            onDismissShareDialog = {
                previewState = previewState.copy(showShareDialog = false)
            },
            getFormattedConversation = { "User: Hello\nBot: Hi Charmy!" },
            onStartVoiceRecognition = {
                previewState = previewState.copy(
                    voiceRecognitionState = com.fahim.mad_lab_compose.voice.RecognitionState.Listening,
                    inputText = "Listening... speak now"
                )
            },
            onStopVoiceRecognition = {
                previewState = previewState.copy(
                    voiceRecognitionState = com.fahim.mad_lab_compose.voice.RecognitionState.Idle,
                    inputText = "My name is Charmy"
                )
            },
            onSpeakText = {},
            onStopSpeaking = {},
            onGenerateSummary = {
                previewState = previewState.copy(
                    showSummaryDialog = true,
                    currentSummary = "• User shared their name is Charmy\n• User studies at NMIMS"
                )
            },
            onSaveSummary = {
                previewState = previewState.copy(showSummaryDialog = false)
            },
            onDismissSummaryDialog = {
                previewState = previewState.copy(showSummaryDialog = false)
            }
        )
    }
}
