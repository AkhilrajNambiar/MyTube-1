package com.example.mytube.models

import java.io.Serializable

data class TopLevelComment(
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: SnippetX
): Serializable