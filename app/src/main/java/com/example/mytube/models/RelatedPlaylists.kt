package com.example.mytube.models

import java.io.Serializable

data class RelatedPlaylists(
    var likes: String = "",
    var uploads: String = ""
): Serializable