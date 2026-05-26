package dev.jmx.client.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "download_albums")
data class DownloadAlbum(
    @PrimaryKey
    val id: Int,
    val name: String,
    val authorList: List<String>,
    val coverPath: String,
    val zipPath: String,
    val progress: Float,
    val status: String, // pending || downloading || complete
    val createTime: Long,
)