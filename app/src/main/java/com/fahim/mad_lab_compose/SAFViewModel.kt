package com.fahim.mad_lab_compose

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import com.fahim.mad_lab_compose.MainActivity.Companion.OPEN_FILE_REQUEST_CODE
import com.fahim.mad_lab_compose.MainActivity.Companion.SAVE_FILE_REQUEST_CODE
import com.fahim.mad_lab_compose.MainActivity.Companion.SELECT_DIRECTORY_REQUEST_CODE

class SAFViewModel(application: Application) : AndroidViewModel(application) {

    fun openFile(context: Context, onFileSelected: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // Allow all file types
        }
        (context as Activity).startActivityForResult(intent, OPEN_FILE_REQUEST_CODE)
    }

    fun saveFile(context: Context, fileName: String, content: ByteArray) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain" // MIME type for text files
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        (context as Activity).startActivityForResult(intent, SAVE_FILE_REQUEST_CODE)
    }

    fun selectDirectory(context: Context, onDirectorySelected: (Uri) -> Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        }
        (context as Activity).startActivityForResult(intent, SELECT_DIRECTORY_REQUEST_CODE)
    }

    fun readFileContent(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                reader.readText()
            }
        } ?: ""
    }
}