package com.kanake10.translate.remote.api

import com.kanake10.translate.remote.dtos.batch.BatchTranslateRequest
import com.kanake10.translate.remote.dtos.batch.BatchTranslateResponse
import com.kanake10.translate.remote.dtos.health.HealthResponse
import com.kanake10.translate.remote.dtos.health.SupportedLanguagesResponse
import com.kanake10.translate.remote.dtos.text.TranslateRequest
import com.kanake10.translate.remote.dtos.text.TranslateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface TranslateApi {
    @POST("v2/translate")
    suspend fun translate(
        @Body request: TranslateRequest
    ): TranslateResponse

    @POST("v2/translate/batch")
    suspend fun batchTranslate(
        @Body request: BatchTranslateRequest
    ): BatchTranslateResponse

    @GET("health")
    suspend fun health(): HealthResponse

    @GET("v2/supported-languages")
    suspend fun getSupportedLanguages(): SupportedLanguagesResponse

}