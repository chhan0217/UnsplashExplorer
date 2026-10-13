package com.bccard.photoexplorer.di

import com.bccard.photoexplorer.data.repository.PhotoRepositoryImpl
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPhotoRepository(implementation: PhotoRepositoryImpl): PhotoRepository
}
