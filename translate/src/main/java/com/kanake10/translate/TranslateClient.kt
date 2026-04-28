package com.kanake10.translate

import com.kanake10.translate.domain.models.BatchTranslation
import com.kanake10.translate.domain.models.HealthStatus
import com.kanake10.translate.domain.models.Language
import com.kanake10.translate.domain.models.Translation
import com.kanake10.translate.remote.TranslateInterceptors
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.repo.TranslateRepositoryImpl
import com.kanake10.translate.util.TranslateResult
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TranslateClient {

    @Volatile
    private var repository: TranslateRepository? = null

    private fun getRepository(): TranslateRepository {
        return requireNotNull(repository) {
            "TranslateClient is not initialized. Call TranslateClient.initialize() in Application class."
        }
    }

    suspend fun translate(
        text: String,
        source: String = "auto",
        target: String
    ): TranslateResult<Translation> {
        return getRepository().translate(text, source, target)
    }

    suspend fun batchTranslate(
        texts: List<String>,
        source: String = "auto",
        target: String
    ): TranslateResult<List<BatchTranslation>> {
        return getRepository().batchTranslate(texts, source, target)
    }

    suspend fun checkHealth(): TranslateResult<HealthStatus> {
        return getRepository().checkHealth()
    }

    suspend fun getSupportedLanguages(): TranslateResult<List<Language>> {
        return getRepository().getSupportedLanguages()
    }
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

class TranslateConfiguration private constructor(
    val apiKey: String,
    val baseUrl: String,
    val okHttpClient: OkHttpClient?,
    val timeoutSeconds: Long
) {
    class Builder(private val apiKey: String) {

        private var baseUrl: String = "https://api.translateplus.io/"
        private var okHttpClient: OkHttpClient? = null
        private var timeoutSeconds: Long = 30L

        fun baseUrl(url: String) = apply { baseUrl = url }

        fun okHttpClient(client: OkHttpClient) = apply { okHttpClient = client }

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