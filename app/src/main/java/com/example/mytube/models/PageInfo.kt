package com.example.mytube.models

import java.io.Serializable

data class PageInfo(
    val resultsPerPage: Int,
    val totalResults: Int
): Serializable