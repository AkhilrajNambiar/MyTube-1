package com.example.mytube.models

import java.io.Serializable

data class ResourceId(
    var kind: String = "",
    var videoId: String = ""
): Serializable