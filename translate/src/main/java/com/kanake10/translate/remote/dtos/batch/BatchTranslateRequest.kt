package com.kanake10.translate.remote.dtos.batch

internal data class BatchTranslateRequest(
    val texts: List<String>,
    val source: String,
    val target: String
)

internal data class BatchTranslateResponse(
    val translations: List<BatchTranslationDto>,
    val total: Int,
    val successful: Int,
    val failed: Int
)

internal data class BatchTranslationDto(
    val text: String,
    val translation: String,
    val source: String,
    val target: String,
    val success: Boolean
)

