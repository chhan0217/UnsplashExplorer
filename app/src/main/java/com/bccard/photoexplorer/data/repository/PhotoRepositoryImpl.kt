package com.bccard.photoexplorer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bccard.photoexplorer.data.remote.MainPhotoPagingSource
import com.bccard.photoexplorer.data.remote.UnsplashApiService
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val apiService: UnsplashApiService,
) : PhotoRepository {
    override fun getPhotos(): Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { MainPhotoPagingSource(apiService) },
    ).flow

    private companion object {
        const val PAGE_SIZE = 20
    }
}
