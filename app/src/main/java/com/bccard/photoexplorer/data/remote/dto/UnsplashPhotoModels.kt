package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhotoUrls(
    val raw: String? = null,
    val full: String? = null,
    val regular: String,
    val small: String? = null,
    val thumb: String? = null,
)

@Serializable
data class UnsplashUser(
    val username: String,
    val name: String,
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
    @SerialName("profile_image")
    val profileImage: UnsplashProfileImage? = null,
    val social: UnsplashSocial? = null,
)

@Serializable
data class UnsplashProfileImage(
    val small: String? = null,
    val medium: String,
    val large: String? = null,
)

@Serializable
data class UnsplashSocial(
    @SerialName("instagram_username")
    val instagramUsername: String? = null,
)
