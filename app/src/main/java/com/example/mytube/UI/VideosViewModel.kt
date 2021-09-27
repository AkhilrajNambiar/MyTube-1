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

    private var _searchResults = MutableLiveData<Resource<VideosList>>()
    val searchResults: LiveData<Resource<VideosList>> = _searchResults

    var nextPageId: String = ""
    var nextSearchPageId: String = ""
    val videos = mutableListOf<AboutVideo>()
    val searchedVideos = mutableListOf<AboutVideo>()
    val channels = mutableMapOf<String, Channels>()

    init {
        getPopularVideos()
    }

    // Network calls
    fun getPopularVideos() = viewModelScope.launch(Dispatchers.IO) {
        _videosList.postValue(Resource.Loading())
        val response = repository.getMostPopularVideos()
        _videosList.postValue(handlePopularVideosResponse(response))
    }

    fun getNextVideos() = viewModelScope.launch(Dispatchers.IO) {
        _videosList.postValue(Resource.Loading())
        val response = repository.getNextVideos(nextPageId)
        _videosList.postValue(handlePopularVideosResponse(response))
    }

    fun getSearchResults(q: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchResults.postValue(Resource.Loading())
        val response = repository.getSearchResults(q)
        _searchResults.postValue(handlePopularVideosResponse(response))
    }

    fun getNextSearchResults(q: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchResults.postValue(Resource.Loading())
        val response = repository.getNextSearchResults(q, nextSearchPageId)
        _searchResults.postValue(handlePopularVideosResponse(response))
    }

    fun getChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _channelResponse.postValue(Resource.Loading())
        val response = repository.getChannel(channelId)
        _channelResponse.postValue(handleChannelResponse(response))
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
    fun findMillisDifference(str1: String) : Map<String, Int>{
        // Converting the Time in string form to Time in LocalDateTime form
        val publishedTime = LocalDateTime.parse(str1.slice(0 until str1.length - 1))
        // Getting the current Time
        val time1 = Calendar.getInstance()
        // Converting the current time to Milliseconds
        val milliseconds = time1.timeInMillis
        // Converting the publishedTime to Milliseconds
        val millis2 = publishedTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val difference = (milliseconds - millis2).toString()
        if(difference.length <= 3) return mapOf("seconds" to 0)
        else if(difference.length == 4) return mapOf("seconds" to difference[0].code)
        val milliStart = difference.length - 3
        val seconds = difference.slice(0 until milliStart).toLong()
        val sec = seconds % 60
        val mins = (seconds / 60) % 60
        val hrs = (seconds / 3600) % 24
        val days = (seconds / (3600 * 24)) % 7
        val weeks = (seconds / (3600 * 24 * 7)) % 4
        val months = (seconds / (3600 * 24 * 30)) % 12
        val years = seconds / (3600 * 24 * 30 * 12)
        if(years.toInt() == 0) {
            if(months.toInt() == 0) {
                if(weeks.toInt() == 0) {
                    if(days.toInt() == 0) {
                        if(hrs.toInt() == 0) {
                            if(mins.toInt() == 0) {
                                return mapOf("seconds" to seconds.toInt())
                            }
                            else {
                                return mapOf("minutes" to mins.toInt())
                            }
                        }
                        else {
                            return mapOf("hours" to hrs.toInt())
                        }
                    }
                    else {
                        return mapOf("days" to days.toInt())
                    }
                }
                else {
                    return mapOf("weeks" to weeks.toInt())
                }
            }
            else {
                return mapOf("months" to months.toInt())
            }
        }
        else {
            return mapOf("years" to years.toInt())
        }
    }

    // Database operations
    fun getSearchHistory() = repository.getSearchHistory()

    fun insertSearchItem(item: String) = viewModelScope.launch (Dispatchers.IO){
        repository.insertSearchedItem(item)
    }

    fun deleteSearchItem(item: String) = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteSearchedItem(item)
    }
}