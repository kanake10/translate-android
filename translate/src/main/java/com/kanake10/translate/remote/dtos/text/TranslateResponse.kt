package com.kanake10.translate.remote.dtos.text

internal data class TranslateResponse(
    val translations: TranslationDto,
    val details: Map<String, Any>?
)

internal data class TranslationDto(
    val text: String,
    val translation: String,
    val source: String,
    val target: String
)

