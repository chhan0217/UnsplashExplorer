package com.bccard.photoexplorer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePhotoDao {
    @Query("SELECT * FROM favorite_photos ORDER BY id DESC")
    fun observeAll(): Flow<List<FavoritePhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: FavoritePhotoEntity)

    @Query("DELETE FROM favorite_photos WHERE id = :photoId")
    suspend fun deleteById(photoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_photos WHERE id = :photoId)")
    suspend fun exists(photoId: String): Boolean
}
