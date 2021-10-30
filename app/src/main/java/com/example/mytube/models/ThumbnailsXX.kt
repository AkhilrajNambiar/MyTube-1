package com.example.mytube.models

import java.io.Serializable

data class ThumbnailsXX(
    val default: Default,
    var high: High = High(0, "", 0),
    var maxres: Maxres = Maxres(0, "", 0),
    val medium: Medium = Medium(0, "", 0),
    val standard: Standard = Standard(0, "", 0)
): Serializable