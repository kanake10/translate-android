package com.kanake10.translate.util

import com.kanake10.translate.domain.models.email.EmailRequest
import com.kanake10.translate.domain.models.html.HtmlRequest
import com.kanake10.translate.domain.models.subtitles.SubtitleRequest
import com.kanake10.translate.remote.dtos.EmailResponseDto
import com.kanake10.translate.remote.dtos.HtmlResponseDto
import com.kanake10.translate.remote.dtos.SubtitleResponseDto
import com.kanake10.translate.remote.dtos.batch.BatchTranslateResponse
import com.kanake10.translate.remote.dtos.batch.BatchTranslationDto
import com.kanake10.translate.remote.dtos.text.TranslateResponse
import com.kanake10.translate.remote.dtos.text.TranslationDto

internal object  TranslateTestSamples {
    val translateResponse = TranslateResponse(
        translations = TranslationDto(
            text = "Hello",
            translation = "Bonjour",
            source = "en",
            target = "fr"
        ),
        details = emptyMap()
    )

    val batchResponse = BatchTranslateResponse(
        translations = listOf(
            BatchTranslationDto("Hello world", "Bonjour le monde", "en", "fr", true),
            BatchTranslationDto("How are you?", "Comment vas-tu?", "en", "fr", true),
            BatchTranslationDto("Good morning", "Bonjour", "en", "fr", true)
        ),
        total = 3,
        successful = 3,
        failed = 0
    )

    val batchInput = listOf(
        "Hello world",
        "How are you?",
        "Good morning"
    )

    val subtitleInput = SubtitleRequest(
        format = "srt",
        content = "1\n00:00:01,000 --> 00:00:02,000\nHello world",
        source = "en",
        target = "es"
    )

    val subtitleResponse = SubtitleResponseDto(
        format = "srt",
        content = """
            1
            00:00:01,000 --> 00:00:02,000
            Hola Mundo
        """.trimIndent()
    )

    val emailInput = EmailRequest(
        subject = "Welcome to our service",
        email_body = "<p>Thank you for signing up!</p><p>We are happy to have you.</p>",
        source = "auto",
        target = "fr"
    )

    val emailResponse = EmailResponseDto(
        subject = "Bienvenue à notre service.",
        html_body = "<p>Merci de vous être inscrit.</p><p>Nous sommes heureux de vous avoir.</p>"
    )

    val htmlInput = HtmlRequest(
        html = """
            <p>Hello <b>world</b>!</p>
            <p>This is a <i>test</i>.</p>
        """.trimIndent(),
        source = "auto",
        target = "fr"
    )

    val htmlResponse = HtmlResponseDto(
        html = """
            <p>Bonjour <b>monde</b>!</p>
            <p>C'est un <i>test</i>.</p>
        """.trimIndent()
    )
}