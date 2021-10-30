package com.example.mytube.models

import java.io.Serializable

data class Statistics(
    var hiddenSubscriberCount: Boolean = false,
    var subscriberCount: String = "",
    var videoCount: String = "",
    var viewCount: String = ""
): Serializable