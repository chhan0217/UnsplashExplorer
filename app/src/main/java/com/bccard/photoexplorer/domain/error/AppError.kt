package com.bccard.photoexplorer.domain.error

sealed interface AppError {
    data object InvalidRequest : AppError
    data object NetworkUnavailable : AppError
    data object Timeout : AppError
    data object Unauthorized : AppError
    data object Forbidden : AppError
    data object NotFound : AppError
    data object RateLimited : AppError
    data object Server : AppError
    data object Storage : AppError
    data object Database : AppError
    data object InvalidResponse : AppError

    data class Unknown(
        val cause: Throwable? = null,
    ) : AppError
}

class AppException(
    val error: AppError,
    cause: Throwable? = null,
) : Exception(error.toString(), cause)

fun Throwable.toAppError(): AppError =
    (this as? AppException)?.error ?: AppError.Unknown(cause = this)
