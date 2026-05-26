package dev.jmx.client.worker

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import dev.jmx.client.cache.deleteDownloadTempDir
import dev.jmx.client.cache.getDownloadDir
import dev.jmx.client.cache.trimDownloadTempCache
import dev.jmx.client.database.dao.DownloadAlbumDao
import dev.jmx.client.database.model.UpdateAlbumCover
import dev.jmx.client.database.model.UpdateAlbumProgress
import dev.jmx.client.database.model.UpdateAlbumStatus
import dev.jmx.client.database.model.UpdateAlbumZipPath
import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.data.remote.model.AlbumImageListResponse
import dev.jmx.client.data.remote.model.NetworkResult
import dev.jmx.client.store.LocalSettingManager
import dev.jmx.client.store.RemoteSettingManager
import dev.jmx.client.store.ToastManager
import dev.jmx.client.utils.tryCreateDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DownloadAlbumWorker(
    private val appContext: Context,
    params: WorkerParameters,
    private val downloadAlbumDao: DownloadAlbumDao,
    private val remoteSettingManager: RemoteSettingManager,
    private val localSettingManager: LocalSettingManager,
    private val albumRepository: AlbumRepository,
    private val toastManager: ToastManager,
    private val imageLoader: ImageLoader,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val albumId = inputData.getInt("albumId", -1)
        if (albumId == -1) {
            return Result.failure()
        }
        return try {
            downloadAlbumDao.updateStatus(
                UpdateAlbumStatus(
                    albumId,
                    "downloading"
                )
            )
            val coverPath = downloadCover(albumId)
            downloadAlbumDao.updateCover(
                UpdateAlbumCover(
                    albumId,
                    coverPath
                )
            )
            val picPathList =
                downloadImageList(albumId, localSettingManager.localSettingState.value.shunt)
            val zipPath = zipPicPathList(albumId, picPathList)
            deleteDownloadTempDir(appContext, albumId)
            trimDownloadTempCache(appContext)
            downloadAlbumDao.updateZipPath(
                UpdateAlbumZipPath(
                    albumId,
                    zipPath
                )
            )
            downloadAlbumDao.updateStatus(
                UpdateAlbumStatus(
                    albumId,
                    "complete"
                )
            )
            toastManager.showAsync("下载成功")
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry() // 如果失败了，系统会自动尝试重试
            } else {
                downloadAlbumDao.updateStatus(
                    UpdateAlbumStatus(
                        albumId,
                        "error"
                    )
                )
                Result.failure()
            }
        }
    }

    private suspend fun downloadCover(albumId: Int): String {
        return withContext(Dispatchers.IO) {
            val coverUrl =
                "${remoteSettingManager.remoteSettingState.value.imgHost}/media/albums/${albumId}_3x4.jpg"
            val request = ImageRequest.Builder(appContext)
                .data(coverUrl)
                .allowHardware(false)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()

            when (val result = imageLoader.execute(request)) {
                is ErrorResult -> {
                    // TODO 处理错误
                    ""
                }

                is SuccessResult -> {
                    val bitmap = result.drawable.toBitmap()
                    val dir = getAlbumCoverDownloadDir()
                    val file = File(dir, "${albumId}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
                    }
                    file.absolutePath
                }
            }
        }
    }

    private suspend fun downloadImageList(albumId: Int, shunt: String): List<String> {
        return withContext(Dispatchers.IO) {
            when (val data = albumRepository.getAlbumImageList(albumId, shunt)) {
                is NetworkResult.Error -> {
                    throw IllegalStateException(data.message)
                }

                is NetworkResult.Success<AlbumImageListResponse> -> {
                    if (data.data.list.isEmpty()) {
                        throw IllegalStateException("Album image list is empty")
                    }

                    val dir = getAlbumImageListDownloadDir(albumId)
                    data.data.list.mapIndexed { index, url ->
                        val request = ImageRequest.Builder(appContext)
                            .data(url)
                            .allowHardware(false)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .build()

                        when (val result = imageLoader.execute(request)) {
                            is ErrorResult -> {
                                throw IllegalStateException(
                                    result.throwable.message ?: "Download image failed"
                                )
                            }

                            is SuccessResult -> {
                                val bitmap = result.drawable.toBitmap()
                                val file = File(dir, "$index.webp")
                                FileOutputStream(file).use { out ->
                                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 50, out)
                                }
                                downloadAlbumDao.updateProgress(
                                    UpdateAlbumProgress(
                                        albumId,
                                        (index + 1).toFloat() / data.data.list.size
                                    )
                                )
                                file.absolutePath
                            }
                        }
                    }
                }
            }
        }
    }

    private fun zipPicPathList(albumId: Int, picPathList: List<String>): String {
        val zipFile = File(getDownloadDir(appContext), "$albumId.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            picPathList.forEach { source ->
                val file = File(source)
                if (file.exists()) {
                    val entryName = "$albumId/${file.name}"
                    val zipEntry = ZipEntry(entryName)
                    zipOut.putNextEntry(zipEntry)
                    FileInputStream(file).use { fis ->
                        fis.copyTo(zipOut)
                    }
                    zipOut.closeEntry()
                }
            }
        }
        return zipFile.absolutePath
    }

    private fun getAlbumImageListDownloadDir(albumId: Int): File {
        val dir = getDownloadDir(appContext)
        return tryCreateDir(File(dir, "$albumId"))
    }

    private fun getAlbumCoverDownloadDir(): File {
        val dir = getDownloadDir(appContext)
        return tryCreateDir(File(dir, "cover"))
    }
}
