package com.kanake10.translate.remote.dtos.health


internal data class HealthResponse(
    val status: String,
    val service: String,
    val version: String
)

internal data class SupportedLanguagesResponse(
    val supported_languages: Map<String, String>
)