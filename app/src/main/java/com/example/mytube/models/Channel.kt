package com.example.mytube.models

import java.io.Serializable

data class Channel(
    var country: String = "",
    var description: String = "",
    var keywords: String = "",
    var title: String = "",
    var unsubscribedTrailer: String = ""
): Serializable