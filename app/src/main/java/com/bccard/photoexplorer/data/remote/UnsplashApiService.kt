package com.bccard.photoexplorer.data.remote

import com.bccard.photoexplorer.data.remote.dto.GetPhotoDetailResponse
import com.bccard.photoexplorer.data.remote.dto.GetPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
    ): List<GetPhotosResponse>

    @GET("photos/{photoId}")
    suspend fun getPhotoDetail(
        @Path("photoId") photoId: String,
    ): GetPhotoDetailResponse
}
