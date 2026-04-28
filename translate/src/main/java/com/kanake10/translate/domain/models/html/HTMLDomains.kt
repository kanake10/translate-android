package com.kanake10.translate.domain.models.html

data class HtmlRequest(
    val html: String,
    val source: String,
    val target: String
)

data class HtmlResult(
    val html: String
)