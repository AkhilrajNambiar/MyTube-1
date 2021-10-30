package com.example.mytube.models

import java.io.Serializable

data class ItemXXX(
    var contentDetails: ContentDetailsX = ContentDetailsX(listOf("")),
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: SnippetXXXX
): Serializable