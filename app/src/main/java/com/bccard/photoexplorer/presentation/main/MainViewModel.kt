package com.bccard.photoexplorer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.error.toAppError
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.usecase.AddFavoritePhotoUseCase
import com.bccard.photoexplorer.domain.usecase.MainPhotosUseCase
import com.bccard.photoexplorer.domain.usecase.ObserveFavoritePhotosUseCase
import com.bccard.photoexplorer.domain.usecase.RemoveFavoritePhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPhotosUseCase: MainPhotosUseCase,
    observeFavoritePhotosUseCase: ObserveFavoritePhotosUseCase,
    private val addFavoritePhotoUseCase: AddFavoritePhotoUseCase,
    private val removeFavoritePhotoUseCase: RemoveFavoritePhotoUseCase,
) : ViewModel() {
    private val cachedPhotos = getPhotosUseCase().cachedIn(viewModelScope)

    private val _updatingPhotoIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingPhotoIds = _updatingPhotoIds.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error = _error.asStateFlow()

    private val favoritePhotos = observeFavoritePhotosUseCase()
        .catch { exception ->
            if (exception is CancellationException) throw exception
            _error.value = exception.toAppError()
            emit(emptyList())
        }

    val photos = combine(
        cachedPhotos,
        favoritePhotos,
    ) { pagingData, favorites ->
        val favoriteById = favorites.associateBy(Photo::id)
        pagingData.map { photo ->
            val favorite = favoriteById[photo.id]
            photo.copy(
                localImagePath = favorite?.localImagePath,
                isFavorite = favorite != null,
            )
        }
    }

    fun onPhotoFavorite(photo: Photo) {
        if (photo.id in _updatingPhotoIds.value) return

        viewModelScope.launch {
            _updatingPhotoIds.update { it + photo.id }
            try {
                if (photo.isFavorite) {
                    removeFavoritePhotoUseCase(photo)
                } else {
                    addFavoritePhotoUseCase(photo)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _error.value = exception.toAppError()
            } finally {
                _updatingPhotoIds.update { it - photo.id }
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
}
