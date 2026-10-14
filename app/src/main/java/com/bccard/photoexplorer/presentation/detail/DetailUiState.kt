package com.bccard.photoexplorer.presentation.detail

import com.bccard.photoexplorer.domain.model.PhotoDetail

sealed interface DetailUiState {
    data object Loading : DetailUiState

    data class Success(
        val photoDetail: PhotoDetail,
    ) : DetailUiState

    data class Error(
        val message: String,
    ) : DetailUiState
}
