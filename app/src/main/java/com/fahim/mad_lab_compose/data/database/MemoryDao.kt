package com.fahim.mad_lab_compose.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memories ORDER BY updatedAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY updatedAt DESC")
    suspend fun getAllMemoriesSync(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE LOWER(`key`) = LOWER(:key) LIMIT 1")
    suspend fun getMemoryByKey(key: String): MemoryEntity?

    @Query("SELECT * FROM memories WHERE id = :id LIMIT 1")
    suspend fun getMemoryById(id: Long): MemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM memories")
    suspend fun deleteAllMemories()

    @Query("SELECT COUNT(*) FROM memories")
    fun getMemoryCount(): Flow<Int>

    /**
     * Helper to insert a new memory or update an existing one if the key already exists.
     * Prevents duplicate keys and updates the timestamp.
     */
    @Transaction
    suspend fun upsertMemoryByKey(key: String, value: String) {
        val existing = getMemoryByKey(key)
        val currentTime = System.currentTimeMillis()
        if (existing != null) {
            updateMemory(
                existing.copy(
                    value = value,
                    updatedAt = currentTime
                )
            )
        } else {
            insertMemory(
                MemoryEntity(
                    key = key,
                    value = value,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
            )
        }
    }
}
