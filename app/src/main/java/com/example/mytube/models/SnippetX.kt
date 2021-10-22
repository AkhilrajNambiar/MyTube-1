package com.example.mytube.models

data class SnippetX(
    val authorChannelId: AuthorChannelId,
    val authorChannelUrl: String,
    val authorDisplayName: String,
    val authorProfileImageUrl: String,
    val canRate: Boolean,
    val likeCount: Int,
    val publishedAt: String,
    val textDisplay: String,
    val textOriginal: String,
    val updatedAt: String,
    val videoId: String,
    val viewerRating: String
)