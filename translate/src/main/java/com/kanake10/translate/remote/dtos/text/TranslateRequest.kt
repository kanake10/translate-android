package com.kanake10.translate.remote.dtos.text

data class TranslateRequest(
    val text: String,
    val source: String,
    val target: String
)