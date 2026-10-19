package com.bccard.photoexplorer.data.repository

import android.content.Context
import com.bccard.photoexplorer.data.error.toStorageAppException
import com.bccard.photoexplorer.di.ImageDownloadClient
import com.bccard.photoexplorer.domain.repository.PhotoFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

@Singleton
class PhotoFileRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @ImageDownloadClient private val client: OkHttpClient,
) : PhotoFileRepository {

    override suspend fun save(photoId: String, imageUrl: String): String =
        withContext(Dispatchers.IO) {
            var destination: File? = null
            try {
                val directory = File(context.filesDir, FAVORITE_IMAGE_DIRECTORY).apply {
                    if (!exists() && !mkdirs()) {
                        throw IOException("좋아요 이미지 저장 폴더를 생성하지 못했습니다.")
                    }
                }
                destination = File(directory, "$photoId.jpg")

                val request = Request.Builder().url(imageUrl).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("이미지 다운로드에 실패했습니다. (HTTP ${response.code})")
                    }
                    val body = response.body
                        ?: throw IOException("이미지 다운로드 응답이 비어 있습니다.")
                    destination.outputStream().buffered().use { output ->
                        body.byteStream().use { input -> input.copyTo(output) }
                    }
                }

                destination.absolutePath
            } catch (exception: Exception) {
                destination?.delete()
                throw exception.toStorageAppException()
            }
        }

    override suspend fun delete(localImagePath: String) {
        withContext(Dispatchers.IO) {
            try {
                val imageFile = File(localImagePath)
                if (imageFile.exists() && !imageFile.delete()) {
                    throw IOException("좋아요 이미지 파일을 삭제하지 못했습니다.")
                }
            } catch (exception: Exception) {
                throw exception.toStorageAppException()
            }
        }
    }

    private companion object {
        const val FAVORITE_IMAGE_DIRECTORY = "favorite_images"
    }
}
