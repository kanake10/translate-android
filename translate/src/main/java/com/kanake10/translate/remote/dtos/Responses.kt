/*
 * Copyright 2026 Ezra Kanake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kanake10.translate.remote.dtos

internal data class SubtitleRequestDto(
    val format: String,
    val content: String,
    val source: String,
    val target: String
)

internal data class SubtitleResponseDto(
    val format: String,
    val content: String
)

internal data class EmailRequestDto(
    val subject: String,
    val email_body: String,
    val source: String,
    val target: String
)

internal data class EmailResponseDto(
    val subject: String,
    val html_body: String
)

internal data class HtmlRequestDto(
    val html: String,
    val source: String,
    val target: String
)

internal data class HtmlResponseDto(
    val html: String
)