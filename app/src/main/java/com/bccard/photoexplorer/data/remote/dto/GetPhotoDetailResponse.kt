package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPhotoDetailResponse(
    val id: String,
    val description: String? = null,
    @SerialName("alt_description")
    val altDescription: String? = null,
    val width: Int,
    val height: Int,
    val color: String? = null,
    val likes: Int = 0,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser,
    val views: Int,
    val downloads: Int,
    val exif: Exif? = null,
    val location: PhotoLocation? = null,
    val tags: List<Tag> = emptyList(),
)

@Serializable
data class Exif(
    val make: String? = null,
    val model: String? = null,
    val name: String? = null,
    @SerialName("exposure_time")
    val exposureTime: String? = null,
    val aperture: String? = null,
    @SerialName("focal_length")
    val focalLength: String? = null,
    val iso: Int? = null,
)

@Serializable
data class PhotoLocation(
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val position: Position? = null
)

@Serializable
data class Position(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class Tag(
    val type: String,
    val title: String,
)
