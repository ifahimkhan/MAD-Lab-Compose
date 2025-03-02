package com.fahim.mad_lab_compose.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.mad_lab_compose.interfaces.Storage
import com.fahim.mad_lab_compose.interfaces.StorageType
import com.fahim.mad_lab_compose.model.ExternalStorage
import com.fahim.mad_lab_compose.model.InternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class FilesViewModel(application: Application) : AndroidViewModel(application) {

    private var storage: Storage = InternalStorage()
    private val internalStorage:Storage = InternalStorage()
    private val externalStorage:Storage = ExternalStorage()
    private val _fileContent = mutableStateOf("")
    val fileContent: State<String> = _fileContent
    private val _fileName = mutableStateOf("example.txt")
    val fileName: State<String> = _fileName
    private val _storageType = mutableStateOf(StorageType.INTERNAL)
    val storageType: State<StorageType> = _storageType

    init {
        Log.e("TAG", "onCreate: ${this.hashCode()}", )

    }
    fun setStorageType(type: StorageType) {
        _storageType.value = type
        storage = if (type == StorageType.INTERNAL) internalStorage else externalStorage
    }

    fun setFileName(name: String) {
        _fileName.value = name
    }

    fun setFileContent(content: String) {
        Log.e("TAG", "setFileContent: $content", )
        _fileContent.value = content
    }

    fun writeToFile(data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                storage.writeToFile(data, getApplication(), fileName.value)
                _fileContent.value = data
                _fileName.value = fileName.value
                _storageType.value = storageType.value

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplication(),
                        "File created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    fun readFromFile() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = storage.readFromFile(getApplication(), fileName.value)
            _fileContent.value = data
            _fileName.value = fileName.value
            _storageType.value = storageType.value
        }

    }

    override fun onCleared() {
        super.onCleared()
        Log.e("TAG", "onCleared: ", )
    }
}