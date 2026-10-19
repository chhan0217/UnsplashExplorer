package com.bccard.photoexplorer.presentation.detail

import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.model.PhotoDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val photoDetail: PhotoDetail,
        val isFavoriteUpdating: Boolean = false,
    ) : DetailUiState

    data class Error(
        val error: AppError,
    ) : DetailUiState
}
