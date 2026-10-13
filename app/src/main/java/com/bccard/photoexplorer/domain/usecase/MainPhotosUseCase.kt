package com.bccard.photoexplorer.domain.usecase

import androidx.paging.PagingData
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainPhotosUseCase @Inject constructor(
    private val repository: PhotoRepository,
) {
    operator fun invoke(): Flow<PagingData<Photo>> = repository.getPhotos()
}
