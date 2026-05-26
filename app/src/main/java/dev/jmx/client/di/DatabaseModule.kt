package dev.jmx.client.di

import androidx.room.Room
import dev.jmx.client.database.AppDatabase
import dev.jmx.client.store.DownloadManager
import dev.jmx.client.ui.viewModel.DownloadViewModel
import dev.jmx.client.worker.DownloadAlbumWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration(false).build()
    }
    single { get<AppDatabase>().downloadAlbumDao() }
    single { DownloadManager(get(), get(), get(), get()) }
    viewModel { DownloadViewModel(get()) }

    worker { DownloadAlbumWorker(get(), get(), get(), get(), get(), get(), get(), get()) }
}
