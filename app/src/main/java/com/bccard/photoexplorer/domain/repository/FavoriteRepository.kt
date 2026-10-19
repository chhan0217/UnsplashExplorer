package com.bccard.photoexplorer.domain.repository

import com.bccard.photoexplorer.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<Photo>>
    fun observeFavorite(photoId: String): Flow<Photo?>
    suspend fun add(photo: Photo)
    suspend fun remove(photoId: String)
}
