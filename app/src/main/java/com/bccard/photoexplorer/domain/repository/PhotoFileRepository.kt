package com.bccard.photoexplorer.domain.repository

interface PhotoFileRepository {
    suspend fun save(photoId: String, imageUrl: String): String

    suspend fun delete(localImagePath: String)
}
