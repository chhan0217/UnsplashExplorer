package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.PhotoDetail
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val repository: PhotoRepository,
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(photoId: String): PhotoDetail {
        val photoDetail = repository.getPhotoDetail(photoId)
        val isFavorite = favoriteRepository.isFavorite(photoId)

        return photoDetail.copy(isFavorite = isFavorite)
    }
}
