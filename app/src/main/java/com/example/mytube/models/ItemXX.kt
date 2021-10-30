package com.example.mytube.models

import java.io.Serializable

data class ItemXX(
    val brandingSettings: BrandingSettings,
    val contentDetails: ContentDetails,
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: SnippetXXX,
    val statistics: Statistics
): Serializable