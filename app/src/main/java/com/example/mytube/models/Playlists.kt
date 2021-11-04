package com.example.mytube.models

import java.io.Serializable

data class Playlists(
    var etag: String = "",
    val items: List<SinglePlaylist>,
    var kind: String = "",
    var nextPageToken: String = "",
    var pageInfo: PageInfo
): Serializable