package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhoto(
    val id: String,
    val description: String? = null,
    @SerialName("alt_description") val altDescription: String? = null,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
)

@Serializable
data class UnsplashPhotoUrls(
    val regular: String,
)

@Serializable
data class UnsplashUser(
    val name: String,
    @SerialName("profile_image") val profileImage: UnsplashProfileImage? = null,
)

@Serializable
data class UnsplashProfileImage(
    val medium: String,
)
