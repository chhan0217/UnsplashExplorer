package com.bccard.photoexplorer.data.repository

import com.bccard.photoexplorer.data.local.FavoritePhotoDao
import com.bccard.photoexplorer.data.local.FavoritePhotoEntity
import com.bccard.photoexplorer.data.error.toDatabaseAppException
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val dao: FavoritePhotoDao,
) : FavoriteRepository {
    override fun observeFavorites(): Flow<List<Photo>> = dao.observeAll().map { photos ->
        photos.map { it.toDomain() }
    }.catch { exception ->
        if (exception is CancellationException) throw exception
        throw exception.toDatabaseAppException()
    }

    override fun observeFavorite(photoId: String): Flow<Photo?> =
        dao.observeById(photoId).map { photo ->
            photo?.toDomain()
        }.catch { exception ->
            if (exception is CancellationException) throw exception
            throw exception.toDatabaseAppException()
        }

    override suspend fun add(photo: Photo) {
        try {
            dao.insert(photo.toEntity())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw exception.toDatabaseAppException()
        }
    }

    override suspend fun remove(photoId: String) {
        try {
            dao.deleteById(photoId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            throw exception.toDatabaseAppException()
        }
    }

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
