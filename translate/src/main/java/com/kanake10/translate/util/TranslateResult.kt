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
package com.kanake10.translate.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

private const val EMPTY_DETAIL = ""
private const val UNKNOWN_ERROR_CODE = -1
private const val CODE_BAD_REQUEST = 400
private const val CODE_UNAUTHORIZED = 401
private const val CODE_PAYMENT_REQUIRED = 402
private const val CODE_FORBIDDEN = 403
private const val CODE_NOT_FOUND = 404
private const val CODE_TOO_MANY_REQUESTS = 429
private const val CODE_INTERNAL_SERVER_ERROR = 500
private const val CODE_SERVICE_UNAVAILABLE = 503

/**
 * Represents all possible errors from the TranslatePlus SDK.
 *
 * Includes:
 * - Network errors
 * - Authentication issues
 * - Rate limiting
 * - API failures
 * - Validation errors
 */
sealed class TranslateError {
    object MissingApiKey : TranslateError()
    object InvalidApiKey : TranslateError()
    data class InsufficientCredits(val remaining: Int, val required: Int) : TranslateError()
    object RateLimitExceeded : TranslateError()
    data class BadRequest(val detail: String) : TranslateError()
    object NotFound : TranslateError()
    object InternalServerError : TranslateError()
    object ServiceUnavailable : TranslateError()
    data class NetworkError(val detail: String) : TranslateError()
    data class Unknown(val code: Int, val detail: String) : TranslateError()

    /**
     * Converts error into a human-readable message.
     *
     * Useful for displaying UI-friendly error messages.
     *
     * @return readable error string
     */
    fun toMessage(): String = when (this) {
        is MissingApiKey -> "API key is missing"
        is InvalidApiKey -> "Invalid API key"
        is InsufficientCredits -> "Not enough credits"
        is RateLimitExceeded -> "Too many requests. Try again later"
        is BadRequest -> detail
        is NotFound -> "Resource not found"
        is InternalServerError -> "Server error"
        is ServiceUnavailable -> "Service unavailable"
        is NetworkError -> "No internet connection"
        is Unknown -> detail.ifEmpty { "Something went wrong" }
    }
}

/**
 * Wrapper for all SDK responses.
 *
 * Represents either:
 * - [Success] containing valid data
 * - [Error] containing a [TranslateError]
 */
sealed class TranslateResult<out T> {
    data class Success<T>(val data: T) : TranslateResult<T>()
    data class Error(val error: TranslateError) : TranslateResult<Nothing>()
}

internal fun HttpException.toTranslateError(): TranslateError {
    val errorBody = this.response()?.errorBody()?.string().orEmpty()
    val detail = runCatching {
        JSONObject(errorBody).optString("detail", EMPTY_DETAIL)
    }.getOrDefault(EMPTY_DETAIL)

    return when (HttpStatusCode.from(this.code())) {
        HttpStatusCode.BAD_REQUEST -> TranslateError.BadRequest(detail)
        HttpStatusCode.UNAUTHORIZED -> TranslateError.InvalidApiKey

        HttpStatusCode.PAYMENT_REQUIRED -> {
            val match = """remaining (\d+).+required (\d+)""".toRegex().find(detail)
            TranslateError.InsufficientCredits(
                remaining = match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0,
                required = match?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 0,
            )
        }

        HttpStatusCode.FORBIDDEN -> if (detail.contains("required", ignoreCase = true)) {
            TranslateError.MissingApiKey
        } else {
            TranslateError.InvalidApiKey
        }

        HttpStatusCode.NOT_FOUND -> TranslateError.NotFound
        HttpStatusCode.TOO_MANY_REQUESTS -> TranslateError.RateLimitExceeded
        HttpStatusCode.INTERNAL_SERVER_ERROR -> TranslateError.InternalServerError
        HttpStatusCode.SERVICE_UNAVAILABLE -> TranslateError.ServiceUnavailable

        null -> TranslateError.Unknown(this.code(), detail)
    }
}

internal enum class HttpStatusCode(val code: Int) {
    BAD_REQUEST(CODE_BAD_REQUEST),
    UNAUTHORIZED(CODE_UNAUTHORIZED),
    PAYMENT_REQUIRED(CODE_PAYMENT_REQUIRED),
    FORBIDDEN(CODE_FORBIDDEN),
    NOT_FOUND(CODE_NOT_FOUND),
    TOO_MANY_REQUESTS(CODE_TOO_MANY_REQUESTS),
    INTERNAL_SERVER_ERROR(CODE_INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE(CODE_SERVICE_UNAVAILABLE);

    companion object {
        fun from(code: Int): HttpStatusCode? =
            entries.firstOrNull { it.code == code }
    }
}

/**
 * Safely executes an API call and wraps the result in [TranslateResult].
 *
 * Handles:
 * - HTTP exceptions
 * - IO/network errors
 * - Unexpected runtime exceptions
 *
 * @param call suspend API call
 * @return wrapped success or error result
 */
internal suspend fun <T> safeApiCall(
    call: suspend () -> T
): TranslateResult<T> = try {
    TranslateResult.Success(call())
} catch (e: HttpException) {
    TranslateResult.Error(e.toTranslateError())
} catch (e: IOException) {
    TranslateResult.Error(TranslateError.NetworkError(e.message ?: "IO error"))
} catch (e: IllegalStateException) {
    TranslateResult.Error(
        TranslateError.Unknown(UNKNOWN_ERROR_CODE, e.message ?: "Illegal state")
    )
} catch (e: IllegalArgumentException) {
    TranslateResult.Error(
        TranslateError.Unknown(UNKNOWN_ERROR_CODE, e.message ?: "Illegal argument")
    )
}