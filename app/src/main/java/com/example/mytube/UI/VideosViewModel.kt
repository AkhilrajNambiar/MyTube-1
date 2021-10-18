package com.example.mytube.UI

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytube.db.SearchItem
import com.example.mytube.models.AboutVideo
import com.example.mytube.models.ChannelDetails
import com.example.mytube.models.Channels
import com.example.mytube.models.VideosList
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class VideosViewModel(private val repository: VideosRepository) : ViewModel(){
    private var _videosList = MutableLiveData<Resource<VideosList>>()
    val videosList: LiveData<Resource<VideosList>> = _videosList

    private var _channelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val channelResponse: LiveData<Resource<ChannelDetails>> = _channelResponse


    var nextPageId: String = ""
    val videos = mutableListOf<AboutVideo>()
    val channels = mutableMapOf<String, Channels>()

    init {
        Log.d("searched","videos viewModel starts")
        getPopularVideos()
    }

    // Network calls
    fun getPopularVideos() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("searched", "getPopularVideos is running")
        _videosList.postValue(Resource.Loading())
        val response = repository.getMostPopularVideos()
        _videosList.postValue(handlePopularVideosResponse(response))
        Log.d("searched", "getPopularVideos has finished running")
    }

    fun getNextVideos() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("searched", "getNextVideos is running")
        _videosList.postValue(Resource.Loading())
        val response = repository.getNextVideos(nextPageId)
        _videosList.postValue(handlePopularVideosResponse(response))
        Log.d("searched", "getNextVideos has finished running")
    }

    fun getChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("searched", "getChannels is running")
        _channelResponse.postValue(Resource.Loading())
        val response = repository.getChannel(channelId)
        _channelResponse.postValue(handleChannelResponse(response))
        Log.d("searched", "getChannels has finished running")
    }

    private fun handlePopularVideosResponse(
        response: Response<VideosList>
    ): Resource<VideosList>{
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleChannelResponse(
        response: Response<ChannelDetails>
    ): Resource<ChannelDetails>{
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun findMillisDifference(time: String) = repository.findMillisDifference(time)

    // Database operations
    fun getSearchHistory() = repository.getSearchHistory()

    fun insertSearchItem(item: String) = viewModelScope.launch {
        repository.insertSearchedItem(item)
    }

    fun deleteSearchItem(item: String) = viewModelScope.launch {
        repository.deleteSearchedItem(item)
    }
}