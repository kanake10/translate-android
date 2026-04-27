package com.example.translate_chat

import android.app.Application
import com.kanake10.translate.Configuration
import com.kanake10.translate.TranslateClient

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val configuration = Configuration.Builder(
            apiKey = ""
        )
            .baseUrl("https://api.translateplus.io/")
            .timeoutSeconds(30)
            .build()

        TranslateClient.initialize(configuration)
    }
}