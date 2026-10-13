package com.bccard.photoexplorer.data.remote

import java.io.IOException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

class ApiException(
    val statusCode: Int,
    val errors: List<String>,
    cause: Throwable,
) : IOException(errors.joinToString("\n"), cause)

fun HttpException.toApiException(): ApiException {
    val errors = response()?.errorBody()?.string()
        ?.let(::parseErrors)
        ?.takeIf { it.isNotEmpty() }
        ?: listOf(defaultMessage(code()))

    return ApiException(
        statusCode = code(),
        errors = errors,
        cause = this,
    )
}

private fun parseErrors(body: String): List<String>? = runCatching {
    Json.decodeFromString<ApiErrorResponse>(body).errors
}.getOrNull()

private fun defaultMessage(statusCode: Int): String = when (statusCode) {
    400 -> "잘못된 요청입니다."
    401 -> "인증 정보가 올바르지 않습니다."
    403 -> "요청 권한이 없습니다."
    404 -> "요청한 데이터를 찾을 수 없습니다."
    500, 503 -> "서버에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요."
    else -> "요청 처리 중 오류가 발생했습니다."
}

@Serializable
private data class ApiErrorResponse(
    val errors: List<String> = emptyList(),
)
