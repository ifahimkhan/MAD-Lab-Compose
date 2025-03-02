package com.fahim.mad_lab_compose.interfaces

import android.content.Context

interface Storage {
    fun writeToFile(data: String, context: Context,fileName: String)
    fun readFromFile(context: Context,fileName: String): String
}
enum class StorageType {
    INTERNAL, EXTERNAL
}

