package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import javax.inject.Inject

class AddFavoritePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val photoFileRepository: PhotoFileRepository,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(photo: Photo) {
        val downloadUrl = photoRepository.getPhotoDownloadUrl(photo.id)
        val localImagePath = photoFileRepository.save(
            photoId = photo.id,
            imageUrl = downloadUrl,
        )
        favoriteRepository.add(
            photo.copy(
                localImagePath = localImagePath,
                isFavorite = true,
            ),
        )
    }
}
