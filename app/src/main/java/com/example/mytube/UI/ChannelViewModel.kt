package com.example.mytube.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.ResourceCursorAdapter
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.mytube.MytubeApplication
import com.example.mytube.models.*
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class ChannelViewModel(
    private val app: Application,
    private val repository: VideosRepository
): AndroidViewModel(app) {
    private var _channelResponse = MutableLiveData<Resource<ChannelFullDetails>>()
    val channelResponse: LiveData<Resource<ChannelFullDetails>> = _channelResponse

    private var _channelSectionsResponse = MutableLiveData<Resource<ChannelSections>>()
    val channelSectionsResponse: LiveData<Resource<ChannelSections>> = _channelSectionsResponse

    private var _channelPlaylistsResponse = MutableLiveData<Resource<ChannelPlaylists>>()
    val channelPlaylistsResponse: LiveData<Resource<ChannelPlaylists>> = _channelPlaylistsResponse

    private var _playListItemsResponse = MutableLiveData<Resource<PlaylistItems>>()
    val playlistItemsResponse: LiveData<Resource<PlaylistItems>> = _playListItemsResponse

    private var _popularVideosOfChannel = MutableLiveData<Resource<PopularVideos>>()
    val popularVideosOfChannel: LiveData<Resource<PopularVideos>> = _popularVideosOfChannel

    private var _singleVideo = MutableLiveData<Resource<VideosList>>()
    val singleVideo: LiveData<Resource<VideosList>> = _singleVideo

    // All the network calls
    fun getCompleteChannelDetails(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _channelResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getCompleteChannelDetails(channelId)
                _channelResponse.postValue(handleChannelResponse(response))
            }
            else {
                _channelResponse.postValue(Resource.Error("No Internet Connection!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _channelResponse.postValue(Resource.Error("IOException has occured!"))
                else -> _channelResponse.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
    }

    fun getChannelSections(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _channelSectionsResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannelSections(channelId)
                _channelSectionsResponse.postValue(handleChannelSectionResponse(response))
            }
            else {
                _channelSectionsResponse.postValue(Resource.Error("No Internet Connection for channel sections!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _channelSectionsResponse.postValue(Resource.Error("IOException has occured!"))
                else -> _channelSectionsResponse.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
    }

    fun getChannelPlaylists(channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        _channelPlaylistsResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getChannelPlaylists(channelId)
                _channelPlaylistsResponse.postValue(handleChannelPlaylistsResponse(response))
            }
            else {
                _channelPlaylistsResponse.postValue(Resource.Error("No Internet connection for channel playlists!"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _channelPlaylistsResponse.postValue(Resource.Error("IOException for channel playlists"))
                else -> _channelPlaylistsResponse.postValue(Resource.Error("Conversion error for channel playlists"))
            }
        }
    }

    fun setUpChannelHomeSection(item: ItemXXX?, playlistId: String, channelId: String) = viewModelScope.launch(Dispatchers.IO) {
        val recentUploads = viewModelScope.async(Dispatchers.IO) {
            getPlaylistItems(playlistId)
        }
        Log.d("playlists", "${recentUploads.await()}")
        if (item!!.snippet.type == "popularuploads") {
            val popularUploads = viewModelScope.async(Dispatchers.IO) {
                getPopularVideosOfChannel(channelId, null)
            }
            Log.d("playlists", "${popularUploads.await()}")
        }
    }

    fun getPlaylistItemsForSmallChannels(playlistId: String) = viewModelScope.launch(Dispatchers.IO) {
        getPlaylistItems(playlistId)
    }

    suspend fun getPlaylistItems(playlistId: String) {
        _playListItemsResponse.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getPlaylistItems(playlistId)
                _playListItemsResponse.postValue(handlePlaylistItemsResponse(response))
            } else {
                _playListItemsResponse.postValue(Resource.Error("No Internet connection for playlist items!"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _playListItemsResponse.postValue(Resource.Error("IOException for playlist items"))
                else -> _playListItemsResponse.postValue(Resource.Error(t.stackTraceToString()))
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
                _singleVideo.postValue(Resource.Error("No Internet Connection to load video!"))
            }
        }
        catch (t:Throwable) {
            when(t) {
                is IOException -> _singleVideo.postValue(Resource.Error("IOException while trying to obtain video"))
                else -> _singleVideo.postValue(Resource.Error("Conversion error while obtaining video"))
            }
        }
    }

    suspend fun getPopularVideosOfChannel(channelId: String, pageToken: String?) {
        _popularVideosOfChannel.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getPoplarVideosOfChannel(channelId, pageToken)
                _popularVideosOfChannel.postValue(handlePopularVideosOfChannel(response))
            }
            else {
                _popularVideosOfChannel.postValue(Resource.Error("No Internet Connection to get popular videos of channel"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _popularVideosOfChannel.postValue(Resource.Error("IOException to get popular videos of channel"))
                else -> _popularVideosOfChannel.postValue(Resource.Error(t.stackTraceToString()))
            }
        }
    }

    private fun handleChannelResponse(
        response: Response<ChannelFullDetails>
    ): Resource<ChannelFullDetails> {
      if (response.isSuccessful) {
          response.body()?.let {
              return Resource.Success(it)
          }
      }
        return Resource.Error(response.message())
    }

    private fun handleChannelSectionResponse(
        response: Response<ChannelSections>
    ): Resource<ChannelSections> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSingleVideoResponse(
        response: Response<VideosList>
    ): Resource<VideosList> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePopularVideosOfChannel(
        response: Response<PopularVideos>
    ): Resource<PopularVideos> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleChannelPlaylistsResponse(
        response: Response<ChannelPlaylists>
    ): Resource<ChannelPlaylists> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handlePlaylistItemsResponse(
        response: Response<PlaylistItems>
    ): Resource<PlaylistItems> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun findMillisDifference(time: String) = repository.findMillisDifference(time)

}