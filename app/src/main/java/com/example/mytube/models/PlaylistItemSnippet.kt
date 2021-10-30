package com.example.mytube.models

import java.io.Serializable

data class PlaylistItemSnippet(
    var channelId: String = "",
    var channelTitle: String = "",
    var description: String = "",
    var playlistId: String = "",
    var position: Int = 0,
    var publishedAt: String = "",
    val resourceId: ResourceId,
    val thumbnails: ThumbnailsXX,
    var title: String = "",
    var videoOwnerChannelId: String = "",
    var videoOwnerChannelTitle: String = ""
): Serializable