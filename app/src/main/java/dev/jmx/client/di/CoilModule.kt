package dev.jmx.client.di

import dev.jmx.client.coil.createAsyncImageLoader
import org.koin.dsl.module

val coilModule = module {
    single { createAsyncImageLoader(get()) }
}