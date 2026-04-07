package com.kanake10.translate.repo

import com.kanake10.translate.domain.models.BatchTranslation
import com.kanake10.translate.domain.models.HealthStatus
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.domain.models.Translation
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.remote.dtos.batch.BatchTranslateRequest
import com.kanake10.translate.remote.dtos.text.TranslateRequest
import com.kanake10.translate.remote.toDomain
import com.kanake10.translate.util.TranslateResult
import com.kanake10.translate.util.safeApiCall

internal class TranslateRepositoryImpl(
    private val api: TranslateApi
) : TranslateRepository {

    override suspend fun translate(
        text: String,
        source: String,
        target: String
    ): TranslateResult<Translation> {
        return safeApiCall {
            api.translate(TranslateRequest(text, source, target))
                .translations
                .toDomain()
        }
    }

    override suspend fun batchTranslate(
        texts: List<String>,
        source: String,
        target: String
    ): TranslateResult<List<BatchTranslation>> {
        return safeApiCall {
            api.batchTranslate(BatchTranslateRequest(texts, source, target))
                .translations
                .map { it.toDomain() }
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