package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RemoveFavoritePhotoUseCaseTest {

    private lateinit var photoFileRepository: PhotoFileRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var useCase: RemoveFavoritePhotoUseCase

    @Before
    fun setUp() {
        photoFileRepository = mockk()
        favoriteRepository = mockk()
        useCase = RemoveFavoritePhotoUseCase(
            photoFileRepository = photoFileRepository,
            favoriteRepository = favoriteRepository,
        )
    }

    @Test
    fun `좋아요 해제 시 이미지 파일과 DB 데이터를 삭제한다`() = runTest {
        // Given
        val localImagePath = "/files/favorite_images/photo-1.jpg"
        val photo = Photo(
            id = "photo-1",
            description = "테스트 사진",
            imageUrl = "https://example.com/photo.jpg",
            photographerName = "테스트 작가",
            localImagePath = localImagePath,
            isFavorite = true,
        )

        coEvery { photoFileRepository.delete(localImagePath) } returns Unit
        coEvery { favoriteRepository.remove(photo.id) } returns Unit

        // When
        useCase(photo)

        // Then
        coVerifyOrder {
            photoFileRepository.delete(localImagePath)
            favoriteRepository.remove(photo.id)
        }
    }
}
