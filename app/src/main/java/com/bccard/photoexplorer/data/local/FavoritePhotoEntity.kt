package com.bccard.photoexplorer.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_photos")
data class FavoritePhotoEntity(
    @PrimaryKey val id: String,
    val description: String?,
    val imageUrl: String,
    val photographerName: String,
    val localImagePath: String?,
    @ColumnInfo(defaultValue = "0")
    val savedAt: Long,
)
