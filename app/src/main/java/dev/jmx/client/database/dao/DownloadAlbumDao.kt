package dev.jmx.client.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.jmx.client.database.model.DownloadAlbum
import dev.jmx.client.database.model.UpdateAlbumCover
import dev.jmx.client.database.model.UpdateAlbumProgress
import dev.jmx.client.database.model.UpdateAlbumStatus
import dev.jmx.client.database.model.UpdateAlbumZipPath
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadAlbumDao {
    @Query("SELECT * FROM download_albums WHERE status = 'pending' OR status = 'downloading' ORDER BY createTime DESC")
    fun getDownloadingList(): PagingSource<Int, DownloadAlbum>

    @Query("SELECT * FROM download_albums WHERE status = 'complete' ORDER BY createTime DESC")
    fun getCompleteList(): PagingSource<Int, DownloadAlbum>

    @Query("SELECT EXISTS(SELECT 1 FROM download_albums WHERE id = :albumId)")
    fun isExist(albumId: Int): Flow<Boolean>

    @Update(entity = DownloadAlbum::class)
    suspend fun updateCover(updateAlbumCover: UpdateAlbumCover)

    @Update(entity = DownloadAlbum::class)
    suspend fun updateStatus(updateAlbumStatus: UpdateAlbumStatus)

    @Update(entity = DownloadAlbum::class)
    suspend fun updateProgress(updateAlbumProgress: UpdateAlbumProgress)

    @Update(entity = DownloadAlbum::class)
    suspend fun updateZipPath(updateAlbumZipPath: UpdateAlbumZipPath)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: DownloadAlbum)

    @Update
    suspend fun update(task: DownloadAlbum)

    @Delete
    suspend fun delete(task: DownloadAlbum)
}