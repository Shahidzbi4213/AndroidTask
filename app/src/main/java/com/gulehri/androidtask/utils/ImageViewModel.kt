package com.gulehri.androidtask.utils

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ImageViewModel : ViewModel() {

    private val _imageList = MutableLiveData<List<Image>>()
    val imageList: LiveData<List<Image>> get() = _imageList

    fun loadImages() {
        GlobalScope.launch(Dispatchers.IO) {
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val taskaDirectory = File(directory, "Android Task")

            if (taskaDirectory.exists() && taskaDirectory.isDirectory) {
                val images = taskaDirectory.listFiles { file -> file.isFile() }?.map { file ->
                    Image(file.absolutePath)
                } ?: emptyList()

                launch(Dispatchers.Main) {
                    _imageList.value = images
                }
            }
        }
    }
}
