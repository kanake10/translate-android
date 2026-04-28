package com.kanake10.translate.remote

import com.kanake10.translate.domain.models.BatchTranslation
import com.kanake10.translate.domain.models.HealthStatus
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.domain.models.Translation
import com.kanake10.translate.domain.models.email.EmailRequest
import com.kanake10.translate.domain.models.email.EmailResult
import com.kanake10.translate.domain.models.html.HtmlRequest
import com.kanake10.translate.domain.models.html.HtmlResult
import com.kanake10.translate.domain.models.subtitles.SubtitleRequest
import com.kanake10.translate.domain.models.subtitles.SubtitleResult
import com.kanake10.translate.remote.dtos.EmailRequestDto
import com.kanake10.translate.remote.dtos.EmailResponseDto
import com.kanake10.translate.remote.dtos.HtmlRequestDto
import com.kanake10.translate.remote.dtos.HtmlResponseDto
import com.kanake10.translate.remote.dtos.SubtitleRequestDto
import com.kanake10.translate.remote.dtos.SubtitleResponseDto
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

internal fun SubtitleRequest.toDto() = SubtitleRequestDto(
    format = format,
    content = content,
    source = source,
    target = target
)

internal fun SubtitleResponseDto.toDomain() = SubtitleResult(
    format = format,
    content = content
)

internal fun EmailRequest.toDto() = EmailRequestDto(
    subject = subject,
    email_body = email_body,
    source = source,
    target = target
)

internal fun EmailResponseDto.toDomain() = EmailResult(
    subject = subject,
    html_body = html_body
)

internal fun HtmlRequest.toDto() = HtmlRequestDto(
    html = html,
    source = source,
    target = target
)

internal fun HtmlResponseDto.toDomain() = HtmlResult(
    html = html
)