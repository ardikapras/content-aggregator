package com.ardikapras.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsItemDto(val newsItemId: Int, val newsItemUrl: String, val parsingStrategy: String)
