package com.gulehri.androidtask.ui.vm

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gulehri.androidtask.model.Image
import kotlinx.coroutines.launch
import java.io.File

class ImageViewModel : ViewModel() {

    private val _imageList = MutableLiveData<List<Image>>()
    val imageList: LiveData<List<Image>> get() = _imageList

    fun loadImages() {
        viewModelScope.launch {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val taskDirectory = File(directory, "Shah Saud Task")

            if (taskDirectory.exists() && taskDirectory.isDirectory) {
                val images = taskDirectory.listFiles { file -> file.isFile }?.map { file ->
                    Image(file.absolutePath)
                } ?: emptyList()

                _imageList.postValue(images)
            }
        }
    }


    fun deleteFile(imagePath: String) {
        viewModelScope.launch {
            File(imagePath).deleteRecursively()
            loadImages()
        }
    }
}

