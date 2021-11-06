package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytube.models.Equatable

@Entity(tableName = "likedVideos")
data class LikedVideoItem(
    @PrimaryKey val videoId: String,
    val videoTitle: String,
    val videoChannelName: String,
    val videoThumbnailUrl: String,
    val videoAddedTime: Long,
    var likedAlready: Boolean = false,
    val likeCount: Long
): Equatable