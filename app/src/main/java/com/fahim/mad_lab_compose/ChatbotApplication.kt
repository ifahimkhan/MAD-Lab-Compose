package com.fahim.mad_lab_compose

import android.app.Application
import com.fahim.mad_lab_compose.data.database.AppDatabase
import com.fahim.mad_lab_compose.data.repository.ChatRepository

class ChatbotApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ChatRepository(database.memoryDao(), database.messageDao(), database.summaryDao()) }

    override fun onCreate() {
        super.onCreate()
    }
}
