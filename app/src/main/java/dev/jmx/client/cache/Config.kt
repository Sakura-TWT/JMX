package dev.jmx.client.cache

import android.content.Context
import dev.jmx.client.utils.tryCreateDir
import java.io.File

private const val COMMON_CACHE_MAX_BYTES = 64L * 1024L * 1024L
private const val PIC_DECODE_CACHE_MAX_BYTES = 48L * 1024L * 1024L
private const val DOWNLOAD_TEMP_CACHE_MAX_BYTES = 32L * 1024L * 1024L

fun getCommonCacheDir(context: Context) = tryCreateDir(File(context.cacheDir, "common"))
fun getCommonPicDecodeCacheDir(context: Context, albumId: Int) = tryCreateDir(File(context.cacheDir, "pic_decode/$albumId"))
fun getDownloadDir(context: Context) = tryCreateDir(File(context.cacheDir, "download"))

fun trimAppCaches(context: Context) {
    trimDirectory(getCommonCacheDir(context), COMMON_CACHE_MAX_BYTES)
    trimDirectory(tryCreateDir(File(context.cacheDir, "pic_decode")), PIC_DECODE_CACHE_MAX_BYTES)
    trimDownloadTempFiles(getDownloadDir(context))
}

fun trimPicDecodeCache(context: Context) {
    trimDirectory(tryCreateDir(File(context.cacheDir, "pic_decode")), PIC_DECODE_CACHE_MAX_BYTES)
}

fun trimDownloadTempCache(context: Context) {
    trimDirectory(getDownloadDir(context), DOWNLOAD_TEMP_CACHE_MAX_BYTES) { file ->
        file.extension.equals("webp", ignoreCase = true) || file.extension.equals("jpg", ignoreCase = true)
    }
}

fun deleteDownloadTempDir(context: Context, albumId: Int) {
    File(getDownloadDir(context), "$albumId").deleteRecursively()
}

private fun trimDownloadTempFiles(downloadDir: File) {
    trimDirectory(downloadDir, DOWNLOAD_TEMP_CACHE_MAX_BYTES) { file ->
        file.extension.equals("webp", ignoreCase = true) || file.extension.equals("jpg", ignoreCase = true)
    }
}

private fun trimDirectory(
    dir: File,
    maxBytes: Long,
    includeFile: (File) -> Boolean = { true },
) {
    if (!dir.exists()) return
    val files = dir
        .walkTopDown()
        .filter { it.isFile && includeFile(it) }
        .toList()
    var total = files.sumOf { it.length() }
    if (total <= maxBytes) return

    files
        .sortedWith(compareBy<File> { it.lastModified() }.thenBy { it.length() })
        .forEach { file ->
            if (total <= maxBytes) return@forEach
            val length = file.length()
            if (file.delete()) {
                total -= length
            }
        }
}
