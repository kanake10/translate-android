package com.kanake10.translate.remote.api

import com.kanake10.translate.remote.dtos.EmailRequestDto
import com.kanake10.translate.remote.dtos.EmailResponseDto
import com.kanake10.translate.remote.dtos.HtmlRequestDto
import com.kanake10.translate.remote.dtos.HtmlResponseDto
import com.kanake10.translate.remote.dtos.SubtitleRequestDto
import com.kanake10.translate.remote.dtos.SubtitleResponseDto
import com.kanake10.translate.remote.dtos.batch.BatchTranslateRequest
import com.kanake10.translate.remote.dtos.batch.BatchTranslateResponse
import com.kanake10.translate.remote.dtos.health.HealthResponse
import com.kanake10.translate.remote.dtos.health.SupportedLanguagesResponse
import com.kanake10.translate.remote.dtos.text.TranslateRequest
import com.kanake10.translate.remote.dtos.text.TranslateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit interface defining all TranslatePlus API endpoints.
 *
 */
internal interface TranslateApi {

    @POST("v2/translate")
    suspend fun translate(
        @Body request: TranslateRequest
    ): TranslateResponse
    @POST("v2/translate/batch")
    suspend fun batchTranslate(
        @Body request: BatchTranslateRequest
    ): BatchTranslateResponse

    @POST("v2/translate/subtitles")
    suspend fun translateSubtitles(
        @Body request: SubtitleRequestDto
    ): SubtitleResponseDto

    @POST("v2/translate/email")
    suspend fun translateEmail(
        @Body request: EmailRequestDto
    ): EmailResponseDto

    @POST("v2/translate/html")
    suspend fun translateHtml(
        @Body request: HtmlRequestDto
    ): HtmlResponseDto

    @GET("health")
    suspend fun health(): HealthResponse

    @GET("v2/supported-languages")
    suspend fun getSupportedLanguages(): SupportedLanguagesResponse
}