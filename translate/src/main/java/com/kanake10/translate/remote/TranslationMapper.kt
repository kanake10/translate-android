package com.kanake10.translate.remote

import com.kanake10.translate.domain.models.BatchTranslation
import com.kanake10.translate.domain.models.HealthStatus
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.domain.models.Translation
import com.kanake10.translate.remote.dtos.batch.BatchTranslationDto
import com.kanake10.translate.remote.dtos.health.HealthResponse
import com.kanake10.translate.remote.dtos.health.SupportedLanguagesResponse
import com.kanake10.translate.remote.dtos.text.TranslationDto

internal fun TranslationDto.toDomain() = Translation(
    text = text,
    translatedText = translation,
    source = source,
    target = target
)

internal fun BatchTranslationDto.toDomain() = BatchTranslation(
    text = text,
    translatedText = translation,
    source = source,
    target = target,
    success = success
)

internal fun HealthResponse.toDomain() = HealthStatus(
    isHealthy = status == "ok",
    service = service,
    version = version
)

internal fun SupportedLanguagesResponse.toDomain(): List<Language> =
    supported_languages
        .map { (name, code) -> Language(name = name, code = code) }
        .sortedWith(compareByDescending<Language> { it.code == "auto" }.thenBy { it.name })