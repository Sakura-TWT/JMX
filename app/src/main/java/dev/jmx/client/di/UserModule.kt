package dev.jmx.client.di

import dev.jmx.client.repository.UserRepository
import dev.jmx.client.repository.impl.UserRepositoryImpl
import dev.jmx.client.ui.viewModel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val userModule = module {
    single { UserRepositoryImpl(get(), get(), get(), get()) } bind UserRepository::class

    viewModel { UserViewModel(get(), get(), get()) }
}
