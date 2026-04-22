package com.kanake10.translate_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.kanake10.translate.TranslateClient
import com.kanake10.translate_ui.ui.TranslationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TranslateClient.init(
            TranslateClient.Builder()
                .apiKey("")
        )

        setContent {
            MaterialTheme {
                TranslationScreen()
            }
        }
    }
}

