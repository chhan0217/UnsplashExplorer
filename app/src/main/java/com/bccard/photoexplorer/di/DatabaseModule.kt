package com.bccard.photoexplorer.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bccard.photoexplorer.data.local.AppDatabase
import com.bccard.photoexplorer.data.local.FavoritePhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE favorite_photos ADD COLUMN localImagePath TEXT",
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unsplash-explorer.db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideFavoritePhotoDao(database: AppDatabase): FavoritePhotoDao =
        database.favoritePhotoDao()
}
