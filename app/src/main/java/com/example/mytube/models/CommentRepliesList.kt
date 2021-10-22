package com.example.mytube.models

data class CommentRepliesList(
    val etag: String,
    val items: List<ItemX>,
    val kind: String,
    val pageInfo: PageInfoX
)