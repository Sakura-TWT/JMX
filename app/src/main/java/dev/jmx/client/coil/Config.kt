package dev.jmx.client.coil

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dev.jmx.client.cache.getCommonCacheDir

fun createAsyncImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.18)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(getCommonCacheDir(context))
                .maxSizeBytes(64L * 1024L * 1024L)
                .build()
        }
        .build()
}
