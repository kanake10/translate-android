/*
 * Copyright 2026 Ezra Kanake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.remote.dtos.batch.BatchTranslateRequest
import com.kanake10.translate.remote.dtos.text.TranslateRequest
import com.kanake10.translate.remote.toDomain
import com.kanake10.translate.remote.toDto
import com.kanake10.translate.util.TranslateError
import com.kanake10.translate.util.TranslateResult
import com.kanake10.translate.util.safeApiCall

internal class TranslateRepositoryImpl(
    private val api: TranslateApi
) : TranslateRepository {

    override suspend fun translate(
        text: String,
        source: String,
        target: String,
    ): TranslateResult<Translation> {
        if (text.isBlank()) return TranslateResult.Error(
            TranslateError.BadRequest("Text must not be blank")
        )
        if (text.length > 5000) return TranslateResult.Error(
            TranslateError.BadRequest("Text must not exceed 5000 characters")
        )
        return safeApiCall {
            api.translate(TranslateRequest(text, source, target)).translations.toDomain()
        }
    }

    override suspend fun batchTranslate(
        texts: List<String>,
        source: String,
        target: String,
    ): TranslateResult<List<BatchTranslation>> {
        if (texts.isEmpty()) return TranslateResult.Error(
            TranslateError.BadRequest("Texts list must not be empty")
        )
        if (texts.size > 100) return TranslateResult.Error(
            TranslateError.BadRequest("Batch size must not exceed 100 texts")
        )
        return safeApiCall {
            api.batchTranslate(BatchTranslateRequest(texts, source, target))
                .translations.map { it.toDomain() }
        }
    }

    override suspend fun translateSubtitles(
        request: SubtitleRequest
    ): TranslateResult<SubtitleResult> {

        if (request.content.isBlank()) {
            return TranslateResult.Error(
                TranslateError.BadRequest("Subtitle content must not be blank")
            )
        }

        return safeApiCall {
            api.translateSubtitles(request.toDto()).toDomain()
        }
    }

    override suspend fun translateEmail(
        request: EmailRequest
    ): TranslateResult<EmailResult> {

        if (request.subject.isBlank()) {
            return TranslateResult.Error(
                TranslateError.BadRequest("Subject must not be blank")
            )
        }

        if (request.email_body.isBlank()) {
            return TranslateResult.Error(
                TranslateError.BadRequest("Email body must not be blank")
            )
        }

        return safeApiCall {
            api.translateEmail(request.toDto()).toDomain()
        }
    }

    override suspend fun translateHtml(
        request: HtmlRequest
    ): TranslateResult<HtmlResult> {

        if (request.html.isBlank()) {
            return TranslateResult.Error(
                TranslateError.BadRequest("HTML must not be blank")
            )
        }

        return safeApiCall {
            api.translateHtml(request.toDto()).toDomain()
        }
    }

    override suspend fun checkHealth(): TranslateResult<HealthStatus> {
        return safeApiCall {
            api.health().toDomain()
        }
    }

    override suspend fun getSupportedLanguages(): TranslateResult<List<Language>> {
        return safeApiCall {
            api.getSupportedLanguages().toDomain()
        }
    }
}