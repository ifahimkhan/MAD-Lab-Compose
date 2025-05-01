package com.fahim.mad_lab_compose

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    val images: StateFlow<List<Uri>> = _images.asStateFlow()

    fun loadImages() {
        viewModelScope.launch(Dispatchers.IO) {
            val imageUris = withContext(Dispatchers.IO) {
                ImageHelper.getImages(getApplication<Application>().applicationContext)
            }
            _images.value = imageUris
        }
    }
}