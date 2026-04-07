package com.kanake10.translate_voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TranslateTTS(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            tts?.language = Locale.getDefault()
        }
    }

    fun speak(text: String, locale: Locale? = null) {
        if (ready) {
            if (locale != null) {
                tts?.language = locale
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}