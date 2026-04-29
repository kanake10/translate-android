package com.kanake10.translate

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
import com.kanake10.translate.remote.TranslateInterceptors
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.repo.TranslateRepositoryImpl
import com.kanake10.translate.util.TranslateResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Entry point for the Translate SDK.
 *
 *  Provides access to all translation features including:
 * - Single text translation
 * - Batch translation
 * - Subtitle translation
 * - Email translation
 * - HTML translation
 * - Language support
 * */

object TranslateClient {

    @Volatile
    private var repository: TranslateRepository? = null

    private fun getRepository(): TranslateRepository {
        return requireNotNull(repository) {
            "TranslateClient is not initialized. Call TranslateClient.initialize() in Application class."
        }
    }

    /**
     * Translates a single text string from a source language to a target language.
     *
     * @param text the text to translate
     * @param source source language code (default: "auto")
     * @param target target language code (required)
     *
     * @return [TranslateResult] containing either [Translation] or [TranslateError]
     */
    suspend fun translate(
        text: String,
        source: String = "auto",
        target: String
    ): TranslateResult<Translation> {
        return getRepository().translate(text, source, target)
    }

    /**
     * Translates multiple text strings in a single request.
     *
     * @param texts list of texts to translate
     * @param source source language code (default: "auto")
     * @param target target language code
     *
     * @return [TranslateResult] containing list of [BatchTranslation]
     */
    suspend fun batchTranslate(
        texts: List<String>,
        source: String = "auto",
        target: String
    ): TranslateResult<List<BatchTranslation>> {
        return getRepository().batchTranslate(texts, source, target)
    }

    /**
     * Translates subtitle content while preserving structure and timing.
     *
     * @param request Subtitle translation request for [SubtitleRequest]
     * Supported formats for [SubtitleRequest.format]:
     * - "srt"
     * - "vtt"
     *
     * @return Translated subtitle result wrapped in [TranslateResult]
     */
    suspend fun translateSubtitles(
        request: SubtitleRequest
    ): TranslateResult<SubtitleResult> {
        return getRepository().translateSubtitles(request)
    }

    /**
     * Translates an email including subject and body while preserving formatting.
     *
     * @param request email translation request for [EmailRequest]
     *
     * @return translated email content wrapped in [TranslateResult]
     */
    suspend fun translateEmail(
        request: EmailRequest
    ): TranslateResult<EmailResult> {
        return getRepository().translateEmail(request)
    }

    /**
     * Translates HTML content while preserving tags and structure.
     *
     * @param request HTML translation request for [HtmlRequest]
     *
     * @return translated HTML result wrapped in [TranslateResult]
     */
    suspend fun translateHtml(
        request: HtmlRequest
    ): TranslateResult<HtmlResult> {
        return getRepository().translateHtml(request)
    }

    suspend fun checkHealth(): TranslateResult<HealthStatus> {
        return getRepository().checkHealth()
    }

    /**
     * Retrieves all languages supported.
     *
     * @return list of supported [Language] wrapped in [TranslateResult]
     */
    suspend fun getSupportedLanguages(): TranslateResult<List<Language>> {
        return getRepository().getSupportedLanguages()
    }

    /**
     * Initializes the TranslateClient with required configuration.
     *
     * This method sets up:
     * - Retrofit networking layer
     * - OkHttp client with API key interceptor
     * - Repository implementation
     *
     *
     * @param translateConfiguration configuration object containing API key, base URL and timeout settings
     */
    fun initialize(translateConfiguration: TranslateConfiguration) {
        val client = translateConfiguration.okHttpClient
            ?.newBuilder()
            ?.addInterceptor(TranslateInterceptors(translateConfiguration.apiKey))
            ?.build()
            ?: OkHttpClient.Builder()
                .addInterceptor(TranslateInterceptors(translateConfiguration.apiKey))
                .connectTimeout(translateConfiguration.timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(translateConfiguration.timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(translateConfiguration.timeoutSeconds, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(translateConfiguration.baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        repository = TranslateRepositoryImpl(
            retrofit.create(TranslateApi::class.java)
        )
    }
}

/**
 * Configuration object used to initialize the Translate SDK.
 *
 * Contains:
 * - API key
 * - Base URL
 * - OkHttp client (optional)
 * - Timeout settings
 */
class TranslateConfiguration private constructor(
    val apiKey: String,
    val baseUrl: String,
    val okHttpClient: OkHttpClient?,
    val timeoutSeconds: Long
) {

    /**
     * Builder used to construct [TranslateConfiguration].
     *
     * @param apiKey API key provided by TranslatePlus
     */
    class Builder(private val apiKey: String) {

        private var baseUrl: String = "https://api.translateplus.io/"
        private var okHttpClient: OkHttpClient? = null
        private var timeoutSeconds: Long = 30L

        fun baseUrl(url: String) = apply { baseUrl = url }

        /**
         * Sets a custom OkHttpClient for networking customization.
         *
         * Useful for:
         * - Logging interceptors
         * - Caching
         * - Custom timeouts
         */
        fun okHttpClient(client: OkHttpClient) = apply { okHttpClient = client }

        /**
         * Sets network timeout in seconds for all requests.
         *
         * @param seconds timeout duration
         */
        fun timeoutSeconds(seconds: Long) = apply { timeoutSeconds = seconds }

        fun build(): TranslateConfiguration {
            return TranslateConfiguration(
                apiKey = apiKey,
                baseUrl = baseUrl,
                okHttpClient = okHttpClient,
                timeoutSeconds = timeoutSeconds
            )
        }
    }
}