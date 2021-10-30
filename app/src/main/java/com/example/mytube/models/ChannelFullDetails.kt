package com.example.mytube.models

import java.io.Serializable

data class ChannelFullDetails(
    val etag: String,
    val items: List<ItemXX>,
    val kind: String,
    val pageInfo: PageInfoXX
): Serializable