package com.bccard.photoexplorer.presentation.common

import androidx.annotation.StringRes
import com.bccard.photoexplorer.R
import com.bccard.photoexplorer.domain.error.AppError

@StringRes
fun AppError.messageResource(): Int = when (this) {
    AppError.InvalidRequest -> R.string.error_invalid_request
    AppError.NetworkUnavailable -> R.string.error_network_unavailable
    AppError.Timeout -> R.string.error_timeout
    AppError.Unauthorized -> R.string.error_unauthorized
    AppError.Forbidden -> R.string.error_forbidden
    AppError.NotFound -> R.string.error_not_found
    AppError.RateLimited -> R.string.error_rate_limited
    AppError.Server -> R.string.error_server
    AppError.Storage -> R.string.error_storage
    AppError.Database -> R.string.error_database
    AppError.InvalidResponse -> R.string.error_invalid_response
    is AppError.Unknown -> R.string.error_unknown
}
