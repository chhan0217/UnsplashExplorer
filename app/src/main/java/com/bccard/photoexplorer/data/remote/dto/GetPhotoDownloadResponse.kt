package com.bccard.photoexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetPhotoDownloadResponse(
    val url: String? = null
)
