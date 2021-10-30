package com.example.mytube.models

import java.io.Serializable

data class PlaylistItems(
    val etag: String,
    var items: List<PlaylistItem>,
    val kind: String,
    var nextPageToken: String = "",
    val pageInfo: PageInfo
): Serializable