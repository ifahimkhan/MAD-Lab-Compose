package com.fahim.mad_lab_compose.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a chat conversation message.
 * Supports persistent chat history across app launches.
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String = "default",
    val sender: String, // "user" or "bot"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
