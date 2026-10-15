package com.bccard.photoexplorer.domain.model

data class Photo(
    val id: String,
    val description: String?,
    val imageUrl: String,
    val photographerName: String,
    val photographerProfileImageUrl: String? = null,
    val localImagePath: String? = null,
    val isFavorite: Boolean = false,
)
