package com.example.mytube.models

import java.io.Serializable

data class ThumbnailsX(
    val default: Default,
    var high: High = High(0, "", 0),
    val maxres: Maxres = Maxres(0, "", 0),
    val medium: Medium = Medium(0, "", 0),
    val standard: Standard = Standard(0, "", 0)
): Serializable