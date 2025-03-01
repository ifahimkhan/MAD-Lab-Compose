package com.fahim.mad_lab_compose.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.fahim.mad_lab_compose.data.entity.Task
import kotlinx.coroutines.flow.Flow

interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("Delete from task_table")
    fun deleteAllTask()

    @Query("Select * from task_table order by id ASC")
    fun getAllTask(): Flow<List<Task>>

}