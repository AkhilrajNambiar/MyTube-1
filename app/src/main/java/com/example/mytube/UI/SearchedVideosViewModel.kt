package com.example.mytube.UI

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytube.models.*
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class SearchedVideosViewModel(private val repository: VideosRepository, private val query: String) : ViewModel(){
    private var _searchResults = MutableLiveData<Resource<SearchedVideosList>>()
    val searchResults: LiveData<Resource<SearchedVideosList>> = _searchResults

    private var _singleVideo = MutableLiveData<Resource<VideosList>>()
    val singleVideo: LiveData<Resource<VideosList>> = _singleVideo

    private var _searchedResultsChannelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val searchedResultsChannelResponse: LiveData<Resource<ChannelDetails>> = _searchedResultsChannelResponse

    var nextSearchPageId: String = ""
    val searchedVideos = mutableListOf<AboutSearchVideo>()
    val searchedVideoDetails = mutableListOf<AboutVideo>()
    val searchedResultchannels = mutableMapOf<String, Channels>()

    init {
        Log.d("searched",query)
        Log.d("searched","searched videos viewModel starts")
        getSearchResults(query)
    }

    fun getSearchResults(q: String) = viewModelScope.launch {
        Log.d("searched", "getSearchResults is running")
        try {
            _searchResults.postValue(Resource.Loading())
            val response = repository.getSearchResults(q)
            _searchResults.postValue(handleSearchVideosResponse(response))
        }
        catch (e: Exception) {
            Log.e("searched", e.stackTraceToString())
        }
    }

    fun getNextSearchResults(q: String) = viewModelScope.launch {
        Log.d("searched", "getNextSearchResults is running")
        try {
            _searchResults.postValue(Resource.Loading())
            val response = repository.getNextSearchResults(q, nextSearchPageId)
            _searchResults.postValue(handleSearchVideosResponse(response))
        }
        catch (e: Exception) {
            Log.e("searched", e.stackTraceToString())
        }
    }

    fun getSearchedResultChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("searched", "getChannels is running")
        _searchedResultsChannelResponse.postValue(Resource.Loading())
        val response = repository.getChannel(channelId)
        _searchedResultsChannelResponse.postValue(handleChannelResponse(response))
        Log.d("searched", "getChannels has finished running")
    }

    fun getVideoDetails(id: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _singleVideo.postValue(Resource.Loading())
            val response = repository.getVideoDetails(id)
            _singleVideo.postValue(handleSingleVideoResponse(response))
        }
        catch(e: Exception) {
            Log.e("searched", e.stackTraceToString())
        }
    }

    private fun handleSearchVideosResponse(
        response: Response<SearchedVideosList>
    ): Resource<SearchedVideosList>{
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSingleVideoResponse(
        response: Response<VideosList>
    ): Resource<VideosList> {
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
}