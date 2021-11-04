package com.example.mytube.API

import com.example.mytube.models.*
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
        @Query("regionCode") country: String = listOf("us","in").random(),
        @Query("maxResults") maxResults:Int = 5,
        @Query("key") key: String = API_KEY): Response<VideosList>

    @GET("videos")
    suspend fun getNextVideosList(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partStats: String = "statistics",
        @Query("chart") chart: String = "mostPopular",
        @Query("regionCode") country: String = listOf("us","in").random(),
        @Query("pageToken") nextPageId: String,
        @Query("maxResults") maxResults:Int = 5,
        @Query("key") key: String = API_KEY): Response<VideosList>

    @GET("videos")
    suspend fun getVideoDetails(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partStats: String = "statistics",
        @Query("id") id: String,
        @Query("key") key: String = API_KEY): Response<VideosList>

    @GET("channels")
    suspend fun getChannelFromId(
        @Query("part") part1: String = "snippet",
        @Query("part") part2: String = "statistics",
        @Query("id") id: String,
        @Query("key") key: String = API_KEY
    ): Response<ChannelDetails>

    @GET("search")
    suspend fun getSearchResults(
        @Query("part") partSnippet: String = "snippet",
        @Query("q") searchQuery: String,
        @Query("type") type: String = "video",
        @Query("key") key: String = API_KEY
    ) : Response<SearchedVideosList>

    @GET("search")
    suspend fun getNextSearchResults(
        @Query("part") partSnippet: String = "snippet",
        @Query("q") searchQuery: String,
        @Query("type") type: String = "video",
        @Query("pageToken") pageToken: String,
        @Query("key") key: String = API_KEY
    ) : Response<SearchedVideosList>

    @GET("search")
    suspend fun getVideosRelatedToCurrentVideo(
        @Query("part") partSnippet: String = "snippet",
        @Query("q") relatedToVideoId: String,
        @Query("type") type: String = "video",
        @Query("pageToken") pageToken: String?,
        @Query("key") key: String = API_KEY
    ): Response<SearchedVideosList>

    @GET("search")
    suspend fun getPopularVideosOfChannel(
        @Query("part") partSnippet: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("type") type: String = "video",
        @Query("pageToken") pageToken: String?,
        @Query("order") orderBy: String = "viewCount",
        @Query("maxResults") maxResults: Int = 50,
        @Query("key") key: String = API_KEY
    ): Response<PopularVideos>

    @GET("commentThreads")
    suspend fun getCommentsForVideo(
        @Query("part") partSnippet: String = "snippet",
        @Query("maxResults") totalResults: Int = 100,
        @Query("order") order:String = "relevance",
        @Query("videoId") videoId: String,
        @Query("key") key: String = API_KEY
    ) : Response<CommentThreadsList>

    @GET("comments")
    suspend fun getCommentReplies(
        @Query("part") partSnippet: String = "snippet",
        @Query("maxResults") totalResults: Int = 100,
        @Query("parentId") parentCommentId: String,
        @Query("key") key: String = API_KEY
    ) : Response<CommentRepliesList>

    @GET("channels")
    suspend fun getCompleteChannelDetails(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partStatistics: String = "statistics",
        @Query("part") partContentDetails: String = "contentDetails",
        @Query("part") partBrandingSettings: String = "brandingSettings",
        @Query("id") id: String,
        @Query("key") key: String = API_KEY
    ): Response<ChannelFullDetails>

    @GET("channelSections")
    suspend fun getChannelSections(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partContentDetails: String = "contentDetails",
        @Query("channelId") channelId: String,
        @Query("key") key: String = API_KEY
    ): Response<ChannelSections>

    @GET("playlists")
    suspend fun getChannelPlaylists(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partContentDetails: String = "contentDetails",
        @Query("maxResults") maxResults: Int = 10,
        @Query("channelId") channelId: String,
        @Query("key") key: String = API_KEY
    ): Response<ChannelPlaylists>

    @GET("playlists")
    suspend fun getAllPlaylistsForChannel(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partContentDetails: String = "contentDetails",
        @Query("pageToken") nextPageId: String?,
        @Query("maxResults") maxResults: Int = 10,
        @Query("channelId") channelId: String,
        @Query("key") key: String = API_KEY
    ): Response<Playlists>

    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") partSnippet: String = "snippet",
        @Query("part") partContentDetails: String = "contentDetails",
        @Query("part") partStatus: String = "status",
        @Query("pageToken") nextPageId: String?,
        @Query("maxResults") maxResults: Int = 50,
        @Query("playlistId") id: String,
        @Query("key") key: String = API_KEY
    ): Response<PlaylistItems>
}


















