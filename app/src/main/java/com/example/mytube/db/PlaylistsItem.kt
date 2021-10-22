package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlistItem")
data class PlaylistItem (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val playlistName: String,
    val videoId: String,
    val channelId: String
)