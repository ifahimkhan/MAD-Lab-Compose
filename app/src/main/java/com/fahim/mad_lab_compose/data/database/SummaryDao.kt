package com.fahim.mad_lab_compose.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {
    
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC")
    fun getAllSummaries(): Flow<List<SummaryEntity>>
    
    @Query("SELECT * FROM summaries ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestSummary(): SummaryEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long
    
    @Query("DELETE FROM summaries WHERE id = :id")
    suspend fun deleteSummaryById(id: Long)
    
    @Query("DELETE FROM summaries")
    suspend fun deleteAllSummaries()
    
    @Query("SELECT * FROM summaries WHERE id = :id")
    suspend fun getSummaryById(id: Long): SummaryEntity?
}