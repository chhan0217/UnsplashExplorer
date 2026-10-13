package com.bccard.photoexplorer.data.remote

import com.bccard.photoexplorer.data.remote.dto.GetPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): List<GetPhotosResponse>

}
