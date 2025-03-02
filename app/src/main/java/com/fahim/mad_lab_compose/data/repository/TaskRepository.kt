package com.fahim.mad_lab_compose.data.repository

import com.fahim.mad_lab_compose.data.dao.TaskDao
import com.fahim.mad_lab_compose.data.entity.Task

class TaskRepository(val taskDao: TaskDao) {
    val allTask = taskDao.getAllTask()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllTask(){
        taskDao.deleteAllTask()
    }
}