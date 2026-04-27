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

    @Volatile
    private var repository: TranslateRepository? = null

    fun initialize(configuration: Configuration) {
        val client = configuration.okHttpClient
            ?.newBuilder()
            ?.addInterceptor(TranslateInterceptors(configuration.apiKey))
            ?.build()
            ?: OkHttpClient.Builder()
                .addInterceptor(TranslateInterceptors(configuration.apiKey))
                .connectTimeout(configuration.timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(configuration.timeoutSeconds, TimeUnit.SECONDS)
                .writeTimeout(configuration.timeoutSeconds, TimeUnit.SECONDS)
                .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(configuration.baseUrl)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        repository = TranslateRepositoryImpl(
            retrofit.create(TranslateApi::class.java)
        )
    }

    fun getClient(): TranslateRepository {
        return requireNotNull(repository) {
            "TranslateClient is not initialized. Call TranslateClient.initialize() in Application class."
        }
    }
}

class Configuration private constructor(
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

        fun build(): Configuration {
            return Configuration(
                apiKey = apiKey,
                baseUrl = baseUrl,
                okHttpClient = okHttpClient,
                timeoutSeconds = timeoutSeconds
            )
        }
    }
}