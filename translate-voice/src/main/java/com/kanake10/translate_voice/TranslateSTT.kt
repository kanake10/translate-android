package com.kanake10.translate_voice

import android.content.Intent
import android.speech.RecognizerIntent
import java.util.*

class TranslateSTT(
    private val onResult: (String) -> Unit
) {
    /**
     * Returns the Intent to launch STT with the desired language
     */
    fun getSpeechIntent(locale: Locale = Locale.getDefault()): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
    }

    /**
     * Helper function to process the result from ActivityResult
     */
    fun handleResult(data: Intent?) {
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!matches.isNullOrEmpty()) {
            onResult(matches[0])
        }
    }
}