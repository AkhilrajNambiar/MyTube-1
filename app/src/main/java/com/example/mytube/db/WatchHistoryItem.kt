package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchHistory")
data class WatchHistoryItem(
    @PrimaryKey val videoId: String,
    val videoTitle: String,
    val videoChannelName: String,
    val videoThumbnailUrl: String,
    val videoViews: String,
    val videoWatchedTime: Long
)