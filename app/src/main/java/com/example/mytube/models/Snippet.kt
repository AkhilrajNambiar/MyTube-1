package com.example.mytube.models

import java.io.Serializable

data class Snippet(
    val canReply: Boolean,
    val isPublic: Boolean,
    val topLevelComment: TopLevelComment,
    val totalReplyCount: Int,
    val videoId: String
): Serializable