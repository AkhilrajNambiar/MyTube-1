package com.example.mytube.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.mytube.MytubeApplication
import com.example.mytube.db.LikedVideoItem
import com.example.mytube.db.SearchItem
import com.example.mytube.db.WatchHistoryItem
import com.example.mytube.db.WatchLaterItem
import com.example.mytube.models.*
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import com.google.android.youtube.player.internal.e
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class VideosViewModel(
    app: Application,
    private val repository: VideosRepository
) : AndroidViewModel(app){
    private var _videosList = MutableLiveData<Resource<VideosList>>()
    val videosList: LiveData<Resource<VideosList>> = _videosList

    private var _channelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val channelResponse: LiveData<Resource<ChannelDetails>> = _channelResponse

    private var _relatedVideoChannelResponse = MutableLiveData<Resource<ChannelDetails>>()
    val relatedVideoChannelResponse: LiveData<Resource<ChannelDetails>> = _relatedVideoChannelResponse

    private var _relatedChannelsReponse = MutableLiveData<Resource<ChannelDetails>>()
    val relatedChannelResponse: LiveData<Resource<ChannelDetails>> = _relatedChannelsReponse

    private var _relatedVideosList = MutableLiveData<Resource<SearchedVideosList>>()
    val relatedVideosList: LiveData<Resource<SearchedVideosList>> = _relatedVideosList

    private var _singleVideo = MutableLiveData<Resource<VideosList>>()
    val singleVideo: LiveData<Resource<VideosList>> = _singleVideo

    private var _recyclerViewVideo = MutableLiveData<Resource<VideosList>>()
    val recyclerViewVideo: LiveData<Resource<VideosList>> = _recyclerViewVideo

    private var _commentResponse = MutableLiveData<Resource<CommentThreadsList>>()
    val commentResponse: LiveData<Resource<CommentThreadsList>> = _commentResponse

    private var _commentRepliesResponse = MutableLiveData<Resource<CommentRepliesList>>()
    val commentRepliesResponse: LiveData<Resource<CommentRepliesList>> = _commentRepliesResponse

    var nextPageId: String = ""
    var nextPageToken: String? = null
    val videos = mutableListOf<AboutVideo>()
    val videoIds = mutableListOf<String>()
    var relatedVideos = mutableListOf<AboutSearchVideo>()
    var relatedVideoDetails = mutableListOf<AboutVideo>()
    var relatedVideoIds = mutableListOf<String>()
    var relatedChannels = mutableMapOf<String, Channels>()
    val channels = mutableMapOf<String, Channels>()
    var commentsForVideo = mutableListOf<Item>()
    var commentReplies = MutableLiveData<MutableList<ItemX>>()

    var equatableLikedVideos: MutableSet<Equatable> = mutableSetOf()
    var equatableWatchLaterVideos: MutableSet<Equatable> = mutableSetOf()

    init {
        getPopularVideos()
    }

    // Network calls
    fun getPopularVideos() = viewModelScope.launch(Dispatchers.IO) {
        _videosList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getMostPopularVideos()
                _videosList.postValue(handlePopularVideosResponse(response))
            }
            else {
                _videosList.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _videosList.postValue(Resource.Error("IOException occured!"))
                else -> _videosList.postValue(Resource.Error("Conversion Error!"))
            }
        }
    }

    fun getNextVideos() = viewModelScope.launch(Dispatchers.IO) {
        _videosList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getNextVideos(nextPageId)
                _videosList.postValue(handlePopularVideosResponse(response))
            }
            else {
                _videosList.postValue(Resource.Error("No Internet Connection for Next Videos!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _videosList.postValue(Resource.Error("IOException occured!"))
                else -> _videosList.postValue(Resource.Error("Conversion Error!"))
            }
        }
    }

    fun getChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _channelResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannel(channelId)
                _channelResponse.postValue(handleChannelResponse(response))
            }
            else {
                _channelResponse.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _channelResponse.postValue(Resource.Error("IOException occured!"))
                else -> _channelResponse.postValue(Resource.Error("Conversion Error!"))
            }
        }
    }

    fun getSingleRelatedChannel(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _relatedVideoChannelResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannel(channelId)
                _relatedVideoChannelResponse.postValue(handleChannelResponse(response))
            }
            else {
                _relatedVideoChannelResponse.postValue(Resource.Error("No Internet Connection for related Video channels!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _relatedVideoChannelResponse.postValue(Resource.Error("IOException occured for related video channel!"))
                else -> _relatedVideoChannelResponse.postValue(Resource.Error("Conversion Error for related video channel!"))
            }
        }
    }

    fun getRelatedChannels(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _relatedChannelsReponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannel(channelId)
                _relatedChannelsReponse.postValue(handleChannelResponse(response))
            }
            else {
                _relatedChannelsReponse.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _relatedChannelsReponse.postValue(Resource.Error("IOException occured!"))
                else -> _relatedChannelsReponse.postValue(Resource.Error("Conversion Error!"))
            }
        }
    }

    fun getVideosRelatedToCurrentVideo(videoId: String, nextPageId: String?) = viewModelScope.launch(Dispatchers.IO) {
        _relatedVideosList.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getVideosRelatedToCurrentVideo(videoId, nextPageId)
                _relatedVideosList.postValue(handleRelatedVideosResponse(response))
            }
            else {
                _relatedVideosList.postValue(Resource.Error("No Internet connection for related videos"))
            }
        }
        catch(t: Throwable) {
            when(t) {
                is IOException -> _relatedVideosList.postValue(Resource.Error("IOException for finding related videos"))
                else -> _relatedVideosList.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
    }

    fun getVideoDetails(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _singleVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getVideoDetails(id)
                _singleVideo.postValue(handleSingleVideoResponse(response))
            }
            else {
                _singleVideo.postValue(Resource.Error("No Internet connection for finding single video details"))
            }
        }
        catch (t:Throwable) {
            when(t) {
                is IOException -> _singleVideo.postValue(Resource.Error("IOException for single video details"))
                else -> _singleVideo.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
    }

    fun getSingleRecyclerViewVideo(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _recyclerViewVideo.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getVideoDetails(id)
                _recyclerViewVideo.postValue(handleSingleVideoResponse(response))
            }
            else {
                _recyclerViewVideo.postValue(Resource.Error("No Internet Connection to load video!"))
            }
        }
        catch (t:Throwable) {
            when(t) {
                is IOException -> _recyclerViewVideo.postValue(Resource.Error("IOException while trying to obtain video"))
                else -> _recyclerViewVideo.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
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

    private fun handleRelatedVideosResponse(
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

    // Watch history methods

    fun insertVideoIntoWatchHistory(video: WatchHistoryItem) = viewModelScope.launch {
        repository.insertVideoToWatchHistory(video)
    }

    fun deleteVideoFromWatchHistory(video: WatchHistoryItem) = viewModelScope.launch {
        repository.deleteVideoInWatchHistory(video)
    }

    fun getRecentlyWatchedVideos() = repository.getMostRecentVideos()

    fun getCompleteWatchHistory() = repository.getCompleteWatchHistory()

    // Watch Later methods
    fun insertVideoIntoWatchLater(video: WatchLaterItem) = viewModelScope.launch {
        repository.insertVideoToWatchLater(video)
    }

    fun deleteVideoFromWatchLater(video: WatchLaterItem) = viewModelScope.launch {
        repository.deleteVideoFromWatchLater(video)
    }

    fun getRecentlyAddedWatchLaterVideos() = repository.getRecentlyAddedWatchLaterVideos()

    fun getOldestAddedWatchLaterVideos() = repository.getOldestAddedWatchLaterVideos()

    fun getMostPopularWatchLaterVideos() = repository.getMostPopularWatchLaterVideos()

    fun getRecentlyPublishedWatchLaterVideos() = repository.getRecentlyPublishedWatchLaterVideos()

    fun getOldestPublishedWatchLaterVideos() = repository.getOldestPublishedWatchLaterVideos()

    fun getCountOfWatchLaterVideos() = repository.getCountOfWatchLaterVideos()

    fun getCountOfUnwatchedVideos() = repository.getCountOfUnwatchedVideos()

    fun updateWatchedVideos(video: WatchLaterItem) = viewModelScope.launch {
        repository.updateWatchedVideos(video)
    }

    // Liked Videos

    fun insertVideoToLikedVideos(video: LikedVideoItem) = viewModelScope.launch {
        repository.insertVideoToLikedVideos(video)
    }

    fun deleteVideoFromLikedVideos(video: LikedVideoItem) = viewModelScope.launch {
        repository.deleteVideoFromLikedVideos(video)
    }

    fun updateLikedVideoStatus(video: LikedVideoItem) = viewModelScope.launch {
        repository.updateLikedVideoStatus(video)
    }

    fun getLikedVideos() = repository.getLikedVideos()

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