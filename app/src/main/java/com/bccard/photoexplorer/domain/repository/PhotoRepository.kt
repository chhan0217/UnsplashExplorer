package com.bccard.photoexplorer.domain.repository

import androidx.paging.PagingData
import com.bccard.photoexplorer.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(): Flow<PagingData<Photo>>
}
