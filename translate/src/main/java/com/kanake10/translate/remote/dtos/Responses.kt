package com.kanake10.translate.remote.dtos

internal data class SubtitleRequestDto(
    val format: String,
    val content: String,
    val source: String,
    val target: String
)

internal data class SubtitleResponseDto(
    val format: String,
    val content: String
)

internal data class EmailRequestDto(
    val subject: String,
    val email_body: String,
    val source: String,
    val target: String
)

internal data class EmailResponseDto(
    val subject: String,
    val html_body: String
)

internal data class HtmlRequestDto(
    val html: String,
    val source: String,
    val target: String
)

internal data class HtmlResponseDto(
    val html: String
)