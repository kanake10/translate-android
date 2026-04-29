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
package com.kanake10.translate.remote.dtos.batch

internal data class BatchTranslateRequest(
    val texts: List<String>,
    val source: String,
    val target: String
)

internal data class BatchTranslateResponse(
    val translations: List<BatchTranslationDto>,
    val total: Int,
    val successful: Int,
    val failed: Int
)

internal data class BatchTranslationDto(
    val text: String,
    val translation: String,
    val source: String,
    val target: String,
    val success: Boolean
)

