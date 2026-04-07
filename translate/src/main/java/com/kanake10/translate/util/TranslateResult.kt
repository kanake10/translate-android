package com.kanake10.translate.util

import java.io.IOException
import org.json.JSONObject
import retrofit2.HttpException
sealed class TranslateError {
    object MissingApiKey : TranslateError()
    object InvalidApiKey : TranslateError()
    data class InsufficientCredits(val remaining: Int, val required: Int) : TranslateError()
    object RateLimitExceeded : TranslateError()
    data class BadRequest(val detail: String) : TranslateError()
    object NotFound : TranslateError()
    object InternalServerError : TranslateError()
    object ServiceUnavailable : TranslateError()
    object NetworkError : TranslateError()
    data class Unknown(val code: Int, val detail: String) : TranslateError()
}

sealed class TranslateResult<out T> {
    data class Success<T>(val data: T) : TranslateResult<T>()
    data class Error(val error: TranslateError) : TranslateResult<Nothing>()
}

internal fun HttpException.toTranslateError(): TranslateError {

    val errorBody = this.response()?.errorBody()?.string().orEmpty()

    val detail = try {
        val json = JSONObject(errorBody)
        json.optString("detail", "")
    } catch (_: Exception) {
        ""
    }

    return when (this.code()) {

        400 -> TranslateError.BadRequest(detail)

        401 -> TranslateError.InvalidApiKey

        402 -> {
            val regex = """remaining (\d+).+required (\d+)""".toRegex()
            val match = regex.find(detail)

            val remaining = match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0
            val required = match?.groupValues?.getOrNull(2)?.toIntOrNull() ?: 0

            TranslateError.InsufficientCredits(remaining, required)
        }

        403 -> if (detail.contains("required", ignoreCase = true)) {
            TranslateError.MissingApiKey
        } else {
            TranslateError.InvalidApiKey
        }

        404 -> TranslateError.NotFound

        429 -> TranslateError.RateLimitExceeded

        500 -> TranslateError.InternalServerError

        503 -> TranslateError.ServiceUnavailable

        else -> TranslateError.Unknown(this.code(), detail)
    }
}

suspend fun <T> safeApiCall(
    call: suspend () -> T
): TranslateResult<T> {
    return try {
        TranslateResult.Success(call())
    } catch (e: HttpException) {
        TranslateResult.Error(e.toTranslateError())
    } catch (e: IOException) {
        TranslateResult.Error(TranslateError.NetworkError)
    } catch (e: Exception) {
        TranslateResult.Error(
            TranslateError.Unknown(-1, e.message ?: "Unknown error")
        )
    }
}