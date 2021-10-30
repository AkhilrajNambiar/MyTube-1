package com.example.mytube.models

import java.io.Serializable

data class SnippetXXXX(
    val channelId: String,
    val position: Int,
    var title: String = "",
    var type: String = ""
): Serializable