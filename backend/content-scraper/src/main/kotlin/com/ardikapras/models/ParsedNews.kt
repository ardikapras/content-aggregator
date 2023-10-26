package com.ardikapras.models

import kotlinx.serialization.Serializable

@Serializable
data class ParsedNews(val title: String, val content: String)
