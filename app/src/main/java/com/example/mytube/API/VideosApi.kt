package com.example.mytube.API

import com.example.mytube.models.ChannelDetails
import com.example.mytube.models.VideosList
import com.example.mytube.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VideosApi {
    @GET("videos")
    suspend fun getMostPopularVideosList(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partStats: String = "statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") country: String = "in",
        @Query("maxResults") maxResults:Int = 5,
        @Query("key") key: String = API_KEY): Response<VideosList>

    @GET("videos")
    suspend fun getNextVideosList(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partStats: String = "statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") country: String = "in",
        @Query("pageToken") nextPageId: String,
        @Query("maxResults") maxResults:Int = 5,
        @Query("key") key: String = API_KEY): Response<VideosList>

    @GET("videos")
    suspend fun getPreviousVideosList(
        @Query("part") partSnippet: String,
        @Query("part") partStats: String,
        @Query("chart") chart: String,
        @Query("pageToken") previousPageId: String,
        @Query("maxResults") maxResults:Int,
        @Query("key") key: String): Response<VideosList>

    @GET("channels")
    suspend fun getChannelFromId(
        @Query("part") part: String = "snippet",
        @Query("id") id: String,
        @Query("key") key: String = API_KEY
    ): Response<ChannelDetails>

    @GET("search")
    suspend fun getSearchResults(
        @Query("part") partSnippet: String = "snippet",
        @Query("q") searchQuery: String,
        @Query("type") type: String = "video",
        @Query("key") key: String = API_KEY
    ) : Response<VideosList>

    @GET("search")
    suspend fun getNextSearchResults(
        @Query("part") partSnippet: String = "snippet",
        @Query("q") searchQuery: String,
        @Query("type") type: String = "video",
        @Query("pageToken") pageToken: String,
        @Query("key") key: String = API_KEY
    ) : Response<VideosList>
}