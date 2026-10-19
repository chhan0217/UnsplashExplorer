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
    private val addFavoritePhoto: AddFavoritePhotoUseCase,
) : ViewModel() {
    private val cachedPhotos = getPhotosUseCase().cachedIn(viewModelScope)

    private val _savingPhotoIds = MutableStateFlow<Set<String>>(emptySet())
    val savingPhotoIds = _savingPhotoIds.asStateFlow()

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
        val favoriteIds = favorites.mapTo(mutableSetOf(), Photo::id)
        pagingData.map { photo ->
            photo.copy(isFavorite = photo.id in favoriteIds)
        }
    }

    fun onPhotoFavorite(photo: Photo) {
        if (photo.id in _savingPhotoIds.value || photo.isFavorite) return

        viewModelScope.launch {
            _savingPhotoIds.update { it + photo.id }
            try {
                addFavoritePhoto(photo)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _error.value = exception.toAppError()
            } finally {
                _savingPhotoIds.update { it - photo.id }
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }
}
