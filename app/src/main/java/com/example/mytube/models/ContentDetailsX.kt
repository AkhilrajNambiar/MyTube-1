package com.example.mytube.models

import java.io.Serializable

data class ContentDetailsX(
    var playlists: List<String> = listOf(),
    var channels: List<String> = listOf()
): Serializable