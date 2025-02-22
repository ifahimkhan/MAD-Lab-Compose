package com.fahim.mad_lab_compose.model

import android.content.Context
import android.os.Environment
import com.fahim.mad_lab_compose.interfaces.Storage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class ExternalStorage : Storage {
    override fun writeToFile(data: String, context: Context, fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), fileName
        )
        FileOutputStream(file).use { fos ->
            fos.write(data.toByteArray())
        }

    }

    override fun readFromFile(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), fileName
        )
        if (file.exists()) try {
            FileInputStream(file).use { fis ->
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
