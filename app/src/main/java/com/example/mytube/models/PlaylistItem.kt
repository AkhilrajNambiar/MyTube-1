package com.example.mytube.models

import java.io.Serializable

data class PlaylistItem(
    val contentDetails: ContentDetailsXXX,
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: PlaylistItemSnippet,
    val status: Status
): Serializable