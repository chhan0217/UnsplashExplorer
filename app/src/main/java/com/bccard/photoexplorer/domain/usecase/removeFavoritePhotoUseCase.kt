package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import javax.inject.Inject

class RemoveFavoritePhotoUseCase @Inject constructor(
    private val photoFileRepository: PhotoFileRepository,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(photo: Photo) {
        favoriteRepository.remove(photoId = photo.id)

        photo.localImagePath?.let { path ->
            photoFileRepository.delete(localImagePath = path)
        }
    }
}
