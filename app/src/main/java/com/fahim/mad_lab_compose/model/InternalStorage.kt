package com.fahim.mad_lab_compose.model

import android.content.Context
import android.widget.Toast
import com.fahim.mad_lab_compose.interfaces.Storage
import java.io.IOException

class InternalStorage : Storage {
    override fun writeToFile(data: String, context: Context, fileName: String) {
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
                fos.write(data.toByteArray())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun readFromFile(context: Context, fileName: String): String {

        val stringBuilder = StringBuilder()

        try {
            context.openFileInput(fileName).use { fis ->
                var content: Int
                while ((fis.read().also { content = it }) != -1) {
                    stringBuilder.append(content.toChar())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }
}
