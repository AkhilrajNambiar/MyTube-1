package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "likedVideos")
data class LikedVideoItem(
    @PrimaryKey val videoId: String,
    val videoTitle: String,
    val videoChannelName: String,
    val videoThumbnailUrl: String,
    val videoViews: Long,
    val videoAddedTime: Long,
)