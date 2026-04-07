package com.kanake10.translate

import com.kanake10.translate.remote.TranslateInterceptors
import com.kanake10.translate.remote.api.TranslateApi
import com.kanake10.translate.repo.TranslateRepository
import com.kanake10.translate.repo.TranslateRepositoryImpl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
object TranslateClient {

    private lateinit var repository: TranslateRepository

    class Builder {
        private var apiKey: String? = null
        private var baseUrl: String = "https://api.translateplus.io/"
        private var okHttpClient: OkHttpClient? = null

        fun apiKey(key: String) = apply { this.apiKey = key }

        fun build() {
            val key = apiKey ?: throw IllegalArgumentException("API key must be provided")

            val client = okHttpClient ?: OkHttpClient.Builder()
                .addInterceptor(TranslateInterceptors(key))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            val api = retrofit.create(TranslateApi::class.java)
            repository = TranslateRepositoryImpl(api)

        }
    }

    fun getClient(): TranslateRepository {
        check(::repository.isInitialized) { "TranslateSdk is not initialized" }
        return repository
    }
}