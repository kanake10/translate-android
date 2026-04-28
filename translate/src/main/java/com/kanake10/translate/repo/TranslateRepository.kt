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

interface TranslateRepository {
    suspend fun translate(
        text: String,
        source: String = "auto",
        target: String
    ): TranslateResult<Translation>

    suspend fun batchTranslate(
        texts: List<String>,
        source: String = "auto",
        target: String
    ): TranslateResult<List<BatchTranslation>>

    suspend fun translateSubtitles(
        request: SubtitleRequest
    ): TranslateResult<SubtitleResult>

    suspend fun translateEmail(
        request: EmailRequest
    ): TranslateResult<EmailResult>

    suspend fun translateHtml(
        request: HtmlRequest
    ): TranslateResult<HtmlResult>
    suspend fun checkHealth(): TranslateResult<HealthStatus>

    suspend fun getSupportedLanguages(): TranslateResult<List<Language>>

}