package com.fahim.mad_lab_compose.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a conversation summary.
 * Supports persistent summaries across app launches.
 */
@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val summary: String,
    val createdAt: Long = System.currentTimeMillis()
)