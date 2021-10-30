package com.example.mytube.models

data class ChannelHomePlaylistItem(
    val videoId: String,
    val videoThumbnailUrl: String,
    val videoViews: String = "",
    val videoTitle: String,
    val videoPostedTime: String
)
