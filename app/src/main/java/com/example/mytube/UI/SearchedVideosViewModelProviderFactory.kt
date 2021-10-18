package com.example.mytube.UI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytube.repository.VideosRepository

class SearchedVideosViewModelProviderFactory(
    private val repository: VideosRepository,
    private val query: String
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchedVideosViewModel(repository, query) as T
    }

}