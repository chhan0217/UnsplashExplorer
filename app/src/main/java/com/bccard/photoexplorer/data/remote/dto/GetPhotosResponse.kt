package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetPhotosResponse(
    val id: String,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
    val width: Int,
    val height: Int,
)
