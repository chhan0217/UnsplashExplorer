package com.bccard.photoexplorer.data.remote

import androidx.paging.PagingSource
import com.bccard.photoexplorer.data.remote.dto.GetPhotosResponse
import com.bccard.photoexplorer.data.remote.dto.UnsplashPhotoUrls
import com.bccard.photoexplorer.data.remote.dto.UnsplashUser
import com.bccard.photoexplorer.domain.model.Photo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class MainPhotoPagingSourceTest {

    private lateinit var apiService: UnsplashApiService
    private lateinit var pagingSource: MainPhotoPagingSource

    @Before
    fun setUp() {
        apiService = mockk()
        pagingSource = MainPhotoPagingSource(apiService)
    }

    @Test
    fun `첫 페이지 조회 성공 시 사진 목록과 다음 페이지 키를 반환한다`() = runTest {
        // Given
        val response = GetPhotosResponse(
            id = "photo-1",
            description = "테스트 사진",
            urls = UnsplashPhotoUrls(
                regular = "https://example.com/photo-1.jpg",
            ),
            user = UnsplashUser(
                username = "photographer-1",
                name = "테스트 작가",
            ),
            width = 1200,
            height = 800,
        )

        coEvery {
            apiService.getPhotos(
                page = 1,
                perPage = 20,
            )
        } returns listOf(response)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false,
            ),
        )

        // Then
        val page = assertIs<PagingSource.LoadResult.Page<Int, Photo>>(result)
        val photo = page.data.single()

        assertEquals("photo-1", photo.id)
        assertEquals("테스트 사진", photo.description)
        assertEquals("https://example.com/photo-1.jpg", photo.imageUrl)
        assertEquals("테스트 작가", photo.photographerName)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)

        coVerify(exactly = 1) {
            apiService.getPhotos(
                page = 1,
                perPage = 20,
            )
        }
    }

}
