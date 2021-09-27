package com.example.mytube.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytube.repository.VideosRepository

class VideosViewModelProviderFactory(private val videosRepository: VideosRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideosViewModel(videosRepository) as T
    }
}