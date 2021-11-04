package com.example.mytube.models

import java.io.Serializable

data class SnippetXXXXXXX(
    var channelId: String = "",
    var channelTitle: String = "",
    var description: String = "",
    val localized: LocalizedXX,
    var publishedAt: String = "",
    val thumbnails: ThumbnailsXXXX,
    var title: String = ""
): Serializable