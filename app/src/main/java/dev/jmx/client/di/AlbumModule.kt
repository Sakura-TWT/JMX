package dev.jmx.client.di

import dev.jmx.client.repository.AlbumRepository
import dev.jmx.client.repository.impl.AlbumRepositoryImpl
import dev.jmx.client.ui.viewModel.AlbumDetailViewModel
import dev.jmx.client.ui.viewModel.AlbumReadViewModel
import dev.jmx.client.ui.viewModel.AlbumViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val albumModule = module {
    single { AlbumRepositoryImpl(get(), get()) } bind AlbumRepository::class

    viewModel { AlbumViewModel(get()) }
    viewModel { AlbumDetailViewModel(get(), get(), get(), get()) }
    viewModel { AlbumReadViewModel(get(), get(), get()) }
}