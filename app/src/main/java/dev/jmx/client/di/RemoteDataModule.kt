package dev.jmx.client.di

import dev.jmx.client.data.remote.ApiClient
import dev.jmx.client.data.remote.converter.PrimitiveToRequestBodyConverterFactory
import dev.jmx.client.data.remote.converter.ResponseConverterFactory
import dev.jmx.client.data.remote.interceptor.BaseUrlInterceptor
import dev.jmx.client.data.remote.interceptor.InitInterceptor
import dev.jmx.client.data.remote.interceptor.ToastInterceptor
import dev.jmx.client.data.remote.interceptor.TokenInterceptor
import dev.jmx.client.data.remote.service.AlbumService
import dev.jmx.client.data.remote.service.RemoteSettingService
import dev.jmx.client.data.remote.service.UserService
import dev.jmx.client.task.AppInitTask
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.converter.scalars.ScalarsConverterFactory

val remoteDataModule = module {
    single {
        ApiClient(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    } bind AppInitTask::class
    single<AlbumService> { get<ApiClient>().createService(AlbumService::class.java) }
    single<RemoteSettingService> { get<ApiClient>().createService(RemoteSettingService::class.java) }
    single<UserService> { get<ApiClient>().createService(UserService::class.java) }
    single { BaseUrlInterceptor(get()) }
    single { TokenInterceptor() }
    single { InitInterceptor(get()) }
    single { ToastInterceptor(get()) }
    single { ResponseConverterFactory(get()) }
    single { PrimitiveToRequestBodyConverterFactory() }
    single { ScalarsConverterFactory.create() }
}
