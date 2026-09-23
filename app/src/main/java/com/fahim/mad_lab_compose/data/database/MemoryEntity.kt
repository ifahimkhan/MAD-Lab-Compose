package com.fahim.mad_lab_compose.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity representing a long-term memory item extracted from conversations.
 * The `key` has a unique index to ensure no duplicate entries for the same attribute (e.g. name, college).
 */
@Entity(
    tableName = "memories",
    indices = [Index(value = ["key"], unique = true)]
)
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
