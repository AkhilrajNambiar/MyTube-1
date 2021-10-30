package com.example.mytube.models

data class PopularVideos(
    val etag: String,
    val items: List<ItemXXXXX>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfoXXXXX,
    val regionCode: String
)