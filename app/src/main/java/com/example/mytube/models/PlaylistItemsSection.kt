package com.example.mytube.models

data class PlaylistItemsSection(
    var title: String = "",
    var description: String = "",
    var playlistItems: List<ChannelHomePlaylistItem>
)