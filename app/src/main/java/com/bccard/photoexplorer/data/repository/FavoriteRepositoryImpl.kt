package com.bccard.photoexplorer.data.repository

import com.bccard.photoexplorer.data.local.FavoritePhotoDao
import com.bccard.photoexplorer.data.local.FavoritePhotoEntity
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoritePhotoDao,
) : FavoriteRepository {
    override fun observeFavorites(): Flow<List<Photo>> = dao.observeAll().map { photos ->
        photos.map { it.toDomain() }
    }

    override suspend fun add(photo: Photo) {
        dao.insert(photo.toEntity())
    }

    override suspend fun remove(photoId: String) {
        dao.deleteById(photoId)
    }

    override suspend fun isFavorite(photoId: String): Boolean = dao.exists(photoId)

    private fun Photo.toEntity() = FavoritePhotoEntity(
        id = id,
        description = description,
        imageUrl = imageUrl,
        photographerName = photographerName,
        localImagePath = localImagePath,
        savedAt = System.currentTimeMillis(),
    )

    private fun FavoritePhotoEntity.toDomain() = Photo(
        id = id,
        description = description,
        imageUrl = imageUrl,
        photographerName = photographerName,
        localImagePath = localImagePath,
        isFavorite = true,
    )
}
