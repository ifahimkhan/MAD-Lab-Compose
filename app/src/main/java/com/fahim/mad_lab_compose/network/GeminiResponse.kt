package com.fahim.mad_lab_compose.network

import com.google.gson.annotations.SerializedName

data class GeminiResponse(
    @SerializedName("candidates")
    val candidates: List<GeminiCandidate>? = null,

    @SerializedName("promptFeedback")
    val promptFeedback: PromptFeedback? = null,

    @SerializedName("usageMetadata")
    val usageMetadata: UsageMetadata? = null,

    @SerializedName("error")
    val error: GeminiApiError? = null
)

data class GeminiCandidate(
    @SerializedName("content")
    val content: GeminiContent? = null,

    @SerializedName("finishReason")
    val finishReason: String? = null,

    @SerializedName("index")
    val index: Int? = null
)

data class PromptFeedback(
    @SerializedName("blockReason")
    val blockReason: String? = null
)

data class UsageMetadata(
    @SerializedName("promptTokenCount")
    val promptTokenCount: Int? = null,

    @SerializedName("candidatesTokenCount")
    val candidatesTokenCount: Int? = null,

    @SerializedName("totalTokenCount")
    val totalTokenCount: Int? = null
)

data class GeminiApiError(
    @SerializedName("code")
    val code: Int? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("status")
    val status: String? = null
)
