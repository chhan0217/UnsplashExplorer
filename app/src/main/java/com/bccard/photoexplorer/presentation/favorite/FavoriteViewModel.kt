package com.bccard.photoexplorer.presentation.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.error.toAppError
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.usecase.ObserveFavoritePhotosUseCase
import com.bccard.photoexplorer.domain.usecase.RemoveFavoritePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val observeFavoritePhotosUseCase: ObserveFavoritePhotosUseCase,
    private val removeFavoritePhotoUseCase: RemoveFavoritePhotoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoriteUiState>(FavoriteUiState.Loading)
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    private var observeFavoritesJob: Job? = null

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        observeFavoritesJob?.cancel()
        observeFavoritesJob = viewModelScope.launch {
            try {
                observeFavoritePhotosUseCase().collect { photos ->
                    _uiState.value = FavoriteUiState.Success(photos)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = FavoriteUiState.Error(
                    error = exception.toAppError(),
                )
            }
        }
    }

    fun retryFavorites() {
        _uiState.value = FavoriteUiState.Loading
        observeFavorites()
    }

    fun removeFavoritePhoto(photo: Photo) {
        viewModelScope.launch {
            try {
                removeFavoritePhotoUseCase(photo)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _error.value = exception.toAppError()
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
}
