package com.example.mytube.models

import java.io.Serializable

data class ChannelPlaylists(
    val etag: String,
    val items: List<ItemXXXX>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
): Serializable