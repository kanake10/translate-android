package com.example.translate_chat

import android.app.Application
import com.kanake10.translate.TranslateClient

class ChatApp : Application() {

    override fun onCreate() {
        super.onCreate()

        TranslateClient.init(
            TranslateClient.Builder()
                .apiKey("")
        )
    }
}