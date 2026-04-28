package com.kanake10.translate.domain.models.email

data class EmailRequest(
    val subject: String,
    val email_body: String,
    val source: String,
    val target: String
)

data class EmailResult(
    val subject: String,
    val html_body: String
)