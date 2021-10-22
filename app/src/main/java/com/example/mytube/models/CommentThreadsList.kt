package com.example.mytube.models

data class CommentThreadsList(
    val etag: String,
    val items: List<Item>,
    val kind: String,
    val pageInfo: PageInfo
)