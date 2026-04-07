package com.kanake10.translate_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.kanake10.translate.TranslateClient
import com.kanake10.translate_ui.ui.TranslationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TranslateClient.Builder()
            .apiKey("tp_live_9235d8be34786a40f6698a363266eac4")
            .build()

        setContent {
            MaterialTheme {
                TranslationScreen(
                    translateTo = "de",
                    translateFrom = "en",
                    showHeader = false,
                    translateLanguageSelector = null
                )
            }
        }
    }
}

