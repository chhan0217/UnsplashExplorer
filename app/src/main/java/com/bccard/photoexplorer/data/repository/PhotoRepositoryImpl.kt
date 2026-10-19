package com.bccard.photoexplorer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bccard.photoexplorer.data.error.toNetworkAppException
import com.bccard.photoexplorer.data.remote.MainPhotoPagingSource
import com.bccard.photoexplorer.data.remote.UnsplashApiService
import com.bccard.photoexplorer.domain.error.AppError
import com.bccard.photoexplorer.domain.error.AppException
import com.bccard.photoexplorer.domain.model.Photo
import com.bccard.photoexplorer.domain.model.PhotoDetail
import com.bccard.photoexplorer.domain.model.PhotoExif
import com.bccard.photoexplorer.domain.model.PhotoLocation
import com.bccard.photoexplorer.domain.model.Photographer
import com.bccard.photoexplorer.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CancellationException
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

    override suspend fun getPhotoDetail(photoId: String): PhotoDetail = try {
        apiService.getPhotoDetail(photoId).let { response ->
            PhotoDetail(
                id = response.id,
                description = response.description ?: response.altDescription,
                imageUrl = response.urls.regular,
                width = response.width,
                height = response.height,
                color = response.color,
                likes = response.likes,
                views = response.views,
                downloads = response.downloads,
                photographer = Photographer(
                    username = response.user.username,
                    name = response.user.name,
                    instagramUsername = response.user.instagramUsername
                        ?: response.user.social?.instagramUsername,
                    profileImageUrl = response.user.profileImage?.medium,
                ),
                exif = response.exif?.let { exif ->
                    PhotoExif(
                        make = exif.make,
                        model = exif.model,
                        name = exif.name,
                        exposureTime = exif.exposureTime,
                        aperture = exif.aperture,
                        focalLength = exif.focalLength,
                        iso = exif.iso,
                    )
                },
                location = response.location?.let { location ->
                    PhotoLocation(
                        name = location.name,
                        city = location.city,
                        country = location.country,
                        latitude = location.position?.latitude,
                        longitude = location.position?.longitude,
                    )
                },
                tags = response.tags.map { it.title },
            )
        }
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        throw exception.toNetworkAppException()
    }

    override suspend fun getPhotoDownloadUrl(photoId: String): String = try {
        apiService.getPhotoDownload(photoId).url
            ?.takeIf(String::isNotBlank)
            ?: throw AppException(AppError.InvalidResponse)
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        throw exception.toNetworkAppException()
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
