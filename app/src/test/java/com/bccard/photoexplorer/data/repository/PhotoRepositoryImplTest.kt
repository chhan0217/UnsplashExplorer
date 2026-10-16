package com.bccard.photoexplorer.data.repository

import com.bccard.photoexplorer.data.remote.UnsplashApiService
import com.bccard.photoexplorer.data.remote.dto.GetPhotoDetailResponse
import com.bccard.photoexplorer.data.remote.dto.Tag
import com.bccard.photoexplorer.data.remote.dto.UnsplashPhotoUrls
import com.bccard.photoexplorer.data.remote.dto.UnsplashUser
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PhotoRepositoryImplTest {

    private lateinit var apiService: UnsplashApiService
    private lateinit var repository: PhotoRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = PhotoRepositoryImpl(apiService)
    }

    @Test
    fun `사진 상세 조회 시 API 응답을 상세 모델로 변환한다`() = runTest {
        // Given
        val photoId = "photo-1"
        val response = GetPhotoDetailResponse(
            id = photoId,
            description = "상세 사진 설명",
            width = 1200,
            height = 800,
            urls = UnsplashPhotoUrls(
                regular = "https://example.com/detail-photo.jpg",
            ),
            user = UnsplashUser(
                username = "photographer-1",
                name = "테스트 작가",
            ),
            views = 1_000,
            downloads = 100,
            tags = listOf(
                Tag(type = "search", title = "자연"),
                Tag(type = "search", title = "풍경"),
            ),
        )

        coEvery { apiService.getPhotoDetail(photoId) } returns response

        // When
        val result = repository.getPhotoDetail(photoId)

        // Then
        assertEquals(photoId, result.id)
        assertEquals("상세 사진 설명", result.description)
        assertEquals("https://example.com/detail-photo.jpg", result.imageUrl)
        assertEquals(1200, result.width)
        assertEquals(800, result.height)
        assertEquals("테스트 작가", result.photographer.name)
        assertEquals(listOf("자연", "풍경"), result.tags)

        coVerify(exactly = 1) {
            apiService.getPhotoDetail(photoId)
        }
    }
}
