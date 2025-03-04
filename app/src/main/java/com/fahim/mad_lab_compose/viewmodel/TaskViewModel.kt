package com.fahim.mad_lab_compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.mad_lab_compose.data.entity.Task
import com.fahim.mad_lab_compose.data.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val allTask = repository.allTask

    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO){
        repository.insertTask(task)
    }

    fun update(task: Task) = viewModelScope.launch (Dispatchers.IO){
        repository.updateTask(task)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTask(task)
    }

    fun deleteAll() = viewModelScope.launch (Dispatchers.IO){
        repository.deleteAllTask()
    }
}