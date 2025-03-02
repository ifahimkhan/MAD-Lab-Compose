package com.fahim.mad_lab_compose

import android.app.Application
import com.fahim.mad_lab_compose.data.TaskDatabase
import com.fahim.mad_lab_compose.data.repository.TaskRepository

class TaskApplication : Application() {
    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }

}