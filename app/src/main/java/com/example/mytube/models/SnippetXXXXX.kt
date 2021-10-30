package com.example.mytube.models

import java.io.Serializable

data class SnippetXXXXX(
    var channelId: String = "",
    var channelTitle: String = "",
    var description: String = "",
    val localized: LocalizedX,
    var publishedAt: String = "",
    val thumbnails: ThumbnailsX,
    val title: String
): Serializable