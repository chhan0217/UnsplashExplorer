package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.PhotoDetail
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import javax.inject.Inject

class GetPhotoDetailUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(photoId: String): PhotoDetail =
        repository.getPhotoDetail(photoId)
}
