package com.bccard.photoexplorer.domain.model

data class PhotoDetail(
    val id: String,
    val description: String?,
    val imageUrl: String,
    val width: Int,
    val height: Int,
    val color: String?,
    val likes: Int,
    val views: Int,
    val downloads: Int,
    val photographer: Photographer,
    val exif: PhotoExif?,
    val location: PhotoLocation?,
    val tags: List<String>,
)

data class Photographer(
    val username: String,
    val name: String,
    val instagramUsername: String?,
    val profileImageUrl: String?,
)

data class PhotoExif(
    val make: String?,
    val model: String?,
    val name: String?,
    val exposureTime: String?,
    val aperture: String?,
    val focalLength: String?,
    val iso: Int?,
)

data class PhotoLocation(
    val name: String?,
    val city: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?,
)
