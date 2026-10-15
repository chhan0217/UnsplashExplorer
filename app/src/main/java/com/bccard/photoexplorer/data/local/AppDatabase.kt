package com.bccard.photoexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoritePhotoEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritePhotoDao(): FavoritePhotoDao
}
