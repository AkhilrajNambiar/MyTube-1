package com.example.mytube.models

import androidx.lifecycle.LiveData
import com.squareup.moshi.Json
import org.w3c.dom.Comment
import java.io.Serializable

data class VideosList(
    val kind: String,
    @Json(name = "etag")val eTag: String,
    val items:MutableList<AboutVideo>,
    var nextPageToken: String = "",
    val pageInfo: PageInformation
)

data class SearchedVideosList(
    val kind: String,
    @Json(name = "etag")val eTag: String,
    val items:MutableList<AboutSearchVideo>,
    var nextPageToken: String = "",
    val pageInfo: PageInformation
)

data class AboutVideo(
    val kind: String,
    @Json(name = "etag")val eTag: String,
    val id: String,
    val snippet: VideoSnippet,
    var statistics: VideoStats = VideoStats("")
): Serializable

data class AboutSearchVideo(
    val kind: String,
    @Json(name = "etag")val eTag: String,
    val id: SearchedVideoId,
    var snippet: VideoSnippet,
    var statistics: VideoStats = VideoStats("")
)

data class PageInformation(
    val totalResults: Int,
    val resultsPerPage: Int
)

data class VideoSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: ThumbnailSizes,
    val channelTitle: String,
    var tags: List<String> = listOf()
): Serializable

data class ThumbnailSizes(
    @Json(name="default")var defaultThumb: ThumbnailFeatures? = null,
    var medium: ThumbnailFeatures? = null,
    var high: ThumbnailFeatures? = null,
    var standard: ThumbnailFeatures? = null,
    var maxres: ThumbnailFeatures? = null
): Serializable

data class ThumbnailFeatures(
    val url: String,
    val width: Int,
    val height: Int
): Serializable

data class VideoStats(
    var viewCount: String,
    var likeCount: Int = 0,
    var dislikeCount: Int = 0,
    var favoriteCount: Int = 0,
    var commentCount: Int = 0
): Serializable

data class ChannelSnippet(
    val title: String,
    var customUrl: String = "",
    val publishedAt: String,
    val thumbnails: ThumbnailSizes
)

data class ChannelStats(
    var viewCount: Long = 0,
    var subscriberCount: Long = 0,
    var hiddenSubscriberCount: Boolean = false,
    var videoCount: Long = 0
)

data class ChannelDetails(
    val items: List<Channels>
)

data class Channels(
    var id: String = "",
    val snippet: ChannelSnippet,
    val statistics: ChannelStats
)

data class SearchedVideoId(
    val kind: String,
    val videoId: String
): Serializable


data class CommentSnippet(
    val videoId: String,
    val textDisplay: String,
    val textOriginal: String,
    val authorDisplayName: String,
    val authorProfileImageUrl: String,
    val authorChannelId: AuthorChannelId
)

data class YoutubeComment(
    val id: String,
    val snippet: CommentSnippet,
    var canRate: Boolean = false,
    var likeCount: Int = 0,
    var publishedAt: String = "",
    var updatedAt: String = ""
)

data class SingleComment(
    val videoId: String,
    val topLevelComment: YoutubeComment
)

