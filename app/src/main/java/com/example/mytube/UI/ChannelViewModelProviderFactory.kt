package com.example.mytube.UI

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mytube.repository.VideosRepository

class ChannelViewModelProviderFactory(
    private val app: Application,
    private val repository: VideosRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChannelViewModel(app, repository) as T
    }
}