package com.bccard.photoexplorer.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bccard.photoexplorer.data.remote.dto.GetPhotosResponse
import com.bccard.photoexplorer.domain.model.Photo
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

class MainPhotoPagingSource(
    private val apiService: UnsplashApiService,
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> = try {
        val page = params.key ?: STARTING_PAGE
        val response = apiService.getPhotos(
            page = page,
            perPage = params.loadSize,
        )

        LoadResult.Page(
            data = response.map { it.toDomain() },
            prevKey = if (page == STARTING_PAGE) null else page - 1,
            nextKey = if (response.size < params.loadSize) null else page + 1,
        )
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: HttpException) {
        LoadResult.Error(exception.toApiException())
    } catch (exception: Exception) {
        LoadResult.Error(exception)
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
    }

    private fun GetPhotosResponse.toDomain() = Photo(
        id = id,
        description = null,
        imageUrl = urls.regular,
        photographerName = user.name,
        photographerProfileImageUrl = user.profileImage?.medium,
    )

    private companion object {
        const val STARTING_PAGE = 1
    }
}
