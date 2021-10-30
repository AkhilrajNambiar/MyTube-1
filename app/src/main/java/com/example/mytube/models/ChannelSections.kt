package com.example.mytube.models

import java.io.Serializable

data class ChannelSections(
    val etag: String,
    val items: List<ItemXXX>,
    val kind: String
): Serializable