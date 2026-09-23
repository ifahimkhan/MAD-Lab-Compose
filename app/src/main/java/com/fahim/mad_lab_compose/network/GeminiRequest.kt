package com.fahim.mad_lab_compose.network

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("contents")
    val contents: List<GeminiContent>,

    @SerializedName("systemInstruction")
    val systemInstruction: GeminiSystemInstruction? = null,

    @SerializedName("generationConfig")
    val generationConfig: GeminiGenerationConfig? = null
)

data class GeminiContent(
    @SerializedName("role")
    val role: String = "user", // "user" or "model"

    @SerializedName("parts")
    val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text")
    val text: String
)

data class GeminiSystemInstruction(
    @SerializedName("parts")
    val parts: List<GeminiPart>
)

data class GeminiGenerationConfig(
    @SerializedName("temperature")
    val temperature: Float = 0.7f,

    @SerializedName("topK")
    val topK: Int = 40,

    @SerializedName("topP")
    val topP: Float = 0.95f,

    @SerializedName("maxOutputTokens")
    val maxOutputTokens: Int = 2048
)
