package com.bccard.photoexplorer.presentation.favorite

import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.model.Photo

sealed interface FavoriteUiState {

    data object Loading : FavoriteUiState

    data class Success(
        val photos: List<Photo>,
    ) : FavoriteUiState

    data class Error(
        val error: AppError,
    ) : FavoriteUiState
}
