package com.fahim.mad_lab_compose.data.repository

import com.fahim.mad_lab_compose.BuildConfig
import com.fahim.mad_lab_compose.data.database.MemoryDao
import com.fahim.mad_lab_compose.data.database.MemoryEntity
import com.fahim.mad_lab_compose.data.database.MessageDao
import com.fahim.mad_lab_compose.data.database.MessageEntity
import com.fahim.mad_lab_compose.data.database.SummaryDao
import com.fahim.mad_lab_compose.data.database.SummaryEntity
import com.fahim.mad_lab_compose.data.memory.MemoryExtractor
import com.fahim.mad_lab_compose.network.ApiClient
import com.fahim.mad_lab_compose.network.GeminiApiService
import com.fahim.mad_lab_compose.network.GeminiContent
import com.fahim.mad_lab_compose.network.GeminiGenerationConfig
import com.fahim.mad_lab_compose.network.GeminiPart
import com.fahim.mad_lab_compose.network.GeminiRequest
import com.fahim.mad_lab_compose.network.GeminiSystemInstruction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String, val isApiKeyError: Boolean = false) : ResultState<Nothing>()
}

class ChatRepository(
    private val memoryDao: MemoryDao,
    private val messageDao: MessageDao,
    private val summaryDao: SummaryDao,
    private val geminiService: GeminiApiService = ApiClient.geminiService
) {

    // Dynamic runtime API key (fallback or override if configured in app)
    private var customApiKey: String? = null

    fun setCustomApiKey(key: String) {
        customApiKey = key.trim()
    }

    fun getEffectiveApiKey(): String {
        val custom = customApiKey
        if (!custom.isNullOrBlank()) return custom
        return BuildConfig.GEMINI_API_KEY.trim()
    }

    fun isApiKeyConfigured(): Boolean {
        return getEffectiveApiKey().isNotBlank()
    }

    // --- Room Database Observers ---
    fun getAllMessages(conversationId: String = "default"): Flow<List<MessageEntity>> {
        return messageDao.getAllMessages(conversationId)
    }

    fun getAllMemories(): Flow<List<MemoryEntity>> {
        return memoryDao.getAllMemories()
    }

    fun getAllSummaries(): Flow<List<SummaryEntity>> {
        return summaryDao.getAllSummaries()
    }

    // --- Memory Operations ---
    suspend fun saveOrUpdateMemory(key: String, value: String) = withContext(Dispatchers.IO) {
        memoryDao.upsertMemoryByKey(key, value)
    }

    suspend fun deleteMemory(memory: MemoryEntity) = withContext(Dispatchers.IO) {
        memoryDao.deleteMemory(memory)
    }

    suspend fun deleteMemoryById(id: Long) = withContext(Dispatchers.IO) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun clearAllMemories() = withContext(Dispatchers.IO) {
        memoryDao.deleteAllMemories()
    }

    suspend fun clearChatHistory(conversationId: String = "default") = withContext(Dispatchers.IO) {
        messageDao.deleteAllMessages(conversationId)
    }

    // --- Summary Operations ---
    suspend fun saveSummary(title: String, summary: String) = withContext(Dispatchers.IO) {
        val summaryEntity = SummaryEntity(
            title = title,
            summary = summary,
            createdAt = System.currentTimeMillis()
        )
        summaryDao.insertSummary(summaryEntity)
    }

    suspend fun deleteSummary(summary: SummaryEntity) = withContext(Dispatchers.IO) {
        summaryDao.deleteSummaryById(summary.id)
    }

    suspend fun deleteSummaryById(id: Long) = withContext(Dispatchers.IO) {
        summaryDao.deleteSummaryById(id)
    }

    suspend fun clearAllSummaries() = withContext(Dispatchers.IO) {
        summaryDao.deleteAllSummaries()
    }

    /**
     * Generate a summary of the current conversation using Gemini.
     */
    suspend fun generateSummary(conversationId: String = "default"): ResultState<String> = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            return@withContext ResultState.Error(
                message = "Gemini API key is not configured. Please add it to local.properties (GEMINI_API_KEY=your_key) or set it in the Settings menu.",
                isApiKeyError = true
            )
        }

        try {
            // Fetch recent messages for summary
            val recentMessages = messageDao.getRecentMessages(conversationId, limit = 20)
            if (recentMessages.isEmpty()) {
                return@withContext ResultState.Error("No conversation to summarize")
            }

            // Build conversation text for Gemini
            val conversationText = recentMessages.joinToString("\n") { msg ->
                "${msg.sender.uppercase()}: ${msg.message}"
            }

            val summaryPrompt = """
                Please analyze the following conversation and provide a concise summary. 
                Focus on the key points, decisions, and important information discussed.
                Format the summary with bullet points for clarity.
                
                Conversation:
                $conversationText
                
                Please provide a structured summary.
            """.trimIndent()

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = summaryPrompt))
                    )
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.3f,
                    maxOutputTokens = 1024
                )
            )

            val response = geminiService.generateContent(
                model = ApiClient.DEFAULT_MODEL,
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val body = response.body()
                val candidateText = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!candidateText.isNullOrBlank()) {
                    val cleanSummary = candidateText.trim()
                    return@withContext ResultState.Success(cleanSummary)
                } else {
                    return@withContext ResultState.Error("Received empty response from Gemini API.")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val code = response.code()
                val errorMessage = when (code) {
                    400 -> {
                        if (errorBody?.contains("API_KEY_INVALID", ignoreCase = true) == true ||
                            errorBody?.contains("API key not valid", ignoreCase = true) == true
                        ) {
                            "Invalid Gemini API Key. Please verify your API key."
                        } else {
                            "Bad Request (400): ${parseErrorDetails(errorBody)}"
                        }
                    }
                    403 -> "Access forbidden (403): Please check your Gemini API key permissions."
                    429 -> "Gemini API rate limit exceeded (429). Please wait a few seconds and try again."
                    500, 503 -> "Gemini server is temporarily unavailable. Please try again later."
                    else -> "API Error ($code): ${parseErrorDetails(errorBody)}"
                }

                val isApiKeyErr = code == 400 && (errorBody?.contains("API_KEY", ignoreCase = true) == true) || code == 403
                return@withContext ResultState.Error(errorMessage, isApiKeyError = isApiKeyErr)
            }

        } catch (e: UnknownHostException) {
            return@withContext ResultState.Error("No internet connection. Please check your network and try again.")
        } catch (e: SocketTimeoutException) {
            return@withContext ResultState.Error("Connection timed out. Gemini API took too long to respond.")
        } catch (e: IOException) {
            return@withContext ResultState.Error("Network error: ${e.localizedMessage ?: "Failed to connect"}")
        } catch (e: Exception) {
            return@withContext ResultState.Error("An unexpected error occurred: ${e.localizedMessage ?: e.javaClass.simpleName}")
        }
    }

    /**
     * Main conversation pipeline:
     * 1. Extract and save memories from the user message into Room.
     * 2. Save user message in Room history.
     * 3. Fetch stored memories from Room.
     * 4. Build prompt + memory context and call Gemini API.
     * 5. Save bot response in Room history.
     */
    suspend fun sendMessage(
        userMessageText: String,
        conversationId: String = "default"
    ): ResultState<String> = withContext(Dispatchers.IO) {
        val trimmedMessage = userMessageText.trim()
        if (trimmedMessage.isBlank()) {
            return@withContext ResultState.Error("Message cannot be empty")
        }

        val apiKey = getEffectiveApiKey()
        if (apiKey.isBlank()) {
            return@withContext ResultState.Error(
                message = "Gemini API key is not configured. Please add it to local.properties (GEMINI_API_KEY=your_key) or set it in the Settings menu.",
                isApiKeyError = true
            )
        }

        try {
            // Step 1: Extract memory and save to Room
            val extractedMemories = MemoryExtractor.extractMemories(trimmedMessage)
            for (mem in extractedMemories) {
                memoryDao.upsertMemoryByKey(mem.key, mem.value)
            }

            // Step 2: Save user message in database
            val userMsgEntity = MessageEntity(
                conversationId = conversationId,
                sender = "user",
                message = trimmedMessage,
                timestamp = System.currentTimeMillis()
            )
            messageDao.insertMessage(userMsgEntity)

            // Step 3: Fetch updated memories from Room database
            val storedMemories = memoryDao.getAllMemoriesSync()

            // Step 4: Build System Instruction with Memory Context
            val systemInstructionText = buildSystemInstruction(storedMemories)

            // Build request contents (including recent conversation for short-term flow)
            val recentHistory = messageDao.getRecentMessages(conversationId, limit = 8).reversed()
            val contentsList = mutableListOf<GeminiContent>()

            // Add recent messages to provide conversational flow
            for (msg in recentHistory) {
                val role = if (msg.sender == "user") "user" else "model"
                contentsList.add(
                    GeminiContent(
                        role = role,
                        parts = listOf(GeminiPart(text = msg.message))
                    )
                )
            }

            // If recentHistory didn't include the current message yet, ensure it is added
            if (contentsList.isEmpty() || contentsList.last().role != "user" || contentsList.last().parts.firstOrNull()?.text != trimmedMessage) {
                contentsList.add(
                    GeminiContent(
                        role = "user",
                        parts = listOf(GeminiPart(text = trimmedMessage))
                    )
                )
            }

            val request = GeminiRequest(
                contents = contentsList,
                systemInstruction = GeminiSystemInstruction(
                    parts = listOf(GeminiPart(text = systemInstructionText))
                ),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    maxOutputTokens = 2048
                )
            )

            // Step 5: Call Gemini API via Retrofit
            val response = geminiService.generateContent(
                model = ApiClient.DEFAULT_MODEL,
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val body = response.body()
                val candidateText = body?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (!candidateText.isNullOrBlank()) {
                    val cleanResponse = candidateText.trim()

                    // Step 6: Save bot response in database
                    val botMsgEntity = MessageEntity(
                        conversationId = conversationId,
                        sender = "bot",
                        message = cleanResponse,
                        timestamp = System.currentTimeMillis()
                    )
                    messageDao.insertMessage(botMsgEntity)

                    return@withContext ResultState.Success(cleanResponse)
                } else {
                    val blockReason = body?.promptFeedback?.blockReason
                    val errorMsg = if (blockReason != null) {
                        "Response was blocked by safety filters: $blockReason"
                    } else {
                        "Received empty response from Gemini API."
                    }
                    return@withContext ResultState.Error(errorMsg)
                }
            } else {
                // Parse API error
                val errorBody = response.errorBody()?.string()
                val code = response.code()

                val errorMessage = when (code) {
                    400 -> {
                        if (errorBody?.contains("API_KEY_INVALID", ignoreCase = true) == true ||
                            errorBody?.contains("API key not valid", ignoreCase = true) == true
                        ) {
                            "Invalid Gemini API Key. Please verify your API key."
                        } else {
                            "Bad Request (400): ${parseErrorDetails(errorBody)}"
                        }
                    }
                    403 -> "Access forbidden (403): Please check your Gemini API key permissions."
                    429 -> "Gemini API rate limit exceeded (429). Please wait a few seconds and try again."
                    500, 503 -> "Gemini server is temporarily unavailable. Please try again later."
                    else -> "API Error ($code): ${parseErrorDetails(errorBody)}"
                }

                val isApiKeyErr = code == 400 && (errorBody?.contains("API_KEY", ignoreCase = true) == true) || code == 403
                return@withContext ResultState.Error(errorMessage, isApiKeyError = isApiKeyErr)
            }

        } catch (e: UnknownHostException) {
            return@withContext ResultState.Error("No internet connection. Please check your network and try again.")
        } catch (e: SocketTimeoutException) {
            return@withContext ResultState.Error("Connection timed out. Gemini API took too long to respond.")
        } catch (e: IOException) {
            return@withContext ResultState.Error("Network error: ${e.localizedMessage ?: "Failed to connect"}")
        } catch (e: Exception) {
            return@withContext ResultState.Error("An unexpected error occurred: ${e.localizedMessage ?: e.javaClass.simpleName}")
        }
    }

    /**
     * Constructs system instructions integrating persistent Room memories.
     */
    private fun buildSystemInstruction(memories: List<MemoryEntity>): String {
        val memoryBuilder = StringBuilder()
        memoryBuilder.append("You are a helpful, friendly, and smart AI assistant built with Kotlin, Jetpack Compose, and Google Gemini.\n")
        memoryBuilder.append("You possess long-term memory about the user that persists across conversations.\n\n")

        if (memories.isNotEmpty()) {
            memoryBuilder.append("--- USER SAVED MEMORIES ---\n")
            for (mem in memories) {
                memoryBuilder.append("• ${mem.key}: ${mem.value}\n")
            }
            memoryBuilder.append("---------------------------\n\n")
            memoryBuilder.append("INSTRUCTIONS ON MEMORY USAGE:\n")
            memoryBuilder.append("1. Always reference and utilize the user's saved memories above whenever relevant.\n")
            memoryBuilder.append("2. When asked questions like 'What is my name?', 'Where do I study?', 'What is my goal?', answer accurately using the saved memories.\n")
            memoryBuilder.append("3. If the user mentions their name or introduces themselves, greet them warmly by name.\n")
            memoryBuilder.append("4. Keep responses clear, polite, and helpful.\n")
        } else {
            memoryBuilder.append("Currently, no long-term user memories are saved yet.\n")
            memoryBuilder.append("When the user shares personal details (like name, college, course, preferences, goals), acknowledge them naturally.\n")
        }

        return memoryBuilder.toString()
    }

    private fun parseErrorDetails(errorJson: String?): String {
        if (errorJson.isNullOrBlank()) return "Unknown error"
        return try {
            val jsonObject = Gson().fromJson(errorJson, Map::class.java)
            val errorMap = jsonObject["error"] as? Map<*, *>
            (errorMap?.get("message") as? String) ?: errorJson.take(150)
        } catch (e: Exception) {
            errorJson.take(150)
        }
    }
}
