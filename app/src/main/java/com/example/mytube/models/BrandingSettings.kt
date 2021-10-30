package com.example.mytube.models

import java.io.Serializable

data class BrandingSettings(
    val channel: Channel,
    var image: Image = Image("")
): Serializable