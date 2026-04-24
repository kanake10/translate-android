package com.kanake10.translate

import com.kanake10.translate.remote.TranslateInterceptors
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.repo.TranslateRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object TranslateClient {

    @Volatile private var repository: TranslateRepository? = null

    fun init(builder: Builder) {
        repository = builder.buildRepository()
    }

    fun getClient(): TranslateRepository =
        repository ?: error(
            "TranslateClient is not initialized. "
        )

    class Builder {
        private var apiKey: String? = null
        private var baseUrl: String = "https://api.translateplus.io/"
        private var okHttpClient: OkHttpClient? = null
        private var timeoutSeconds: Long = 30L

        fun apiKey(key: String) = apply { apiKey = key }

        fun baseUrl(url: String) = apply { baseUrl = url }

        fun okHttpClient(client: OkHttpClient) = apply { okHttpClient = client }

        fun timeoutSeconds(seconds: Long) = apply { timeoutSeconds = seconds }

        internal fun buildRepository(): TranslateRepository {
            val key = requireNotNull(apiKey) {
                "apiKey must be provided. Call Builder.apiKey(\"your-key\")"
            }
            val client = okHttpClient
                ?.newBuilder()
                ?.addInterceptor(TranslateInterceptors(key))
                ?.build()
                ?: OkHttpClient.Builder()
                    .addInterceptor(TranslateInterceptors(key))
                    .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return TranslateRepositoryImpl(retrofit.create(TranslateApi::class.java))
        }
    }
}