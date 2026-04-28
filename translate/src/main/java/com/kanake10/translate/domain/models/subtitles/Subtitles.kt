package com.kanake10.translate.domain.models.subtitles

data class SubtitleRequest(
    val format: String,
    val content: String,
    val source: String,
    val target: String
)

data class SubtitleResult(
    val format: String,
    val content: String
)