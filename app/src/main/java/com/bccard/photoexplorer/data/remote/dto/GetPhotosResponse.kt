package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPhotosResponse(
    val id: String,
    val description: String? = null,
    @SerialName("alt_description")
    val altDescription: String? = null,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
    val width: Int,
    val height: Int,
)
