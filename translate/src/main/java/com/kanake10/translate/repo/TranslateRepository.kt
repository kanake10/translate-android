package com.kanake10.translate.repo

import com.kanake10.translate.domain.models.BatchTranslation
import com.kanake10.translate.domain.models.HealthStatus
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.domain.models.Translation
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

    suspend fun checkHealth(): TranslateResult<HealthStatus>

    suspend fun getSupportedLanguages(): TranslateResult<List<Language>>

}