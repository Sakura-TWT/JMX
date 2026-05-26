package dev.jmx.client.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jmx.client.database.converter.ListStringToStringConverter
import dev.jmx.client.database.dao.DownloadAlbumDao
import dev.jmx.client.database.model.DownloadAlbum

@Database(entities = [DownloadAlbum::class], version = 2)
@TypeConverters(ListStringToStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadAlbumDao(): DownloadAlbumDao
}