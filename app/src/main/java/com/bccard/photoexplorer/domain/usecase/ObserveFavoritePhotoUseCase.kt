package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveFavoritePhotoUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    operator fun invoke(photoId: String): Flow<Photo?> =
        favoriteRepository.observeFavorite(photoId)
}
