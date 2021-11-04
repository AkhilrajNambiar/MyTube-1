package com.example.mytube.models

import java.io.Serializable

data class SinglePlaylist(
    val contentDetails: ContentDetailsXXXX,
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: SnippetXXXXXXX
): Serializable, Equatable