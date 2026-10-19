package com.bccard.photoexplorer.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.error.toAppError
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.model.PhotoDetail
import com.bccard.photoexplorer.domain.usecase.AddFavoritePhotoUseCase
import com.bccard.photoexplorer.domain.usecase.GetPhotoDetailUseCase
import com.bccard.photoexplorer.domain.usecase.ObserveFavoritePhotoUseCase
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
class DetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
    private val observeFavoritePhotoUseCase: ObserveFavoritePhotoUseCase,
    private val addFavoritePhotoUseCase: AddFavoritePhotoUseCase,
    private val removeFavoritePhotoUseCase: RemoveFavoritePhotoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    private var favoritePhoto: Photo? = null
    private var loadJob: Job? = null

    fun loadPhotoDetail(photoId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            favoritePhoto = null
            try {
                val photoDetail = getPhotoDetailUseCase(photoId)
                observeFavoritePhotoUseCase(photoId).collect { favorite ->
                    favoritePhoto = favorite
                    val isFavoriteUpdating =
                        (_uiState.value as? DetailUiState.Success)
                            ?.isFavoriteUpdating == true
                    _uiState.value = DetailUiState.Success(
                        photoDetail = photoDetail.copy(isFavorite = favorite != null),
                        isFavoriteUpdating = isFavoriteUpdating,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    error = e.toAppError(),
                )
            }
        }
    }

    fun onFavoriteClick() {
        val state = _uiState.value as? DetailUiState.Success ?: return
        if (state.isFavoriteUpdating) return

        _uiState.value = state.copy(isFavoriteUpdating = true)
        viewModelScope.launch {
            try {
                val savedFavorite = favoritePhoto
                if (savedFavorite == null) {
                    favoritePhoto = addFavoritePhotoUseCase(
                        state.photoDetail.toPhoto(),
                    )
                    updateFavoriteState(isFavorite = true)
                } else {
                    removeFavoritePhotoUseCase(savedFavorite)
                    favoritePhoto = null
                    updateFavoriteState(isFavorite = false)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _error.value = exception.toAppError()
            } finally {
                val currentState = _uiState.value as? DetailUiState.Success
                if (currentState != null) {
                    _uiState.value = currentState.copy(isFavoriteUpdating = false)
                }
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }

    private fun updateFavoriteState(isFavorite: Boolean) {
        val state = _uiState.value as? DetailUiState.Success ?: return
        _uiState.value = state.copy(
            photoDetail = state.photoDetail.copy(isFavorite = isFavorite),
        )
    }

    private fun PhotoDetail.toPhoto() = Photo(
        id = id,
        description = description,
        imageUrl = imageUrl,
        photographerName = photographer.name,
        photographerProfileImageUrl = photographer.profileImageUrl,
        isFavorite = isFavorite,
    )
}
