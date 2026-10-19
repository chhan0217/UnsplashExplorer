package com.bccard.photoexplorer.data.error

import com.bccard.photoexplorer.data.remote.ApiException
import com.bccard.photoexplorer.data.remote.toApiException
import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.error.AppException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

fun Throwable.toNetworkAppException(): AppException {
    if (this is CancellationException) throw this
    if (this is AppException) return this

    val normalizedException = if (this is HttpException) toApiException() else this
    val error = when (normalizedException) {
        is ApiException -> normalizedException.statusCode.toHttpAppError()
        is SocketTimeoutException -> AppError.Timeout
        is UnknownHostException,
        is ConnectException,
        is IOException -> AppError.NetworkUnavailable

        else -> AppError.Unknown(this)
    }

    return AppException(error = error, cause = this)
}

fun Throwable.toStorageAppException(): AppException {
    if (this is CancellationException) throw this
    if (this is AppException) return this
    return AppException(error = AppError.Storage, cause = this)
}

fun Throwable.toDatabaseAppException(): AppException {
    if (this is CancellationException) throw this
    if (this is AppException) return this
    return AppException(error = AppError.Database, cause = this)
}

private fun Int.toHttpAppError(): AppError = when (this) {
    400 -> AppError.InvalidRequest
    401 -> AppError.Unauthorized
    403 -> AppError.Forbidden
    404 -> AppError.NotFound
    429 -> AppError.RateLimited
    in 500..599 -> AppError.Server
    else -> AppError.Unknown()
}
