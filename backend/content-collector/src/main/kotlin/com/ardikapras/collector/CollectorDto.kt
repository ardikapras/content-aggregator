package com.ardikapras.collector

import kotlinx.serialization.Serializable

@Serializable
data class NewsItemDto(val newsItemId: Int, val newsItemUrl: String)
