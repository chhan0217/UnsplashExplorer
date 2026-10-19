package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

class AddFavoritePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val photoFileRepository: PhotoFileRepository,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(photo: Photo): Photo {
        val downloadUrl = photoRepository.getPhotoDownloadUrl(photo.id)
        val localImagePath = photoFileRepository.save(
            photoId = photo.id,
            imageUrl = downloadUrl,
        )
        val favoritePhoto = photo.copy(
            localImagePath = localImagePath,
            isFavorite = true,
        )
        try {
            favoriteRepository.add(
                favoritePhoto,
            )
        } catch (exception: Exception) {
            withContext(NonCancellable) {
                try {
                    photoFileRepository.delete(localImagePath)
                } catch (_: Exception) {
                    // Preserve the original database error after best-effort cleanup.
                }
            }
            throw exception
        }
        return favoritePhoto
    }
}
