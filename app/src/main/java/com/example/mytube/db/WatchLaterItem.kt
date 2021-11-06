package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mytube.models.Equatable

@Entity(tableName = "watchLater")
data class WatchLaterItem(
    @PrimaryKey val videoId: String,
    val videoTitle: String,
    val videoChannelName: String,
    val videoThumbnailUrl: String,
    val videoViews: Long,
    val videoPublishedDate: Long,
    val videoAddedTime: Long,
    var videoWatchedAfterAddingToWatchLater: Boolean = false
): Equatable