package com.example.mytube.adapters

import com.example.mytube.models.Equatable
import java.io.Serializable

data class VideoInPlaylistDetail(
    val videoId: String,
    val videoThumbnailUrl: String,
    val channelTitle: String,
    val videoTitle: String
): Serializable, Equatable