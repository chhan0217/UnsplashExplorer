package com.bccard.photoexplorer.domain.usecase

import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.repository.FavoriteRepository
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddFavoritePhotoUseCaseTest {

    private lateinit var photoRepository: PhotoRepository
    private lateinit var photoFileRepository: PhotoFileRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var useCase: AddFavoritePhotoUseCase

    @Before
    fun setUp() {
        photoRepository = mockk()
        photoFileRepository = mockk()
        favoriteRepository = mockk()

        useCase = AddFavoritePhotoUseCase(
            photoRepository = photoRepository,
            photoFileRepository = photoFileRepository,
            favoriteRepository = favoriteRepository,
        )
    }

    @Test
    fun `좋아요 추가 시 로컬 이미지 경로와 함께 사진을 저장한다`() = runTest {
        // Given
        val photo = Photo(
            id = "photo-1",
            description = "테스트 사진",
            imageUrl = "https://example.com/list-image.jpg",
            photographerName = "테스트 작가",
        )
        val downloadUrl = "https://example.com/download-image.jpg"
        val localImagePath = "/files/favorite_images/photo-1.jpg"

        coEvery {
            photoRepository.getPhotoDownloadUrl(photo.id)
        } returns downloadUrl

        coEvery {
            photoFileRepository.save(
                photoId = photo.id,
                imageUrl = downloadUrl,
            )
        } returns localImagePath

        coEvery { favoriteRepository.add(any()) } returns Unit

        // When
        useCase(photo)

        // Then
        val savedPhotoSlot = slot<Photo>()
        coVerifyOrder {
            photoRepository.getPhotoDownloadUrl(photo.id)
            photoFileRepository.save(
                photoId = photo.id,
                imageUrl = downloadUrl,
            )
            favoriteRepository.add(capture(savedPhotoSlot))
        }

        val savedPhoto = savedPhotoSlot.captured
        assertEquals(localImagePath, savedPhoto.localImagePath)
        assertTrue(savedPhoto.isFavorite)
    }
}
