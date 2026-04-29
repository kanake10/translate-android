package com.kanake10.translate.repo

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
import com.kanake10.translate.util.TranslateResult

/**
 * Internal abstraction layer for translation operations.
 *
 * Handles:
 * - API calls
 * - Input validation
 * - Mapping responses to domain models
 * - Error handling
 */
interface TranslateRepository {

    /**
     * Translates a single text string.
     */
    suspend fun translate(
        text: String,
        source: String = "auto",
        target: String
    ): TranslateResult<Translation>

    /**
     * Translates multiple text strings in batch mode.
     */
    suspend fun batchTranslate(
        texts: List<String>,
        source: String = "auto",
        target: String
    ): TranslateResult<List<BatchTranslation>>

    /**
     * Translates subtitle content while preserving format.
     */
    suspend fun translateSubtitles(
        request: SubtitleRequest
    ): TranslateResult<SubtitleResult>

    /**
     * Translates email subject and body content.
     */
    suspend fun translateEmail(
        request: EmailRequest
    ): TranslateResult<EmailResult>

    /**
     * Translates HTML while preserving markup structure.
     */
    suspend fun translateHtml(
        request: HtmlRequest
    ): TranslateResult<HtmlResult>
    suspend fun checkHealth(): TranslateResult<HealthStatus>

    /**
     * Fetches supported languages from the API.
     */
    suspend fun getSupportedLanguages(): TranslateResult<List<Language>>

}