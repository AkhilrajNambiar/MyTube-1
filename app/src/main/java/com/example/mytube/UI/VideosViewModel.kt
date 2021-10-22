package com.example.mytube.UI

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytube.db.SearchItem
import com.example.mytube.models.*
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class VideosViewModel(private val repository: VideosRepository) : ViewModel(){
    private var _videosList = MutableLiveData<Resource<VideosList>>()
    val videosList: LiveData<Resource<VideosList>> = _videosList

    private var _channelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val channelResponse: LiveData<Resource<ChannelDetails>> = _channelResponse

    private var _commentResponse = MutableLiveData<Resource<CommentThreadsList>>()
    val commentResponse: LiveData<Resource<CommentThreadsList>> = _commentResponse

    private var _commentRepliesResponse = MutableLiveData<Resource<CommentRepliesList>>()
    val commentRepliesResponse: LiveData<Resource<CommentRepliesList>> = _commentRepliesResponse

    var nextPageId: String = ""
    val videos = mutableListOf<AboutVideo>()
    val channels = mutableMapOf<String, Channels>()
    var commentsForVideo = mutableListOf<Item>()
    var commentReplies = MutableLiveData<MutableList<ItemX>>()

    init {
        Log.d("searched","videos viewModel starts")
        try {
            getPopularVideos()
        }
        catch (e:Exception) {
            Log.e("MainVideoScreen", e.stackTraceToString())
        }
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
        try {
            _channelResponse.postValue(Resource.Loading())
            val response = repository.getChannel(channelId)
            _channelResponse.postValue(handleChannelResponse(response))
        }
        catch (e:Exception) {
            Log.e("MainVideoScreen", e.stackTraceToString())
        }
        Log.d("searched", "getChannels has finished running")
    }

    fun getCommentsForVideo(videoId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _commentResponse.postValue(Resource.Loading())
            val response = repository.getCommentsForVideo(videoId)
            _commentResponse.postValue(handleCommentResponse(response))
        }
        catch (e: Exception) {
            Log.d("comments", e.stackTraceToString())
        }
    }

    fun getCommentsReplies(parentComment: String) = viewModelScope.launch(Dispatchers.IO) {
        try{
            _commentRepliesResponse.postValue(Resource.Loading())
            val response = repository.getCommentReplies(parentComment = parentComment)
            _commentRepliesResponse.postValue(handleCommentRepliesResponse(response))
        }
        catch (e: Exception) {
            Log.e("commentReplies", e.stackTraceToString())
        }
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

    private fun handleCommentResponse(
        response: Response<CommentThreadsList>
    ) : Resource<CommentThreadsList> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleCommentRepliesResponse(
        response: Response<CommentRepliesList>
    ) : Resource<CommentRepliesList> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
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