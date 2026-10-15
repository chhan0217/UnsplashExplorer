package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritePhotosUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<Photo>> {
        return favoriteRepository.observeFavorites()
    }
}
