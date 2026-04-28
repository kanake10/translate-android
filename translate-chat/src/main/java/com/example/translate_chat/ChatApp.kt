package com.example.translate_chat

import android.app.Application
import com.kanake10.translate.TranslateClient
import com.kanake10.translate.TranslateConfiguration

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val translateConfiguration = TranslateConfiguration.Builder(
            apiKey = ""
        )
            .baseUrl("https://api.translateplus.io/")
            .timeoutSeconds(30)
            .build()

        TranslateClient.initialize(translateConfiguration)
    }
}