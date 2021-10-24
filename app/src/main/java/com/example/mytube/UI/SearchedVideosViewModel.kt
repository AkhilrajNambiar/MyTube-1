package com.example.mytube.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.mytube.MytubeApplication
import com.example.mytube.models.*
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class SearchedVideosViewModel(
    private val app: Application,
    private val repository: VideosRepository,
    private val query: String
) : AndroidViewModel(app){

    private var _searchResults = MutableLiveData<Resource<SearchedVideosList>>()
    val searchResults: LiveData<Resource<SearchedVideosList>> = _searchResults

    private var _singleVideo = MutableLiveData<Resource<VideosList>>()
    val singleVideo: LiveData<Resource<VideosList>> = _singleVideo

    private var _searchedResultsChannelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val searchedResultsChannelResponse: LiveData<Resource<ChannelDetails>> = _searchedResultsChannelResponse

    var nextSearchPageId: String = ""
    val searchedVideos = mutableListOf<AboutSearchVideo>()
    val searchedVideoDetails = mutableListOf<AboutVideo>()
    var searchedVideoIds = mutableListOf<String>()
    val searchedResultchannels = mutableMapOf<String, Channels>()

    init {
        Log.d("searched",query)
        Log.d("searched","searched videos viewModel starts")
        getSearchResults(query)
    }

    fun getSearchResults(q: String) = viewModelScope.launch {
        Log.d("searched", "getSearchResults is running")
        _searchResults.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getSearchResults(q)
                _searchResults.postValue(handleSearchVideosResponse(response))
            }
            else {
                _searchResults.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _searchResults.postValue(Resource.Error("IOException has occured!"))
                else -> _searchResults.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getNextSearchResults(q: String) = viewModelScope.launch {
        Log.d("searched", "getNextSearchResults is running")
        _searchResults.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getNextSearchResults(q, nextSearchPageId)
                _searchResults.postValue(handleSearchVideosResponse(response))
            }
            else {
                _searchResults.postValue(Resource.Error("No Internet Connection for next videos!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _searchResults.postValue(Resource.Error("IOException has occured!"))
                else -> _searchResults.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    fun getSearchedResultChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("searched", "getChannels is running")
        _searchedResultsChannelResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannel(channelId)
                _searchedResultsChannelResponse.postValue(handleChannelResponse(response))
            }
            else {
                _searchedResultsChannelResponse.postValue(Resource.Error("No Internet Connection for obtaining channels"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _searchedResultsChannelResponse.postValue(Resource.Error("IOException has occured!"))
                else -> _searchedResultsChannelResponse.postValue(Resource.Error("Conversion Error"))
            }
        }
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

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MytubeApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> true
                }
            }
        }
        return false
    }
}