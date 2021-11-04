package com.example.mytube.models

import com.example.mytube.models.Equatable
import java.io.Serializable

data class VideoInPlaylistDetail(
    val videoId: String,
    val videoThumbnailUrl: String,
    val channelTitle: String,
    val videoTitle: String
): Serializable, Equatable