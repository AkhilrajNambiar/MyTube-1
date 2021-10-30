package com.example.mytube.models

import java.io.Serializable

data class SnippetXXX(
    var country: String = "",
    var customUrl: String = "",
    var description: String = "",
    var localized: Localized,
    var publishedAt: String = "",
    val thumbnails: Thumbnails,
    var title: String = ""
): Serializable