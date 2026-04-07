package com.kanake10.translate.domain.models

data class Translation(
    val text: String,
    val translatedText: String,
    val source: String,
    val target: String
)

data class BatchTranslation(
    val text: String,
    val translatedText: String,
    val source: String,
    val target: String,
    val success: Boolean
)

data class HealthStatus(
    val isHealthy: Boolean,
    val service: String,
    val version: String
)

data class Language(
    val name: String,
    val code: String
)